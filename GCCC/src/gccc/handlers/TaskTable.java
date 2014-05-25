package gccc.handlers;

import static gccc.HTMLUtil.$;
import static gccc.HTMLUtil.attrs;
import static gccc.HTMLUtil.escape;
import static gccc.HTMLUtil.page;
import static gccc.HTMLUtil.tag;
import static java.util.stream.Collectors.toList;
import gccc.Attempt;
import gccc.Competition;
import gccc.HTMLUtil.HTML;
import gccc.Task;
import gccc.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TaskTable extends HTMLHandler {

	public TaskTable(Competition competition) {
		super(competition);
	}

	@Override
	public HTML get(Session session) throws Throwable {
		Map<String, String> params = session.getParams();
		List<User> user=competition.getUserByName(params.get("user")).map((u)->Arrays.asList(u)).orElse(Collections.emptyList());
		List<Task> tasks=new ArrayList<>(competition.getTasks());
		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return page(
			AttemptTable.getPageTop(session),
			tag("table", 
				attrs($("border", "1")),
				tag("h2", escape("Tasks:")),
				tag("tr", 
					tag("th", escape("Name")),
					tag("th", escape("Description")),
					tag("th", escape("Attempts")),
					tag("th", escape("Upload"))
				),
				tasks.stream().map((u) -> {
					return render(u, user);
				}
			).collect(toList()))
		);
	}
	
	private HTML render(Task task, List<User> users) {
		List<Attempt> attempts = competition.getAttempts(users, Arrays.asList(task));
		String userLink=users.isEmpty() ? "" : "&user="+users.get(0).getName();
		return tag("tr", 
				tag("td", escape(task.getName())),
				tag("td", escape("")),
				tag("td", escape(Integer.toString(attempts.size()))),
				tag("td", 
					tag("a", attrs($("href", "attempts?task="+task.getName()+userLink)),
						escape("Upload")
					)
				)
			);
	}
}
