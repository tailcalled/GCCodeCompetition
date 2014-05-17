package gccc.handlers;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import com.sun.net.httpserver.*;
import gccc.*;
import static gccc.HTMLUtil.*;

public class Doc extends AbstractHandler {

	private final Competition competition;
	private final File parentDir;

	public Doc(Competition competition, File parentDir) {
		this.competition = competition;
		this.parentDir = parentDir;
	}

	public void handle(HttpExchange sess, Map<String, String> params) throws Throwable {
		String method = sess.getRequestMethod();
		if (method.equalsIgnoreCase("get") && params.containsKey("problem")) {
			Task problem = competition.getTask(params.get("problem"));
			File doc = new File(parentDir, problem.getName() + "/doc.pdf");
			sess.getResponseHeaders().set("Content-Type", "application/pdf");
			if (!doc.exists()) {
				System.out.println("Doc doesn't exist.");
			}
			byte[] bytes = Files.readAllBytes(doc.toPath());
			sess.sendResponseHeaders(200, bytes.length);
			System.out.println(bytes.length);
			try (OutputStream os = sess.getResponseBody()) {
				os.write(bytes);
			}
		}
		else {
			throw new UnsupportedOperationException("todo: better error message");
		}
	}

}