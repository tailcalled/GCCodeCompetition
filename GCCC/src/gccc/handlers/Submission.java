package gccc.handlers;

import java.util.*;
import java.io.*;
import gccc.*;
import static gccc.HTMLUtil.*;

public class Submission extends AbstractHandler {

	private final File parentDir;

	public Submission(Competition competition, File parentDir) {
		super(competition);
		this.parentDir = parentDir;
	}

	public HTML post(Session sess) throws Throwable {
		Map<String, String> params = sess.getParams();
		if (params.containsKey("problem") && params.containsKey("upload")) {
			Task problem = competition.getTask(params.get("problem"));
			List<Attempt> attempts = competition.getAttempts(sess.getUser(), problem);
			// WARNING: potential attack vector; filename might contain '../''es
			File dir = new File(parentDir, problem.getName() + "/" + sess.getUser().getAddress().getHostAddress() + "/attempt" + attempts.size());
			dir.mkdirs();
			File file = new File(dir, params.get("upload__filename"));
			file.delete();
			try (Writer out = new FileWriter(file)) {
				out.write(params.get("upload"));
			}
			competition.submitAttempt(new Attempt(sess.getUser(), file, problem));
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
		int nAttempt = attempts.size() - 1;
		if (params.containsKey("attempt")) {
			nAttempt = Integer.parseInt(params.get("attempt"));
		}
		HTML resultInfo;
		if (nAttempt >= 0) {
			Attempt attempt = attempts.get(nAttempt);
			AttemptResult result = attempt.getResult();
			if (result == null) {
				resultInfo = tag("p", escape("Attempt has not been run yet."));
			}
			else {
				resultInfo = tag("p",
					escape("Attempt has been run."),
						result.isSuccess()?
							escape("Your program has completed this task!")
						 :	escape("Your program has failed this task.")
				);
			}
		}
		else {
			resultInfo = tag("p", escape("You have not selected a program."));
		}
		List<HTML> parts = new ArrayList<HTML>();
		for (int i = 0; i < attempts.size(); i++) {
			AttemptResult result = attempts.get(i).getResult();
			String status;
			if (result == null) status = "waiting...";
			else if (result.isSuccess()) status = "success!";
			else status = "failure";
			parts.add(tag("li",
				tag("a", attrs($("href", "/submission?problem=" + problem.getName() + "&attempt=" + i)), escape("Attempt #" + i + ": " + status))
			));
		}
		if (parts.isEmpty()) {
			parts.add(tag("li",
				escape("(You have not submitted anything to this task.)")
			));
		}
		return page(
			tag("h1", escape("Attempts at " + problem.getDisplayName())),
			resultInfo,
			tag("p",
				escape("Click "), tag("a", attrs($("href", "/")), escape("here")),
				escape(" to return to the main page. "), escape("All attempts by you:")
			),
			tag("ll", parts)
		);
	}

}