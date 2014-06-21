package gccc;

import gccc.handlers.AttemptSubmit;
import gccc.handlers.AttemptTable;
import gccc.handlers.Doc;
import gccc.handlers.FileHandler;
import gccc.handlers.Home;
import gccc.handlers.Submission;
import gccc.handlers.TaskInfo;
import gccc.handlers.TaskTable;
import gccc.handlers.TestTable;
import gccc.handlers.UserTable;

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

	private static final File SUBMISSIONS = new File("tasks/");

	private final Competition competition;
	private final HttpServer server;

	public final Handlers handlers;
	/** Container namespace for HTTPHandlers */
	public class Handlers {
		public final HttpHandler home;
		public final HttpHandler submission;
		public final HttpHandler taskInfo;
		public final HttpHandler doc;
		public final HttpHandler attemptTable;
		public final HttpHandler attemptSubmit;
		public final HttpHandler users;
		public final HttpHandler tasks;
		public final HttpHandler tests;
		public final HttpHandler files;
		private Handlers(Competition competition) {
			home = new Home(competition);
			submission = new Submission(competition);
			taskInfo = new TaskInfo(competition);
			doc = new Doc(competition);
			attemptTable=new AttemptTable(competition);
			attemptSubmit=new AttemptSubmit(competition);
			users=new UserTable(competition);
			tasks=new TaskTable(competition);
			tests=new TestTable(competition);
			files=new FileHandler(competition);
		}
	}

	public GCCC(HttpServer server, Competition competition) {
		this.competition = competition;
		handlers = new Handlers(competition);
		this.server = server;
		server.createContext("/", handlers.tasks);
		server.createContext("/submission", handlers.submission);
		server.createContext("/submit", handlers.submission);
		server.createContext("/task", handlers.taskInfo);
		server.createContext("/doc", handlers.doc);
		server.createContext("/attempts", handlers.attemptTable);
		server.createContext("/attemptsubmit", handlers.attemptSubmit);
		server.createContext("/users", handlers.users);
		server.createContext("/tasks", handlers.tasks);
		server.createContext("/tests", handlers.tests);
		server.createContext("/file", handlers.files);
	}

	public HttpServer getServer() {
		return server;
	}

	public static void main(String[] args) throws Throwable {
		HttpServer server = HttpServer.create(new InetSocketAddress(80), 16);
		Competition competition = CompetitionFileHandler.loadCompetition(SUBMISSIONS);
		final GCCC gccc = new GCCC(server, competition);
		server.setExecutor(null);
		server.start();
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				gccc.getServer().stop(1);
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
		frame.getContentPane().add(new MainPanel(competition));
		frame.setVisible(true);
		frame.setSize(300, 200);
	}
	
	@Override
	public void close() throws Exception {
		competition.close();
	}

}
