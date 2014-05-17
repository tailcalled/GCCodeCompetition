package gccc;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

public class ExecutorTest {
	
	public static void main(String[] args) throws Exception {
		AttemptQueue queue = new AttemptQueue();
		try (Executor executor = new Executor(queue)) {
			User user = new User(InetAddress.getLocalHost());
			List<Task> tasks = TaskFileHandler.getTasks(new File("submissions"));
			//Attempt attempt = new Attempt(user, new File("C:/Users/Niels/git/GCCodeCompetition/jhugo/src/Main.java"), tasks.get(0), 0);
			Attempt attempt = new Attempt(user, new File("C:/Users/Niels/git/GCCodeCompetition/jhugo/src/Main.class"), tasks.get(0), 0);
			executor.runAttempt(attempt);
			AttemptResult result = attempt.getResult();
			if (result.isSuccess())
				System.out.println("Attempt succeeded");
			else {
				System.out.println("Attempt failed");
				System.out.println("error="+result.getErrorMessage());
			}
			System.out.println("duration="+result.getDurationms());
			System.out.println("output: "+result.getOutput());
		}
	}
}
