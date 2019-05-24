package com.example.a3.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class Ticket {

    private String id;
    private String from;
    private String to;
    private Date date;

    private static final String USERS = "users";
    private static final String CART = "cart";
    private static final String ORDERS = "orders";
    private static final String TICKETS = "tickets";

    public Ticket(String id, String from, String to, Date date) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.date = date;
    }

    public Ticket(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void addTicket() {
       // DatabaseReference m
    }

    public void addUser(String name) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(USERS).child(name).setValue("new user");
    }

    public void addTicketToCart(Ticket ticket) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(USERS)
                .child("user1")
                .child(CART)
                .child(ticket.getId())
                .setValue(true);
    }
}
