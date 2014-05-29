package gccc;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class GenerateInput {

	public static void main(String[] args) throws Exception {
		int stier=999;
		int apples=3000;
		int length=50000;
		Random r=new Random();
		try (PrintWriter w=new PrintWriter("input.txt")) {
			w.println(stier);
			for (int i=0; i<stier; i++) {
				int a=r.nextInt(apples)+1;
				w.print(a);
				int pos=0;
				int frekvens=length/a*2;
				for (int j=0; j<a; j++) {
					int s=r.nextInt(frekvens)+1;
					pos+=s;
					w.print(" ");
					w.print(pos);
					if (pos>100000)
						throw new Exception("pos>100000");
				}
				w.println();
			}
		}
	}

}
