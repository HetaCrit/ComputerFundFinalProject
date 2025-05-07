package src;

import java.util.ArrayList;
import java.util.Random;


// Simulate sales for a day.I'm good with coeficients but hey it atleast it something todo.  
public class SimulateTrade {
    private static final Random RNG = new Random();

    //simulate method
    public static int simulateDay(ArrayList<Product> inventory) {
        int soldOutToday = 0;
        for (Product p : inventory) {
            if (p.getQuantity() == 0) continue;
            int maxCanSell = Math.max(1, (int)(p.getQuantity() * 0.4)); //can only sell max 40% of a particular product
            int soldNow = RNG.nextInt(maxCanSell + 1);   //rng based

            //handling
            p.addSold(soldNow);
            p.setQuantity(p.getQuantity() - soldNow);
            if (p.getQuantity() == 0) soldOutToday++;
        }
        return soldOutToday;
    }
}
