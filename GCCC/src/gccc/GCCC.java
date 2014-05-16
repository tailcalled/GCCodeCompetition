package gccc;

import java.net.*;
import com.sun.net.httpserver.*;
import gccc.handlers.*;

/**
 * Main class. Responsible for the UI.
 */
public class GCCC {

	private final Competition competition;

	public final Handlers handlers = new Handlers();
	/** Container namespace for HTTPHandlers */
	public class Handlers {
		public final HttpHandler home = new Home();
		private Handlers() {}
	}

	public GCCC() {
		competition = new Competition();
	}

	public static void main(String[] args) throws Throwable {
		GCCC gccc = new GCCC();
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 16);
		server.createContext("/", gccc.handlers.home);
		server.setExecutor(null);
		server.start();
	}

}