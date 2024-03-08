import java.util.*;

public class Main {
    Scanner SC = new Scanner(System.in);
    public static void main(String args[]) {
        Main FC27 = new Main();
        FC27.solve();
    }

    public void solve() {
    	String s = SC.next();
        int n = s.length();
        if(n == 1) {
            System.out.println(-1);
            return;
        }
        for(int i=0;i<n;i++) {
            if(s.charAt(i) != 'a') {
                System.out.println(s.substring(0, i) + "a" + s.substring(i + 1, n));
                return;
            }
        }
        System.out.println(s.substring(0, n-1) + "b");
    }
}