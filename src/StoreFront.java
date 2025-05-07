//keynote: I don't know why but I'm getting warnings and codespaces wont tell me what the warning is inside this VScode enviroment, but I mean it still runs!


package src;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import static src.TerminalStyle.*;   //color helper

//While at first I used a swing class from java I realized codespaces has no UI interface so it can render swing packages.
//This class is the "UI" (really just the terminal lol)
public class StoreFront {

    //variable setup
    private static final String DATA_DIR  = "data";
    private static final String INV_FILE  = DATA_DIR + "/inventory.csv";
    private static final String SOLD_FILE = DATA_DIR + "/soldout.csv";
    private static final int    MAX_SOLD_ARCHIVE = 500;

    private final ArrayList<Product> inventory = new ArrayList<>();
    private final Product[] soldOut = new Product[MAX_SOLD_ARCHIVE];
    private int soldOutCount = 0;

    //Init method
    public void run() {
        System.out.println(fx(BOLD + CYAN, "== WELCOME TO CLI STORE FRONT =="));
        load();
        boolean done = false;
        while (!done) {
            int choice = menu();
            switch (choice) {
                case 1:
                    AddOrUpdateProduct();
                    break;
                case 2:
                    viewInventory();
                    break;
                case 3:
                    simulateSales();
                    break;
                case 4:
                    done = true;
                    break;
                default:
                    // unreachable
                    break;
            }
        }

        save();
        System.out.println("Bye - data saved!");
    }

    //Recursion point into menu system.
    private int menu() {
        System.out.println(fx(BOLD + MAGENTA, "\nMenu:"));
        System.out.println("1) Add product");
        System.out.println("2) View / sort inventory");
        System.out.println("3) Simulate one day of sales");
        System.out.println("4) Save & Exit");
        return InputValidator.getInt("Choose > ", 1, 4);
    }

    //ADD (merges if name matches, case‑insensitive)
    private void AddOrUpdateProduct() {
        System.out.println("\nNEW PRODUCT (or merge)");
        String name = InputValidator.getNonEmpty("Name: ");

        Product existing = findByName(name);

        //check archive if not in live inventory
        if (existing == null) {
            for (int i = 0; i < soldOutCount; i++) {
                if (soldOut[i].getName().equalsIgnoreCase(name)) {
                    existing = soldOut[i];
                    //bring it back into inventory
                    inventory.add(existing);
                    soldOut[i] = soldOut[--soldOutCount];
                    break;
                }
            }
        }

        if (existing != null) {
            int newQty = InputValidator.getInt("Quantity to set: ", 0, 10_000);
            existing.setQuantity(newQty);
            System.out.println("Updated quantity for " + existing.getName() + " to " + newQty);
            return;
        }

        //brand‑new entry
        String cat = InputValidator.getNonEmpty("Category: ");
        double price = InputValidator.getDouble("Price ($): ", 0.0);
        int qty = InputValidator.getInt("Starting Qty: ", 1, 10_000);
        inventory.add(new Product(name, cat, price, qty));
        System.out.println("Added: " + name + " (" + cat + ")  $" + price + " x" + qty);
    }

    //VIEW
    private void viewInventory() {
        if (inventory.isEmpty()) {
            System.out.println(fx(YELLOW, "Inventory empty."));
            return;
        }

        System.out.println("Sort by:");
        System.out.println("1) Name   2) Quantity   3) Amount Sold   4) Revenue");
        int pick = InputValidator.getInt(">", 1, 4);
        Comparator<Product> cmp;
        switch (pick) {
            case 2:
                cmp = Comparator.comparingInt(Product::getQuantity).reversed();
                break;
            case 3:
                cmp = Comparator.comparingInt(Product::getSold).reversed();
                break;
            case 4:
                cmp = Comparator.comparingDouble(Product::getRevenue).reversed();
                break;
            case 1:
            default:
                cmp = Comparator.comparing(Product::getName);
                break;
        }
        inventory.sort(cmp);

        System.out.println("\n-- Current Inventory --");
        //highlight zero‑stock lines in bold yellow
        inventory.forEach(p -> {
            String line = p.toString();
            if (p.getQuantity() == 0) line = fx(BOLD + YELLOW, line);
            System.out.println(line);
        });

        if (soldOutCount > 0) {
            System.out.println("\n-- Sold‑Out Archive --");
            for (int i = 0; i < soldOutCount; i++) {
                System.out.println(fx(BOLD + YELLOW, soldOut[i].toString()));
            }
        }
    }

    //SIMULATE
    private void simulateSales() {
        int exhausted = SimulateTrade.simulateDay(inventory);
        // move zero‑qty items to archive
        for (Iterator<Product> it = inventory.iterator(); it.hasNext(); ) {
            Product p = it.next();
            if (p.getQuantity() == 0) {
                if (soldOutCount < soldOut.length) soldOut[soldOutCount++] = p;
                it.remove();
            }
        }
        System.out.printf("Day complete. %d product(s) sold out today.%n", exhausted);
    }

    //helper to find live product by name
    private Product findByName(String name) {
        for (Product p : inventory)
            if (p.getName().equalsIgnoreCase(name)) return p;
        return null;
    }

    //load data from files
    private void load() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException ignored) {}

        inventory.clear();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(INV_FILE))) {
            br.lines().filter(l -> !l.isBlank())
                    .filter(l -> !l.toLowerCase().startsWith("name,"))   //skip header if present
                    .forEach(l -> inventory.add(Product.fromCsv(l)));
        } catch (IOException e) {
            System.out.println("(no prior inventory found – fresh start)");
        }

        // sold‑out archive
        soldOutCount = 0;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(SOLD_FILE))) {
            br.lines().filter(l -> !l.isBlank())
                      .filter(l -> !l.toLowerCase().startsWith("name,"))
                      .forEach(l -> {
                          if (soldOutCount < soldOut.length)
                              soldOut[soldOutCount++] = Product.fromCsv(l);
                      });
        } catch (IOException ignored) {}
    }

    //save data to files
    private void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(INV_FILE))) {
            for (Product p : inventory) bw.write(p.toCsv() + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Couldn't save inventory: " + e.getMessage());
        }

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(SOLD_FILE))) {
            for (int i = 0; i < soldOutCount; i++)
                bw.write(soldOut[i].toCsv() + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Couldn't save sold‑out list: " + e.getMessage());
        }
    }
}
