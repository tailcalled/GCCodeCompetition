package gccc.handlers;

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
		public Session(HttpExchange sess) {
			this.sess = sess;
		}
		public User getUser() {
			return competition.getUserByAddress(sess.getRemoteAddress().getAddress());
		}
	}

	public void handle(HttpExchange sess) {
		try {
			respond(200, sess, get(new Session(sess)));
		}
		catch (Throwable t1) {
			try {
				respond(500, sess, page(
					tag("p",
						escape("Internal server error: " + t1.getMessage())
					)
				));
			}
			catch (Throwable t2) {
				// todo
			}
		}
	}

	public abstract HTML get(Session sess) throws Throwable;

}