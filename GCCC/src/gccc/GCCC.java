package gccc;

import java.net.*;
import com.sun.net.httpserver.*;
import gccc.handlers.*;

/**
 * Main class. Responsible for the UI.
 */
public class GCCC {

	private final Competition competition;

	public final Handlers handlers;
	/** Container namespace for HTTPHandlers */
	public class Handlers {
		public final HttpHandler home;
		private Handlers(Competition competition) {
			home = new Home(competition);
		}
	}

	public GCCC() {
		competition = new Competition();
		handlers = new Handlers(competition);
	}

	public static void main(String[] args) throws Throwable {
		GCCC gccc = new GCCC();
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 16);
		server.createContext("/", gccc.handlers.home);
		server.setExecutor(null);
		server.start();
	}

}