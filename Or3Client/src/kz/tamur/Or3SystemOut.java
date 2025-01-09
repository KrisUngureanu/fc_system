package kz.tamur;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Or3SystemOut {
	// Подключиться к System.out и System.err
	public static void bind() {
		bind(LogFactory.getLog("STDOUT"), LogFactory.getLog("STDERR"));
	}

	public static void bind(Log out, Log err) {
		System.setOut(new PrintStream(new LoggerStream(out, false, System.out), true));
		System.setErr(new PrintStream(new LoggerStream(err, true, System.err), true));
	}

	private static class LoggerStream extends OutputStream {
		private Set<Long> threads = Collections.synchronizedSet(new HashSet<Long>()); 
		private final Log logger;
		private final boolean error;
		private final PrintStream realStream;
		private StringBuilder sbBuffer;

		public LoggerStream(Log logger, boolean error, PrintStream outputStream) {
			this.logger = logger;
			this.error = error;
			this.realStream = outputStream;
			sbBuffer = new StringBuilder();
		}

		@Override
		public void write(byte[] b) throws IOException {
			doWrite(new String(b));
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			doWrite(new String(b, off, len));
		}

		@Override
		public void write(int b) throws IOException {
			doWrite(String.valueOf((char) b));
		}

		private void doWrite(String str) throws IOException {
			sbBuffer.append(str);
			if (sbBuffer.length() > 0 && sbBuffer.charAt(sbBuffer.length() - 1) == '\n') {
				// The output is ready
				sbBuffer.setLength(sbBuffer.length() - 1); // remove '\n'
				if (sbBuffer.length() > 0 && sbBuffer.charAt(sbBuffer.length() - 1) == '\r') {
					sbBuffer.setLength(sbBuffer.length() - 1); // remove '\r'
				}
				String buf = sbBuffer.toString();
				sbBuffer.setLength(0);
				
				realStream.write(buf.getBytes());
				realStream.write('\n');
				
				long tid = Thread.currentThread().getId();
				if (!threads.contains(tid)) {
					threads.add(tid);
					
					if (error)
						logger.error(buf);
					else
						logger.info(buf);
					
					threads.remove(tid);
				}
			}
		}
	}

}