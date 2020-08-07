package com.nibou.niboucustomer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.activitys.EarningDetailsActivity;
import com.nibou.niboucustomer.models.PaymentListModel;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.DateFormatUtil;


public class EarningListAdapter extends RecyclerView.Adapter<EarningListAdapter.MyViewHolder> {


    private Context context;
    private PaymentListModel paymentListModel;

    public EarningListAdapter(Context context, PaymentListModel paymentListModel) {
        this.context = context;
        this.paymentListModel = paymentListModel;
    }

    @NonNull
    @Override
    public EarningListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.earning_list_item, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningListAdapter.MyViewHolder myViewHolder, final int i) {
        myViewHolder.date.setText(DateFormatUtil.getRequiredDateFormat(DateFormatUtil.getServerMilliSeconds(paymentListModel.getData().get(i).getAttributes().getDate_at()), "dd MMMM , yyyy"));
        myViewHolder.value.setText((AppUtil.getAmountSign(context) + paymentListModel.getData().get(i).getAttributes().getAmount()));
        if (paymentListModel.getData().get(i).getAttributes().getTotalSeconds() != null) {
            myViewHolder.money.setText(((Long.parseLong(paymentListModel.getData().get(i).getAttributes().getTotalSeconds()) % 3600) / 60) + " " + context.getString(R.string.minutes));
        }

        myViewHolder.item.setTag(i);
        myViewHolder.item.setOnClickListener(v -> {
            Intent intent = new Intent(context, EarningDetailsActivity.class);
            intent.putExtra("date", paymentListModel.getData().get(Integer.parseInt(v.getTag().toString())).getAttributes().getDate_at());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (paymentListModel != null && paymentListModel.getData() != null) {
            return paymentListModel.getData().size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView date, money, value;
        private View item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            money = itemView.findViewById(R.id.money);
            value = itemView.findViewById(R.id.value);
            item = itemView.findViewById(R.id.item);
        }
    }
}
