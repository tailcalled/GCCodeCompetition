package gccc.handlers;

import java.util.*;
import java.util.function.*;
import java.io.*;
import gccc.*;
import static gccc.HTMLUtil.*;
import static java.util.stream.Collectors.*;

public class Submission extends HTMLHandler {

	public Submission(Competition competition) {
		super(competition);
	}

	public HTML post(Session sess) throws Throwable {
		Map<String, String> params = sess.getParams();
		if (params.containsKey("problem") && params.containsKey("upload")) {
			Task problem = competition.getTask(params.get("problem"));
			List<Attempt> attempts = competition.getAttempts(sess.getUser(), problem);
			// WARNING: potential attack vector; filename might contain '../''es
			File dir = new File(competition.getFolder(), problem.getName() + "/" + sess.getUser().getAddress().getHostAddress().replace(":", ".") + "/attempt" + attempts.size());
			dir.mkdirs();
			File file = new File(dir, params.get("upload__filename"));
			file.delete();
			try (Writer out = new FileWriter(file)) {
				out.write(params.get("upload"));
			}
			competition.submitAttempt(new Attempt(sess.getUser(), file, problem, attempts.size()));
		}
		return get(sess);
	}
	public HTML get(Session sess) {
		Map<String, String> params = sess.getParams();
		if (!params.containsKey("problem")) {
			return null;
		}
		Task problem = competition.getTask(params.get("problem"));
		List<Attempt> attempts = competition.getAttempts(sess.getUser(), problem);
		int nAttempt;
		if (params.containsKey("attempt"))
			nAttempt = Integer.parseInt(params.get("attempt"));
		else
			nAttempt = attempts.size() - 1;
		return page(
			tag("h1", escape("Attempts at " + problem.getDisplayName())),
			code(() -> {
				if (nAttempt >= 0) {
					Attempt attempt = attempts.get(nAttempt);
					AttemptResult result = attempt.getResult();
					HTML resultInfo;
					switch (attempt.getState()) {
						case Waiting:
						case Executing:
							return tag("p", escape("Attempt has not been run yet."));
						case Failed:
							resultInfo = tag("p", escape("Your program has failed this task. The error was: " + result.getErrorMessage()));
							break;
						case Completed:
							resultInfo = tag("p", escape("Your program has completed this task!"));
							break;
						default: throw new RuntimeException("should not happen");
					}
					return elements(
						resultInfo,
						tag("pre", escape(result.getOutput()))
					);
				}
				else {
					return tag("p", escape("You have not selected a program."));
				}
			}),
			tag("p",
				escape("Click "), tag("a", attrs($("href", "/")), escape("here")), escape(" to return to the main page. "),
				escape("Click "), tag("a", attrs($("href", "/task?problem=" + problem.getName())), escape("here")),
				escape(" to go to the task page. All attempts by you:")
			),
			code(() -> {
				HTML attemptsInfo;
				if (attempts.size() > 0) {
					attemptsInfo = tag("ll", attempts.stream().map((attempt) -> {
						return tag("li", render(attempt));
					}).collect(toList()));
				}
				else {
					attemptsInfo = tag("p", escape("(You have not submitted anything to this task.)"));
				}
				return attemptsInfo;
			})
		);
	}

	public static HTML render(Attempt attempt) {
		AttemptResult result = attempt.getResult();
		String status;
		if (result == null) status = "waiting...";
		else if (result.isSuccess()) status = "success!";
		else status = "failure";
		return tag("a", attrs($("href", "/submission?problem=" + attempt.getTask().getName() + "&attempt=" + attempt.getAttemptNum())),
			escape("Attempt #" + attempt.getAttemptNum() + ": " + status)
		);
	}

}