import com.expense.logic.SplitEngine;
import com.expense.model.Expense;
import com.expense.model.Participant;
import com.expense.util.PDFExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JFrame {

    private List<Participant> participants = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();
    private double totalTripCost = 0;

    private DefaultTableModel expenseTableModel;
    private JTable expenseTable;
    private JTextArea summaryArea;
    private JLabel totalCostLabel;
    private JComboBox<Participant> payerComboBox;
    private JList<Participant> selectionList;
    private DefaultListModel<Participant> listModel;

    public Main() {
        setupUI();
    }

    private void setupUI() {
        setTitle("Adventure Trip Expense Splitter Pro");
        setSize(1000, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Set Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 179, 113));
        JLabel titleLabel = new JLabel(" Trip Expense Manager ", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Area (Split Pane) ---
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(350);

        // --- Left Panel: Management ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Participant Management
        JPanel partPanel = createStyledSection("Add Participant");
        JTextField partField = new JTextField();
        JButton addPartBtn = createStyledButton("Add Person", new Color(70, 130, 180));
        partPanel.add(new JLabel("Name:"));
        partPanel.add(partField);
        partPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        partPanel.add(addPartBtn);

        // 2. Expense Management
        JPanel expPanel = createStyledSection("Record Expense");
        JTextField descField = new JTextField();
        JTextField amountField = new JTextField();
        payerComboBox = new JComboBox<>();
        
        // Multi-select list for participants sharing this expense
        listModel = new DefaultListModel<>();
        selectionList = new JList<>(listModel);
        selectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane listScroll = new JScrollPane(selectionList);
        listScroll.setPreferredSize(new Dimension(0, 100));

        JButton addExpBtn = createStyledButton("Add Expense", new Color(60, 179, 113));

        expPanel.add(new JLabel("Payer:"));
        expPanel.add(payerComboBox);
        expPanel.add(new JLabel("Who Shares this Expense (Ctrl+Click):"));
        expPanel.add(listScroll);
        expPanel.add(new JLabel("Description:"));
        expPanel.add(descField);
        expPanel.add(new JLabel("Amount (₹):"));
        expPanel.add(amountField);
        expPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        expPanel.add(addExpBtn);

        // 3. Stats Dashboard
        JPanel statsPanel = createStyledSection("Dashboard Summary");
        totalCostLabel = new JLabel("Total Trip Cost: ₹0.00");
        totalCostLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statsPanel.add(totalCostLabel);

        leftPanel.add(partPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(expPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(statsPanel);
        leftPanel.add(Box.createVerticalGlue());

        // --- Right Panel: View ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        // Table
        String[] columns = {"Payer", "Description", "Amount (₹)", "Timestamp"};
        expenseTableModel = new DefaultTableModel(columns, 0);
        expenseTable = new JTable(expenseTableModel);
        expenseTable.setRowHeight(30);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleTable();
        JScrollPane tableScroll = new JScrollPane(expenseTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Detailed Expense Log"));

        // Summary Area
        summaryArea = new JTextArea(10, 30);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        summaryArea.setBackground(new Color(245, 245, 245));
        JScrollPane summaryScroll = new JScrollPane(summaryArea);
        summaryScroll.setBorder(BorderFactory.createTitledBorder("Final Settlements (Who owes Whom)"));

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportBtn = createStyledButton("Export to PDF Report", new Color(220, 20, 60));
        JButton resetBtn = createStyledButton("Clear All", Color.GRAY);
        actionPanel.add(resetBtn);
        actionPanel.add(exportBtn);

        rightPanel.add(tableScroll, BorderLayout.CENTER);
        rightPanel.add(summaryScroll, BorderLayout.SOUTH);
        rightPanel.add(actionPanel, BorderLayout.NORTH);

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);
        add(mainSplitPane, BorderLayout.CENTER);

        // --- Event Handling ---
        addPartBtn.addActionListener(e -> {
            String name = partField.getText().trim();
            if (!name.isEmpty()) {
                Participant p = new Participant(name);
                if (!participants.contains(p)) {
                    participants.add(p);
                    payerComboBox.addItem(p);
                    listModel.addElement(p);
                    partField.setText("");
                    updateSummary();
                } else {
                    JOptionPane.showMessageDialog(this, "Participant already exists!");
                }
            }
        });

        addExpBtn.addActionListener(e -> {
            try {
                Participant payer = (Participant) payerComboBox.getSelectedItem();
                List<Participant> sharesWith = selectionList.getSelectedValuesList();
                String desc = descField.getText().trim();
                String amountStr = amountField.getText().trim();

                if (payer == null || sharesWith.isEmpty() || desc.isEmpty() || amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select payer, sharers, and enter details!");
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                Expense exp = new Expense(desc, amount, payer, sharesWith);
                expenses.add(exp);
                payer.addSpent(amount);
                totalTripCost += amount;

                expenseTableModel.addRow(new Object[]{
                        payer.getName(), desc, String.format("%.2f", amount), exp.getTimestamp()
                });

                descField.setText("");
                amountField.setText("");
                selectionList.clearSelection();
                updateSummary();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        exportBtn.addActionListener(e -> exportPDF());

        resetBtn.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all data?", "Reset", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                participants.clear();
                expenses.clear();
                totalTripCost = 0;
                expenseTableModel.setRowCount(0);
                payerComboBox.removeAllItems();
                listModel.clear();
                updateSummary();
            }
        });
    }

    private void updateSummary() {
        totalCostLabel.setText(String.format("Total Trip Cost: ₹%.2f", totalTripCost));
        if (participants.isEmpty()) {
            summaryArea.setText("No participants added.");
            return;
        }

        // --- Per-Expense Netting Logic ---
        Map<Participant, Double> netBalances = new HashMap<>();
        for (Participant p : participants) netBalances.put(p, 0.0);

        for (Expense e : expenses) {
            Participant payer = e.getPayer();
            List<Participant> sharers = e.getParticipants();
            double shareAmount = e.getAmount() / sharers.size();

            netBalances.put(payer, netBalances.get(payer) + e.getAmount());
            for (Participant s : sharers) {
                netBalances.put(s, netBalances.get(s) - shareAmount);
            }
        }

        List<String> settlements = SplitEngine.getSettlementList(netBalances);

        StringBuilder sb = new StringBuilder();
        sb.append("ADVANCED DEBT SIMPLIFICATION\n");
        sb.append("===========================\n");
        sb.append(String.format("Total Trip Cost: ₹%.2f\n", totalTripCost));
        sb.append("---------------------------\n\n");
        
        sb.append("NET STATUS PER PERSON:\n");
        for (Participant p : participants) {
            double bal = netBalances.get(p);
            String type = bal >= 0.01 ? "(Creditor - is owed)" : (bal < -0.01 ? "(Debtor - owes)" : "(Settled)");
            sb.append(String.format("%-12s: ₹%10.2f %s\n", p.getName(), Math.abs(bal), type));
        }

        sb.append("\nSIMPLIFIED FINAL SETTLEMENTS:\n");
        if (settlements.isEmpty()) {
            sb.append("✅ All debts are settled!");
        } else {
            for (String s : settlements) {
                sb.append(" 👉 ").append(s).append("\n");
            }
        }
        summaryArea.setText(sb.toString());
    }

    private void exportPDF() {
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No expenses to export!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Location to Save PDF Report");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
                file = new java.io.File(path);
            }

            // Calculate current net balances based on per-expense shares
            Map<Participant, Double> netBalances = new HashMap<>();
            for (Participant p : participants) netBalances.put(p, 0.0);
            for (Expense e : expenses) {
                double shareAmount = e.getAmount() / e.getParticipants().size();
                netBalances.put(e.getPayer(), netBalances.get(e.getPayer()) + e.getAmount());
                for (Participant s : e.getParticipants()) {
                    netBalances.put(s, netBalances.get(s) - shareAmount);
                }
            }
            List<String> settlements = SplitEngine.getSettlementList(netBalances);
            
            // Pass the selected file to the exporter
            PDFExporter.exportReportToFile(this, file, expenses, participants, settlements, totalTripCost);
        }
    }

    private JPanel createStyledSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                title, 0, 0, new Font("Segoe UI", Font.BOLD, 14)));
        panel.setMaximumSize(new Dimension(400, 350));
        return panel;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        expenseTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        expenseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        expenseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        expenseTable.getTableHeader().setBackground(new Color(240, 240, 240));
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--cli")) {
            System.out.println("ExpenseSplitter CLI - Mode Active");
            System.out.println("Type commands starting with 'expense' (e.g., 'expense add...')");
            System.out.println("Type 'exit' to quit.");
            
            com.expense.logic.CLIController cli = new com.expense.logic.CLIController();
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) break;
                if (input.startsWith("expense ")) {
                    cli.processCommand(input);
                } else {
                    System.out.println("Invalid command. Start with 'expense'.");
                }
            }
            scanner.close();
        } else {
            SwingUtilities.invokeLater(() -> new Main().setVisible(true));
        }
    }
}
