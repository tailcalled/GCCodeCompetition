package gccc;

import java.util.ArrayList;
import java.util.List;

public class AttemptResult {

	public AttemptResult(Attempt attempt) {
		this.attempt = attempt;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public long getDurationms() {
		long total=0;
		for (TestResult r: testResults)
			total+=r.getDurationms();
		return total;
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

	public List<TestResult> getTestResults() {
		return testResults;
	}

	public void addTestResult(TestResult testResult) {
		this.testResults.add(testResult);
		if (!testResult.isSuccess()) {
			success=false;
			errorMessage=testResult.getErrorMessage();
		}
	}

	private final Attempt attempt;
	private boolean success;
	private String output="";
	private String errorMessage="";
	private final List<TestResult> testResults=new ArrayList<>();
}
