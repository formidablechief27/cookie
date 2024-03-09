import java.io.*;
import java.util.*;

public class Main826 {
    
   static class FastReader {
        BufferedReader br;
        StringTokenizer st;
        public FastReader(){br = new BufferedReader(new InputStreamReader(System.in));}
        String next(){while (st == null || !st.hasMoreElements()) {try {st = new StringTokenizer(br.readLine());}catch (IOException e) {e.printStackTrace();}}return st.nextToken();}
        int nextInt() { return Integer.parseInt(next()); }
        long nextLong() { return Long.parseLong(next()); }
        double nextDouble() {return Double.parseDouble(next());}
   }
    
    FastReader sc=new FastReader();
    PrintWriter out=new PrintWriter(System.out);
    
    public static void main(String args[]) {
        Main826 FC27 = new Main826();
        FC27.solve();
    }

    public void solve() {
        // solve the solution here
        long n = sc.nextLong();
        long ans = (n - 1)*10 + 2;
        out.println(ans);
        out.close();
    }
}