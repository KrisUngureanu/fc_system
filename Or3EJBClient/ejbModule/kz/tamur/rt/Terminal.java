package kz.tamur.rt;

import java.io.*;
import java.util.*;

import kz.tamur.lang.EvalException;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;

public class Terminal {
	
	private static String user;
	private static String pd;
	private static String fileName;
	private static String expr;
	private static boolean serverMode;
	private static String baseName;

	public static void main(String[] args) throws Exception {
		
		parseCmdLine(args);
		
		final Kernel krn = Kernel.instance();
		
		BufferedReader r =
			new BufferedReader(new InputStreamReader(System.in));

		System.out.println("OR3 Terminal v.1.0.0");
		if (user == null) {
			System.out.print("User: ");
			user = Funcs.sanitizeUsername(r.readLine());
		}
		if (pd == null) {
			//System.out.print("Password: ");
			//passwd = r.readLine();
			pd = readPassword();
		}
		
		try {
			//TODO добавить считывание новых параметров из командной строки
			//krn.init(user, passwd, null, null, baseName, "terminal");
			System.out.println("Logged in as " + user + (serverMode ? " (server mode)" : " (client mode)"));

			TerminalInterfaceManager mgr = new TerminalInterfaceManager(krn);
			InterfaceManagerFactory.instance().register(mgr);
			
			Map<String, Object> vars = new HashMap<String, Object>();
			ClientOrLang lang = null;
			if (!serverMode) {
				lang = new ClientOrLang(null);
			}
			
			if (fileName != null) {
				byte[] b = Funcs.read(fileName);
				if (serverMode) {
					krn.execute(new String(b, "UTF-8"), new HashMap<String, Object>(), true);
				} else {
					lang.evaluate(new String(b, "UTF-8"), vars, null, new Stack<String>());
				}
			} else if (expr != null) {
				if (serverMode) {
					krn.execute(expr, new HashMap<String, Object>(), true);
				} else {
					lang.evaluate2(expr, vars, null, true, new Stack<String>());
				}
			} else {
				System.out.print(user + ">");
				String line = null;
				while((line = r.readLine()) != null && !".".equals(line)) {
					try {
						if (serverMode) {
							krn.execute(line, new HashMap<String, Object>(), true);
						} else {
							lang.evaluate(line, vars, null, new Stack<String>());
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
					System.out.print(user + ">");
				}
			}
		} catch (EvalException e) {
			Throwable th = e.getCause();
			while (th != null && !(th instanceof KrnException)) {
				th = th.getCause();
			}
			if (th == null) {
				e.printStackTrace();
			} else {
				System.err.println("\n" + ((KrnException)th).getMessage());
			}
		} catch (KrnException e) {
			System.err.println("\n" + e.getMessage());
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			krn.release();
			if (fileName == null && expr == null) {
				System.out.println("Logged off.");
				System.out.println("Press any key to exit.");
				r.read();
			}
		}
	}
	
	private static String readPassword() throws IOException {
		EraserThread et = new EraserThread("Password:  ");
		new Thread(et).start();

		BufferedReader r =
			new BufferedReader(new InputStreamReader(System.in));
		String pd = r.readLine();
		et.stopMasking();
		
		return pd;
	}

	private static void parseCmdLine(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String name = Funcs.normalizeInput(args[i]);
			if ("-u".equals(name)) {
				user = Funcs.sanitizeUsername(args[++i]);
			} else if (name.equals("-p")) {
				pd = args[++i];
			} else if (name.equals("-f")) {
				fileName = Funcs.sanitizeFileName(args[++i]);
			} else if (name.equals("-s")) {
				serverMode = true;
			} else if (name.equals("-e")) {
				expr = args[++i];
			} else if (name.equals("-b")) {
				baseName = Funcs.sanitizeUsername(args[++i]);
			}
		}
	}
	
	private static class EraserThread implements Runnable {
		
		private boolean stop;
		
		public EraserThread(String prompt) {
			System.out.print(prompt);
		}

		public void run() {
			this.stop = true;
			while(stop) {
				System.out.print("\010*");
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		 public void stopMasking() {
			 this.stop = false;
		 }
	}
}
