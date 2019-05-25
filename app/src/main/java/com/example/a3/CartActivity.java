package com.example.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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

public class CartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "CartActivity";
    private static final String USERS = "users";
    private static final String CART = "cart";
    private static final String ORDERS = "orders";
    private static final String TICKETS = "tickets";

    private DrawerLayout drawerLayout;

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

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyCallback myCallback = adapter::addToTickets;
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getValue(Boolean.class)) {
                        Log.d(TAG, "key from firebase: " + ds.getKey());
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

        DatabaseReference refForTickets = FirebaseDatabase.getInstance().getReference()
                .child(TICKETS);

        refForTickets.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyCallback myCallback = o -> {
                    Log.d(TAG, "adding ticket");
                    ticketList.add(o);
                    //adapter.addToTickets(o);
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

    }

    private void initListeners(){
        findViewById(R.id.finalize).setOnClickListener(this);
        drawerLayout = findViewById(R.id.cart_layout);
        NavigationView navigationView = findViewById(R.id.menu_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(CART);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getValue(Boolean.class)) {
                        Log.d(TAG, "key from firebase: " + ds.getKey());
                        int index = Integer.parseInt(Objects.requireNonNull(ds.getKey())) - 1;
                        Log.d(TAG, "adding key: " + ds.getKey() + " index: " + index);
                        if (ticketList.get(index) == null) Log.d(TAG, "ticketList[index] is null");
                        else {
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child(USERS)
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .child(ORDERS)
                                    .push()
                                    .child(ds.getKey())
                                    .setValue(true);

                            mDatabase.child(USERS)
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .child(CART)
                                    .child(ds.getKey())
                                    .setValue(false);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.flights:
                finish();
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                break;
            case R.id.orders:
                finish();
                startActivity(new Intent(getApplicationContext(), OrdersActivity.class));
                break;
            case R.id.logout:
                finish();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else super.onBackPressed();
    }

}
