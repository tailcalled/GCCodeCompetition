package gccc.handlers;

import static gccc.HTMLUtil.$;
import static gccc.HTMLUtil.attrs;
import static gccc.HTMLUtil.code;
import static gccc.HTMLUtil.escape;
import static gccc.HTMLUtil.page;
import static gccc.HTMLUtil.tag;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import gccc.Attempt;
import gccc.AttemptResult;
import gccc.Competition;
import gccc.HTMLUtil.HTML;

public class AttemptTable extends HTMLHandler {

	public AttemptTable(Competition competition) {
		super(competition);
	}

	@Override
	public HTML get(Session sess) throws Throwable {
		List<Attempt> attempts = competition.getAttempts(null, null);
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
			),
			tag("table", attempts.stream().map((attempt) -> {
				return render(attempt);
			}).collect(toList()))
		);
	}

	public static HTML render(Attempt attempt) {
		AttemptResult result = attempt.getResult();
		String status;
		if (result == null) 
			status = "waiting...";
		else if (result.isSuccess()) 
			status = "success!";
		else 
			status = "failure";
		return tag("tr", 
			tag("td", escape(attempt.getUser().getName())),
			tag("td", escape(attempt.getTask().getDisplayName())),
			tag("td", escape(attempt.getCreated().toString())),
			tag("td", escape(status))
		);
	}

}
