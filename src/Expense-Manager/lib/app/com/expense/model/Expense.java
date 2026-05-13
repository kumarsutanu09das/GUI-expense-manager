package com.expense.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Expense {
    private String description;
    private double amount;
    private Participant payer;
    private String timestamp;
    private List<Participant> participants;
    private Map<Participant, Double> shares;
    private String tripName;

    public Expense(String description, double amount, Participant payer, List<Participant> participants) {
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.participants = participants;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public Expense(String description, double amount, Participant payer) {
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public Expense(String description, double amount, Participant payer, List<Participant> participants, Map<Participant, Double> shares, String tripName) {
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.participants = participants;
        this.shares = shares;
        this.tripName = tripName;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public Participant getPayer() { return payer; }
    public String getTimestamp() { return timestamp; }
    public List<Participant> getParticipants() { return participants; }
    public Map<Participant, Double> getShares() { return shares; }
    public String getTripName() { return tripName; }

    @Override
    public String toString() {
        return String.format("[%s] %s paid ₹%.2f for %s", tripName, payer.getName(), amount, description);
    }
}
