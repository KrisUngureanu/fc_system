package kz.tamur;

import org.apache.commons.logging.Log;

import com.cifs.or2.client.Kernel;

/**
* @author Erik
*
*/
public class SecurityContextHolder {

	private static final ThreadLocal<Object> frames = new ThreadLocal<Object>();
	private static final ThreadLocal<Kernel> krns = new ThreadLocal<Kernel>();
	private static final ThreadLocal<Log> logs = new ThreadLocal<Log>();

	public final static Kernel getKernel() {
		return krns.get();
	}
	
	public final static void setKernel(Kernel k) {
		krns.set(k);
	}

	public final static Log getLog() {
		return logs.get();
	}
	
	public final static void setLog(Log log) {
		logs.set(log);
	}

	public final static Object getFrame() {
		return frames.get();
	}
	
	public final static void setFrame(Object frame) {
		frames.set(frame);
	}
}
