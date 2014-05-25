package gccc;

import java.net.InetAddress;

public class User {
	
	public User(InetAddress address, Competition competition) {
		this.address = address;
		this.competition = competition;
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public String getName() {
		if (name != null) {
			return name;
		}
		return address.toString();
	}

	public void setName(String name) {
		this.name = name;
		competition.usersChanged();
	}

	public String getInternalName() {
		return address.getHostAddress().replace(":", ".");
	}

	public int hashCode() {
		return address.hashCode();
	}
	public boolean equals(Object that) {
		if (that == null || !(that instanceof User)) {
			return false;
		}
		return address.equals(((User) that).address);
	}

	private final Competition competition;
	private final InetAddress address;
	private String name;

}
