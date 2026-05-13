package com.expense.logic;

import com.expense.model.Participant;
import java.util.*;

public class SplitEngine {

    /**
     * Implements the Debt Simplification (Netting) logic.
     * 1. Uses the pre-calculated Net Balances: (Total Paid) - (Equal Share).
     * 2. Uses a Greedy Algorithm to settle debtors against creditors.
     * 3. This naturally nets all mutual debts into the simplest possible transactions.
     */
    public static List<String> getSettlementList(Map<Participant, Double> netBalances) {
        List<Participant> debtors = new ArrayList<>();
        List<Participant> creditors = new ArrayList<>();

        // Separate participants into Debtors (negative balance) and Creditors (positive balance)
        for (Map.Entry<Participant, Double> entry : netBalances.entrySet()) {
            double balance = entry.getValue();
            if (balance < -0.01) {
                debtors.add(entry.getKey());
            } else if (balance > 0.01) {
                creditors.add(entry.getKey());
            }
        }

        // Sort to optimize the greedy settlement
        debtors.sort(Comparator.comparingDouble(p -> netBalances.get(p)));
        creditors.sort(Comparator.comparingDouble(p -> -netBalances.get(p)));

        List<String> settlements = new ArrayList<>();
        int d = 0, c = 0;
        
        // Use a mutable copy of balances for the greedy loop
        Map<Participant, Double> tempBalances = new HashMap<>(netBalances);

        while (d < debtors.size() && c < creditors.size()) {
            Participant debtor = debtors.get(d);
            Participant creditor = creditors.get(c);

            double debtAmount = Math.abs(tempBalances.get(debtor));
            double creditAmount = tempBalances.get(creditor);
            
            double settledAmount = Math.min(debtAmount, creditAmount);

            if (settledAmount > 0.01) {
                settlements.add(String.format("%s owes %s: ₹%.2f", debtor.getName(), creditor.getName(), settledAmount));
            }

            // Update balances
            tempBalances.put(debtor, tempBalances.get(debtor) + settledAmount);
            tempBalances.put(creditor, tempBalances.get(creditor) - settledAmount);

            // Move to next debtor/creditor if their balance is settled
            if (Math.abs(tempBalances.get(debtor)) < 0.01) d++;
            if (Math.abs(tempBalances.get(creditor)) < 0.01) c++;
        }

        return settlements;
    }

    /**
     * Helper to calculate shares for custom ratios (used by CLI)
     */
    public static Map<Participant, Double> calculateCustomShares(double totalAmount, List<Participant> participants, String ratioStr) {
        Map<Participant, Double> shares = new HashMap<>();
        String[] parts = ratioStr.split(":");
        if (parts.length != participants.size()) {
            double equalShare = totalAmount / participants.size();
            for (Participant p : participants) shares.put(p, equalShare);
            return shares;
        }

        double totalRatio = 0;
        double[] ratios = new double[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                ratios[i] = Double.parseDouble(parts[i]);
                totalRatio += ratios[i];
            }
            for (int i = 0; i < participants.size(); i++) {
                shares.put(participants.get(i), (ratios[i] / totalRatio) * totalAmount);
            }
        } catch (NumberFormatException e) {
            double equalShare = totalAmount / participants.size();
            for (Participant p : participants) shares.put(p, equalShare);
        }
        return shares;
    }
}
