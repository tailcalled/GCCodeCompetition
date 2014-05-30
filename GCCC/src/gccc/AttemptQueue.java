package gccc;

import gccc.Attempt.State;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AttemptQueue {

	public void add(Attempt attempt) {
		synchronized(waiting) {
			waiting.add(attempt);
			allAttempts.add(attempt);
			waiting.notifyAll();
		}
		synchronized(listeners) {
			for (Runnable listener: listeners)
				listener.run();
		}
	}
	
	public List<Attempt> getAllAttempts() {
		synchronized(waiting) {
			return new ArrayList<>(allAttempts);
		}
	}

	/**
	 * If there are no waiting attempts then it will wait for one
	 * @return Never null
	 * @throws InterruptedException
	 */
	public Attempt getWaitingAttempt() throws InterruptedException {
		synchronized(waiting) {
			while (true) {
				Attempt a = waiting.poll();
				if (a!=null) {
					a.setState(State.Executing);
					return a;
				}
				waiting.wait();
			}
		}
	}
	
	public void addListener(Runnable listener) {
		synchronized(listeners) {
			listeners.add(listener);
		}
	}
	
	private Queue<Attempt> waiting=new ArrayDeque<>();
	private List<Attempt> allAttempts=new ArrayList<>();
	private List<Runnable> listeners=new ArrayList<>();
}
