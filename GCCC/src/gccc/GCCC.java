package gccc;

import gccc.handlers.Doc;
import gccc.handlers.Home;
import gccc.handlers.Submission;
import gccc.handlers.TaskInfo;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;

import javax.swing.JFrame;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Main class. Responsible for the UI.
 */
public class GCCC implements AutoCloseable {

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
		gccc = new GCCC();
		server = HttpServer.create(new InetSocketAddress(8080), 16);
		server.createContext("/", gccc.handlers.home);
		server.createContext("/submission", gccc.handlers.submission);
		server.createContext("/submit", gccc.handlers.submission);
		server.createContext("/task", gccc.handlers.taskInfo);
		server.createContext("/doc", gccc.handlers.doc);
		server.setExecutor(null);
		server.start();
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				server.stop(1);
				try {
					gccc.close();
				} 
				catch (Exception e1) {
					System.out.println("Cannot close gccc");
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		frame.setVisible(true);
		frame.setSize(300, 200);
	}
	
	@Override
	public void close() throws Exception {
		competition.close();
	}
	
	private static HttpServer server;
	private static GCCC gccc;

}