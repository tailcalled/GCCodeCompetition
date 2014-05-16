package gccc.handlers;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import com.sun.net.httpserver.*;
import gccc.*;
import static gccc.HTMLUtil.*;

public abstract class AbstractHandler implements HttpHandler {

	private Competition competition;

	public AbstractHandler(Competition competition) {
		this.competition = competition;
	}

	public class Session {
		private final HttpExchange sess;
		private final Map<String, String> params;
		public Session(HttpExchange sess, Map<String, String> params) {
			this.sess = sess;
			this.params = params;
		}
		public Map<String, String> getParams() {
			return Collections.unmodifiableMap(params);
		}
		public User getUser() {
			return competition.getUserByAddress(sess.getRemoteAddress().getAddress());
		}
	}

	private Map<String, String> parseParams(HttpExchange sess) throws Throwable {
		Headers headers = sess.getRequestHeaders();
		Map<String, String> params = new HashMap<String, String>();
		String query = sess.getRequestURI().getRawQuery();
		if (query != null)
			parseInto(query, params);
		String contentType = headers.getFirst("Content-Type");
		if (contentType != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(sess.getRequestBody()));
			String line;
			if (contentType.equalsIgnoreCase("application/x-www-form-urlencoded")) {
				while ((line = br.readLine()) != null) {
					parseInto(line, params);
				}
			}
			else if (contentType.startsWith("multipart/form-data")) {
				Pattern regex = Pattern.compile("([A-Za-z\\-]+): (.+)");
				Pattern boundaryRegex = Pattern.compile("multipart/form-data boundary=(.+)");
				Pattern dispositionRegex = Pattern.compile("form-data; name=\"(.)+\"(?:; filename=\"(.)+\")?");
				Matcher m = boundaryRegex.matcher(contentType); m.matches();
				String boundary = m.group(1);
				while ((line = br.readLine()) != null) {
					// state 1: reading header thingies
					String name = null;
					m = regex.matcher(line);
					while (!line.trim().equals("")) {
						while (m.matches()) {
							if (m.group(1) == "Content-Type") {
								m = boundaryRegex.matcher(contentType); m.matches();
								boundary = m.group(1);
							}
							else if (m.group(1) == "Content-Disposition") {
								m = dispositionRegex.matcher(contentType); m.matches();
								name = m.group(1);
							}
							m = regex.matcher(line = br.readLine());
						}
					}
					// state 2: reading input
					if (name != null) {
						String input = null;
						while (!(line = br.readLine()).trim().equals("--" + boundary)) {
							if (input == null) input = "";
							else input += "\n";
							input += line;
						}
						params.put(name, input == null? "" : input);
					}
				}
			}
			br.close();
		}
		return params;
	}
	private void parseInto(String query, Map<String, String> params) {
		String[] parts = query.split("&");
		for (String part: parts) {
			String[] kv = part.split("=");
			if (kv.length == 2) {
				params.put(kv[0], kv[1]);
			}
		}
	}

	public void handle(HttpExchange sess) {
		try {
			Map<String, String> params = parseParams(sess);
			Session session = new Session(sess, params);
			String method = sess.getRequestMethod();
			int code = 200;
			HTML result = null;
			if (method.equalsIgnoreCase("get")) {
				result = get(session);
			}
			else if (method.equalsIgnoreCase("post")) {
				result = post(session);
			}
			if (result == null) {
				code = 405;
				result = page(
					tag("p",
						escape("Unsupported method!")
					)
				);
			}
			respond(code, sess, result);
		}
		catch (Throwable t1) {
			try {
				respond(500, sess, page(
					tag("p",
						escape("Internal server error: " + t1.getMessage())
					)
				));
				t1.printStackTrace();
			}
			catch (Throwable t2) {
				t2.printStackTrace();
				// todo
			}
		}
	}

	public HTML post(Session sess) throws Throwable {
		return null;
	}
	public abstract HTML get(Session sess) throws Throwable;

}