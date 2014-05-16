package gccc;

import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class Test {

	public abstract String getInput();
	public abstract void verifyOutput(String output) throws Exception;
	
	@SuppressWarnings("serial")
	public class TestException extends Exception {
		public TestException(String message) {
			super(message);
		}
	}
	
	protected void checkInts(String output, int[] result) throws Exception {
		try (Scanner scanner = new Scanner(output)) {
			for (int i=0; i<result.length; i++) {
				try {
					int n = scanner.nextInt();
					if (n!=result[i])
						throw new TestException("Value #"+(i+1)+" is wrong: Expected: "+result[i]+" found: "+n);
				}
				catch (NoSuchElementException error) {
					throw new TestException("Too few values. Expected: "+result.length+" found: "+i);
				}
			}
		}
	}
	
}
