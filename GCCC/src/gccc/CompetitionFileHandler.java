package gccc;

import java.util.*;
import java.io.*;
import java.net.*;
import gccc.io.*;
import static gccc.io.TabbedText.*;
import static java.util.stream.Collectors.toList;

public class CompetitionFileHandler {

	private CompetitionFileHandler() {}

	private static Comparator<File> likelySubmission = (a, b) -> {
		if (a.isDirectory()) return 1;
		else if (b.isDirectory()) return -1;
		else if (a.getName().endsWith(".java")) return -1;
		else if (b.getName().endsWith(".java")) return 1;
		else if (a.getName().endsWith(".exe")) return -1;
		else if (b.getName().endsWith(".exe")) return 1;
		else return 0;
	};

	private static InetAddress readIP(String addr) {
		String[] parts = addr.split("\\.");
		byte[] bparts;
		if (parts.length == 4) {
			bparts = new byte[4];
			for (int i = 0; i < 4; i++) {
				bparts[i] = (byte) Integer.parseInt(parts[i]);
			}
		}
		else if (parts.length == 8) {
			bparts = new byte[16];
			for (int i = 0; i < 8; i++) {
				int p = Integer.parseInt(parts[i]);
				bparts[2*i    ] = (byte) ((p & 0xFF00) >>> 8);
				bparts[2*i + 1] = (byte) ((p & 0x00FF) >>> 0);

			}
		}
		else throw new RuntimeException(addr);
		try {
			return InetAddress.getByAddress(bparts);
		}
		catch (UnknownHostException exc) {
			throw new RuntimeException(addr, exc);
		}
	}
	public static Competition loadCompetition(File folder) throws InterruptedException {
		Competition competition = new Competition(folder);
		try {
			File info = new File(folder, "competition.dsttf");
			if (info.exists()) {
				try (FileReader fr = new FileReader(info)) {
					TabbedText infoTabs = TabbedText.deserialize(fr);
					infoTabs.handle($each("competition", (part) -> {
						part.handle($each("users", (userTag) -> {
							InetAddress ip = readIP(userTag.tag);
							User user = competition.getUserByAddress(ip);
							userTag.handle($each((userPart) ->
								userPart.handle($1("name", (nm) ->
									user.setName(nm.tag)
								))
							));
						}));
					}));
				}
			}
			List<Task> tasks = TaskFileHandler.getTasks(folder);
			for (Task task: tasks) {
				competition.addTask(task);
				System.out.println("Added task " + task.getName());
				for (User user: competition.getUsers()) {
					File submissions = new File(folder, task.getName() + "/" + user.getInternalName());
					if (submissions.exists()) {
						System.out.println("Adding submissions from " + user.getName());
						for (int attemptN = 0;; attemptN++) {
							File attemptFolder = new File(submissions, "attempt" + attemptN);
							if (attemptFolder.exists()) {
								File[] attempts = attemptFolder.listFiles();
								Arrays.sort(attempts, likelySubmission);
								competition.submitAttempt(new Attempt(user, attempts[0], task, attemptN));
							}
							else break;
						}
					}
				}
			}
		}
		catch (Throwable error) {
			Tools.checkError(error);
			System.out.println("Cannot read task from folder "+folder.getAbsolutePath());
			error.printStackTrace();
		}
		return competition;
	}
	public static void saveCompetition(File folder, Competition competition) throws InterruptedException {
		try {
			TabbedText tabs = tab("competition",
				tab("users",
					competition.getUsers().stream().map((user) ->
						tab(user.getInternalName(),
							tab("name", tab(user.getName()))
						)
					).collect(toList())
				)
			);
			File info = new File(folder, "competition.dsttf");
			try (FileWriter fw = new FileWriter(info)) {
				tabs.serialize(fw);
			}
		}
		catch (Throwable error) {
			Tools.checkError(error);
			System.out.println("Cannot read task from folder "+folder.getAbsolutePath());
			error.printStackTrace();
		}
	}

}