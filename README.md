# 💸 Expense Manager Pro

A sleek, portable, and efficient tool to manage, split, and export your expenses seamlessly. Whether you prefer a modern **Graphical User Interface (GUI)** or a powerful **Command Line Interface (CLI)**, Expense Manager has you covered.

---

## 🚀 Quick Start

### 🐧 Linux (Native)
1. **Clone the repo:** 
   ```bash
   git clone https://github.com/kumarsutanu09das/GUI-expense-manager.git
   cd GUI-expense-manager
   ```
2. **Run the app:** 
   ```bash
   ./release/ExpenseManager/bin/ExpenseManager
   ```

### 🪟 Windows
1. **Launch:** Double-click `expense-manager.exe` in the release folder.

### 💻 Developer Mode (Run from Source)
Requires **Java 21+**.
```bash
# Compile
javac -d out/production/expense-manager -cp src src/Main.java

# Run GUI
java -cp out/production/expense-manager Main

# Run CLI
java -cp out/production/expense-manager Main --cli
```

---

## ✨ Key Features

- **🎨 Intuitive GUI:** Built with Java Swing/AWT, featuring a clean layout for rapid expense entry.
- **💻 Power CLI:** Full-featured command-line interface for automation and advanced users.
- **⚖️ Smart Split Engine:** Uses a greedy debt-simplification algorithm to minimize the number of transactions required to settle up.
- **📄 PDF Export:** Generate professional HTML-templated PDF reports with detailed logs and settlement instructions.
- **🛡️ 100% Offline:** No cloud, no tracking. Your data stays on your machine.
- **🎯 Zero Setup:** Bundled JRE in releases means no Java installation is required for end-users.

---

## 🖥️ Using the GUI

1. **Add Participants:** Enter names in the left panel to build your trip group.
2. **Record Expenses:** 
   - Select the **Payer**.
   - **Multi-select** who shared the expense (Ctrl+Click).
   - Enter description and amount.
3. **Live Dashboard:** The right panel updates instantly with a detailed log and the "Who owes Whom" summary.
4. **Export:** Click "Export to PDF" to save a formatted report.

---

## ⌨️ Using the CLI

Start the application with the `--cli` flag. Commands follow the `expense <action>` syntax.

### Common Commands:

| Command | Description | Example |
| :--- | :--- | :--- |
| `add` | Add a new expense | `expense add 1200 --payer Alice --participants Alice,Bob,Charlie --trip "Goa" --split equal` |
| `record-payment` | Record a direct payment | `expense record-payment Bob 400 --to Alice --trip "Goa"` |
| `settle` | View settlement plan | `expense settle trip "Goa"` |
| `list` | Show all transactions | `expense list trip "Goa"` |
| `report` | Generate trip report | `expense report trip "Goa"` |
| `clear` | Wipe trip data | `expense clear trip "Goa"` |

> **Note:** The CLI supports custom split ratios using `--split 1:2:1`.

---

## 🛠️ Technical Deep Dive

### Debt Simplification Algorithm
The core `SplitEngine` implements a greedy netting logic:
1. Calculates the **Net Balance** for each participant (Total Spent - Total Share).
2. Separates participants into **Debtors** and **Creditors**.
3. Successively matches the largest debtor with the largest creditor until all balances are near zero.
4. This results in the **mathematically minimum** number of transactions.

### PDF Generation
The `PDFExporter` utilizes Java's `Printable` API combined with an HTML template engine. This allows for rich styling (CSS) in the generated reports while remaining lightweight and dependency-free.

---

## 🤝 Contributing

1. Fork the project.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

*Developed with ❤️ by [kumarsutanu09das](https://github.com/kumarsutanu09das)*
