package com.example.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();
        //initDatabase();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            //restaurantActivity

            finish();
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
        }

    }

    /**
     * This method is to initialize views
     */
    private void initViews() {

        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = findViewById(R.id.textViewLinkRegister);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    /**
     * This implemented method is to listen the click on view
     *
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                signInWithFirebase();
                break;
            case R.id.textViewLinkRegister:
                // Navigate to RegisterActivity
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void signInWithFirebase() {
        firebaseAuth.signInWithEmailAndPassword(Objects.requireNonNull(textInputEditTextEmail.getText()).toString().trim(), Objects.requireNonNull(textInputEditTextPassword.getText()).toString().trim())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    }
                });

    }

//    private void initDatabase() {
//        Ticket ticket1 = new Ticket("1", "CLJ", "MDK", new Date());
//        Ticket ticket2 = new Ticket("2", "MDK", "CLJ", new Date());
//        Ticket ticket3 = new Ticket("3", "UK", "USA", new Date());
//        Ticket ticket4 = new Ticket("4", "USA", "Canada", new Date());
//
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("tickets")
//                .child(ticket1.getId())
//                .setValue(ticket1);
//        mDatabase.child("tickets")
//                .child(ticket2.getId())
//                .setValue(ticket2);
//        mDatabase.child("tickets")
//                .child(ticket3.getId())
//                .setValue(ticket3);
//        mDatabase.child("tickets")
//                .child(ticket4.getId())
//                .setValue(ticket4);
//    }
}
