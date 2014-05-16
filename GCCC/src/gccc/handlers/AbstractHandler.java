package gccc.handlers;

import com.sun.net.httpserver.*;
import gccc.*;
import static gccc.HTMLUtil.*;

public abstract class AbstractHandler implements HttpHandler {

	public void handle(HttpExchange sess) {
		try {
			respond(200, sess, get(sess));
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

	public abstract HTML get(HttpExchange sess) throws Throwable;

}