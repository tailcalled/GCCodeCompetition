package gccc.handlers;

import static gccc.HTMLUtil.$;
import static gccc.HTMLUtil.attrs;
import static gccc.HTMLUtil.escape;
import static gccc.HTMLUtil.tag;
import gccc.Competition;
import gccc.HTMLUtil.HTML;

import java.util.Map;

public class AttemptSubmit extends Submission {

	public AttemptSubmit(Competition competition) {
		super(competition);
	}

	public HTML post(Session sess) throws Throwable {
		Map<String, String> params = sess.getParams();
		taskName=params.get("problem");
		return super.post(sess);
	}

	@Override
	public HTML get(Session sess) throws Throwable {
		String url="attempts";
		String t=taskName;
		if (t!=null && !t.isEmpty())
			url+="?task="+t;
		return 
			tag("html", 
				tag("head", 
					tag("meta",
						attrs($("http-equiv", "refresh"), $("content", "3;URL="+url))
					)
				),
				tag("p",
					escape("Your file was submitted")
				)
			);
	}

	private String taskName="";
}
