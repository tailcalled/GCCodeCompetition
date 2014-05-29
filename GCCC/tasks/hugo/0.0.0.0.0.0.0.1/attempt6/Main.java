import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int n = in.nextInt();
		List<BitSet> apples=new ArrayList<>();
		int max=0;
		for (int i=0; i<n; i++) {
			BitSet af = new BitSet();
			apples.add(af);
			int k=in.nextInt();
			for (int j=0; j<k; j++) {
				int a=in.nextInt();
				af.set(a);
				max=Math.max(max,  a);
			}
		}
		int start=n/2;
		for (int i=0; i<start; i++) {
			for (int j=0; j<start-i; j++) {
				apples.get(j).clear(i+1);
				apples.get(n-1-j).clear(i+1);
			}
		}
		int[] count=new int[n];
		if (apples.get(start).get(1))
			count[start]=1;
		for (int i=2; i<=max; i++) {
			int[] newCount=new int[n];
			for (int j=0; j<n; j++) {
				int c=count[j];
				if (j>0)
					c=Math.max(c, count[j-1]);
				if (j+1<n)
					c=Math.max(c, count[j+1]);
				if (apples.get(j).get(i))
					c++;
				newCount[j]=c;
			}
			count=newCount;
		}
		int result=0;
		for (int i=0; i<n; i++)
			result=Math.max(result, count[i]);
		System.out.println(result);
	}
}
