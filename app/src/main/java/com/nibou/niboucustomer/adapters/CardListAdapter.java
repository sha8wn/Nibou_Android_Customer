package com.nibou.niboucustomer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.activitys.PaymentCardListActivity;
import com.nibou.niboucustomer.activitys.UserCheckActivity;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.models.CardModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.DateFormatUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.ArrayList;


public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.MyViewHolder> {

    private Context context;
    private ProfileModel profileModel;

    public CardListAdapter(Context context, ProfileModel profileModel) {
        this.context = context;
        this.profileModel = profileModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_card_list, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        if (DateFormatUtil.isCardExpired(profileModel.getIncluded().get(position).getAttributes().getExp_date()) || !profileModel.getIncluded().get(position).getAttributes().isIs_active()) {
            myViewHolder.expire_icon.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.expire_icon.setVisibility(View.INVISIBLE);
        }

        if (profileModel.getIncluded().get(position).getAttributes().isIs_default()) {
            myViewHolder.item.setBackground(ContextCompat.getDrawable(context, R.drawable.white_bg_drawable));
        } else {
            myViewHolder.item.setBackground(ContextCompat.getDrawable(context, R.drawable.shadow_bg_drawable));
        }

        if (profileModel.getIncluded().get(position).getAttributes().isIs_server_default()) {
            myViewHolder.delete.setVisibility(View.INVISIBLE);
        } else {
            myViewHolder.delete.setVisibility(View.VISIBLE);
        }

        myViewHolder.card_number.setText("****          ****          ****          " + profileModel.getIncluded().get(position).getAttributes().getLast_numbers());
        myViewHolder.card_type.setText(profileModel.getIncluded().get(position).getAttributes().getCard_type());
        myViewHolder.expire.setText(profileModel.getIncluded().get(position).getAttributes().getExp_date());


        myViewHolder.item.setTag(position);
        myViewHolder.item.setOnClickListener(v -> {
            int pos = Integer.parseInt(v.getTag().toString());
            if (!profileModel.getIncluded().get(pos).getAttributes().isIs_default()) {
                selectCardDefault(pos);
                if (context instanceof PaymentCardListActivity) {
                    ((PaymentCardListActivity) context).cardSelected(true, pos);
                }
            }
        });

        myViewHolder.card_number.setTag(position);
        myViewHolder.card_number.setOnClickListener(v -> {
            int pos = Integer.parseInt(v.getTag().toString());
            if (!profileModel.getIncluded().get(pos).getAttributes().isIs_default()) {
                selectCardDefault(pos);
                if (context instanceof PaymentCardListActivity) {
                    ((PaymentCardListActivity) context).cardSelected(true, pos);
                }
            }
        });

        myViewHolder.delete.setTag(position);
        myViewHolder.delete.setOnClickListener(v -> {
            AppDialogs.getInstance().showConfirmCustomDialog(context, context.getString(R.string.alert), context.getString(R.string.delete_alert), context.getString(R.string.no).toUpperCase(), context.getString(R.string.yes).toUpperCase(), status -> {
                if (status) {
                    if (AppUtil.isInternetAvailable(context)) {
                        if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null) {
                            deleteCardNetworkCall(Integer.parseInt(v.getTag().toString()));
                        }
                    } else {
                        AppUtil.showToast(context, context.getString(R.string.internet_error));
                    }
                }
            });
        });

    }

    private void selectCardDefault(int p) {
        if (profileModel != null && profileModel.getIncluded() != null) {
            for (ProfileModel.Data model : profileModel.getIncluded()) {
                model.getAttributes().setIs_default(false);
            }
            profileModel.getIncluded().get(p).getAttributes().setIs_default(true);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (profileModel != null && profileModel.getIncluded() != null) {
            return profileModel.getIncluded().size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView card_type, delete, card_number, expire;
        private View item;
        private ImageView expire_icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            card_type = itemView.findViewById(R.id.card_type);
            delete = itemView.findViewById(R.id.delete);
            card_number = itemView.findViewById(R.id.card_number);
            expire = itemView.findViewById(R.id.expire);
            expire_icon = itemView.findViewById(R.id.expire_icon);
            expire_icon.setOnClickListener(v -> AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.expired_text), context.getString(R.string.card_expired_alert), context.getString(R.string.OK), null));
        }
    }

    private void deleteCardNetworkCall(int position) {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).deleteCreditCard(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), profileModel.getIncluded().get(position).getId()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    profileModel.getIncluded().remove(position);
                    notifyDataSetChanged();
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.error).toUpperCase(), context.getString(R.string.wrong_with_backend), context.getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }
}
