package com.expense.logic;

import com.expense.model.Expense;
import com.expense.model.Participant;
import com.expense.model.Payment;
import com.expense.model.TripData;
import java.util.*;

public class CLIController {

    private Map<String, TripData> trips = new HashMap<>();

    public void processCommand(String input) {
        String[] tokens = input.trim().split("\\s+");
        if (tokens.length < 2) return;
        String command = tokens[1].toLowerCase();
        switch (command) {
            case "add":
                handleAdd(tokens);
                break;
            case "record-payment":
                handleRecordPayment(tokens);
                break;
            case "list":
                handleList(tokens);
                break;
            case "settle":
                handleSettle(tokens);
                break;
            case "report":
                handleReport(tokens);
                break;
            case "clear":
                handleClear(tokens);
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }

    private void handleAdd(String[] tokens) {
        try {
            double amount = Double.parseDouble(tokens[2]);
            String payerName = extractValue(tokens, "--payer");
            String participantsStr = extractValue(tokens, "--participants");
            String tripName = extractValue(tokens, "--trip");
            String splitType = extractValue(tokens, "--split");

            TripData trip = getOrCreateTrip(tripName);
            Participant payer = trip.getOrCreateParticipant(payerName);
            List<Participant> splitAmong = new ArrayList<>();
            for (String name : participantsStr.split(",")) {
                splitAmong.add(trip.getOrCreateParticipant(name.trim()));
            }

            Map<Participant, Double> shares = splitType.equalsIgnoreCase("equal") ? 
                calculateEqualShares(amount, splitAmong) : SplitEngine.calculateCustomShares(amount, splitAmong, splitType);

            trip.addExpense(new Expense("Expense", amount, payer, splitAmong, shares, tripName));
            System.out.println("✅ ADDED: ₹" + String.format("%.0f", amount) + " paid by " + payerName);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleRecordPayment(String[] tokens) {
        try {
            String fromName = tokens[2].replace("\"", "");
            double amount = Double.parseDouble(tokens[3]);
            String toName = extractValue(tokens, "--to");
            String tripName = extractValue(tokens, "--trip");
            TripData trip = trips.get(tripName);
            if (trip == null) {
                System.out.println("Error: Trip not found.");
                return;
            }
            trip.addPayment(new Payment(trip.getOrCreateParticipant(fromName), trip.getOrCreateParticipant(toName), amount, tripName));
            System.out.println("✅ RECORDED: ₹" + amount + " from " + fromName + " to " + toName);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleSettle(String[] tokens) {
        String tripName = extractValue(tokens, "trip");
        TripData trip = trips.get(tripName);
        if (trip == null) {
            System.out.println("Error: Trip not found.");
            return;
        }

        double total = trip.getExpenses().stream().mapToDouble(Expense::getAmount).sum();
        double share = total / trip.getParticipants().size();

        System.out.println("\n--- SMART SETTLEMENT PLAN: " + tripName + " ---");
        System.out.println("STEP 1: Calculate individual shares (₹" + String.format("%.2f", share) + " each)");
        
        System.out.println("\nSTEP 2: Calculate net amounts (Paid vs Owed):");
        Map<Participant, Double> baseBalances = trip.calculateBaseBalances();
        for (Participant p : trip.getParticipants()) {
            double bal = baseBalances.getOrDefault(p, 0.0);
            String status = bal >= 0 ? "OWED ₹" + String.format("%.2f", bal) : "OWES ₹" + String.format("%.2f", Math.abs(bal));
            System.out.printf(" - %-10s: %s\n", p.getName(), status);
        }

        System.out.println("\nSTEP 3: Adjust for mutual payments:");
        Map<String, Double> mutual = trip.getNetMutualPayments();
        if (mutual.isEmpty()) {
            System.out.println(" - No mutual payments recorded.");
        } else {
            for (Map.Entry<String, Double> entry : mutual.entrySet()) {
                System.out.println(" - NET Adjustment: " + entry.getKey().replace("->", " pays ") + " ₹" + String.format("%.2f", entry.getValue()));
            }
        }

        // Final Calculation
        Map<Participant, Double> finalBalances = new HashMap<>(baseBalances);
        for (Payment p : trip.getPayments()) {
            finalBalances.put(p.getFrom(), finalBalances.getOrDefault(p.getFrom(), 0.0) + p.getAmount());
            finalBalances.put(p.getTo(), finalBalances.getOrDefault(p.getTo(), 0.0) - p.getAmount());
        }

        System.out.println("\nFINAL SETTLEMENT INSTRUCTIONS:");
        List<String> settlements = SplitEngine.getSettlementList(finalBalances);
        if (settlements.isEmpty()) {
            System.out.println(" ✅ Everything is settled!");
        } else {
            for (String s : settlements) {
                System.out.println(" 👉 " + s.replace("$", "₹").replace("owes", "pays"));
            }
        }
    }

    private void handleList(String[] tokens) {
        String tripName = extractValue(tokens, "trip");
        TripData trip = trips.get(tripName);
        if (trip == null) return;
        System.out.println("\n--- TRANSACTION LOG: " + tripName + " ---");
        trip.getExpenses().forEach(e -> System.out.println(" [EXPENSE] " + e.getPayer().getName() + " paid ₹" + e.getAmount()));
    }

    private void handleReport(String[] tokens) {
        handleSettle(tokens);
    }

    private void handleClear(String[] tokens) {
        trips.remove(extractValue(tokens, "trip"));
        System.out.println("Cleared.");
    }

    private Map<Participant, Double> calculateEqualShares(double amount, List<Participant> participants) {
        Map<Participant, Double> shares = new HashMap<>();
        double p = amount / participants.size();
        for (Participant pt : participants) {
            shares.put(pt, p);
        }
        return shares;
    }

    private String extractValue(String[] tokens, String key) {
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase(key) && i + 1 < tokens.length) {
                return tokens[i+1].replace("\"", "");
            }
        }
        return "";
    }

    private TripData getOrCreateTrip(String name) {
        return trips.computeIfAbsent(name, k -> new TripData(name));
    }
}
