package gccc.handlers;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.sun.net.httpserver.*;

import gccc.*;
import static gccc.HTMLUtil.*;

public abstract class HTMLHandler extends AbstractHandler {

	protected final Competition competition;

	public HTMLHandler(Competition competition) {
		this.competition = competition;
	}

	public class Session {
		private final HttpExchange sess;
		private final Map<String, Object> params;
		public Session(HttpExchange sess, Map<String, Object> params) {
			this.sess = sess;
			this.params = params;
		}
		public Map<String, Object> getParams() {
			return Collections.unmodifiableMap(params);
		}
		public User getUser() {
			return competition.getUserByAddress(sess.getRemoteAddress().getAddress());
		}
	}

	public void handle(HttpExchange sess, Map<String, Object> params) throws Throwable {
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

	public HTML post(Session sess) throws Throwable {
		return null;
	}
	public abstract HTML get(Session sess) throws Throwable;

}