package gccc.handlers;

import static gccc.HTMLUtil.*;
import static java.util.stream.Collectors.toList;
import gccc.*;

import java.util.*;

public class TaskTable extends HTMLHandler {

	public TaskTable(Competition competition) {
		super(competition);
	}

	@Override
	public HTML post(Session session) throws Throwable {
		Map<String, String> params = session.getParams();
		if (params.containsKey("newUsername")) {
			session.getUser().setName(params.get("newUsername"));
		}
		return get(session);
	}

	@Override
	public HTML get(Session session) throws Throwable {
		Map<String, String> params = session.getParams();
		List<User> user=Optional.ofNullable(params.get("user")).map((u)->Arrays.asList(competition.getUserByAddress(Tools.readIP(u)))).orElse(Collections.emptyList());
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
		Collection<Attempt> attempts = competition.getAttempts(users, Arrays.asList(task));
		String userLink=users.isEmpty() ? "" : "&user="+users.get(0).getInternalName();
		return tag("tr", 
				tag("td", escape(task.getDisplayName())),
				tag("td", 
					tag("a", 
						attrs($("href", "/doc/" + task.getName() + "?problem=" + task.getName())), 
						escape("Description")
					)
				),
				tag("td", escape(Integer.toString(attempts.size()))),
				tag("td", 
					tag("a", attrs($("href", "attempts?task="+task.getName()+userLink)),
						escape("Upload")
					)
				)
			);
	}
}
