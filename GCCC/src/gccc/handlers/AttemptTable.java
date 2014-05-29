package gccc.handlers;

import static gccc.HTMLUtil.*;
import static java.util.stream.Collectors.toList;
import gccc.*;

import java.util.*;

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
		Map<String, Object> params = sess.getParams();
		List<User> user=Optional.ofNullable((String)params.get("user")).map((u)->Arrays.asList(competition.getUserByAddress(Tools.readIP(u)))).orElse(Collections.emptyList());
		List<Task> task=competition.getTask(params.get("task").toString()).map((u)->Arrays.asList(u)).orElse(Collections.emptyList());
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
		Collection<Attempt> attempts = competition.getAttempts(user, task);
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
					tag("th", escape("Duration")),
					tag("th", escape("Details"))
				),
				attempts.stream().map((attempt) -> {
					return render(attempt);
				}
			).collect(toList()))
		);
	}

	public static final HTML success=tag("font", attrs($("color", "green")), escape("\u2713"));
	public static final HTML failure=tag("font", attrs($("color", "red")), escape("\u00F7"));
	
	public static HTML render(Attempt attempt) {
		Optional<AttemptResult> result = attempt.getResult();
		HTML status=escape("waiting...");
		if (result.isPresent()) {
			AttemptResult ar=result.get();
			List<TestResult> rs=ar.getTestResults();
			if (rs.isEmpty())
				status=ar.isSuccess() ? success : failure;
			else {
				List<HTML> s=new ArrayList<>();
				for (TestResult t: rs) {
					s.add(t.isSuccess() ? success : failure);
				}
				status=tag("div", s);
			}
		}
		return tag("tr", 
			tag("td", escape(attempt.getUser().getName())),
			tag("td", escape(attempt.getTask().getDisplayName())),
			tag("td", escape(attempt.getCreated().toString())),
			tag("td", status),
			tag("td", 
				escape(
					result.map(
						(r)->String.format("%.3f", r.getDurationms()/1000.0)
					).orElse("")
				)
			),
			tag("td", 
				tag("a", attrs($("href", "tests?task="+attempt.getTask().getName()+"&user="+attempt.getUser().getInternalName()+"&index="+attempt.getAttemptNum())),
					escape("Details")
				)
			)
		);
	}

}
