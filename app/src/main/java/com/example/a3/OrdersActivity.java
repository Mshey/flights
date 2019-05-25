package com.example.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.example.a3.adapters.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrdersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "OrdersActivity";
    private static final String USERS = "users";
    private static final String ORDERS = "orders";

    private List<String> orderIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        orderIds = new ArrayList<>();

        NavigationView navigationView = findViewById(R.id.menu_view);
        navigationView.setNavigationItemSelectedListener(this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(ORDERS);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final OrderAdapter adapter = new OrderAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyCallbackString myCallbackString = adapter::addToOrderIds;
                Log.d(TAG, "onDataChange");
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    //addToOrderId(ds.getKey());
                    myCallbackString.onCallback(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //adapter.setOrderIds(orderIds);
    }

    private void addToOrderId(String s) {
        Log.d(TAG, "added to orderIds");
        orderIds.add(s);
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

        return true;
    }
}
