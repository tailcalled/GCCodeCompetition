package gccc;

public class Executor {

	public Executor(AttemptQueue queue) {
		this.queue = queue;
	}

	private Thread thread=new Thread() {
		
	};
	private final AttemptQueue queue;
}
