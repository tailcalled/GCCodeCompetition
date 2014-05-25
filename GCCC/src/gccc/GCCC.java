package gccc;

import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.List;

import com.sun.net.httpserver.*;

import gccc.handlers.*;

/**
 * Main class. Responsible for the UI.
 */
public class GCCC {

	private static final File SUBMISSIONS = new File("submissions/");

	private final Competition competition;

	public final Handlers handlers;
	/** Container namespace for HTTPHandlers */
	public class Handlers {
		public final HttpHandler home;
		public final HttpHandler submission;
		public final HttpHandler taskInfo;
		public final HttpHandler doc;
		private Handlers(Competition competition) {
			home = new Home(competition);
			submission = new Submission(competition, SUBMISSIONS);
			taskInfo = new TaskInfo(competition);
			doc = new Doc(competition, SUBMISSIONS);
		}
	}

	public GCCC() throws InterruptedException {
		competition = new Competition();
		List<Task> tasks = TaskFileHandler.getTasks(SUBMISSIONS);
		for (Task task: tasks)
			competition.addTask(task);
		//competition.addTask(new Task("hworld", "Hello, World!", 1000, Collections.<Test>emptyList()));
		handlers = new Handlers(competition);
	}

	public static void main(String[] args) throws Throwable {
		GCCC gccc = new GCCC();
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 16);
		server.createContext("/", gccc.handlers.home);
		server.createContext("/submission", gccc.handlers.submission);
		server.createContext("/submit", gccc.handlers.submission);
		server.createContext("/task", gccc.handlers.taskInfo);
		server.createContext("/doc", gccc.handlers.doc);
		server.setExecutor(null);
		server.start();
	}

}