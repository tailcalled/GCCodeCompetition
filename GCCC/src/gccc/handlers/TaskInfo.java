package gccc.handlers;

import java.util.*;
import java.util.function.*;
import gccc.*;
import static gccc.HTMLUtil.*;
import static java.util.stream.Collectors.*;

public class TaskInfo extends AbstractHandler {

	public TaskInfo(Competition competition) {
		super(competition);
	}

	public HTML get(Session sess) {
		Map<String, String> params = sess.getParams();
		if (!params.containsKey("problem")) {
			return null;
		}
		Task problem = competition.getTask(params.get("problem"));
		List<Attempt> attempts = competition.getAttempts(sess.getUser(), problem);
		return page(
			tag("h1", escape(problem.getDisplayName())),
			tag("p",
				escape("Click "), tag("a", attrs($("href", "/")), escape("here")), escape(" to return to the main page.")
			),
			tag("p",
				escape("Click "), tag("a", attrs($("href", "/doc?problem=" + problem.getName())), escape("here")),
				escape(" to download the documentation.")
			),
			tag("form", attrs($("action", "/submit?problem=" + problem.getName()), $("method", "post"), $("enctype", "multipart/form-data")),
				escape("Submit attempt."), tag("br"),
				escape("Language: "), escape("Only Java for now."), tag("br"),
				escape("File: "), tag("input", attrs($("name", "upload"), $("type", "file"))), tag("br"),
				tag("input", attrs($("type", "submit")))
			),
			code(() -> {
				if (attempts.size() == 0) {
					return tag("p", escape("You have not submitted anything to this task yet."));
				}
				else {
					return tag("ll", attempts.stream().map((attempt) ->
						tag("li", Submission.render(attempt))
					).collect(toList()));
				}
			})
		);
	}

}