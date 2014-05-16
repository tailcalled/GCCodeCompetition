package gccc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Executor {

	public Executor(AttemptQueue queue) {
		this.queue = queue;
		thread.start();
	}

	private Thread thread=new Thread() {
		public void run() {
			while(true) {
				try {
					Attempt attempt = queue.getWaitingAttempt();
					runAttempt(attempt);
				} 
				catch (InterruptedException error) {
					break;
				}
				catch (Throwable error) {
					// Should never happen
					error.printStackTrace();
				}
			}
		}
	};

	private void runAttempt(Attempt attempt) {
		long start=System.currentTimeMillis();
		AttemptResult result = new AttemptResult(attempt);
		try {
			String fileName=attempt.getFile().getName();
			if (fileName.endsWith(".java"))
				result.setOutput(runJava(attempt.getFile(), attempt.getTask().getMaxTimems()));
			else if (fileName.endsWith(".exe"))
				result.setOutput(runExe(attempt.getFile(), attempt.getTask().getMaxTimems()));
			else if (fileName.endsWith(".class"))
				result.setOutput(runClass(attempt.getFile(), attempt.getTask().getMaxTimems()));
			else
				throw new Exception("Unknown file type: "+fileName);
			result.setSuccess(true);
		}
		catch (InterruptedException error) {
			return;
		}
		catch (Throwable error) {
			result.setSuccess(false);
			for (Throwable e=error; e!=null; e=e.getCause()) {
				if (e instanceof ExecutionException) {
					result.setOutput(((ExecutionException)e).getOutput());
					break;
				}
			}
		}
		result.setDurationms(System.currentTimeMillis()-start);
		attempt.setResult(result);
	}

	private String runJava(File file, long timeoutms) throws Exception {
		try {
			run(new String[] { "javac.exe", file.getName() }, file.getParentFile(), 100000);
		}
		catch (InterruptedException error) {
			throw error;
		}
		catch (Throwable error) {
			throw new Exception("Could not compile "+file.getAbsolutePath(), error);
		}
		String path=file.getAbsolutePath();
		int n=path.lastIndexOf('.');
		String classFile=path.substring(0, n)+".class";
		return runClass(new File(classFile), timeoutms);
	}

	private String runClass(File file, long timeoutms) throws Exception {
		try {
			String name = file.getName();
			int n=name.lastIndexOf(".");
			if (n>=0)
				name=name.substring(0, n);
			return run(new String[] { "java.exe", name }, file.getParentFile(), timeoutms);
		}
		catch (InterruptedException error) {
			throw error;
		}
		catch (Throwable error) {
			throw new Exception("Could not run "+file.getAbsolutePath(), error);
		}
	}

	private String runExe(File file, long timeoutms) throws Exception {
		try {
			return run(new String[] { file.getName() }, file.getParentFile(), timeoutms);
		}
		catch (InterruptedException error) {
			throw error;
		}
		catch (Throwable error) {
			throw new Exception("Could not run "+file.getAbsolutePath(), error);
		}
	}

	@SuppressWarnings("serial")
	private class ExecutionException extends Exception {

		public ExecutionException(String command, int exitCode, String output) {
			this.command = command;
			this.exitCode = exitCode;
			this.output = output;
		}

		public String toString() {
			return "Process terminated with error code "+exitCode+" Output was:\n"+output;			
		}
		
		public String getOutput() {
			return output;
		}

		private final String command;
		private final int exitCode;
		private final String output;
	}
	
	private String run(String[] command, File dir, long timeoutms) throws Exception {
		Process process = Runtime.getRuntime().exec(command, null, dir);
		BufferedReader reader =	new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder output=new StringBuilder();
		while (true) {
			String line = reader.readLine();
			if (line==null)
				break;
			output.append(line).append("\n");
		}
		int exitCode = process.waitFor();
		if (exitCode!=0)
			throw new ExecutionException(command[0], exitCode, output.toString());
		return output.toString();
	}
	
	private final AttemptQueue queue;
}
