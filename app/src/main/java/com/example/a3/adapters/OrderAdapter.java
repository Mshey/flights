package com.example.a3.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a3.OrderDetailsActivity;
import com.example.a3.R;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private static final String TAG = "OrderAdapter";

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderItemView;

        private OrderViewHolder(View itemView) {
            super(itemView);
            orderItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private Context context;
    private List<String> orderIds;

    public OrderAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        orderIds = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        if (orderIds != null) {
            final String current = orderIds.get(position);
            holder.orderItemView.setText(current);
            holder.orderItemView.setOnClickListener(v -> {
                Log.d(TAG, "clicked: " + current);
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderId", current);
                context.startActivity(intent);
            });

        } else {
            // Covers the case of data not being ready yet.
            holder.orderItemView.setText("No orders yet.");
        }
    }

    @Override
    public int getItemCount() {
        if (orderIds != null) return orderIds.size();
        return 0;
    }

    public void addToOrderIds(String s){
        Log.d(TAG, "add to orderIDs");
        orderIds.add(s);
        notifyDataSetChanged();
    }
}
