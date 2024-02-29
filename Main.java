import java.util.*;

public class Main {
    Scanner SC = new Scanner(System.in);
    public static void main(String args[]) {
        Main FC27 = new Main();
        FC27.solve();
    }

    public void solve() {
    	long n = SC.nextLong();
        System.out.println((n - 1)*10 + 2);
    }
}