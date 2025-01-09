package kz.tamur.util;

public class Task {
	
	private final Runnable job;
	
	public Task(Runnable job) {
		this.job = job;
	}
	
	public void start(final long timeOut) {
		new Thread(new Runnable() {
			public void run() {
				Thread t = new Thread(job);
				t.start();
				try {
					t.join(timeOut);
					if (t.isAlive()) {
						t.interrupt();
					}
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}
}
