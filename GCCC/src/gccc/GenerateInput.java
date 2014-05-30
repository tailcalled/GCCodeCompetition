package gccc;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class GenerateInput {

	public static void main(String[] args) throws Exception {
		castle();
	}
	
	private static void castle() throws FileNotFoundException {
		int xs=6;
		int ys=6;
		int removes=xs*ys;
		int[][] castle=new int[xs][ys];
		for (int i=0; i<xs; i++)
			for (int j=0; j<ys; j++)
				castle[i][j]=15;
		Random r=new Random();
		for (int i=0; i<removes; ) {
			int ix=r.nextInt(xs);
			int iy=r.nextInt(ys);
			if (castle[ix][iy]!=15) {
				if (r.nextInt(5)!=1)
					continue;
			}
			int d=r.nextInt(4);
			int jx=ix;
			int jy=iy;
			switch (d) {
				case 0:
					jx--;
					break;
				case 1:
					jy--;
					break;
				case 2:
					jx++;
					break;
				case 3:
					jy++;
					break;
			}
			if (jx<0 || jx>=xs || jy<0 || jy>=ys)
				continue;
			int id=1 << d;
			int jd=1 << ((d+2)%4);
			castle[ix][iy]&=~id;
			castle[jx][jy]&=~jd;
			i++;
		}
		try (PrintWriter w=new PrintWriter("input.txt")) {
			w.println(ys);
			w.println(xs);
			for (int y=0; y<ys; y++) {
				for (int x=0; x<xs; x++) {
					w.print(castle[x][y]);
					w.print(" ");
				}
				w.println();
			}
		}
	}
	
	private static void hugo() throws FileNotFoundException {
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
