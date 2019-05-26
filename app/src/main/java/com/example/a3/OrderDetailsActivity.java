package com.example.a3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.a3.adapters.CartAdapter;
import com.example.a3.model.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailsActivity";
    private static final String USERS = "users";
    private static final String ORDERS = "orders";
    private static final String TICKETS = "tickets";
    private String orderId = "0";

    private List<Ticket> ticketList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Log.d(TAG, "onCreate: started.");

        getIncomingIntent();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final CartAdapter adapter = new CartAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference refForTickets = FirebaseDatabase.getInstance().getReference()
                .child(TICKETS);

        refForTickets.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyCallback myCallback = o -> {
                    Log.d(TAG, "adding ticket");
                    ticketList.add(o);
                };
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Log.d(TAG, "keys from tickets: " + ds.getKey());
                    //if (keyList.contains(ds.getKey())) {
                    myCallback.onCallBack(ds.getValue(Ticket.class));
                    //}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(ORDERS)
                .child(orderId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyCallback myCallback = adapter::addToTickets;
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getValue(Boolean.class)) {
                        int index = Integer.parseInt(Objects.requireNonNull(ds.getKey())) - 1;
                        Log.d(TAG, "adding key: " + ds.getKey() + " index: " + index);
                        if (ticketList.get(index) == null) Log.d(TAG, "ticketList[index] is null");
                        else {
                            myCallback.onCallBack(ticketList.get(index));
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //or at least an alert
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if (getIntent().hasExtra("orderId")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            orderId = getIntent().getStringExtra("orderId");
        }
    }
}
