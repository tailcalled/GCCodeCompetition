package gccc.handlers;

import gccc.Attempt;
import gccc.Competition;
import gccc.Task;
import gccc.TestResult;
import gccc.Tools;
import gccc.User;
import gccc.HTMLUtil.HTML;
import gccc.handlers.HTMLHandler.Session;
import static gccc.HTMLUtil.$;
import static gccc.HTMLUtil.attrs;
import static gccc.HTMLUtil.escape;
import static gccc.HTMLUtil.page;
import static gccc.HTMLUtil.tag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileHandler extends HTMLHandler {

	public FileHandler(Competition competition) {
		super(competition);
	}
	
	@Override
	public HTML get(Session sess) throws Throwable {
		Map<String, String> params = sess.getParams();
		List<User> user=competition.getUserByName(params.get("user")).map((u)->Arrays.asList(u)).orElse(Collections.emptyList());
		List<Task> task=competition.getTask(params.get("task")).map((u)->Arrays.asList(u)).orElse(Collections.emptyList());
		long index=Tools.getLong(params.get("index"), 1);
		long test=Tools.getLong(params.get("test"), 1);
		String type=params.get("file");
		TestResult result=null;
		loop:
		for (Attempt a: competition.getAttempts(user, task)) {
			if (a.getAttemptNum()==index) {
				if (!a.getResult().isPresent())
					break;
				for (TestResult r: a.getResult().get().getTestResults())
					if (r.getTest().getNumber()==test) {
						result=r;
						break loop;
					}
			}
		}
		String file=type.equals("input") ? result.getTest().getInput() : result.getOutput();
		return page(tag("pre", escape(file)));
	}

}
