package gccc.handlers;

import gccc.*;
import static gccc.HTMLUtil.*;

public class Home extends AbstractHandler {

	public Home(Competition competition) {
		super(competition);
	}

	public HTML get(Session sess) {
		return page(
			tag("p",
				escape("Hello, " + sess.getUser().getName() + "!")
			)
		);
	}

}