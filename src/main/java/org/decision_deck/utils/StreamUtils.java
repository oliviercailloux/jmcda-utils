package org.decision_deck.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.Writer;

public class StreamUtils {
	static public BufferedWriter getBuffered(Writer writer) {
		if (writer instanceof BufferedWriter) {
			BufferedWriter buffered = (BufferedWriter) writer;
			return buffered;
		}
		return new BufferedWriter(writer);
	}

	static public BufferedOutputStream getBuffered(OutputStream outputStream) {
		if (outputStream instanceof BufferedOutputStream) {
			BufferedOutputStream buffered = (BufferedOutputStream) outputStream;
			return buffered;
		}
		return new BufferedOutputStream(outputStream);
	}
}
