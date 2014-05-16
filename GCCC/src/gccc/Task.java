package gccc;

public class Task {

	public Task(String name, int maxTimems) {
		this.name = name;
		this.maxTimems = maxTimems;
	}

	public int getMaxTimems() {
		return maxTimems;
	}

	public String getName() {
		return name;
	}

	private final String name;
	private final int maxTimems;

}
