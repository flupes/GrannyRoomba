package org.flupes.ljf.grannyroomba;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class SimpleService extends Thread {

	static private ExecutorService m_executor = Executors.newCachedThreadPool();

	enum State {
		RUNNING, STARTED, STOPPED
	}

	private volatile State m_state;
	protected int m_msDelay;

	public SimpleService() {
		launch(10);
	}

	public SimpleService(int msdelay) {
		launch(msdelay);
	}

	private void launch(int msdelay) {
		m_msDelay = msdelay;
		m_executor.execute(this);
		m_state = State.RUNNING;
	}

	@Override
	public void run() {
		try {
			// Wait for the client to call "start"
			synchronized(this) {
				if ( m_state != State.STARTED ) {
					wait();
				}
				// Perform initialization
				init();
			}
			m_state = State.STARTED;
			// Start looping
			while ( m_state != State.STOPPED ) {
				loop();
				sleep(m_msDelay);
			}

		} catch (InterruptedException e1) {
			// thread was interrupted...
		}
		// Cleanup
		fini();
	} 

	public synchronized void start() {
		notify();
		m_state = State.STARTED;
	}

	public synchronized void cancel() {
		m_state = State.STOPPED;
		interrupt();
	}

	public static void terminateAll() throws InterruptedException {
		m_executor.shutdownNow();
	}

	public abstract int loop() throws InterruptedException;

	public abstract int init();

	public abstract int fini();
}
