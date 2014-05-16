package gccc;

public class Executor {

	public Executor(AttemptQueue queue) {
		this.queue = queue;
		thread.start();
	}

	private Thread thread=new Thread() {
		public void run() {
			while(true) {
				try {
					Attempt attempt = queue.getWaitingAttempt();
					runAttempt(attempt);
				} 
				catch (InterruptedException error) {
					break;
				}
				catch (Throwable error) {
					// Should never happen
					error.printStackTrace();
				}
			}
		}
	};

	private void runAttempt(Attempt attempt) {
		try {
			long start=System.currentTimeMillis();
			AttemptResult result = new AttemptResult(attempt);
			
			result.setDurationms(System.currentTimeMillis()-start);
			result.setSuccess(true);
			attempt.setResult(result);
		}
		/*catch (InterruptedException error) {
			return;
		}*/
		catch (Throwable error) {
			
		}
	}
	
	private final AttemptQueue queue;
}
