package com.example.a3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CartActivity";
    private static final String USERS = "users";
    private static final String CART = "cart";
    private static final String ORDERS = "orders";
    private static final String TICKETS = "tickets";

    private List<String> keyList = new ArrayList<>();
    private List<Ticket> ticketList = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initListeners();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final CartAdapter adapter = new CartAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(CART);

        DatabaseReference refForTickets = FirebaseDatabase.getInstance().getReference()
                .child(TICKETS);

//        try {
//            keyList = new CartAsyncTask().execute().get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyCallback myCallback = o -> {
                    Log.d(TAG, "adding key" + (String)o);
                    Integer index = Integer.parseInt((String)o) - 1;
                    if (ticketList.isEmpty()) Log.d(TAG, "ticketList is empty");
                    else tickets.add(ticketList.get(index));
                };
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getValue(Boolean.class)) {
                        Log.d(TAG, "key from firebase: " + ds.getKey());
                        myCallback.onCallBack(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //or at least an alert
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        if (keyList.isEmpty()) {
            Log.d(TAG, "keyList is empty");
        }
        else
        for (String s:keyList) {
            Log.d(TAG, "keyList: " + s);
        }

        refForTickets.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyCallback myCallback = o -> {
                    Log.d(TAG, "adding ticket");
                    ticketList.add((Ticket)o);
                };
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Log.d(TAG, "keys from tickets: " + ds.getKey());
                    if (keyList.contains(ds.getKey())) {
                        myCallback.onCallBack(ds.getValue(Ticket.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter.setTickets(tickets);
    }

//    private void removeFromTickets(String s){
//        for(Ticket t:ticketList){
//            if (t.getId().equals(s)){
//                ticketList.remove(t);
//            }
//        }
//    }
//
    private void addToKeys(String key){
        Log.d(TAG, "added to keyList: " + key);
        keyList.add(key);
        for(String s:keyList){
            Log.d(TAG, "for in addToKeys: " + s);
        }
    }
//
//    private void addToTickets(Ticket ticket) {
//        Log.d(TAG, "added to ticketList: " + ticket.getId());
//        ticketList.add(ticket);
//    }

    private void initListeners(){
        findViewById(R.id.finalize).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.finalize:
                finalizeOrder();
                break;
        }
    }

    private void finalizeOrder(){

    }

}
