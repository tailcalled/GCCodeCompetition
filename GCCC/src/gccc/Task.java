package gccc;

public class Task {

	private final String name;
	private final String displayName;

	public Task(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}
	public String getDisplayName() {
		return displayName;
	}

}
