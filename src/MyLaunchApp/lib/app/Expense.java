import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Expense {
    private String description;
    private double amount;
    private Participant payer;
    private List<Participant> sharers;
    private String timestamp;

    public Expense(String description, double amount, Participant payer, List<Participant> sharers) {
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.sharers = sharers;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
    }

    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public Participant getPayer() { return payer; }
    public List<Participant> getSharers() { return sharers; }
    public String getTimestamp() { return timestamp; }
}
