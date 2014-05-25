package gccc.io;

import java.util.*;
import java.util.function.*;
import java.io.*;

/**
 * A data type for Dead Simple Tagged Text Format.
 */
public class TabbedText implements Iterable<TabbedText> {

	public final String tag;
	public final List<TabbedText> children;

	public TabbedText(String tag, List<TabbedText> children) {
		this.tag = tag;
		this.children = Collections.unmodifiableList(children);
	}

	public Iterator<TabbedText> iterator() {
		return Collections.singletonList(this).iterator();
	}

	@SafeVarargs
	public static TabbedText tab(String tag, Iterable<TabbedText>... children) {
		List<TabbedText> tt = new ArrayList<>();
		for (Iterable<TabbedText> tabs: children) {
			for (TabbedText tab: tabs) {
				tt.add(tab);
			}
		}
		return new TabbedText(tag, tt);
	}

	public Optional<TabbedText> get(String tag) {
		for (TabbedText text: children) { // optimization not needed
			if (text.tag.equals(tag)) return Optional.of(text);
		}
		return Optional.empty();
	}

	public static class Handler {
		public final Function<TabbedText, Boolean> handler;
		public Handler(Function<TabbedText, Boolean> handler) {
			this.handler = handler;
		}
	}
	public static Handler $(String tag, Consumer<? super List<TabbedText>> handler) {
		return new Handler((tt) -> {
			if (tt.tag.equals(tag)) {
				handler.accept(tt.children);
				return true;
			}
			else {
				return false;
			}
		});
	}
	public static Handler $1(String tag, Consumer<? super TabbedText> handler) {
		return new Handler((tt) -> {
			if (tt.tag.equals(tag) && tt.children.size() == 1) {
				handler.accept(tt.children.get(0));
				return true;
			}
			else {
				return false;
			}
		});
	}
	public static Handler $each(String tag, Consumer<? super TabbedText> handler) {
		return $(tag, (list) -> {
			for (TabbedText tt: list) handler.accept(tt);
		});
	}
	public static Handler $each(Consumer<? super TabbedText> handler) {
		return new Handler((tt) -> {
			for (TabbedText t: tt.children) {
				handler.accept(t);
			}
			return true;
		});
	}
	public void handle(Handler... handlers) {
		for (Handler handler: handlers) {
			if (handler.handler.apply(this)) {
				return;
			}
		}
		throw new RuntimeException(tag);
	}

	private void serialize(Writer output, int depth) throws IOException {
		for (int i = 0; i < depth; i++) {
			output.write("\t");
		}
		if (!tag.contains("\n") && !tag.startsWith("{{raw}}") && !tag.startsWith("\t")) {
			output.write(tag + "\n");
		}
		else {
			String[] parts = tag.split("\n");
			output.write("{{raw}}" + parts.length + "\n");
			for (String part: parts) {
				for (int i = 0; i < depth; i++) {
					output.write("\t");
				}
				output.write(part + "\n");
			}
		}
		for (TabbedText child: children) {
			child.serialize(output, depth + 1);
		}
	}
	public void serialize(Writer output) throws IOException {
		serialize(output, 0);
	}

	private static class TabbedTextHeader {
		String tag;
		int depth;
	}

	private static TabbedTextHeader header(BufferedReader input) throws IOException {
		String line = input.readLine();
		if (line == null) return null;
		TabbedTextHeader header = new TabbedTextHeader();
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '\t') header.depth++;
			else break;
		}
		line = line.substring(header.depth);
		if (!line.startsWith("{{raw}}")) header.tag = line;
		else {
			header.tag = "";
			int lines = Integer.parseInt(line.substring("{{raw}}".length()));
			for (int l = 0; l < lines; l++) {
				line = input.readLine();
				line = line.substring(header.depth); // should technically check that the first header.depth characters are tabs
				header.tag += line;
			}
		}
		return header;
	}
	public static TabbedText deserialize(Reader in) throws IOException {
		BufferedReader input = new BufferedReader(in);
		List<List<TabbedText>> stack = new ArrayList<>();
		List<TabbedText> list = new ArrayList<>();
		stack.add(list);
		TabbedTextHeader header = header(input);
		assert header.depth == 0;
		TabbedText result = new TabbedText(header.tag, list);
		while ((header = header(input)) != null) {
			for (int i = stack.size() - 1; i >= header.depth; i--) {
				stack.remove(i);
			}
			list = new ArrayList<>();
			stack.add(list);
			TabbedText tab = new TabbedText(header.tag, list);
			stack.get(header.depth - 1).add(tab);
		}
		return result;
	}
	public String toString() {
		StringWriter sw = new StringWriter();
		try {
			serialize(sw);
		}
		catch (IOException e) {
			// won't happen
		}
		return sw.toString();
	}

}