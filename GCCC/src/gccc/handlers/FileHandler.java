package gccc.handlers;

import gccc.*;
import static gccc.HTMLUtil.*;

import java.util.*;

public class FileHandler extends HTMLHandler {

	public FileHandler(Competition competition) {
		super(competition);
	}
	
	@Override
	public HTML get(Session sess) throws Throwable {
		Map<String, Object> params = sess.getParams();
		List<User> user=Optional.ofNullable(params.get("user").toString()).map((u)->Arrays.asList(competition.getUserByAddress(Tools.readIP(u)))).orElse(Collections.emptyList());
		List<Task> task=competition.getTask(params.get("task").toString()).map((u)->Arrays.asList(u)).orElse(Collections.emptyList());
		long index=Tools.getLong(params.get("index").toString(), 1);
		long test=Tools.getLong(params.get("test").toString(), 1);
		String type=params.get("file").toString();
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
