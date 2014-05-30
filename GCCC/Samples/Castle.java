import java.util.Scanner;


public class Castle {

	public static class Cell {
		
		public Cell(int walls) {
			this.walls=walls;
		}
		
		boolean isMaster() {
			return parent==this;
		}
		
		Cell getMaster() {
			Cell c=this;
			while (c!=c.parent)
				c=c.parent;
			return c;
		}
		
		int size=1;
		Cell parent=this;
		int walls;
	}
	
	static int[] dx={ -1, 0, 1, 0 };
	static int[] dy={ 0, -1, 0, 1 };
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int sy=in.nextInt();
		int sx=in.nextInt();
		Cell[][] castle=new Cell[sx][sy];
		for (int y=0; y<sy; y++)
			for (int x=0; x<sx; x++)
				castle[x][y]=new Cell(in.nextInt());
		int rooms=sx*sy;
		for (int y=0; y<sy; y++) {
			for (int x=0; x<sx; x++) {
				Cell c=castle[x][y];
				for (int d=0; d<4; d++) {
					int m=1<<d;
					if ((c.walls & m)==0) {
						int x2=x+dx[d];
						int y2=y+dy[d];
						Cell c2=castle[x2][y2];
						Cell master1=c.getMaster();
						Cell master2=c2.getMaster();
						if (master1!=master2){
							if (master2.size>master1.size) {
								Cell t=master1;
								master1=master2;
								master2=t;
							}
							master1.size+=master2.size;
							master2.parent=master1;
							rooms--;
						}
					}
				}
			}
		}
		Cell largest=castle[0][0].getMaster();
		int maxsize=largest.size;
		int maxx=0;
		int maxy=0;
		char maxd=' ';
		for (int y=0; y<sy; y++) {
			for (int x=0; x<sx; x++) {
				Cell c=castle[x][y];
				Cell m=c.getMaster();
				if (m.size>largest.size) {
					largest=m;
				}
				if (x+1<sx && (c.walls & 4)>0) {
					Cell m2=castle[x+1][y].getMaster();
					if (m2!=m) {
						int size=m2.size+m.size;
						if (size>maxsize) {
							maxx=x;
							maxy=y;
							maxsize=size;
							maxd='E';
						}
					}
				}
				if (y+1<sy && (c.walls & 8)>0) {
					Cell m2=castle[x][y+1].getMaster();
					if (m2!=m) {
						int size=m2.size+m.size;
						if (size>maxsize) {
							maxx=x;
							maxy=y;
							maxsize=size;
							maxd='S';
						}
					}
				}
			}
		}
		System.out.println(rooms);
		System.out.println(largest.size);
		System.out.println(maxsize);
	}

}
