package gccc.handlers;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import com.sun.net.httpserver.*;
import gccc.*;
import static gccc.HTMLUtil.*;

public abstract class AbstractHandler implements HttpHandler {

	protected final Competition competition;

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
				Pattern boundaryRegex = Pattern.compile("multipart/form-data(?:; boundary=(.+))?");
				Pattern dispositionRegex = Pattern.compile("form-data; name=\"([^\"]+)\"(?:; filename=\"([^\"]+)\")?");
				Matcher m = boundaryRegex.matcher(contentType); m.matches();
				String boundary = "--" + m.group(1).trim();
				line = br.readLine();
				assert line.trim().startsWith(boundary);
				while ((line = br.readLine()) != null) {
					// state 1: reading header thingies
					String name = null;
					String filename = null;
					m = regex.matcher(line);
					while (!line.trim().equals("")) {
						if (m.matches()) {
							while (m.matches()) {
								if (m.group(1).equals("Content-Type")) {
									m = boundaryRegex.matcher(m.group(2));
									if (m.matches()) {
										boundary = "--" + m.group(1).trim();
									}
								}
								else if (m.group(1).equals("Content-Disposition")) {
									m = dispositionRegex.matcher(m.group(2)); m.matches();
									name = m.group(1);
									filename = m.group(2);
								}
								else {
									System.out.println("Warning: invalid header: " + m.group(1));
								}
								m = regex.matcher(line = br.readLine());
							}
						}
						else {
							System.out.println("Warning: invalid line: " + line);
							m = regex.matcher(line = br.readLine());
						}
					}
					// state 2: reading input
					if (name != null) {
						String input = null;
						while ((line = br.readLine()) != null) {
							if (line.trim().startsWith(boundary)) {
								break;
							}
							if (input == null) input = "";
							else input += "\n";
							input += line;
						}
						params.put(name, input == null? "" : input);
						if (filename != null) {
							params.put(name + "__filename", filename);
						}
					}
				}
				System.out.println(params.keySet());
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
			t1.printStackTrace();
			try {
				respond(500, sess, page(
					tag("p",
						escape("Internal server error: " + t1.getMessage())
					)
				));
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