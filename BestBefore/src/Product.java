import java.util.Calendar;

/**
 * Product Class
 *
 * This class contains the information that each product in the database will hold.
 *
 * UPC (string):          The UPC # of the product, a unique ID for each product
 * productName (string):  The name of the product
 * quantity (int):        The total amount of this one product in the database
 * expDate (Calendar):    The expiry date for the product in the format (DAY/MONTH/YEAR)
 *
 *@authors Stefan Stojsic,Emmanuel Ojo, Osama Hameed
 */
public class Product {

    private String UPC;
    private String productName;
    private int quantity;
    private Calendar expDate = Calendar.getInstance();

    public Product(String UPC, String productName, int quantity, int year, int month, int day) {
        this.UPC = UPC;
        this.productName = productName;
        this.quantity = quantity;

        this.expDate.set(Calendar.YEAR, year);
        this.expDate.set(Calendar.MONTH, month - 1);
        this.expDate.set(Calendar.DATE, day);
    }

    public Calendar getExpDate() {
        return expDate;
    }

    public Calendar setExpDate(int year, int month, int day) {
        this.expDate.set(Calendar.YEAR, year);
        this.expDate.set(Calendar.MONTH, month - 1);
        this.expDate.set(Calendar.DATE, day);
        return expDate;
    }

    @Override
    public String toString() {
        int day = getExpDate().get(Calendar.DATE);
        int month = getExpDate().get(Calendar.MONTH) + 1;
        int year = getExpDate().get(Calendar.YEAR);
        return getUPC() + "\t" + getProductName() + "\t" + getQuantity() + "\t" + day + "/" + month + "/" + year;
    }

    public String getUPC() {
        return UPC;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int newQuantity) {
       this.quantity = newQuantity;
    }

    public void updateQuantity(int minusQuantity) {
        this.quantity -= minusQuantity;
    }
}
