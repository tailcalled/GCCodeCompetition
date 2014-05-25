package gccc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TaskFileHandler {

	public static List<Task> getTasks(File folder) throws InterruptedException {
		List<Task> tasks=new ArrayList<>();
		for (File child: folder.listFiles()) {
			if (!child.isDirectory())
				continue;
			Task task=getTask(child);
			if (task!=null)
				tasks.add(task);
		}
		return tasks;
	}
	
	public static Task getTask(File folder) throws InterruptedException {
		try {
			File infoFile=new File(folder, "task.inf");
			if (!infoFile.exists()) {
				System.out.println("Cannot find task.inf in "+folder.getAbsolutePath());
				return null;
			}
			Properties properties = new Properties();
			try (InputStream input=new FileInputStream(infoFile)) {
				properties.load(input);
				String name=folder.getName();
				String displayName=properties.getProperty("displayname", name);
				long maxtimems=Tools.getLong(properties, "maxtimems", 1000);
				List<Test> tests=new ArrayList<>();
				for (int i=1; ; i++) {
					File testInput=new File(folder, "input"+i+".txt");
					File testOutput=new File(folder, "output"+i+".txt");
					if (!testInput.exists() || !testOutput.exists())
						break;
					try {
						tests.add(new TestNumbers(Tools.readFile(testInput), Tools.readFile(testOutput)));
					}
					catch (Throwable error) {
						Tools.checkError(error);
						System.out.println("Cannot read test "+i+" for task "+name);
						error.printStackTrace();
					}
				}
				return new Task(name, displayName, (int)maxtimems, tests);
			}
		}
		catch (Throwable error) {
			Tools.checkError(error);
			System.out.println("Cannot read task from folder "+folder.getAbsolutePath());
			error.printStackTrace();
			return null;
		}
	}
	
	
}
