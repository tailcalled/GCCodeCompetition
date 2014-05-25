package gccc;

public class TestResult {

	public TestResult(Attempt attempt, Test test) {
		this.test = test;
		this.attempt = attempt;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public long getDurationms() {
		return durationms;
	}

	public void setDurationms(long durationms) {
		this.durationms = durationms;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Test getTest() {
		return test;
	}

	private final Attempt attempt;
	private boolean success=true;
	private long durationms=0;
	private String output="";
	private String errorMessage="";
	private Test test;
}
