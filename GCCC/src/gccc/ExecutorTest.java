package gccc;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ExecutorTest {
	
	public static void main(String[] args) throws UnknownHostException {
		AttemptQueue queue = new AttemptQueue();
		Executor executor = new Executor(queue);
		User user = new User(InetAddress.getLocalHost());
		Task task=new Task("Hugo", 10000);
		Attempt attempt = new Attempt(user, new File("C:/Users/Niels/git/GCCodeCompetition/jhugo/src/Main.java"), task);
		//Attempt attempt = new Attempt(user, new File("C:/Users/Niels/git/GCCodeCompetition/jhugo/src/Main.class"), task);
		queue.add(attempt);
	}
}
