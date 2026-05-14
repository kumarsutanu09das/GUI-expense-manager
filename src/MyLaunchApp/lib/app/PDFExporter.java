import javax.swing.*;
import java.awt.print.*;
import java.text.MessageFormat;
import java.util.List;

public class PDFExporter {
    public static void exportReport(JFrame parent, List<Expense> expenses, List<Participant> participants, List<String> settlements, double total) {
        JTextPane pane = new JTextPane();
        pane.setContentType("text/html");
        
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Sans-Serif; padding:20px;'>");
        html.append("<h1 style='color:#2F4F4F; text-align:center;'>Trip Expense Report</h1>");
        html.append("<hr>");
        html.append("<h3>Summary</h3>");
        html.append("<p><b>Total Expenditure:</b> ₹").append(String.format("%.2f", total)).append("</p>");
        html.append("<p><b>Total Participants:</b> ").append(participants.size()).append("</p>");
        
        html.append("<h3>Final Settlements</h3><ul>");
        for (String s : settlements) html.append("<li>").append(s).append("</li>");
        html.append("</ul><hr>");

        html.append("<h3>Detailed Logs</h3>");
        html.append("<table border='1' width='100%' style='border-collapse:collapse;'>");
        html.append("<tr style='background-color:#F5F5F5;'><th>Date</th><th>Payer</th><th>Amount</th><th>Description</th></tr>");
        for (Expense e : expenses) {
            html.append("<tr><td>").append(e.getTimestamp()).append("</td>")
                .append("<td>").append(e.getPayer().getName()).append("</td>")
                .append("<td>₹").append(String.format("%.2f", e.getAmount())).append("</td>")
                .append("<td>").append(e.getDescription()).append("</td></tr>");
        }
        html.append("</table></body></html>");
        
        pane.setText(html.toString());

        try {
            boolean done = pane.print(new MessageFormat("Trip Report"), new MessageFormat("Page {0}"), true, null, null, true);
            if (done) JOptionPane.showMessageDialog(parent, "Report generated successfully!");
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(parent, "Error printing: " + e.getMessage());
        }
    }
}
