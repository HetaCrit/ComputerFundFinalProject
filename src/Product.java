package src;


//DEFINE MY DATA (PROUDCT IN THIS CASE)
public class Product {

    //variable setup
    private String name;
    private String category;
    private double price;      // dollars
    private int quantity;      // Stock
    private int sold;          // profits.hehehe


    //constructor for new products
    public Product(String name, String category,
                   double price, int quantity, int sold) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.sold = sold;
    }

    //check for a zero sold product and set it to 0 if so.
    public Product(String name, String category,
                   double price, int quantity) {
        this(name, category, price, quantity, 0);
    }

    //getter and setter methods for the product class.
    public String getName()          { return name; }
    public String getCategory()      { return category; }
    public double getPrice()         { return price; }
    public int    getQuantity()      { return quantity; }
    public int    getSold()          { return sold; }
    public double getRevenue()       { return sold * price; }

    public void   setQuantity(int q) { this.quantity = q; }
    public void   addSold(int n)     { this.sold += n; }

    //CSV DATA Handling && updating
    public String toCsv() {
        // name,category,price,quantity,sold
        return String.join(",",
                escape(name),
                escape(category),
                String.valueOf(price),
                String.valueOf(quantity),
                String.valueOf(sold));
    }

    public static Product fromCsv(String line) {
        String[] t = line.split(",", -1);
        return new Product(
                unescape(t[0]),
                unescape(t[1]),
                Double.parseDouble(t[2]),
                Integer.parseInt(t[3]),
                Integer.parseInt(t[4]));
    
    }
    private static String escape(String s) { return s.replace(",", "\\,"); }
    private static String unescape(String s){ return s.replace("\\,", ","); }


    //toString method to print the data in a readable format.
    @Override
    public String toString() {
        return String.format("%-16s | %-12s | $%7.2f | qty %4d | sold %4d | revenue $%8.2f",
                name, category, price, quantity, sold, getRevenue());
    }
}
