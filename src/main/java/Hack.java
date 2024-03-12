
public class Hack {
	public static void main(String args[]) {
		System.out.println(10000);
    	for(int i=0;i<10000;i++) {
    		System.out.println(100);
    		for(int j=0;j<100;j++) {
    			if(j % 6 == 0) System.out.print('m');
    			if(j % 6 == 1) System.out.print('a');
    			if(j % 6 == 2) System.out.print('p');
    			if(j % 6 == 3) System.out.print('p');
    			if(j % 6 == 4) System.out.print('i');
    			if(j % 6 == 5) System.out.print('e');
    		}
    		System.out.println();
    	}
	}
}
