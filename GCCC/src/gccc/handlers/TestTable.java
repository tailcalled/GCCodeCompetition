package gccc.handlers;

import java.util.*;

import static gccc.HTMLUtil.*;
import static java.util.stream.Collectors.toList;
import gccc.*;

public class TestTable extends HTMLHandler {

	public TestTable(Competition competition) {
		super(competition);
	}
	
	@Override
	public HTML get(Session sess) throws Throwable {
		Map<String, Object> params = sess.getParams();
		List<User> user=Optional.ofNullable(params.get("user").toString()).map((u)->Arrays.asList(competition.getUserByAddress(Tools.readIP(u)))).orElse(Collections.emptyList());
		List<Task> task=competition.getTask(params.get("task").toString()).map((u)->Arrays.asList(u)).orElse(Collections.emptyList());
		long index=Tools.getLong(params.get("index"), 1);
		Attempt attempt=null;
		for (Attempt a: competition.getAttempts(user, task)) {
			if (a.getAttemptNum()==index) {
				attempt=a;
			}
		}
		return page(
			AttemptTable.getPageTop(sess),
			tag("H2", escape("Attempt tests:")),
			tag("table", 
				attrs($("border", "1")),
				tag("tr", 
					tag("th", escape("Number")),
					tag("th", escape("Status")),
					tag("th", escape("Duration")),
					tag("th", escape("Message")),
					tag("th", escape("Input")),
					tag("th", escape("Output"))
				),
				attempt.getResult().isPresent() ?
					attempt.getResult().get().getTestResults().isEmpty() ?
						render(attempt.getResult().get()) :
						attempt.getResult().get().getTestResults().stream().map((r) -> {
							return render(r);
						}).collect(toList())
					: tag("div")
			),
			task.isEmpty() ?
					tag("div")
					: tag("a", attrs($("href", "attempts?task="+task.get(0).getName())),
						escape("Submit")
					)
		);
	}
	
	public static HTML render(TestResult result) {
		String link="/file?task="+result.getAttempt().getTask().getName()+"&user="+result.getAttempt().getUser().getInternalName()+"&index="+result.getAttempt().getAttemptNum()+"&test="+result.getTest().getNumber();
		return tag("tr", 
			tag("td", escape(Integer.toString(result.getTest().getNumber()))),
			tag("td", result.isSuccess() ? AttemptTable.success : AttemptTable.failure),
			tag("td", escape(String.format("%.3f",  result.getDurationms()/1000.0))),
			tag("td", tag("pre", escape(result.getErrorMessage()))),
			tag("td", 
				tag("a", 
					attrs($("href", link+"&file=input")), 
					escape("Input")
				)
			),
			tag("td", 
				tag("a", 
					attrs($("href", link+"&file=output")), 
					escape("Output")
				)
			)
		);
	}
	
	public static HTML render(AttemptResult result) {
		return tag("tr", 
			tag("td", escape("")),
			tag("td", result.isSuccess() ? AttemptTable.success : AttemptTable.failure),
			tag("td", escape(String.format("%.3f",  result.getDurationms()/1000.0))),
			tag("td", tag("pre", escape(result.getErrorMessage()))),
			tag("td", escape("")),
			tag("td", tag("pre", escape(result.getOutput())))
		);
	}
	
}
