package gccc;

import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

public class HTMLUtil {

	private HTMLUtil() {}

	public static class HTML implements Iterable<HTML> {
		private final String toString;
		public HTML(String toString) {
			this.toString = toString;
		}
		public String toString() {
			return toString;
		}
		public Iterator<HTML> iterator() {
			return Collections.singletonList(this).iterator();
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
		return new HTML(text);
	}
	@SafeVarargs
	public static HTML tag(String tagName, Iterable<HTML>... elements) {
		return tag(tagName, attrs(), elements);
	}
	@SafeVarargs
	public static HTML tag(String tagName, KVPair[] attrs, Iterable<HTML>... elements) {
		String attrText = "";
		for (KVPair p: attrs) {
			attrText += " " + p;
		}
		String elText = "";
		for (Iterable<HTML> hs: elements) {
			for (HTML h: hs) {
				elText += h;
			}
		}
		return new HTML("<" + tagName + attrText + ">" + elText + "</" + tagName + ">");
	}
	public static KVPair[] attrs(KVPair... a) {
		return a;
	}
	public static KVPair $(String key, String value) {
		return new KVPair(key, value);
	}
	public static <A extends Iterable<HTML>> A code(Supplier<A> prod) {
		return prod.get();
	}
	public static Iterable<HTML> elements(HTML... elements) {
		return Arrays.asList(elements);
	}
	@SafeVarargs
	public static HTML page(Iterable<HTML>... body) {
		return tag("html", tag("body", body));
	}
	public static void respond(int code, HttpExchange sess, HTML response) throws Throwable {
		byte[] bytes = response.toString().getBytes("UTF-8");
		sess.sendResponseHeaders(code, bytes.length);
		try (OutputStream os = sess.getResponseBody()) {
			os.write(bytes);
		}
	}
	public 

}