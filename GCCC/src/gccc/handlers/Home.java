package gccc.handlers;

import com.sun.net.httpserver.*;
import gccc.*;
import static gccc.HTMLUtil.*;

public class Home extends AbstractHandler {

	public HTML get(HttpExchange sess) {
		return page(
			tag("p",
				escape("Hello, world!")
			)
		);
	}

}