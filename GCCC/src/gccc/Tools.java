package gccc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	public static long getLong(Properties properties, String key, long defaultValue) {
		String value=properties.getProperty(key, "");
		if (value.isEmpty())
			return defaultValue;
		return getLong(value, defaultValue);
	}
	
	public static long getLong(String value, long defaultValue) {
		try {
			return Long.parseLong(value);
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
