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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CartActivity";
    private static final String USERS = "users";
    private static final String CART = "cart";
    private static final String ORDERS = "orders";
    private static final String TICKETS = "tickets";

    private List<String> keyList = new ArrayList<>();
    private List<Ticket> ticketList = new ArrayList<>();

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

        try {
            keyList = new CartAsyncTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot ds: dataSnapshot.getChildren()) {
//                    if (!ds.getValue(Boolean.class)) {
//                        Log.d(TAG, "key from firebase: " + ds.getKey());
//                        removeFromTickets(ds.getKey());
//                        addToKeys(ds.getKey());
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //or at least an alert
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });

        if (keyList.isEmpty()) {
            Log.d(TAG, "keyList is empty");
        }
        else
        for (String s:keyList) {
            Log.d(TAG, "keyList: " + s);
        }

//        refForTickets.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds :dataSnapshot.getChildren()){
//                    Log.d(TAG, "keys from tickets: " + ds.getKey());
//                    //if (keyList.contains(ds.getKey())) {
//                        //Log.d(TAG, "here we are");
//                        addToTickets(ds.getValue(Ticket.class));
//                    //}
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        try {
            ticketList = new TicketAsyncTask(refForTickets).execute(keyList).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        adapter.setTickets(ticketList);
    }

//    private void removeFromTickets(String s){
//        for(Ticket t:ticketList){
//            if (t.getId().equals(s)){
//                ticketList.remove(t);
//            }
//        }
//    }
//
//    private void addToKeys(String key){
//        Log.d(TAG, "added to keyList: " + key);
//        keyList.add(key);
//        for(String s:keyList){
//            Log.d(TAG, "for in addToKeys: " + s);
//        }
//    }
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

    private static void cartAsyncFinished(List<String> result) {
        //keyList = result;
    }

    private class CartAsyncTask extends AsyncTask<Void, Void, List<String>> {
        private DatabaseReference reference;
        private List<String> keys;

        CartAsyncTask() {
            Log.d(TAG, "CartAsyncTask constructor");
            reference = FirebaseDatabase.getInstance().getReference()
                    .child(USERS)
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .child(CART);
            keys = new ArrayList<>();
        }

        @Override
        protected List<String> doInBackground(final Void... params) {
            Log.d(TAG, "CartAsyncTask doInBackground");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "in CartAsyncTask");
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        if (ds.getValue(Boolean.class)) {
                            Log.d(TAG, "key from firebase: " + ds.getKey());
                            keyList.add(ds.getKey());
                            addKeys(ds.getKey());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //or at least an alert
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

            if (keys.isEmpty()) {
                Log.d(TAG, "in CartAsyncTask not ok");
            }
            return keys;
        }

        @Override
        protected void onPostExecute(List<String> result){
            cartAsyncFinished(result);
        }

        void addKeys(String key){
            Log.d(TAG, "added to keys");
            keys.add(key);
        }
    }

    private static class TicketAsyncTask extends AsyncTask<List<String>, Void, ArrayList<Ticket>> {
        private DatabaseReference reference;
        private ArrayList<Ticket> tickets;

        TicketAsyncTask(DatabaseReference dr) {
            reference = dr;
            tickets = new ArrayList<>();
        }

        @Override
        protected ArrayList<Ticket> doInBackground(final List<String>... lists) {
            if (lists[0].isEmpty()) return null;
            final List<String> passed = lists[0];
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds :dataSnapshot.getChildren()){
                        Log.d(TAG, "keys from tickets: " + ds.getKey());
                        if (passed.contains(ds.getKey())) {
                            Log.d(TAG, "adding ticket");
                            addTickets(ds.getValue(Ticket.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
            if(tickets.isEmpty()) {
                Log.d(TAG, "in TicketAsyncTask is not ok");
            }
            return tickets;
        }

        void addTickets(Ticket ticket) {
            Log.d(TAG, "added ticket");
            tickets.add(ticket);
        }
    }

}
