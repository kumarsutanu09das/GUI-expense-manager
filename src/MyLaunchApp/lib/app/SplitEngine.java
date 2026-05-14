import java.util.*;

public class SplitEngine {
    public static List<String> getSettlements(List<Participant> participants, List<Expense> expenses) {
        if (participants.isEmpty()) return new ArrayList<>();

        // Reset and calculate net balances
        Map<Participant, Double> netBalances = new HashMap<>();
        for (Participant p : participants) netBalances.put(p, 0.0);

        for (Expense e : expenses) {
            double share = e.getAmount() / e.getSharers().size();
            // Payer gets credit for total amount
            netBalances.put(e.getPayer(), netBalances.get(e.getPayer()) + e.getAmount());
            // Every sharer (including payer if they are in the list) owes their share
            for (Participant s : e.getSharers()) {
                netBalances.put(s, netBalances.get(s) - share);
            }
        }

        List<Participant> debtors = new ArrayList<>();
        List<Participant> creditors = new ArrayList<>();

        for (Participant p : participants) {
            double bal = netBalances.get(p);
            if (bal < -0.01) debtors.add(p);
            else if (bal > 0.01) creditors.add(p);
        }

        debtors.sort(Comparator.comparingDouble(p -> netBalances.get(p)));
        creditors.sort(Comparator.comparingDouble(p -> -netBalances.get(p)));

        List<String> results = new ArrayList<>();
        int d = 0, c = 0;
        while (d < debtors.size() && c < creditors.size()) {
            Participant debtor = debtors.get(d);
            Participant creditor = creditors.get(c);
            
            double amount = Math.min(-netBalances.get(debtor), netBalances.get(creditor));
            results.add(String.format("%s → %s : ₹%.2f", debtor.getName(), creditor.getName(), amount));

            netBalances.put(debtor, netBalances.get(debtor) + amount);
            netBalances.put(creditor, netBalances.get(creditor) - amount);

            if (Math.abs(netBalances.get(debtor)) < 0.01) d++;
            if (Math.abs(netBalances.get(creditor)) < 0.01) c++;
        }
        return results;
    }
}
