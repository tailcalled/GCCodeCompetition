package gccc;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Class for keeping track of the current state of the competition.
 */
public class Competition implements AutoCloseable {

	private final Map<InetAddress, User> users = new ConcurrentHashMap<>();

	private final Map<String, Task> tasks = new ConcurrentHashMap<>();
	private final AttemptQueue queue = new AttemptQueue();
	private final Executor executor;
	private final ThreadPool threadPool;

	private final File folder;

	public Competition(File folder) {
		threadPool = new ThreadPool();
		executor = new Executor(queue, threadPool);
		this.folder = folder;
	}

	public File getFolder() {
		return folder;
	}
	public Collection<Task> getTasks() {
		return tasks.values();
	}
	public Collection<User> getUsers() {
		return users.values();
	}
	public void addTask(Task t) {
		tasks.put(t.getName(), t);
	}
	
	public Optional<Task> getTask(String name) {
		if (name==null)
			return Optional.empty();
		return Optional.ofNullable(tasks.get(name));
	}
	
	public User getUserByAddress(InetAddress address) {
		synchronized(users) {
			if (users.containsKey(address)) {
				return users.get(address);
			}
			users.put(address, new User(address, this));
			usersChanged();
			return users.get(address);
		}
	}
	public void usersChanged() {
		threadPool.execute(() -> {
			try {
				CompetitionFileHandler.saveCompetition(folder, this);
			}
			catch (InterruptedException e) {
				// ending anyway
			}
		});
	}
	
	public Optional<User> getUserByName(String name) {
		if (name==null)
			return Optional.empty();
		for (User user: users.values())
			if (user.getName().equals(name))
				return Optional.of(user);
		return Optional.empty();
	}
	
	public List<Attempt> getAttempts(Collection<User> users, Collection<Task> problems) {
		List<Attempt> res = new ArrayList<>();
		for (Attempt a: queue.getAllAttempts()) {
			if ((users.isEmpty() || users.contains(a.getUser())) && (problems.isEmpty() || problems.contains(a.getTask()))) {
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
