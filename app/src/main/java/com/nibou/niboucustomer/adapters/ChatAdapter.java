package com.nibou.niboucustomer.adapters;

import android.content.Context;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kevalpatel2106.emoticongifkeyboard.widget.EmoticonTextView;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.models.RealmChatModel;
import com.nibou.niboucustomer.realm.RealmHandler;
import com.nibou.niboucustomer.utils.*;

import io.realm.RealmChangeListener;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ChatAdapter extends RealmRecyclerViewAdapter<RealmChatModel, ChatAdapter.ViewHolder> implements View.OnClickListener {

    private ChatCallback chatCallback;
    private Context context;
    private String USER_ID = "";

    public interface ChatCallback {
        void retry(RealmChatModel realmChatModel);

        void download(RealmChatModel realmChatModel);

        void loadMoreMessageHistory(RealmChatModel realmChatModel);
    }

    public ChatAdapter(Context context, ChatCallback chatCallback, String chatId) {
        super(new RealmHandler().getRealm().where(RealmChatModel.class).equalTo("chatId", chatId).findAllSortedAsync("timeStamp", Sort.DESCENDING), true);
        this.context = context;
        this.chatCallback = chatCallback;
        USER_ID = LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat, viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //pagination click handler
        if ((position > getItemCount() - 5) && (getItemCount() > 20)) {
            if (chatCallback != null) {
                chatCallback.loadMoreMessageHistory(getItem(getItemCount() - 1));
            }
        }

        final RealmChatModel model = getItem(position);

        // date header handler
        if (showheader(position)) {
            holder.tv_date.setVisibility(View.VISIBLE);
            holder.tv_date.setText(getDate(getItem(position).getTimeStamp()));
        } else {
            holder.tv_date.setVisibility(View.GONE);
        }


        if (model.getUserId().equals(USER_ID)) {
            //sender messages
            holder.left_view.setVisibility(View.GONE);
            holder.right_view.setVisibility(View.VISIBLE);

            //message text handle
            if (model.getMessage() != null && !model.getMessage().isEmpty()) {
                holder.right_text_message.setVisibility(View.VISIBLE);
                holder.right_text_message.setText(model.getMessage());

                holder.right_text_time.setVisibility(View.VISIBLE);
                holder.right_image_time.setVisibility(View.GONE);
                holder.right_text_time.setText(DateFormatUtil.getRequiredDateFormat(getItem(position).getTimeStamp(), "HH:mm"));
            } else {
                holder.right_text_message.setVisibility(View.GONE);
                holder.right_text_time.setVisibility(View.GONE);
                holder.right_image_time.setVisibility(View.VISIBLE);
                holder.right_image_time.setText(DateFormatUtil.getRequiredDateFormat(getItem(position).getTimeStamp(), "HH:mm"));
            }

            //image handler
            if (model.getLocalImageUrl() != null && !model.getLocalImageUrl().isEmpty()) {
                holder.right_mediaView.setVisibility(View.VISIBLE);
                showImage(holder.right_imageView, model.getLocalImageUrl());
            } else {
                if (model.getImages() != null && !model.getImages().isEmpty()) {
                    holder.right_mediaView.setVisibility(View.VISIBLE);
                    showBlurImage(holder.right_imageView, model.getImages().split(ChatConstants.IMAGE_PREVIEW_SEPRATOR)[0]);
                } else {
                    holder.right_mediaView.setVisibility(View.GONE);
                }
            }

            // image sending error handler
            if (model.isError()) {
                holder.right_progress.setVisibility(View.GONE);
                holder.right_image_action.setVisibility(View.VISIBLE);
                holder.right_download.setImageResource(R.drawable.error);
            } else {
                // image download alert handler
                if (isExitsOnDevice(model.getLocalImageUrl())) {
                    holder.right_progress.setVisibility(View.GONE);
                    holder.right_image_action.setVisibility(View.GONE);
                } else {
                    holder.right_image_action.setVisibility(View.VISIBLE);
                    holder.right_download.setImageResource(R.drawable.download_play);
                }
            }

            // image sending handler
            if (model.isSending()) {
                holder.right_progress.setVisibility(View.VISIBLE);
            } else {
                holder.right_progress.setVisibility(View.GONE);
            }

            // image downloading handler
            if (model.isDownloading()) {
                holder.right_progress.setVisibility(View.VISIBLE);
            } else {
                holder.right_progress.setVisibility(View.GONE);
            }
        } else {
            // reciever messages
            holder.left_view.setVisibility(View.VISIBLE);
            holder.right_view.setVisibility(View.GONE);

            //message text handle
            if (model.getMessage() != null && !model.getMessage().isEmpty()) {
                holder.left_text_message.setVisibility(View.VISIBLE);
                holder.left_text_message.setText(model.getMessage());
                holder.left_text_time.setVisibility(View.VISIBLE);
                holder.left_image_time.setVisibility(View.GONE);
                holder.left_text_time.setText(DateFormatUtil.getRequiredDateFormat(getItem(position).getTimeStamp(), "HH:mm"));
            } else {
                holder.left_text_message.setVisibility(View.GONE);
                holder.left_text_time.setVisibility(View.GONE);
                holder.left_image_time.setVisibility(View.VISIBLE);
                holder.left_image_time.setText(DateFormatUtil.getRequiredDateFormat(getItem(position).getTimeStamp(), "HH:mm"));
            }

            //image handler
            if (model.getLocalImageUrl() != null && !model.getLocalImageUrl().isEmpty()) {
                holder.left_mediaView.setVisibility(View.VISIBLE);
                showImage(holder.left_imageView, model.getLocalImageUrl());
            } else {
                if (model.getImages() != null && !model.getImages().isEmpty()) {
                    holder.left_mediaView.setVisibility(View.VISIBLE);
                    showBlurImage(holder.left_imageView, model.getImages().split(ChatConstants.IMAGE_PREVIEW_SEPRATOR)[0]);
                } else {
                    holder.left_mediaView.setVisibility(View.GONE);
                }
            }

            // image download handler
            if (isExitsOnDevice(model.getLocalImageUrl())) {
                holder.left_image_action.setVisibility(View.GONE);
                holder.left_progress.setVisibility(View.GONE);
            } else {
                holder.left_image_action.setVisibility(View.VISIBLE);
                holder.left_download.setImageResource(R.drawable.download_play);
            }

            // image downloading handler
            if (model.isDownloading()) {
                holder.left_progress.setVisibility(View.VISIBLE);
            } else {
                holder.left_progress.setVisibility(View.GONE);
            }
        }

        // message click handler
        holder.left_image_action.setTag(position);
        holder.left_image_action.setOnClickListener(v -> {
            chatCallback.download(getItem(Integer.parseInt(v.getTag().toString())));
        });

        // message click handler
        holder.right_image_action.setTag(position);
        holder.right_image_action.setOnClickListener(v -> {
            RealmChatModel realmChatModel = getItem(Integer.parseInt(v.getTag().toString()));
            if (realmChatModel.isError()) {
                if (chatCallback != null)
                    chatCallback.retry(realmChatModel);
            } else {
                if (chatCallback != null)
                    chatCallback.download(realmChatModel);
            }
        });
        holder.right_mediaView.setTag(model.getLocalImageUrl());
        holder.right_mediaView.setOnClickListener(v -> {
            if (isExitsOnDevice(String.valueOf(v.getTag())))
                AppDialogs.getInstance().showFullScreenImage(context, String.valueOf(v.getTag()));
        });
        holder.left_mediaView.setTag(model.getLocalImageUrl());
        holder.left_mediaView.setOnClickListener(v -> {
            if (isExitsOnDevice(String.valueOf(v.getTag())))
                AppDialogs.getInstance().showFullScreenImage(context, String.valueOf(v.getTag()));
        });
    }


    private void showImage(final ImageView imageView, String path) {
        Glide.with(context).load(path).centerCrop().placeholder(R.drawable.default_placeholder).error(R.drawable.default_placeholder).into(imageView);
    }

    private void showBlurImage(final ImageView imageView, String path) {
        Glide.with(context).load(path).centerCrop().bitmapTransform(new BlurTransformation(context, 2, 5)).placeholder(R.drawable.default_placeholder).error(R.drawable.default_placeholder).into(imageView);
    }

    private boolean isExitsOnDevice(String url) {
        try {
            if (url != null && new File(url) != null && new File(url).exists()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getDate(String millies) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        if (DateFormatUtil.getRequiredDate(millies).before(calendar.getTime())) {
            return DateFormatUtil.getRequiredDateFormat(millies, "EEEE, dd MMM");
        } else {
            return DateFormatUtil.getRequiredDateFormat(millies, "EEEE, HH:mm");
        }
    }

    private boolean showheader(int pos) {
        if (pos + 1 < getItemCount()) {
            Date curr = DateFormatUtil.getRequiredDate(getItem(pos).getTimeStamp());
            Date per = DateFormatUtil.getRequiredDate(getItem(pos + 1).getTimeStamp());
            if (curr.getYear() != per.getYear())
                return true;
            if (curr.getMonth() != per.getMonth())
                return true;
            if (curr.getDay() != per.getDay())
                return true;
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View right_image_action, left_image_action, right_view, left_view;
        private TextView tv_date, right_action_text, left_action_text, left_text_time, right_text_time, left_image_time, right_image_time;
        private EmoticonTextView right_text_message, left_text_message;
        private CardView right_mediaView, left_mediaView;
        private ImageView left_imageView, right_imageView, left_download, right_download;

        private ProgressBar right_progress, left_progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            right_view = itemView.findViewById(R.id.right_view);
            left_view = itemView.findViewById(R.id.left_view);
            tv_date = itemView.findViewById(R.id.tv_date);
            right_text_message = itemView.findViewById(R.id.right_text_message);
            left_text_message = itemView.findViewById(R.id.left_text_message);
            right_mediaView = itemView.findViewById(R.id.right_mediaView);
            left_mediaView = itemView.findViewById(R.id.left_mediaView);
            left_imageView = itemView.findViewById(R.id.left_imageView);
            right_imageView = itemView.findViewById(R.id.right_imageView);

            right_action_text = itemView.findViewById(R.id.right_action_text);
            right_progress = itemView.findViewById(R.id.right_progress);
            right_image_action = itemView.findViewById(R.id.right_image_action);

            left_action_text = itemView.findViewById(R.id.left_action_text);
            left_progress = itemView.findViewById(R.id.left_progress);
            left_image_action = itemView.findViewById(R.id.left_image_action);
            left_download = itemView.findViewById(R.id.left_download);
            right_download = itemView.findViewById(R.id.right_download);
            left_text_time = itemView.findViewById(R.id.left_text_time);
            left_image_time = itemView.findViewById(R.id.left_image_time);
            right_text_time = itemView.findViewById(R.id.right_text_time);
            right_image_time = itemView.findViewById(R.id.right_image_time);
        }
    }
}
