package src;


//Class made to elvate user experience and make it more colorful. It's simply splendid. 
public class TerminalStyle {
    //defining ANSI color codes
    public static final String RESET = "\u001B[0m";
    public static final String BOLD  = "\u001B[1m";

    public static final String CYAN    = "\u001B[36m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String RED     = "\u001B[31m";
    public static final String MAGENTA = "\u001B[35m";

    //wraps text with ANSI escape codes.
    public static String fx(String ansi, String text) {
        return ansi + text + RESET;
    }
}
