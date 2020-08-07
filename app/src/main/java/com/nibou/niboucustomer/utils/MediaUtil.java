package com.nibou.niboucustomer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.nibou.niboucustomer.R;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MediaUtil {
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_MULTIPLE_IMAGE_GALLERY = 500;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_FILE_CREATE_PERMISSION = 5;
    private Context context;
    private Uri cameraPhotoUri;
    private Fragment fragment;
    private SelectedImageCallback selectedImageCallback;
    private boolean deleteOldFile;

    public interface CompressImageCallback {
        void response(Bitmap bitmap, String fileName, String path);
    }

    public interface SelectedImageCallback {
        void response(boolean isCamera, Object imageResult);
    }

    public interface DownloadedFileCallback {
        void progress(int progress);

        void response(boolean isSuccess, String taskId, String path);
    }

    public MediaUtil(Context context) {
        this.context = context;
    }

    public MediaUtil(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    public static void scanFileForGallery(Context context, File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, (path, uri) -> Log.i("Scan", "Done"));
    }

    public void openMultipleImageGallery(SelectedImageCallback selectedImageCallback) {
        this.selectedImageCallback = selectedImageCallback;
        ImagePicker.create(fragment)
                .returnAfterFirst(true)
                .folderMode(true)
                .folderTitle("Select")
                .imageTitle("Select")
                .single()
                .showCamera(true)
                .theme(R.style.ImagePickerTheme)
                .start(REQUEST_MULTIPLE_IMAGE_GALLERY);
    }

    public void openCameraForImage(boolean deleteOldFile, SelectedImageCallback selectedImageCallback) {
        this.deleteOldFile = deleteOldFile;
        this.selectedImageCallback = selectedImageCallback;
        if (hasCameraPermission(context) && hasStoragePermission(context)) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(((AppCompatActivity) context), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                File photoFile = createImageFile("Camera" + System.currentTimeMillis() + ".jpg");
                if (photoFile != null) {
                    cameraPhotoUri = Uri.fromFile(photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
                    fragment.startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            if (selectedImageCallback != null) {
                selectedImageCallback.response(true, cameraPhotoUri);
            }
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_CAMERA) {
            try {
                if (getRealPathFromURI(cameraPhotoUri) != null && new File(getRealPathFromURI(cameraPhotoUri)).exists()) {
                    new File(getRealPathFromURI(cameraPhotoUri)).delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_MULTIPLE_IMAGE_GALLERY) {
            ArrayList<Image> imagesList = (ArrayList<Image>) ImagePicker.getImages(data);
            if (selectedImageCallback != null && imagesList != null && imagesList.size() > 0) {
                selectedImageCallback.response(false, imagesList);
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (hasCameraPermission(context) && hasStoragePermission(context)) {
                dispatchTakePictureIntent();
            }
        }
    }

    private File createImageFile(String fileName) throws IOException {
        return getFolderLocation(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, fileName);
    }

    private File createMultipleImageFile(String imageName) throws IOException {
        return getFolderLocation(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, imageName);
    }

    private boolean hasRecodingPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasStoragePermission(Context context) {
        int write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean hasCameraPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }
                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if (uri.getPath().startsWith("/root_path")) {
            return uri.getPath().replaceFirst("/root_path", "");
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                return cursor.getString(column_index);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public File getFolderLocation(String folderName, String fileName) {
        if (hasStoragePermission(context)) {
            File root = new File(Environment.getExternalStorageDirectory(), folderName);
            if (!root.exists()) {
                root.mkdirs();
            }
            return new File(root, fileName);
        } else {
            ActivityCompat.requestPermissions(((AppCompatActivity) context), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FILE_CREATE_PERMISSION);
        }
        return null;
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public void compressMultipleImages(final ArrayList<Image> imageArrayList, final int compressionRatio, final CompressImageCallback compressImageCallback) {
        class MyAsyncTask extends AsyncTask<Void, Void, Void> {
            Bitmap scaledBitmap = null;
            File locationToSaveFile = null;
            private String fileName;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                for (Image image : imageArrayList) {
                    try {
                        fileName = System.currentTimeMillis() + ".jpg";
                        locationToSaveFile = createMultipleImageFile(fileName);
                        Uri uri = Uri.parse(image.getPath());
                        String filePath = getRealPathFromURI(uri);
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
                        //you try the use the bitmap here, you will get null.
                        options.inJustDecodeBounds = true;
                        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

                        int actualHeight = options.outHeight;
                        int actualWidth = options.outWidth;

                        //max Height and width values of the compressed image is taken as 816x612
                        float maxHeight = 816.0f;
                        float maxWidth = 612.0f;
                        float imgRatio = actualWidth / actualHeight;
                        float maxRatio = maxWidth / maxHeight;

                        //width and height values are set maintaining the aspect ratio of the image
                        if (actualHeight > maxHeight || actualWidth > maxWidth) {
                            if (imgRatio < maxRatio) {
                                imgRatio = maxHeight / actualHeight;
                                actualWidth = (int) (imgRatio * actualWidth);
                                actualHeight = (int) maxHeight;
                            } else if (imgRatio > maxRatio) {
                                imgRatio = maxWidth / actualWidth;
                                actualHeight = (int) (imgRatio * actualHeight);
                                actualWidth = (int) maxWidth;
                            } else {
                                actualHeight = (int) maxHeight;
                                actualWidth = (int) maxWidth;
                            }
                        }

                        //setting inSampleSize value allows to load a scaled down version of the original image
                        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

                        //inJustDecodeBounds set to false to load the actual bitmap
                        options.inJustDecodeBounds = false;

                        //this options allow android to claim the bitmap memory if it runs low on memory
                        options.inPurgeable = true;
                        options.inInputShareable = true;
                        options.inTempStorage = new byte[16 * 1024];

                        //load the bitmap from its path
                        bmp = BitmapFactory.decodeFile(filePath, options);

                        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);

                        float ratioX = actualWidth / (float) options.outWidth;
                        float ratioY = actualHeight / (float) options.outHeight;
                        float middleX = actualWidth / 2.0f;
                        float middleY = actualHeight / 2.0f;

                        Matrix scaleMatrix = new Matrix();
                        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

                        Canvas canvas = new Canvas(scaledBitmap);
                        canvas.setMatrix(scaleMatrix);
                        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

                        //check the rotation of the image and display it properly
                        ExifInterface exif = new ExifInterface(filePath);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                        Matrix matrix = new Matrix();
                        if (orientation == 6) {
                            matrix.postRotate(90);
                        } else if (orientation == 3) {
                            matrix.postRotate(180);
                        } else if (orientation == 8) {
                            matrix.postRotate(270);
                        }
                        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                        FileOutputStream out = new FileOutputStream(locationToSaveFile);

                        //write the compressed bitmap at the destination specified by filename.
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, out);
                        if (deleteOldFile && filePath != null && new File(filePath).exists()) {
                            new File(filePath).delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        compressImageCallback.response(null, null, null);
                    }
                    compressImageCallback.response(scaledBitmap, fileName, locationToSaveFile.getAbsolutePath());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        new MyAsyncTask().execute();

    }

    public void downloadImage(String taskId, String fileUrl, final DownloadedFileCallback downloadedFileCallback) {
        class DownloadService extends AsyncTask<String, String, String> {
            private File videoFilePath;
            private boolean isSucess;
            private String taskId;

            public DownloadService(String taskId) {
                this.taskId = taskId;
            }

            @Override
            protected String doInBackground(String... f_url) {
                int count;
                try {
                    URL url = new URL(f_url[0]);
                    videoFilePath = getFolderLocation(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, System.currentTimeMillis() + "." + FilenameUtils.getExtension(f_url[0]));
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    int lenghtOfFile = conection.getContentLength();
                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    // Output stream
                    OutputStream output = new FileOutputStream(videoFilePath);

                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }
                    // flushing output
                    output.flush();
                    // closing streams
                    output.close();
                    input.close();
                    isSucess = true;
                } catch (Exception e) {
                    isSucess = false;
                }
                if (isSucess) {
                    return videoFilePath.getAbsolutePath();
                } else {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String file_url) {
                if (downloadedFileCallback != null) {
                    if (file_url == null)
                        downloadedFileCallback.response(false, taskId, file_url);
                    else {
                        scanFileForGallery(context, new File(file_url));
                        downloadedFileCallback.response(true, taskId, file_url);
                    }
                }
            }
        }
        new DownloadService(taskId).execute(fileUrl);
    }

    public void openFile(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(new File(url));
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.toString().contains(".rtf")) {
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                intent.setDataAndType(uri, "video/*");
            } else if (url.toString().contains(".apk")) {
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
