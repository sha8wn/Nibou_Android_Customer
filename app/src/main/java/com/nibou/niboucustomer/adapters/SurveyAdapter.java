package com.nibou.niboucustomer.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.activitys.SignupActivity;
import com.nibou.niboucustomer.activitys.SurveyActivity;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.models.ExpertiseModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.models.SurveyModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;


import java.util.ArrayList;
import java.util.HashMap;


public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.MyViewHolder> {

    private Context context;
    private SurveyModel surveyModel;

    public SurveyAdapter(Context context, SurveyModel surveyModel) {
        this.context = context;
        this.surveyModel = surveyModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_survey, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        if (position < surveyModel.getData().size()) {
            myViewHolder.survey_name.setVisibility(View.VISIBLE);
            myViewHolder.plus.setVisibility(View.GONE);
            myViewHolder.survey_name.setText(surveyModel.getData().get(position).getAttributes().getTitle());
            if (surveyModel.getData().get(position).isSelected()) {
                myViewHolder.item.setBackground(ContextCompat.getDrawable(context, R.drawable.white_bg_drawable));
                myViewHolder.survey_name.setTextColor(ContextCompat.getColor(context, R.color.app_theme_color));
            } else {
                myViewHolder.item.setBackground(ContextCompat.getDrawable(context, R.drawable.white_corner_drawable));
                myViewHolder.survey_name.setTextColor(Color.WHITE);
            }
        } else if (position == surveyModel.getData().size()) {
            myViewHolder.item.setBackground(null);
            myViewHolder.survey_name.setTextColor(Color.WHITE);
            myViewHolder.survey_name.setVisibility(View.INVISIBLE);
            if (isSelectionAvailable()) {
                myViewHolder.plus.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.plus.setVisibility(View.GONE);
            }
        }

        myViewHolder.item.setTag(position);
        myViewHolder.item.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (Integer.parseInt(v.getTag().toString()) == surveyModel.getData().size()) {
                if (isSelectionAvailable()) {
                    AppDialogs.getInstance().showInputCustomDialog(context, context.getString(R.string.what_trouble_you), context.getString(R.string.OK), result -> {
                        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        if (result != null && !result.isEmpty()) {
                            if (AppUtil.isInternetAvailable(context)) {
                                addExpertiseNetworkCall(result);
                            } else {
                                AppUtil.showToast(context, context.getString(R.string.internet_error));
                            }
                        }
                    });
                }
            } else {
                if (surveyModel.getData().get(Integer.parseInt(v.getTag().toString())).isSelected()) {
                    surveyModel.getData().get(Integer.parseInt(v.getTag().toString())).setSelected(false);
                    notifyDataSetChanged();
                } else {
                    if (isSelectionAvailable()) {
                        surveyModel.getData().get(Integer.parseInt(v.getTag().toString())).setSelected(true);
                        notifyDataSetChanged();
                    }
                }
            }
            if (isOptionsSelected()) {
                if (context instanceof SurveyActivity) {
                    ((SurveyActivity) context).showContinueButton(true);
                }
            } else {
                if (context instanceof SurveyActivity) {
                    ((SurveyActivity) context).showContinueButton(false);
                }
            }
        });
    }

    private boolean isSelectionAvailable() {
        int count = 0;
        for (SurveyModel.Data model : surveyModel.getData()) {
            if (model.isSelected()) {
                count++;
            }
        }
        if (count == 3) {
            return false;
        }
        return true;
    }

    public boolean isOptionsSelected() {
        for (SurveyModel.Data model : surveyModel.getData()) {
            if (model.isSelected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return surveyModel.getData().size() + 1;
    }


    private void addExpertiseNetworkCall(String title) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("title", title);

        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).addExpertise(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object result) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    ExpertiseModel data = (ExpertiseModel) result;
                    data.getData().setSelected(true);
                    surveyModel.getData().add(data.getData());
                    notifyDataSetChanged();
                    if (isOptionsSelected()) {
                        if (context instanceof SurveyActivity) {
                            ((SurveyActivity) context).showContinueButton(true);
                        }
                    } else {
                        if (context instanceof SurveyActivity) {
                            ((SurveyActivity) context).showContinueButton(false);
                        }
                    }
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

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView survey_name;
        private View plus, item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            survey_name = itemView.findViewById(R.id.survey_name);
            plus = itemView.findViewById(R.id.img_plus);
            item = itemView.findViewById(R.id.item);
        }
    }
}
