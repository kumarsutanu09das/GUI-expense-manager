package com.expense.model;

import java.util.*;

public class TripData {
    private String tripName;
    private List<Participant> participants = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();

    public TripData(String tripName) {
        this.tripName = tripName;
    }

    public String getTripName() {
        return tripName;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public Participant getOrCreateParticipant(String name) {
        for (Participant p : participants) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        Participant p = new Participant(name);
        participants.add(p);
        return p;
    }

    public void addExpense(Expense e) {
        expenses.add(e);
    }

    public void addPayment(Payment p) {
        payments.add(p);
    }

    public Map<Participant, Double> calculateBaseBalances() {
        Map<Participant, Double> balances = new HashMap<>();
        for (Participant p : participants) {
            balances.put(p, 0.0);
        }

        for (Expense e : expenses) {
            Participant payer = e.getPayer();
            Map<Participant, Double> shares = e.getShares();
            
            // Payer gets back (total - their_share)
            double currentPayerVal = balances.getOrDefault(payer, 0.0);
            balances.put(payer, currentPayerVal + (e.getAmount() - shares.get(payer)));
            
            // Others owe their share
            for (Map.Entry<Participant, Double> shareEntry : shares.entrySet()) {
                Participant p = shareEntry.getKey();
                if (!p.equals(payer)) {
                    double currentVal = balances.getOrDefault(p, 0.0);
                    balances.put(p, currentVal - shareEntry.getValue());
                }
            }
        }
        return balances;
    }

    public Map<String, Double> getNetMutualPayments() {
        Map<String, Double> netted = new HashMap<>();
        for (Payment p : payments) {
            String pairKey = p.getFrom().getName() + "->" + p.getTo().getName();
            String reverseKey = p.getTo().getName() + "->" + p.getFrom().getName();
            
            if (netted.containsKey(reverseKey)) {
                double prev = netted.get(reverseKey);
                if (prev > p.getAmount()) {
                    netted.put(reverseKey, prev - p.getAmount());
                } else {
                    netted.remove(reverseKey);
                    if (p.getAmount() > prev) {
                        netted.put(pairKey, p.getAmount() - prev);
                    }
                }
            } else {
                netted.put(pairKey, netted.getOrDefault(pairKey, 0.0) + p.getAmount());
            }
        }
        return netted;
    }
}
