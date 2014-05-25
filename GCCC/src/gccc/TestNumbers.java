package gccc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestNumbers extends Test {

	public TestNumbers(String input, String output, int number) {
		super(number);
		this.input = input;
		List<Long> values=new ArrayList<>();
		try (Scanner scanner = new Scanner(output)) {
			while (scanner.hasNextLong())
				values.add(scanner.nextLong());
		}
		long[] r=new long[values.size()];
		for (int i=0; i<r.length; i++)
			r[i]=values.get(i);
		result=r;
	}
	
	@Override
	public String getInput() {
		return input;
	}

	@Override
	public void verifyOutput(String output) throws Exception {
		checkLongs(output, result);
	}

	private final String input;
	private final long[] result;
}
