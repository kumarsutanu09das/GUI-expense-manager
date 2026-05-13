package com.expense.util;

import com.expense.model.Expense;
import com.expense.model.Participant;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.swing.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;

public class PDFExporter {

    /**
     * Uses Java's built-in Printing API to save the report to the selected file.
     * This opens a print dialog where the user can choose "Print to PDF" or their printer.
     */
    public static void exportReportToFile(JFrame parent, File targetFile, List<Expense> expenses, List<Participant> participants, List<String> settlements, double totalCost) {
        
        // Create formatted HTML content for the report
        JTextPane reportPane = new JTextPane();
        reportPane.setContentType("text/html");
        reportPane.setEditable(false);

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: sans-serif;'>");
        html.append("<h1 style='text-align: center; color: #3CB371;'>Trip Expense Summary Report</h1>");
        
        html.append("<h3>Overall Summary</h3>");
        html.append("<p><b>Total Trip Cost:</b> ₹").append(String.format("%.2f", totalCost)).append("</p>");
        html.append("<p><b>Participants:</b> ").append(participants.size()).append("</p>");

        html.append("<h3>Detailed Expenses</h3>");
        html.append("<table border='1' style='width: 100%; border-collapse: collapse;'>");
        html.append("<tr style='background-color: #3CB371; color: white;'><th>Payer</th><th>Description</th><th>Amount (₹)</th><th>Date</th></tr>");
        
        for (Expense e : expenses) {
            html.append("<tr>");
            html.append("<td style='padding: 5px;'>").append(e.getPayer().getName()).append("</td>");
            html.append("<td style='padding: 5px;'>").append(e.getDescription()).append("</td>");
            html.append("<td style='padding: 5px; text-align: center;'>").append(String.format("%.2f", e.getAmount())).append("</td>");
            html.append("<td style='padding: 5px; text-align: center;'>").append(e.getTimestamp()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");

        html.append("<h3>Individual Spending</h3>");
        html.append("<ul>");
        for (Participant p : participants) {
            html.append("<li>").append(p.getName()).append(": ₹").append(String.format("%.2f", p.getTotalSpent())).append("</li>");
        }
        html.append("</ul>");

        html.append("<h3>Simplified Final Settlements</h3>");
        html.append("<div style='background-color: #f0f0f0; padding: 10px; border-radius: 5px;'>");
        if (settlements.isEmpty()) {
            html.append("<p>All debts are settled!</p>");
        } else {
            for (String s : settlements) {
                html.append("<p>• ").append(s).append("</p>");
            }
        }
        html.append("</div>");
        html.append("</body></html>");
        
        reportPane.setText(html.toString());

        try {
            MessageFormat header = new MessageFormat("Trip Expense Report");
            MessageFormat footer = new MessageFormat("Page {0}");
            
            // Set up print attributes to point to the file destination
            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new Destination(targetFile.toURI()));
            
            // Show the print dialog to give the user final control over the printer/settings
            // Setting the Destination attribute above pre-populates the file save location in many OSs
            boolean complete = reportPane.print(header, footer, true, null, attr, true);
            
            if (complete) {
                JOptionPane.showMessageDialog(parent, "Report Generated Successfully!\nSaved to: " + targetFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(parent, "Printing failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
