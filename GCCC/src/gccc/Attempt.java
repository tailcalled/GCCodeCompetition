package gccc;

import java.io.File;
import java.util.Date;

public class Attempt {

	public Attempt(User user, File file, Task task, int attemptNum) {
		this.user = user;
		this.file = file;
		this.task = task;
		this.attemptNum = attemptNum;
	}

	public Date getCreated() {
		return created;
	}

	public User getUser() {
		return user;
	}

	public File getFile() {
		return file;
	}

	public Task getTask() {
		return task;
	}

	public int getAttemptNum() {
		return attemptNum;
	}

	/**
	 * @return Is be null if the Attempt has not yet been executed 
	 */
	public AttemptResult getResult() {
		return result;
	}

	public void setResult(AttemptResult result) {
		this.result = result;
		setState(result.isSuccess() ? State.Completed : State.Failed);
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public enum State { Waiting, Executing, Failed, Completed }
	
	private final User user;
	private final File file;
	private final Date created=new Date();
	private final Task task;
	private final int attemptNum;
	private State state=State.Waiting;
	private AttemptResult result=null;

}
