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

import java.util.*;

public class UserTable extends HTMLHandler {

	public UserTable(Competition competition) {
		super(competition);
	}

	@Override
	public HTML get(Session session) throws Throwable {
		Map<String, Object> params = session.getParams();
		List<Task> task=competition.getTask((String)params.get("task")).map((u)->Arrays.asList(u)).orElse(Collections.emptyList());
		List<User> users=new ArrayList<>(competition.getUsers());
		Collections.sort(users, new Comparator<User>() {
			@Override
			public int compare(User o1, User o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return page(
			AttemptTable.getPageTop(session),
			tag("h2", escape("Users:")),
			tag("table", 
				attrs($("border", "1")),
				tag("tr", 
					tag("th", escape("Name")),
					tag("th", escape("IP-address")),
					tag("th", escape("Attempts")),
					tag("th", escape("Show attempts"))
				),
				users.stream().map((u) -> {
					return render(u, task);
				}
			).collect(toList()))
		);
	}
	
	private HTML render(User user, List<Task> tasks) {
		Collection<Attempt> attempts = competition.getAttempts(Arrays.asList(user), Collections.emptyList());
		String taskLink=tasks.isEmpty() ? "" : "&task="+tasks.get(0).getName();
		return tag("tr", 
				tag("td", escape(user.getName())),
				tag("td", escape(user.getAddress().toString())),
				tag("td", escape(Integer.toString(attempts.size()))),
				tag("td", 
					tag("a", attrs($("href", "attempts?user="+user.getInternalName()+taskLink)),
						escape("Show")
					)
				)
			);
	}
}
