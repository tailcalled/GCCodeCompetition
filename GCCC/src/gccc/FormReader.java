package gccc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class FormReader implements AutoCloseable {

	public FormReader(InputStream stream, Charset encoding) {
		this.stream = stream;
		this.encoding = encoding;
	}

	@Override
	public void close() {
	}
	
	public String readLine() throws IOException {
		int i=0;
		loop:
		while (true) {
			switch (peekByte(i)) {
				case -1:
					if (i==0)
						return null;
					break loop;
				case 13:
				case 10:
					break loop;
			};
			i++;
		}
		byte[] bytes = getBytes(i);
		skipLineEnd();
		return new String(bytes, encoding);
	}
	
	public byte[] readBinary(String boundary) throws IOException {
		byte[] bb = boundary.getBytes(encoding);
		int i=0;
		while (true) {
			boolean found=true;
			for (int j=0; j<bb.length; j++) {
				if (bb[j]!=peekByte(i+j)) {
					found=false;
					break;
				}
			}
			if (found)
				break;
			i++;
		}
		boolean hasLineEnd=false;
		if (i>=lineEnd.length) {
			hasLineEnd=true;
			for (int j=0; j<lineEnd.length; j++)
				if (peekByte(i-lineEnd.length+j)!=lineEnd[j]) {
					hasLineEnd=false;
					break;
				}
		}
		byte[] b=new byte[i-(hasLineEnd ? lineEnd.length : 0)];
		System.arraycopy(buffer, current, b, 0, b.length);
		current+=i;
		for (int j=0; j<bb.length; j++)
			getByte();
		skipLineEnd();
		return b;
	}
	
	private void skipLineEnd() throws IOException {
		switch (peekByte(0)) {
			case 13:
				getByte();
				if (peekByte(0)==10) {
					getByte();
					if (lineEnd.length==0)
						lineEnd=new byte[] { 13, 10 };
				}
				else
					if (lineEnd.length==0)
						lineEnd=new byte[] { 13 };
				break;
			case 10:
				getByte();
				if (lineEnd.length==0)
					lineEnd=new byte[] { 10 };
				break;
		};
	}

	private int peekByte(int offset) throws IOException {
		if (!fillBuffer(offset+1))
			return -1;
		return buffer[current+offset];
	}

	private int getByte() throws IOException {
		if (!fillBuffer(1))
			return -1;
		return buffer[current++];
	}
	
	private byte[] getBytes(int length) throws IOException {
		fillBuffer(length);
		length=Math.min(length, data-current);
		byte[] b=new byte[length];
		System.arraycopy(buffer, current, b, 0, length);
		current+=length;
		return b;
	}
	
	private boolean fillBuffer(int length) throws IOException {
		while (length+current>data) {
			// Not enough data
			if (current>0) {
				// Move existing data to start of buffer:
				int newData=data-current;
				System.arraycopy(buffer, current, buffer, 0, newData);
				data=newData;
				current=0;
			}
			// Make buffer larger if needed:
			if (buffer.length==data)
				buffer=Arrays.copyOf(buffer,  buffer.length*2);
			// Fill rest of buffer:
			int read = stream.read(buffer, data, buffer.length-data);
			if (read==-1)
				return false;
			data+=read;
		}
		return true;
	}
	
	private InputStream stream;
	private Charset encoding;
	private byte[] buffer=new byte[60000];
	private int current=0;
	private int data=0;
	private byte[] lineEnd=new byte[0];

}
