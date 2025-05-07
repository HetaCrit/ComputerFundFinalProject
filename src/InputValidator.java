package src;

import java.util.Scanner;
import static src.TerminalStyle.*;  //color helper

//class to validate get and ensure userinput is acceptable to various formats. Incorperates iterative recursion to ensure user input is valid.
public class InputValidator {
    private static final Scanner sc = new Scanner(System.in); //scanner variable setup.

    //integers
    public static int getInt(String prompt, int min, int max) {
        System.out.print(prompt);
        String raw = sc.nextLine();
        try {
            int val = Integer.parseInt(raw.trim());
            if (val < min || val > max) throw new NumberFormatException(
                    "Value must be in [" + min + "," + max + "]");
            return val;
        } catch (NumberFormatException e) {
            System.out.println(fx(RED, "Error: " + e.getMessage()));
            return getInt(prompt, min, max);   // recursion
        }
    }

    //doubles
    public static double getDouble(String prompt, double min) {
        System.out.print(prompt);
        String raw = sc.nextLine();
        try {
            double val = parseCurrency(raw.trim());
            if (val < min) throw new NumberFormatException(
                    "Value must be ≥ " + min);
            return val;
        } catch (NumberFormatException e) {
            System.out.println(fx(RED, "Error: " + e.getMessage()));
            return getDouble(prompt, min);     // recursion
        }
    }

    //parsing non important data such as $ or commas or spaces for better user experience.
    private static double parseCurrency(String s) {
        String cleaned = s.replaceAll("[^0-9.]", "");  // keep digits & dot
        if (cleaned.isEmpty()) throw new NumberFormatException("Not a number");
        return Double.parseDouble(cleaned);
    }

    //non empty strings
    public static String getNonEmpty(String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        if (s.isEmpty()) {
            System.out.println(fx(RED, "Error: Cannot be blank."));
            return getNonEmpty(prompt);        // recursion
        }
        return s;
    }
}
