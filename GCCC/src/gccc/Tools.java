package gccc;

import java.io.*;
import java.net.*;
import java.util.Properties;

public class Tools {
	
	public static String readFile(File file) throws FileNotFoundException, IOException {
		try (InputStream input=new FileInputStream(file)) {
			return readInputStream(input);
		}
	}
	
	public static String readInputStream(InputStream input) throws IOException {
		try (InputStreamReader ir=new InputStreamReader(input);
			 BufferedReader r=new BufferedReader(ir)) {
			StringBuilder output=new StringBuilder();
			while (true) {
				String line = r.readLine();
				if (line==null)
					break;
				output.append(line).append("\n");
			}
			return output.toString();
		}
	}
	public static InetAddress readIP(String addr) {
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
				int p = Integer.parseInt(parts[i],16);
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
	
	public static long getLong(Properties properties, String key, long defaultValue) {
		String value=properties.getProperty(key, "");
		if (value.isEmpty())
			return defaultValue;
		return getLong(value, defaultValue);
	}
	
	public static long getLong(Object value, long defaultValue) {
		try {
			return Long.parseLong(value.toString());
		}
		catch (NumberFormatException e) {
			System.out.println("Cannot parse '"+value+"'. Will use default value ("+defaultValue+") instead.");
			return defaultValue;
		}
	}
	
	public static void checkError(Throwable error) throws InterruptedException {
		for (Throwable e=error; e!=null; e=e.getCause())
			if (error instanceof InterruptedException)
				throw (InterruptedException)error;
	}

}
