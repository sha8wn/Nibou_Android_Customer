package com.nibou.niboucustomer.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.nibou.niboucustomer.R;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;


public class AppUtil {

    public static String getAmountSign(Context context) {
        if (LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE) != null) {
            if (AppConstant.ENGLISH.equals(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE))) {
                return context.getString(R.string.doller);
            } else if (AppConstant.ARABIC.equals(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE))) {
                return context.getString(R.string.aed);
            } else if (AppConstant.TURKISH.equals(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE))) {
                return context.getString(R.string.tl);
            } else {
                return context.getString(R.string.doller);
            }
        } else {
            return context.getString(R.string.doller);
        }
    }

    public static void setFragment(int containerViewId, FragmentManager fragmentManager, Fragment fragment, String fragmentTag, boolean addBackStack, boolean replace) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (replace) {
            fragmentTransaction.replace(containerViewId, fragment, fragmentTag);
        } else {
            fragmentTransaction.add(containerViewId, fragment, fragmentTag);
        }
        if (addBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public static void changeStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLog(String key, String message) {
        Log.e(key + ":", ":" + message);
    }


    public static boolean hasCameraPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasFineLocationPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasCoarseLocationPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasStoragePermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAvailableLocationProvider(Context context) {
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean network = mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (network) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @SuppressLint("MissingPermission")
    public static Location getLastKnownLocation(Context context) {
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String provider;
        if (isAvailableLocationProvider(context)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            provider = LocationManager.GPS_PROVIDER;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return mlocManager.getLastKnownLocation(provider);
    }

    @SuppressLint("MissingPermission")
    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getActiveNetworkInfo() != null)
                return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getAppVersion(Context context) {
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static void openDialer(Context context, String phone) {
        try {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + phone));
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "", Toast.LENGTH_LONG).show();
        }
    }

    public static void openEmail(Context context, String email) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            context.startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "", Toast.LENGTH_LONG).show();
        }
    }


    public static void hideKeyBoard(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void watchYoutubeVideo(Context context, String id) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
        try {
            context.startActivity(webIntent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "", Toast.LENGTH_LONG).show();
        }
    }

    public static float getDPFromPixels(Context context, float px) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    public static float getPixelFromDP(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static File getImageFolderPath() {
        File myDir = new File(Environment.getExternalStorageDirectory() + "/" + "");
        if (!myDir.exists())
            myDir.mkdirs();
        return myDir;
    }

    public static File saveBitampAsImage(String name, Bitmap bitmap) {
        File file = new File(getImageFolderPath(), name);
        if (bitmap != null) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                if (file.exists()) {
                    file.delete();
                }
                file = null;
            }
        } else {
            if (file.exists()) {
                file.delete();
            }
            file = null;
        }
        return file;
    }


    public static void openFile(Context context, String url) {
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
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap loadBitmapFromView(View v) {
        v.setDrawingCacheEnabled(true);
        return v.getDrawingCache();
    }

    public static Bitmap combineImageIntoOne(ArrayList<Bitmap> bitmap) {
        int w = 0, h = 0;
        if (bitmap.size() == 1) {
            w += bitmap.get(0).getWidth();
            h += bitmap.get(0).getHeight();
        } else {
            for (int i = 0; i < bitmap.size(); i++) {
                if (i < bitmap.size() - 1) {
                    w = bitmap.get(i).getWidth() > bitmap.get(i + 1).getWidth() ? bitmap.get(i).getWidth() : bitmap.get(i + 1).getWidth();
                }
                h += bitmap.get(i).getHeight();
            }
        }

        Bitmap temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        int top = 0;
        for (int i = 0; i < bitmap.size(); i++) {
            canvas.drawBitmap(bitmap.get(i), 0f, top, null);
            top = top + bitmap.get(i).getHeight();
        }
        return temp;
    }

    public static Bitmap getBitmapOfFile(File image) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        return bitmap;
    }

    public static String generateTimestampInGMT(Long millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date date = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.setTimeZone(TimeZone.getDefault());
        date = calendar.getTime();
        return simpleDateFormat.format(date);
    }

    public static String changeFormatInGMT(String dateString, String newformat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newDateFormat = new SimpleDateFormat(newformat);
        newDateFormat.setTimeZone(TimeZone.getDefault());
        String format = "";
        try {
            Date d = simpleDateFormat.parse(dateString);
            format = newDateFormat.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return format;
    }
}
