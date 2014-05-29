#include "stdio.h"
#include "stdlib.h"

int N;
int counts[100001];
int outs[100000];

int main() {
    scanf("%d", &N);
    for (int i=0; i<100000; i++)
    	counts[0]=0;
    for (int i=0; i<N; i++) {
    	int home, out;
        scanf("%d %d", &home, &out);
        outs[i]=out;
        counts[home]++;
    }
    for (int i=0; i<N; i++) {
    	int home=N-1;
    	home+=counts[outs[i]];
        printf("%d %d\n", home, 2*N-2-home);
    }
    fflush(stdout);
	return 0;
}
