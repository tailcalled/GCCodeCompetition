package gccc;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Class for keeping track of the current state of the competition.
 */
public class Competition implements AutoCloseable {

	private final Map<InetAddress, User> users;
	private final Object usersBottleneck = new Object();

	private final Map<String, Task> tasks = new ConcurrentHashMap<String, Task>();
	private final AttemptQueue queue = new AttemptQueue();
	private final Executor executor;

	private final File folder;

	public Competition(File folder) {
		ThreadPool threadPool = new ThreadPool();
		users = new HashMap<InetAddress, User>();
		executor = new Executor(queue, threadPool);
		this.folder = folder;
	}

	public static Competition loadCompetition(File folder) throws InterruptedException {
		Competition competition = new Competition(folder);
		List<Task> tasks = TaskFileHandler.getTasks(folder);
		for (Task task: tasks)
			competition.addTask(task);
		return competition;
	}

	public File getFolder() {
		return folder;
	}
	public Collection<Task> getTasks() {
		return tasks.values();
	}
	public void addTask(Task t) {
		tasks.put(t.getName(), t);
	}
	public Task getTask(String name) {
		return tasks.get(name);
	}
	public User getUserByAddress(InetAddress address) {
		if (users.containsKey(address)) {
			return users.get(address);
		}
		synchronized (usersBottleneck) {
			if (users.containsKey(address)) {
				return users.get(address);
			}
			users.put(address, new User(address));
			return users.get(address);
		}
	}
	
	public List<Attempt> getAttempts(Optional<User> user, Optional<Task> problem) {
		List<Attempt> res = new ArrayList<>();
		for (Attempt a: queue.getAllAttempts()) {
			if (user.map((u)->u==a.getUser()).orElse(true) && problem.map((m)->m==a.getTask()).orElse(true)) {
				res.add(a);
			}
		}
		return res;
	}
	
	public void submitAttempt(Attempt attempt) {
		queue.add(attempt);
	}

	@Override
	public void close() throws Exception {
		executor.close();
	}

}
