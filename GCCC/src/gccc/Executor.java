package gccc;

import gccc.Test.TestException;
import gccc.ThreadPool.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Executor implements AutoCloseable {

	public Executor(AttemptQueue queue, ThreadPool threadPool) {
		this.queue = queue;
		this.threadPool = threadPool;
		javaPath="C:/Program Files/Java/jdk1.8.0_05/bin/";
		if (!new File(javaPath+"javac").exists())
			javaPath="";
		task = threadPool.execute(pollTask);
	}

	@Override
	public void close() throws Exception {
		task.interrupt();
	}

	private Runnable pollTask=new Runnable() {
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

	@SuppressWarnings("serial")
	public class CompilationError extends Exception {
		public CompilationError(String message, Throwable cause) {
			super(message, cause);
		}
	}
	
	public void runAttempt(Attempt attempt) {
		AttemptResult result = new AttemptResult(attempt);
		try {
			File file=attempt.getFile();
			if (file.getName().endsWith(".java")) {
				try {
					run(new String[] { javaPath+"javac", file.getName() }, file.getParentFile(), "", 100000);
					System.out.println("Compiling of "+file.getAbsolutePath()+" succeeded");
				}
				catch (InterruptedException error) {
					throw error;
				}
				catch (Throwable error) {
					result.setErrorMessage("Cannot compile");
					throw new CompilationError("Could not compile "+file.getAbsolutePath(), error);
				}
				String path=file.getAbsolutePath();
				int n=path.lastIndexOf('.');
				file=new File(path.substring(0, n)+".class");
			}
			else if (file.getName().endsWith(".cpp") || file.getName().endsWith(".c") || file.getName().endsWith(".cs")) {
				try {
					run(new String[] { "cl", file.getName() }, file.getParentFile(), "", 100000);
					System.out.println("Compiling of "+file.getAbsolutePath()+" succeeded");
				}
				catch (InterruptedException error) {
					throw error;
				}
				catch (Throwable error) {
					result.setErrorMessage("Cannot compile");
					throw new CompilationError("Could not compile "+file.getAbsolutePath(), error);
				}
				String path=file.getAbsolutePath();
				int n=path.lastIndexOf('.');
				file=new File(path.substring(0, n)+".exe");
			}
			String fileName=file.getName();
			String[] command;
			if (fileName.endsWith(".class")) {
				int n=fileName.lastIndexOf(".");
				String name=n>=0 ? fileName.substring(0, n) : fileName;
				command=new String[] { javaPath+"java", name };
			}
			else if (fileName.endsWith(".exe"))
				command=new String[] { fileName };
			else if (fileName.endsWith(".jar"))
				command=new String[] { javaPath+"java", "-jar", fileName };
			else if (fileName.endsWith(".py"))
				command=new String[] { "python", fileName };
			else
				throw new Exception("Unknown file type: "+fileName);
			int tn=1;
			for (Test test: attempt.getTask().getTests()) {
				System.out.println("Test "+tn+" of "+fileName+" starts");
				TestResult testResult = new TestResult(attempt, test);
				long start=System.currentTimeMillis();
				try {
					String output=run(command, file.getParentFile(), test.getInput(), attempt.getTask().getMaxTimems());
					testResult.setOutput(output);
					test.verifyOutput(output);
				}
				catch (InterruptedException error) {
					throw error;
				}
				catch (Throwable error) {
					System.out.println("Test "+tn+" of "+fileName+" fails");
					error.printStackTrace();
					testResult.setSuccess(false);
					testResult.setErrorMessage(error.toString());
					for (Throwable e=error; e!=null; e=e.getCause()) {
						if (e instanceof ExecutionException) {
							ExecutionException ee=(ExecutionException)e;
							testResult.setErrorMessage(ee.getMessage());
							testResult.setOutput(ee.getOutput());
						}
						else if (e instanceof TestException) {
							testResult.setErrorMessage(e.getMessage());
						}
					}
				}
				long duration=System.currentTimeMillis()-start;
				testResult.setDurationms(duration);
				result.addTestResult(testResult);
				System.out.println("Test "+tn+" of "+fileName+" completes. Duration was "+duration+" ms");
				tn++;
			}
			result.setSuccess(true);
		}
		catch (InterruptedException error) {
			return;
		}
		catch (Throwable error) {
			error.printStackTrace();
			result.setSuccess(false);
			result.setErrorMessage(error.toString());
			for (Throwable e=error; e!=null; e=e.getCause()) {
				if (e instanceof ExecutionException) {
					ExecutionException ee=(ExecutionException)e;
					result.setErrorMessage(ee.getMessage());
					result.setOutput(ee.getOutput());
				}
				else if (e instanceof CompilationError) {
					result.setErrorMessage(e.getMessage());
				}
			}
		}
		attempt.setResult(result);
	}

	@SuppressWarnings("serial")
	private class ExecutionException extends Exception {

		public ExecutionException(String command, int exitCode, boolean timeout, String output) {
			this.command = command;
			this.exitCode = exitCode;
			this.timeout = timeout;
			this.output = output;
		}

		public String getMessage() {
			return toString();
		}
		
		public String toString() {
			return "Process terminated "+(timeout ? "due to timeout" : "with error code "+exitCode)+" Output was:\n"+output;			
		}
		
		public String getOutput() {
			return output;
		}

		private final String command;
		private final int exitCode;
		private final String output;
		private boolean timeout;
	}
	
	private String run(String[] command, File dir, String input, final long timeoutms) throws Exception {
		String commandLine=new File(command[0]).getName();
		for (int i=1; i<command.length; i++)
			commandLine+=" "+command[i];
		System.out.println("Running "+commandLine);
		final Process process = Runtime.getRuntime().exec(command, null, dir);
		try {
			final boolean[] isTimeout=new boolean[1];
			Task timeoutTask = threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(timeoutms);
						isTimeout[0]=true;
						process.destroy();
					} 
					catch (InterruptedException e) {
					}
				}
			});
			if (!input.isEmpty()) {
				try (Writer w=new OutputStreamWriter(process.getOutputStream());
					 Writer bw=new BufferedWriter(w)) {
					bw.write(input);
				}
			}
			String output=Tools.readInputStream(process.getInputStream())+Tools.readInputStream(process.getErrorStream());
			int exitCode = process.waitFor();
			timeoutTask.interrupt();
			System.out.println(commandLine+" completed with exit code "+exitCode);
			if (exitCode!=0)
				throw new ExecutionException(command[0], exitCode, isTimeout[0], output);
			return output;
		}
		finally {
			process.destroy();
		}
	}
	
	private final AttemptQueue queue;
	public String javaPath="";
	private ThreadPool threadPool;
	private final ThreadPool.Task task;
}
