package com.example.a3;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a3.adapters.FlightAdapter;
import com.example.a3.model.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawerLayout;
    private static final String TAG = "SearchActivity";
    private static final String TICKETS = "tickets";
    private List<Ticket> listOfTickets = new ArrayList<>();
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initListeners();

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.menu_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(TICKETS);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Ticket ticket = ds.getValue(Ticket.class);
                    Log.d(TAG, "ticket stuff:" + Objects.requireNonNull(ticket).getId() + " " + ticket.getFrom() + ticket.getTo() + ticket.getDate());
                    addToTheList(ticket);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //or at least an alert
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void initListeners(){
        findViewById(R.id.selectDate).setOnClickListener(this);
        findViewById(R.id.search).setOnClickListener(this);
        findViewById(R.id.fab).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectDate:
                makeCalendar();
                break;
            case R.id.search:
                search();
                break;
            case R.id.fab:
                finish();
                startActivity(new Intent(context, CartActivity.class));
                break;
        }
    }

    private void makeCalendar(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        Log.d(TAG, "calendar things: " + day + " " + month + " " + year);

        DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this, (view, myear, mmonth, dayOfMonth) -> {
            mmonth+=1;
            String s = myear + "/" + mmonth + "/" + dayOfMonth;
            TextView textView = findViewById(R.id.date);
            textView.setText(s);
        }, day, month, year);

        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    private void search(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final FlightAdapter adapter = new FlightAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG, "search button pushed");
        EditText where = findViewById(R.id.where);
        EditText from = findViewById(R.id.from);
        TextView date = findViewById(R.id.date);
        String[] dateString = date.getText().toString().split("/");
        List<Ticket> foundTickets = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        CheckBox checkBox = findViewById(R.id.b_f);
        for (Ticket t: listOfTickets){
            calendar.setTimeInMillis(t.getDate().getTime());
            if (t.getTo().equals(where.getText().toString()) &&
                    t.getFrom().equals(from.getText().toString()) &&
                    calendar.get(Calendar.YEAR) == Integer.parseInt(dateString[0]) &&
                    calendar.get(Calendar.MONTH) == Integer.parseInt(dateString[1]) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(dateString[2])) {
                foundTickets.add(t);
                Log.d(TAG, "found");
            }

            if (checkBox.isChecked() &&
                    t.getTo().equals(from.getText().toString()) &&
                    t.getFrom().equals(where.getText().toString())) {
                foundTickets.add(t);
                Log.d(TAG, "found");
            }
        }

        adapter.setTickets(foundTickets);
    }

    private void addToTheList(Ticket ticket) {
        listOfTickets.add(ticket);
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
