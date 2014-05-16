package gccc;

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

	private final Attempt attempt;
	private boolean success;
	private long durationms;
	private String output="";
}
