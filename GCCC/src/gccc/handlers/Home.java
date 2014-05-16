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
		List<HTML> taskOpts = new ArrayList<HTML>();
		for (Task t: competition.getTasks()) {
			taskOpts.add(tag("option", attrs($("value", t.getName())), escape(t.getDisplayName())));
		}
		return page(
			tag("p",
				escape("Hello, " + sess.getUser().getName() + "! Welcome to the competition.")
			),
			tag("form", attrs($("action", "/"), $("method", "post")),
				escape("Change name:"), tag("input", attrs($("type", "text"), $("name", "newUsername"))),
				tag("input", attrs($("type", "submit")))
			),
			tag("form", attrs($("action", "/submit"), $("method", "post"), $("enctype", "multipart/form-data")),
				escape("Submit attempt."), tag("br"),
				escape("Problem: "), tag("select", attrs($("name", "problem")), taskOpts), tag("br"),
				escape("Language: "), escape("Only Java for now."), tag("br"),
				escape("File: "), tag("input", attrs($("name", "upload"), $("type", "file"))), tag("br"),
				tag("input", attrs($("type", "submit")))
			)
		);
	}

}