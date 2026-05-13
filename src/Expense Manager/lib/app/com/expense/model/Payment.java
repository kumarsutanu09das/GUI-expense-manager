package com.expense.model;

public class Payment {
    private Participant from;
    private Participant to;
    private double amount;
    private String tripName;

    public Payment(Participant from, Participant to, double amount, String tripName) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.tripName = tripName;
    }

    public Participant getFrom() { return from; }
    public Participant getTo() { return to; }
    public double getAmount() { return amount; }
    public String getTripName() { return tripName; }
}
