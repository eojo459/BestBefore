import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

/*
 * Report is a class that contains all the methods for generating, manipulating, printing
 * and saving of the reports and logs(Expired products report, Expiry report, Sales report,
 * Receiving Log, Order Report).
 *
 * @authors Stefan Stojsic,Emmanuel Ojo, Osama Hameed
 */

public class Report extends Main {

    // Load Today's Sales Method
    // Takes the SalesReport file which consists of product UPCs and their
    // quantities sold,
    // and then matches those UPC's with our product data, and decreases the
    // quantities
    // by the sold number.
    static public void loadSales(String SalesFile) throws NullPointerException {
        Scanner sc;
        InputStream file = Main.class.getResourceAsStream(SalesFile + ".txt");

        sc = new Scanner(file);

        // for each Product in the SalesReport .txt file, update(decrease) the quantity
        while (sc.hasNextLine()) {

            String str[] = sc.nextLine().split("\\s+");
            int quant = Integer.parseInt(str[1]);
            for (int j = 0; j < productList.size(); j++) {
                if (productList.get(j).getUPC().compareTo(str[0]) == 0) {
                    productList.get(j).updateQuantity(quant);

                    // in case of a product being sold out/out of stock at the store,
                    // that product is added to the order list
                    if (productList.get(j).getQuantity() <= 0) {
                        toOrderList.add(productList.get(j));
                    }
                }
            }
        }
        sc.close();
    }

    // Method for outputing a order list to the given output stream.
    // Order list is a list of all the products with quantities of 0
    // which indicates that these products should be reordered.
    static public void addToOrder(PrintStream output) {
        for (int i = 0; i < toOrderList.size(); i++) {
            output.println(toOrderList.get(i));
        }
        output.close();
    }

    // Method that takes the list of expired products and resets
    // the quantities of those products to 0, and also putting those
    // product on the order list to be reordered.
    static public void updateQuantities() {
        for (int i = 0; i < expiredList.size(); i++) {
            String upc = expiredList.get(i).getUPC();
            for (int j = 0; j < productList.size(); j++) {
                if (productList.get(j).getUPC().compareTo(upc) == 0) {
                    toOrderList.add(productList.get(j));
                    productList.get(j).setQuantity(0);
                }
            }
        }
    }

    // Search function that takes the upc number of a product,
    // and by traversing once through the list of product
    // tries to match the given upc with the product,
    // returns false if product was not found.
    static public boolean searchByUPC(String searchUPC) {

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            if (searchUPC.equals(product.getUPC())) {
                System.out.println("  Found:");
                System.out.println("  UPC\tName\t\tQuantity\tExp.Date");
                System.out.println("  " + product);
                return true;
            }
        }
        return false;
    }

    // Method that creates a list of all the product that have expired to the
    // current date.
    static public void generateExpiredList() {

        productList.forEach((product) -> {
            int days = (int) Duration.between(todayDate.toInstant(), product.getExpDate().toInstant()).toDays();
            int quant = product.getQuantity();
            if (days < 0 && quant > 0) {
                expiredList.add(product);
            }
        });

        printExpired(System.out);
    }

    // Method that creates the file output stream and calls the printExpired
    // to save the expired report to a file.
    static public void saveExpiredList() {
        try {
            writer = new PrintStream(expiredReport);
            printExpired(writer);
            writer.close();

        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    // Method that creates a list of product that are expiring in the
    // time frame of a given number of days.
    static public void generateExpiryList(int expiryRange) {

        productList.forEach((product) -> {
            int days = (int) Duration.between(todayDate.toInstant(), product.getExpDate().toInstant()).toDays();

            if (expiryRange >= days && days >= 0) {
                expiryList.add(product);
            }
        });

        printExpiry(System.out, expiryRange);
    }

    // Method that creates the file output stream and calls the printExpiry
    // to save the expiry report to a file.
    static public void saveExpiryList(int range) {
        try {
            writer = new PrintStream(expiryReport);
            printExpiry(writer, range);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    // Method that reads a receiving file and populates the product list with
    // products from the
    // receiving list.If the product is already in the list, it just updates the
    // quantities and the new
    // expiry dates.
    static public List<Product> readReceiving(String receivingFile) throws NullPointerException {

        Scanner sc;
        InputStream file = Main.class.getResourceAsStream(receivingFile + ".txt");

        sc = new Scanner(file);

        // open the file and read the contents
        // the format for the file is: UPC # | ProductName | Quantity | ExpiryDate
        // (DAY/MONTH/YEAR)
        while (sc.hasNextLine()) {
            String str[] = sc.nextLine().split("\\s+");
            int quantity = Integer.parseInt(str[2]);
            String date[] = str[3].split("/");
            int day = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int year = Integer.parseInt(date[2]);

            boolean updated = false;
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getUPC().equals(str[0])) {
                    productList.get(i).setQuantity(quantity);
                    productList.get(i).setExpDate(year, month, day);
                    updated = true;
                }
            }
            if (!updated) {
                productList.add(new Product(str[0], str[1], quantity, year, month, day));
            }
        }

        sc.close();
        return productList;
    }

    // Displays the expired products on the screen in the format
    // UPC # | ProductName | Quantity | ExpiryDate (DAY/MONTH/YEAR)
    static public void printExpired(PrintStream stream) {

        stream.println("  Expired products report");
        stream.println("  " + todayDate.getTime() + "\n");
        stream.println("  ----------------------------------------------------"
                + "----------------------------------------------");
        stream.println("  UPC\tName\t\tQuantity\tExp.Date");
        stream.println("  ----------------------------------------------------"
                + "----------------------------------------------");

        for (int i = 0; i < expiredList.size(); i++) {
            stream.println("  " + expiredList.get(i).toString());
        }
    }

    // Displays the products within the expiry date range on the screen in the format
    // UPC # | ProductName | Quantity | ExpiryDate (DAY/MONTH/YEAR)
    static public void printExpiry(PrintStream stream, int expiryRange) {

        stream.println("  Expiry report for the next " + expiryRange + " days");
        stream.println("  " + todayDate.getTime() + "\n");
        stream.println("  ----------------------------------------------------"
                + "----------------------------------------------");
        stream.println("  UPC\tName\t\tQuantity\tExp.Date");
        stream.println("  ----------------------------------------------------"
                + "----------------------------------------------");
        for (int i = 0; i < expiryList.size(); i++) {
            stream.println("  " + expiryList.get(i).toString());
        }
    }
}
