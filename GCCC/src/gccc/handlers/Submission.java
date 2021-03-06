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
		Map<String, Object> params = sess.getParams();
		if (params.containsKey("problem") && params.containsKey("upload")) {
			Task problem = competition.getTask(params.get("problem").toString()).get();
			Collection<Attempt> attempts = competition.getAttempts(Arrays.asList(sess.getUser()), Arrays.asList(problem));
			// WARNING: potential attack vector; filename might contain '../''es
			File dir = new File(competition.getFolder(), problem.getName() + "/" + sess.getUser().getInternalName() + "/"+CompetitionFileHandler.attemptFolderPrefix + attempts.size());
			dir.mkdirs();
			File file = new File(dir, params.get("upload__filename").toString());
			file.delete();
			try (FileOutputStream out = new FileOutputStream(file)) {
				out.write((byte[])params.get("upload"));
			}
			competition.submitAttempt(new Attempt(sess.getUser(), file, problem, attempts.size()));
		}
		return get(sess);
	}
	
	@Override
	public HTML get(Session sess) throws Throwable {
		Map<String, Object> params = sess.getParams();
		if (!params.containsKey("problem")) {
			return null;
		}
		Task problem = competition.getTask(params.get("problem").toString()).get();
		Collection<Attempt> attempts = competition.getAttempts(Arrays.asList(sess.getUser()), Arrays.asList(problem));
		int nAttempt;
		if (params.containsKey("attempt"))
			nAttempt = Integer.parseInt(params.get("attempt").toString());
		else
			nAttempt = attempts.size() - 1;
		return page(
			tag("h1", escape("Attempts at " + problem.getDisplayName())),
			code(() -> {
				if (nAttempt >= 0) {
					Attempt attempt = competition.getAttempt(sess.getUser(), problem, nAttempt);
					Optional<AttemptResult> result = attempt.getResult();
					HTML resultInfo;
					switch (attempt.getState()) {
						case Waiting:
						case Executing:
							return tag("p", escape("Attempt has not been run yet."));
						case Failed:
							String text="Your program has failed this task.";
							text+=result.map((r)->" The error was: "+r.getErrorMessage()).orElse("");
							resultInfo = tag("p", escape(text));
							break;
						case Completed:
							resultInfo = tag("p", escape("Your program has completed this task!"));
							break;
						default: throw new RuntimeException("should not happen");
					}
					return elements(
						resultInfo,
						tag("pre", escape(result.map((r)->r.getOutput()).orElse("")))
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
		Optional<AttemptResult> result = attempt.getResult();
		String status=result.map((r)->r.isSuccess() ? "success!" : "failure").orElse("waiting...");
		return tag("a", attrs($("href", "/submission?problem=" + attempt.getTask().getName() + "&attempt=" + attempt.getAttemptNum())),
			escape("Attempt #" + attempt.getAttemptNum() + ": " + status)
		);
	}

}