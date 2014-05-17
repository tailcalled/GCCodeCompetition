package gccc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPool implements AutoCloseable {


	public static class Task implements Runnable {
		
		public Task(Runnable r) {
			this.r = r;
		}
		
		@Override
		public void run() {
			synchronized(this) {
				if (stop)
					return;
				thread=Thread.currentThread();
			}
			try {
				r.run();
			}
			finally {
				synchronized(this) {
					thread=null;
				}
			}
		}
		
		public void interrupt() {
			synchronized(this) {
				if (thread!=null)
					thread.interrupt();
				stop=true;
			}
		}

		private Thread thread;
		private boolean stop=false;
		private Runnable r;
	}
	
	public Task execute(Runnable r) {
		Task task = new Task(r);
		threadPool.execute(task);
		return task;
	}
	
	@Override
	public void close() throws Exception {
		// Prevent new tasks from starting:
		threadPool.shutdown(); 
		try {
			// Wait a while for existing tasks to terminate
			if (threadPool.awaitTermination(5, TimeUnit.SECONDS))
				return;
			threadPool.shutdownNow(); 
			// Wait a while for tasks to respond to being cancelled
			if (threadPool.awaitTermination(5, TimeUnit.SECONDS))
				return;
		} 
		catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			threadPool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	private ExecutorService threadPool=Executors.newCachedThreadPool();

}
