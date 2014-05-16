package gccc;

import java.net.InetAddress;

public class User {
	
	public User(InetAddress address) {
		this.address = address;
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

	private final InetAddress address;
	private String name;

}
