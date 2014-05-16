package gccc.handlers;

import java.util.*;
import gccc.*;
import static gccc.HTMLUtil.*;

public class Home extends AbstractHandler {

	public Home(Competition competition) {
		super(competition);
	}

	public HTML post(Session sess) {
		Map<String, String> params = sess.getParams();
		if (params.containsKey("newUsername")) {
			sess.getUser().setName(params.get("newUsername"));
		}
		return get(sess);
	}
	public HTML get(Session sess) {
		return page(
			tag("p",
				escape("Hello, " + sess.getUser().getName() + "! Welcome to the competition.")
			),
			tag("form", attrs($("action", "/"), $("method", "post")),
				escape("Change name:"), tag("input", attrs($("type", "text"), $("name", "newUsername"))),
				tag("input", attrs($("type", "submit")))
			)
		);
	}

}