package org.flupes.ljf.grannyroomba;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Helper class to start a looper service with one 
 * initialization phase and a cleanup phase.
 * 
 * The user of SimleService needs to extend the class and implement
 * the following methods:
 *   - init: code that will be called before looping
 *   - loop: code that will be repetitively called in an infinite loop
 *   - fini: code that will be executed before the thread terminates
 *   
 * The lifecycle of the SimpleService is the following:
 * SimpleService creation -> RUNNING
 *                           (thread is launched in the constructor)
 * client call "start"    -> STARTED
 *                           the "init" method is called once and then
 *                           the "loop" method is called repetitively
 * client call "cancel"   -> STOPPED
 *                           the current loop is interrupted,
 *                           the "fini" method is called once,
 *                           and the thread stops.
 *                           
 * It is also possible to stop all the SimpleService by calling
 * the static method terminateAll                          
 */
public abstract class SimpleService extends Thread {

	static private int DEFAULT_PERIOD = 10;
	static private ExecutorService m_executor = Executors.newCachedThreadPool();

	enum State {
		RUNNING, STARTED, STOPPED
	}

	private volatile State m_state;
	protected int m_msDelay;

	/** Create a SimpleService with the default waiting period
	 */
	public SimpleService() {
		launch(DEFAULT_PERIOD);
	}

	/**
	 * Create a SimpleService and specify the desired waiting period
	 * @param msdelay	duration in milliseconds the thread will
	 * 					sleep between two successive loop call
	 */
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
				if ( m_state != State.STARTED  ) {
					wait();
				}
				// Perform initialization
				init();
				m_state = State.STARTED;
			}
			
			// Start looping
			while ( m_state == State.STARTED ) {
				loop();
				sleep(m_msDelay);
			}

		} catch (InterruptedException e1) {
			// thread was interrupted...
		}
		// Cleanup
		fini();
	} 

	/**
	 * Notify the thread that was initialized to start:
	 * 1) call the "init" method and 2) start to loop
	 * Note that the thread was indeed already started before 
	 * this call, but in a waiting state.
	 */
	public synchronized void start() {
		// Simply call notify... However,
		// because the start method is synchronized, 
		// it grabs the object monitor first, which 
		// is required to notify a thread.
		notify();
	}

	/**
	 * Terminate the thread.
	 * After the loop has been interrupted, the "fini" method
	 * will be called, then the thread will stop.
	 */
	public synchronized void cancel() {
		m_state = State.STOPPED;
		interrupt();
	}

	/**
	 * Notify all SimpleServices to cancel their threads.
	 * @throws InterruptedException
	 */
	public static void terminateAll() throws InterruptedException {
		m_executor.shutdownNow();
	}

	public abstract void loop() throws InterruptedException;

	public abstract int init();

	public abstract int fini();
}
