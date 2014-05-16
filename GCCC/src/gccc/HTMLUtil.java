package gccc;

import com.sun.net.httpserver.*;
import java.io.*;

public class HTMLUtil {

	private HTMLUtil() {}

	public static class HTML {
		private final String toString;
		public HTML(String toString) {
			this.toString = toString;
		}
		public String toString() {
			return toString;
		}
	}

	public static class KVPair {
		private final String key;
		private final String value;
		public KVPair(String key, String value) {
			this.key = key; this.value = value;
		}
		public String toString() {
			return key + "=\"" + value + "\"";
		}
	}

	public static HTML escape(String text) {
		// TODO
		return new HTML(text);
	}
	public static HTML tag(String tagName, HTML... elements) {
		return tag(tagName, attrs(), elements);
	}
	public static HTML tag(String tagName, KVPair[] attrs, HTML... elements) {
		String attrText = "";
		for (KVPair p: attrs) {
			attrText += " " + p;
		}
		String elText = "";
		for (HTML h: elements) {
			elText += h;
		}
		return new HTML("<" + tagName + attrText + ">" + elText + "</" + tagName + ">");
	}
	public static KVPair[] attrs(KVPair... a) {
		return a;
	}
	public static HTML page(HTML... body) {
		return tag("html", tag("body", body));
	}
	public static void respond(int code, HttpExchange sess, HTML response) throws Throwable {
		byte[] bytes = response.toString().getBytes("UTF-8");
		sess.sendResponseHeaders(code, bytes.length);
		OutputStream os = sess.getResponseBody();
		os.write(bytes);
		os.close();
	}

}