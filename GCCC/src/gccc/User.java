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
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private final InetAddress address;
	private String name;

}
