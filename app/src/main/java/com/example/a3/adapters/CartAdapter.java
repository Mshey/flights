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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private static final String TAG = "CartAdapter";
    private static final String CART = "cart";
    private static final String USERS = "users";

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
    private Context context;

    public CartAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
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

//            holder.layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG,"onClick: clicked on:" + tickets.get(position));
//                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//                    mDatabase.child(USERS)
//                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
//                            .child(CART)
//                            .child(tickets.get(position).getId())
//                            .setValue(true);
//
////                    Intent intent = new Intent(context, MenuActivity.class);
////                    intent.putExtra("id", (position + 1));
////                    context.startActivity(intent);
//
//                }
//            });

        } else {
            // Covers the case of data not being ready yet.
            holder.cartItemView.setText("No items in cart");
        }
    }

    public void setTickets(List<Ticket> tickets){
        this.tickets = tickets;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // restaurants has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (tickets != null)
            return tickets.size();
        else return 0;
    }
}
