package gccc;

import java.io.File;
import java.net.InetAddress;
import java.util.Arrays;

public class ExecutorTest {
	
	public static void main(String[] args) throws Exception {
		AttemptQueue queue = new AttemptQueue();
		try (Executor executor = new Executor(queue)) {
			User user = new User(InetAddress.getLocalHost());
			Test hugoTest=new Test() {
				@Override
				public String getInput() {
					return "5\n2 2 6\n2 1 5\n1 4\n1 3\n1 2\n";
				}
				@Override
				public void verifyOutput(String output) throws Exception {
					checkInts(output, new int[] { 4 });
				}
			};
			Task task=new Task("Hugo", 1000, Arrays.asList(hugoTest));
			//Attempt attempt = new Attempt(user, new File("C:/Users/Niels/git/GCCodeCompetition/jhugo/src/Main.java"), task);
			Attempt attempt = new Attempt(user, new File("C:/Users/Niels/git/GCCodeCompetition/jhugo/src/Main.class"), task);
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
