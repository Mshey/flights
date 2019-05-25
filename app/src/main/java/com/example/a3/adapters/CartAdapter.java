package com.example.a3.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a3.R;
import com.example.a3.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private static final String TAG = "CartAdapter";

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView cartItemView;
        private final LinearLayout layout;

        private CartViewHolder(View itemView) {
            super(itemView);
            cartItemView = itemView.findViewById(R.id.textView);
            layout = itemView.findViewById(R.id.recycler_view_item);
        }
    }

    private final LayoutInflater mInflater;
    private List<Ticket> tickets;
    private List<String> keys;
    private Context context;

    public CartAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        tickets = new ArrayList<>();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        if (tickets != null) {
            final Ticket current = tickets.get(position);
            String s = current.getFrom() + " - " + current.getTo() + ": " + current.getDate();
            holder.cartItemView.setText(s);

        } else {
            // Covers the case of data not being ready yet.
            holder.cartItemView.setText("No items in cart");
        }

//        if (keys != null) {
//            final String current = keys.get(position);
//            holder.cartItemView.setText(current);
//        }
//        else {
//            Log.d(TAG, "no items in cart");
//            holder.cartItemView.setText("No items in cart");
//        }
    }

    public void setTickets(List<Ticket> tickets){
        Log.d(TAG, "set tickets");
        this.tickets = tickets;
        notifyDataSetChanged();
    }

    public void setKeys(List<String> key){
        Log.d(TAG, "added keys");
        keys = key;
        notifyDataSetChanged();
    }

    public void addToTickets(Ticket t) {
        if (t==null) Log.d(TAG, "ticket is null");
        else if (tickets == null) Log.d(TAG, "tickets is null");
        else {
            Log.d(TAG, "added to tickets");
            tickets.add(t);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (tickets != null)
            return tickets.size();
        else return 0;
    }
}
