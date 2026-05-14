# 💸 Expense Manager Pro

A sleek, portable, and efficient tool to manage, split, and export your expenses seamlessly. Expense Manager features a modern **Graphical User Interface (GUI)** to help you keep track of shared costs.

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
   ./src/MyLaunchApp/bin/MyLaunchApp
   ```

### 💻 Developer Mode (Run from Source)
Requires **Java 17+**.
```bash
# Compile
javac -d . src/*.java

# Run GUI
java Main
```

---

## ✨ Key Features

- **🎨 Intuitive GUI:** Built with Java Swing/AWT, featuring a clean layout for rapid expense entry.
- **⚖️ Smart Split Engine:** Uses a greedy debt-simplification algorithm to minimize the number of transactions required to settle up.
- **📄 PDF Export:** Generate professional HTML-templated PDF reports with detailed logs and settlement instructions.
- **🛡️ 100% Offline:** No cloud, no tracking. Your data stays on your machine.
- **🎯 Zero Setup:** Bundled JRE in the `MyLaunchApp` folder means no Java installation is required for the native launcher.

---

## 🖥️ Using the GUI

1. **Add Participants:** Enter names in the left panel to build your trip group.
2. **Record Expenses:** 
   - Select the **Payer**.
   - **Multi-select** who shared the expense (Ctrl+Click).
   - Enter description and amount.
3. **Live Dashboard:** The right panel updates instantly with a detailed log and the "Who owes Whom" summary.
4. **Export:** Click "Generate PDF" to save a formatted report.

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
