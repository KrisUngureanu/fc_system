package com.cifs.or2.server.sgds;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.IOException;

public class HexStringOutputStream extends FilterOutputStream
{
	public HexStringOutputStream(OutputStream out) {
		super(out);
	}

	public void write(int b) throws IOException {
		char[] chs = {hexChar[(b & 0xf0) >>> 4], hexChar[b & 0x0f]};
		out.write(new String(chs).getBytes());
	}

	public void write(byte b[], int off, int len) throws IOException {
		String str = toHexString(b, off, len);
		out.write(str.getBytes());
	}

	public static String toHexString(byte[] b, int off, int len) {
		StringBuffer sb = new StringBuffer(len * 2);
		for (int i = off ; i < len ; ++i) {
			// look up high nibble char
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			// look up low nibble char
			sb.append(hexChar[b[i] & 0x0f]) ;
		}
		return sb.toString() ;
	}

	public static String toHexString(byte[] b) {
		return toHexString(b, 0, b.length);
	}
	
	public static String toHexStringNullable(byte[] b) {
		return b != null ? toHexString(b, 0, b.length) : "";
	}

	public static byte[] fromHexString (String s) {
		int stringLength = (s == null ? 0 : s.length());
		if ((stringLength & 0x1) != 0) {
			throw new IllegalArgumentException(
					"fromHexString requires an even number of hex characters");
		}
		byte[] b = new byte[stringLength / 2];
		for (int i = 0, j = 0; i < stringLength; i += 2, ++j)	{
			int high = Character.digit(s.charAt(i), 16);
			int low  = Character.digit(s.charAt(i + 1), 16);
			b[j] = (byte) ((high << 4) | low);
		}
		return b;
	}

	public static byte[] fromHexStringNullable(String s) {
		if (s == null || s.length() == 0) return null;
		return fromHexString(s);
	}

	static char[] hexChar =	{'0', '1', '2', '3', '4', '5', '6', '7', '8',
													 '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
