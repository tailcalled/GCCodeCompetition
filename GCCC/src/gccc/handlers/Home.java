package gccc.handlers;

import static gccc.HTMLUtil.$;
import static gccc.HTMLUtil.attrs;
import static gccc.HTMLUtil.escape;
import static gccc.HTMLUtil.page;
import static gccc.HTMLUtil.tag;
import static java.util.stream.Collectors.toList;
import gccc.Competition;
import gccc.HTMLUtil.HTML;

import java.util.Map;

public class Home extends HTMLHandler {

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
			),
			tag("ll",
				competition.getTasks().stream().map(t ->
					tag("li", tag("a", attrs($("href", "/task?problem=" + t.getName())), escape(t.getDisplayName())))
				).collect(toList())
			),
			tag("form", attrs($("action", "/submit"), $("method", "post"), $("enctype", "multipart/form-data")),
				escape("Submit attempt."), tag("br"),
				escape("Problem: "), tag("select", attrs($("name", "problem")),
					competition.getTasks().stream().map(t ->
						tag("option", attrs($("value", t.getName())), escape(t.getDisplayName()))
					).collect(toList())
				), tag("br"),
				escape("File: "), tag("input", attrs($("name", "upload"), $("type", "file"))), tag("br"),
				tag("input", attrs($("type", "submit")))
			)
		);
	}

}
