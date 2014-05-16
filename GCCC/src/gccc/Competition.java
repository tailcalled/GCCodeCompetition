package gccc;

import java.net.*;
import java.util.*;

/**
 * Class for keeping track of the current state of the competition.
 */
public class Competition {

	private Map<InetAddress, User> users;
	private Object usersBottleneck = new Object();

	public Competition() {
		users = new HashMap<InetAddress, User>();
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

}