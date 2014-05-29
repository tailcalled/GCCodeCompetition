package gccc;

import java.util.List;

public class Task {

	public Task(String name, String displayName, int maxTimems, List<Test> tests) {
		this.name = name;
		this.displayName = displayName;
		this.maxTimems = maxTimems;
		this.tests = tests;
	}

	public int getMaxTimems() {
		return maxTimems;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public String getName() {
		return name;
	}

	public List<Test> getTests() {
		return tests;
	}
	
	private final String name;
	private final int maxTimems;
	private final List<Test> tests;
	private final String displayName;

}
