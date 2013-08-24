package org.flupes.ljf.grannyroomba;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static int DEFAULT_PERIOD = 10;

	protected static ExecutorService s_executor = Executors.newCachedThreadPool();
	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

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

	private synchronized void launch(int msdelay) {
		s_logger.info("Starting SimpleService Thread");
		m_msDelay = msdelay;
		s_executor.execute(this);
		m_state = State.RUNNING;
	}

	@Override
	public void run() {
		try {
			// Wait for the client to call "start"
			synchronized(this) {
				s_logger.info("SimpleService wating to be started");
				if ( m_state != State.STARTED  ) {
					wait();
				}
				// Perform initialization
				s_logger.info("SimpleService calling init code");
				init();
			}
			
			// Start looping
			s_logger.info("SimpleService starting looping");
			while ( m_state != State.STOPPED ) {
				loop();
				sleep(m_msDelay);
			}

		} catch (InterruptedException e1) {
			s_logger.info("SimpleService was interrupted");
			// thread was interrupted...
		}
		// Cleanup
		s_logger.info("SimpleService calling fini code");
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
		s_logger.info("starting the service");
		notify();
		m_state = State.STARTED;
	}

	/**
	 * Terminate the thread.
	 * After the loop has been interrupted, the "fini" method
	 * will be called, then the thread will stop.
	 */
	public synchronized void cancel() {
		s_logger.info("terminating the service");
		m_state = State.STOPPED;
		interrupt();
	}

	public synchronized boolean isLooping() {
		return m_state==State.STARTED;
	}
	
	/**
	 * Notify all SimpleServices to cancel their threads.
	 * @throws InterruptedException
	 */
	public static void terminateAll() throws InterruptedException {
		s_executor.shutdownNow();
	}

	public abstract void loop() throws InterruptedException;

	public abstract int init();

	public abstract int fini();
}
