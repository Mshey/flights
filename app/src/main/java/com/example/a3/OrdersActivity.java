package com.example.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class OrdersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        NavigationView navigationView = findViewById(R.id.menu_view);
        navigationView.setNavigationItemSelectedListener(this);
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
