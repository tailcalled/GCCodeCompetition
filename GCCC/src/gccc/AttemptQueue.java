package gccc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AttemptQueue {

	public void add(Attempt attempt) {
		synchronized(waiting) {
			waiting.add(attempt);
			waiting.notifyAll();
		}
	}
	
	public List<Attempt> getAllAttempts() {
		List<Attempt> list = new ArrayList<>(evaluated);
		synchronized(waiting) {
			list.addAll(waiting);
		}
		return list;
	}
	
	public Attempt getWaitingAttempt() throws InterruptedException {
		synchronized(waiting) {
			while (true) {
				Attempt a = waiting.poll();
				if (a!=null)
					return a;
				waiting.wait();
			}
		}
	}
	
	private Queue<Attempt> waiting=new ArrayDeque<>();
	private List<Attempt> evaluated=new ArrayList<>();
}
