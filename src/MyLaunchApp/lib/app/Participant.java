import java.util.Objects;

public class Participant {
    private String name;
    private double balance; // Net balance: positive means creditor, negative means debtor

    public Participant(String name) {
        this.name = name;
        this.balance = 0.0;
    }

    public String getName() { return name; }
    public double getBalance() { return balance; }
    
    public void updateBalance(double amount) { this.balance += amount; }
    public void resetBalance() { this.balance = 0.0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(name.toLowerCase(), that.name.toLowerCase());
    }

    @Override
    public int hashCode() { return Objects.hash(name.toLowerCase()); }

    @Override
    public String toString() { return name; }
}
