package gccc.handlers;

import static gccc.HTMLUtil.$;
import static gccc.HTMLUtil.attrs;
import static gccc.HTMLUtil.escape;
import static gccc.HTMLUtil.page;
import static gccc.HTMLUtil.tag;
import static java.util.stream.Collectors.toList;
import gccc.Attempt;
import gccc.AttemptResult;
import gccc.Competition;
import gccc.HTMLUtil.HTML;
import gccc.Task;
import gccc.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AttemptTable extends HTMLHandler {

	public AttemptTable(Competition competition) {
		super(competition);
	}

	static HTML getPageTop(Session session) {
		return 
			tag("div", 
				tag("p",
					escape("Hello, " + session.getUser().getName() + "! Welcome to the competition.")
				),
				tag("form", attrs($("action", "/"), $("method", "post")),
					escape("Change name:"), tag("input", attrs($("type", "text"), $("name", "newUsername"))),
					tag("input", attrs($("type", "submit")))
				),
				tag("p", 
					tag("a", attrs($("href", "users")),
						escape("Users")
					),
					escape(" "),
					tag("a", attrs($("href", "tasks")),
							escape("Tasks")
					),
					escape(" "),
					tag("a", attrs($("href", "attempts")),
						escape("Attempts")
					)
				)
			);
	}
	
	@Override
	public HTML get(Session sess) throws Throwable {
		Map<String, String> params = sess.getParams();
		List<User> user=competition.getUserByName(params.get("user")).map((u)->Arrays.asList(u)).orElse(Collections.emptyList());
		List<Task> task=competition.getTask(params.get("task")).map((u)->Arrays.asList(u)).orElse(Collections.emptyList());
		String header="Attempts";
		HTML submit=tag("div");
		if (!user.isEmpty())
			header+=", user "+user.get(0).getName();
		if (!task.isEmpty()) {
			header+=", task "+task.get(0).getName();
			submit=tag("form", attrs($("action", "/attemptsubmit?problem=" + task.get(0).getName()), $("method", "post"), $("enctype", "multipart/form-data")),
				tag("h2", escape("Submit attempt.")),
				escape("File: "), tag("input", attrs($("name", "upload"), $("type", "file"))), tag("br"),
				tag("input", attrs($("type", "submit")))
			);
		}			
		List<Attempt> attempts = competition.getAttempts(user, task);
		return page(
			getPageTop(sess),
			submit,
			tag("H2", escape(header+":")),
			tag("table", 
				attrs($("border", "1")),
				tag("tr", 
					tag("th", escape("User")),
					tag("th", escape("Task")),
					tag("th", escape("Submitted")),
					tag("th", escape("Status")),
					tag("th", escape("Duration"))
				),
				attempts.stream().map((attempt) -> {
					return render(attempt);
				}
			).collect(toList()))
		);
	}

	public static HTML render(Attempt attempt) {
		Optional<AttemptResult> result = attempt.getResult();
		return tag("tr", 
			tag("td", escape(attempt.getUser().getName())),
			tag("td", escape(attempt.getTask().getDisplayName())),
			tag("td", escape(attempt.getCreated().toString())),
			tag("td", escape(
					result.map((r)->r.isSuccess() ? "success!" : "failure").orElse("waiting...")
			)),
			tag("td", 
				escape(
					result.map(
						(r)->String.format("%.1f", r.getDurationms()/1000.0)
					).orElse("")
				)
			)
		);
	}

}
