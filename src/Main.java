import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Main extends JFrame {
    // Data
    private List<Participant> participants = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();
    private double totalTripCost = 0;

    // UI Components
    private DefaultTableModel tableModel;
    private JTextArea summaryArea;
    private JLabel totalCostLabel;
    private JComboBox<Participant> payerCombo;
    private JList<Participant> sharerList;
    private DefaultListModel<Participant> listModel;
    
    // Colors - Modern Palette
    private final Color PRIMARY = new Color(33, 150, 243); // Blue
    private final Color ACCENT = new Color(76, 175, 80);  // Green
    private final Color DANGER = new Color(244, 67, 54);  // Red
    private final Color BG_DARK = new Color(33, 37, 41);
    private final Color CARD_BG = Color.WHITE;
    private final Color TEXT_MAIN = new Color(44, 62, 80);

    public Main() {
        initTheme();
        initUI();
    }

    private void initTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception ignored) {}
    }

    private void initUI() {
        setTitle("Elite Expense Manager - Pro Edition");
        setSize(1100, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 242, 245));
        setLayout(new BorderLayout(0, 0));

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(new EmptyBorder(0, 25, 0, 25));
        
        JLabel logo = new JLabel("ELITE EXPENSE");
        logo.setFont(new Font("Inter", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        header.add(logo, BorderLayout.WEST);

        totalCostLabel = new JLabel("Total Trip: ₹0.00");
        totalCostLabel.setFont(new Font("Inter", Font.BOLD, 18));
        totalCostLabel.setForeground(ACCENT);
        header.add(totalCostLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Container Split ---
        JPanel body = new JPanel(new BorderLayout(20, 20));
        body.setBorder(new EmptyBorder(20, 20, 20, 20));
        body.setOpaque(false);

        // --- Left Sidebar (Forms) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(350, 0));

        // Section 1: Members
        JPanel pCard = createCard("Manage Members");
        JTextField nameIn = createStyledField();
        JButton addPBtn = createStyledButton("Add Member", PRIMARY);
        pCard.add(new JLabel("Member Name:"));
        pCard.add(nameIn);
        pCard.add(Box.createRigidArea(new Dimension(0, 10)));
        pCard.add(addPBtn);

        // Section 2: Expense
        JPanel eCard = createCard("New Transaction");
        payerCombo = new JComboBox<>();
        listModel = new DefaultListModel<>();
        sharerList = new JList<>(listModel);
        sharerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane sharerScroll = new JScrollPane(sharerList);
        sharerScroll.setPreferredSize(new Dimension(0, 120));
        
        JTextField amtIn = createStyledField();
        JTextField descIn = createStyledField();
        JButton addEBtn = createStyledButton("Record Expense", ACCENT);

        eCard.add(new JLabel("Who Paid?"));
        eCard.add(payerCombo);
        eCard.add(Box.createRigidArea(new Dimension(0, 10)));
        eCard.add(new JLabel("Split Among (Ctrl+Click):"));
        eCard.add(sharerScroll);
        eCard.add(Box.createRigidArea(new Dimension(0, 10)));
        
        eCard.add(new JLabel("Expense Description:"));
        eCard.add(descIn);
        eCard.add(Box.createRigidArea(new Dimension(0, 10)));
        
        eCard.add(new JLabel("Amount (₹):"));
        eCard.add(amtIn);
        eCard.add(Box.createRigidArea(new Dimension(0, 15)));
        eCard.add(addEBtn);

        sidebar.add(pCard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(eCard);
        body.add(sidebar, BorderLayout.WEST);

        // --- Right Content (Table & Settlement) ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);

        // Table Card
        JPanel tableCard = createCard("Transaction History");
        tableCard.setLayout(new BorderLayout(0, 10));
        String[] cols = {"Payer", "Amount", "Description", "Date"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableActions.setOpaque(false);
        JButton clearBtn = createStyledButton("Reset Trip", Color.GRAY);
        JButton pdfBtn = createStyledButton("Generate PDF", DANGER);
        tableActions.add(clearBtn);
        tableActions.add(pdfBtn);
        tableCard.add(tableActions, BorderLayout.SOUTH);

        // Settlement Card
        JPanel setCard = createCard("Live Settlements");
        summaryArea = new JTextArea(10, 20);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        summaryArea.setBackground(new Color(250, 250, 250));
        setCard.add(new JScrollPane(summaryArea));

        mainContent.add(tableCard, BorderLayout.CENTER);
        mainContent.add(setCard, BorderLayout.SOUTH);
        body.add(mainContent, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        // --- Logic Handlers ---
        addPBtn.addActionListener(e -> {
            String name = nameIn.getText().trim();
            if (!name.isEmpty()) {
                Participant p = new Participant(name);
                if (!participants.contains(p)) {
                    participants.add(p);
                    listModel.addElement(p);
                    payerCombo.addItem(p);
                    nameIn.setText("");
                    updateUIState();
                } else {
                    JOptionPane.showMessageDialog(this, "Name already exists!");
                }
            }
        });

        addEBtn.addActionListener(e -> {
            try {
                Participant payer = (Participant) payerCombo.getSelectedItem();
                List<Participant> sharers = sharerList.getSelectedValuesList();
                double amt = Double.parseDouble(amtIn.getText());
                String desc = descIn.getText().trim();

                if (payer == null || sharers.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields!");
                    return;
                }

                Expense ex = new Expense(desc, amt, payer, sharers);
                expenses.add(ex);
                totalTripCost += amt;
                tableModel.addRow(new Object[]{payer.getName(), String.format("₹%.2f", amt), desc, ex.getTimestamp()});
                
                amtIn.setText("");
                descIn.setText("");
                updateUIState();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount format!");
            }
        });

        pdfBtn.addActionListener(e -> {
            if (expenses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data to export!");
                return;
            }
            List<String> sets = SplitEngine.getSettlements(participants, expenses);
            PDFExporter.exportReport(this, expenses, participants, sets, totalTripCost);
        });

        clearBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Clear all data?", "Confirm", JOptionPane.YES_NO_OPTION) == 0) {
                participants.clear();
                expenses.clear();
                totalTripCost = 0;
                tableModel.setRowCount(0);
                listModel.clear();
                payerCombo.removeAllItems();
                updateUIState();
            }
        });
    }

    private void updateUIState() {
        totalCostLabel.setText(String.format("Total Trip: ₹%.2f", totalTripCost));
        List<String> sets = SplitEngine.getSettlements(participants, expenses);
        
        StringBuilder sb = new StringBuilder();
        sb.append("FINAL SETTLEMENTS\n");
        sb.append("==============================\n");
        if (sets.isEmpty()) {
            sb.append("\nNo debts pending. All settled!");
        } else {
            for (String s : sets) sb.append(" • ").append(s).append("\n");
        }
        summaryArea.setText(sb.toString());
    }

    // --- Component Styling Helpers ---
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLvl = new JLabel(title.toUpperCase());
        titleLvl.setFont(new Font("Inter", Font.BOLD, 13));
        titleLvl.setForeground(new Color(150, 150, 150));
        titleLvl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(titleLvl);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        return card;
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 35));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setFont(new Font("Inter", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(0, 10, 0, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(0, 40));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Inter", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.setGridColor(new Color(245, 245, 245));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
