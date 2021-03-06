package gccc.handlers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

import com.sun.net.httpserver.*;

import gccc.*;
import static gccc.HTMLUtil.*;

public abstract class AbstractHandler implements HttpHandler {

	private Map<String, Object> parseParams(HttpExchange sess) throws Throwable {
		Headers headers = sess.getRequestHeaders();
		Map<String, Object> params = new HashMap<>();
		String query = sess.getRequestURI().getRawQuery();
		if (query != null)
			parseInto(query, params);
		String contentType = headers.getFirst("Content-Type");
		if (contentType != null) {
			//BufferedReader br = new BufferedReader(new InputStreamReader(sess.getRequestBody()));
			FormReader br=new FormReader(sess.getRequestBody(), StandardCharsets.UTF_8);
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
					while (line!=null && !line.trim().equals("")) {
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
								line = br.readLine();
								if (line==null)
									break;
								m = regex.matcher(line);
							}
						}
						else {
							System.out.println("Warning: invalid line: " + line);
							line = br.readLine();
							if (line==null)
								break;
							m = regex.matcher(line);
						}
					}
					// state 2: reading input
					if (name != null) {
						/*String input = null;
						while ((line = br.readLine()) != null) {
							if (line.trim().startsWith(boundary)) {
								break;
							}
							if (input == null) input = "";
							else input += "\n";
							input += line;
						}
						params.put(name, input == null? "" : input);
						*/
						byte[] binary = br.readBinary(boundary);
						params.put(name, binary);
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
	private void parseInto(String query, Map<String, Object> params) {
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
			Map<String, Object> params = parseParams(sess);
			handle(sess, params);
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

	public abstract void handle(HttpExchange sess, Map<String, Object> params) throws Throwable;

}