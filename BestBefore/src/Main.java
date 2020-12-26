import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This is the main class which creates the user interface and depending on the
 * users actions, calls the needed methods from the Report class.
 *
 * The GUI user interface consists of 7 buttons:
 *
 * Load The Receiving Log: Loads the initial receiving log into the system.
 * Populates the database with the contents inside the receiving log's .txt
 * file. Each product has a UPC number, Product name, Quantity, and Expiry date.
 *
 * Generate Expiry Report: Generates an expiry report containing all of the
 * products that will be expiring within a given time frame (days/integers) from
 * the current date, which is specified by the user. It Generates a .txt file
 * called "expiryReport" that contains the UPC number, Product name, Quantity,
 * and Expiry date for every product that will expire within that specific time
 * frame.
 *
 * Generate Expired Report: Generates an expiry report containing all of the
 * products that have already expired prior to the current date. It Generates a
 * .txt file called "expiredReport" that contains the UPC number, Product name,
 * Quantity, and Expiry date for every product that has already expired prior to
 * the current date.
 *
 * Product Search by UPC: Searches the entire database for a product that has a
 * specific UPC number. If a product is found it displays the information about
 * the product including the UPC number, Product name, Quantity, and Expiry
 * date. If no product is found, an error message will show, displaying a
 * product with that UPC number could not be found.
 *
 * Load Today's Sales: Loads a simulated SalesReport .txt file to simulate the
 * process of items being sold by decreasing their quantities.
 *
 * Generate Order Report: Generates a simulated order report containing all of
 * the products that have been sold and now have a quantity of 0. This is to
 * emphasize how the database will be maintained and constantly restocked, by
 * re-purchasing all the items that have a quantity of 0.
 *
 * Quit: Closes the program.
 *
 * @authors Stefan Stojsic,Emmanuel Ojo, Osama Hameed
 */
public class Main {

    static Calendar todayDate = Calendar.getInstance();
    static String currentDate = todayDate.get(Calendar.DATE) + "-" + (todayDate.get(Calendar.MONTH) + 1) + "-"
            + todayDate.get(Calendar.YEAR);

    static List<Product> productList = new ArrayList<Product>();
    static List<Product> expiryList = new ArrayList<Product>();
    static List<Product> expiredList = new ArrayList<Product>();
    static List<Product> toOrderList = new ArrayList<Product>();

    static File expiryReport = new File("expiryReport" + currentDate + ".txt");
    static File expiredReport = new File("expiredReport" + currentDate + ".txt");
    static File toOrder = new File("OrderList" + currentDate + ".txt");

    static PrintStream writer;

    public static void main(String[] args) {

        // Creation of the JFrame GUI window of size 500x400
        JFrame f = new JFrame("BestBefore");
        f.setSize(500, 400);
        f.setLocation(500, 200);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create all the buttons in the user inerface
        final JButton expiryButton = new JButton("Generate Expiry Report");
        final JButton expiredButton = new JButton("Generate Expired Report");
        final JButton searchButton = new JButton("Product Search by UPC");
        final JButton quitButton = new JButton("Quit");
        final JButton salesButton = new JButton("Load Today's Sales");
        final JButton toOrderButton = new JButton("Generate Order Report");
        final JButton receivingButton = new JButton("Load The Receiving Log");

        JPanel buttonPanel = new JPanel(new GridLayout(7, 1));
        f.setContentPane(buttonPanel);

        f.getContentPane().add(BorderLayout.LINE_START, receivingButton);
        f.getContentPane().add(BorderLayout.SOUTH, expiryButton);
        f.getContentPane().add(BorderLayout.SOUTH, expiredButton);
        f.getContentPane().add(BorderLayout.SOUTH, searchButton);
        f.getContentPane().add(BorderLayout.SOUTH, salesButton);
        f.getContentPane().add(BorderLayout.SOUTH, toOrderButton);
        f.getContentPane().add(BorderLayout.SOUTH, quitButton);

        // Generate Expiry Report Button Method
        expiryButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                expiryList.clear();

                JFrame newWindow = new JFrame("Expiry Report");
                final JButton saveButton = new JButton("Save");

                createReportScreen(newWindow, saveButton);

                // user enters a specific expiry date range, from the current date in days
                String ExpR = JOptionPane.showInputDialog("Check expiry dates for period of");
                int ExpiryRange = Integer.parseInt(ExpR);

                Report.generateExpiryList(ExpiryRange);

                // save report button action
                saveButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Report.saveExpiryList(ExpiryRange);
                        JOptionPane.showMessageDialog(null, "File Saved");
                    }
                });

                if (expiryList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No products are expiring in the time range given");
                } else {
                    newWindow.setVisible(true);
                }
            }
        });

        // Generate Expired Report Button Method
        expiredButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                expiredList.clear();

                JFrame newWindow = new JFrame("Expired Report");
                final JButton saveButton = new JButton("Save");

                createReportScreen(newWindow, saveButton);

                Report.generateExpiredList();

                saveButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Report.saveExpiredList();
                        JOptionPane.showMessageDialog(null, "File Saved");
                    }
                });

                if (expiredList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "None Expired");
                } else {
                    newWindow.setVisible(true);

                    // resets the quantitites for all the products that expired to zero
                    Report.updateQuantities();
                }
            }
        });

        // Product Search by UPC Button Method
        searchButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame newWindow = new JFrame("Search inquiry");
                newWindow.setSize(500, 400);
                newWindow.setLocation(500, 200);

                final JTextArea textArea = new JTextArea(10, 40);
                newWindow.getContentPane().add(BorderLayout.CENTER, textArea);
                PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
                System.setOut(printStream);
                System.setErr(printStream);

                // prevents the user from modifying the output text on the screen
                textArea.setEditable(false);

                // user enters the UPC number to search for in the database
                String searchUPC = JOptionPane.showInputDialog("Enter the product UPC number");

                if (!Report.searchByUPC(searchUPC)) {
                    JOptionPane.showMessageDialog(null, "Product not found");
                } else {
                    newWindow.setVisible(true);
                }
            }
        });

        // Load Today's Sales Button Method
        salesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog("Enter name of the file");
                try {
                    Report.loadSales(fileName);
                    JOptionPane.showMessageDialog(null, "Sales Report Loaded");
                } catch (NullPointerException error) {
                    JOptionPane.showMessageDialog(null, "File Doesn't Exist");

                }
            }
        });

        // Load The Receiving Log Button Method
        receivingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog("Enter name of the file");
                try {
                    productList = Report.readReceiving(fileName);
                    JOptionPane.showMessageDialog(null, "Receiving Log Loaded");

                } catch (NullPointerException error) {
                    JOptionPane.showMessageDialog(null, "File Doesn't Exist");

                }
            }
        });

        // Generate Order Report Button Method
        toOrderButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (toOrderList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Order List Empty");
                } else {
                    JFrame newWindow = new JFrame("Order Report");
                    final JButton saveButton = new JButton("Save");

                    createReportScreen(newWindow, saveButton);

                    Report.addToOrder(System.out);

                    saveButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                writer = new PrintStream(toOrder);
                                Report.addToOrder(writer);
                                JOptionPane.showMessageDialog(null, "File Saved");

                                // clears the order report only when file is saved,
                                // so that the user can view an order report multiple times before
                                // saving it
                                toOrderList.clear();
                            } catch (FileNotFoundException e1) {
                                System.out.println(e1);
                            }
                        }
                    });
                    newWindow.setVisible(true);
                }
            }
        });

        // Quit Button Method
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        f.setVisible(true);
    }

    // Method for creating any of the required report screens that would be selected
    // from the main menu
    static void createReportScreen(JFrame newWindow, JButton saveButton) {
        newWindow.setSize(500, 400);
        newWindow.setLocation(500, 200);

        newWindow.getContentPane().add(BorderLayout.SOUTH, saveButton);

        final JTextArea textArea = new JTextArea(10, 40);
        textArea.setEditable(false);
        // In case of a long report, there is a scroll bar
        JScrollPane sp = new JScrollPane(textArea);
        newWindow.getContentPane().add(BorderLayout.CENTER, sp);

        // Redirected output stream to the textArea by using the CustomOutputStream
        // Class
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
        System.setOut(printStream);
        System.setErr(printStream);
    }
}
