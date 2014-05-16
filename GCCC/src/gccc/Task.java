package gccc;

import java.util.List;

public class Task {

	public Task(String name, int maxTimems, List<Test> tests) {
		this.name = name;
		this.maxTimems = maxTimems;
		this.tests = tests;
	}

	public int getMaxTimems() {
		return maxTimems;
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

}
