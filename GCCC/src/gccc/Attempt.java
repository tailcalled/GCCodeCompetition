package gccc;

import java.io.File;
import java.util.Date;

public class Attempt {

	public Attempt(User user, File file, Task task) {
		this.user = user;
		this.file = file;
		this.task = task;
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

	/**
	 * @return Is be null if the Attempt has not yet been executed 
	 */
	public AttemptResult getResult() {
		return result;
	}

	public void setResult(AttemptResult result) {
		this.result = result;
	}

	private final User user;
	private final File file;
	private final Date created=new Date();
	private final Task task;
	private AttemptResult result=null;

}
