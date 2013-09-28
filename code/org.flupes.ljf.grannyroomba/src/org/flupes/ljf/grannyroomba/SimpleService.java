package org.flupes.ljf.grannyroomba;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
public abstract class SimpleService {

	public enum State {
		RUNNING, STARTED, STOPPED
	}
	public static int DEFAULT_PERIOD = 10;

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");
	protected static ExecutorService s_executor;

	private volatile State m_state;
	private int m_msDelay;
	private Future<?> m_task;
	private Runnable m_thread;
	private static List< Future<?> > s_serviceThreads = new ArrayList< Future<?> >();
	
	private class ServiceThread implements Runnable {
		ServiceThread() {
			s_logger.debug("Creating ServiceThread");
		}
		
		@Override
		public void run() {
			try {
				// Wait for the client to call "start"
				synchronized(this) {
					s_logger.debug("SimpleService wating to be started");
					if ( m_state != State.STARTED  ) {
						wait();
					}
					// Perform initialization
					s_logger.debug("SimpleService calling init code");
					init();
				}
				
				// Start looping
				s_logger.debug("SimpleService starting looping");
				while ( m_state != State.STOPPED ) {
					TimeUnit.MILLISECONDS.sleep(m_msDelay);
					if ( m_state == State.STARTED ) loop();
				}

			} catch (InterruptedException e1) {
				s_logger.debug("SimpleService was interrupted");
				// thread was interrupted...
			}
			// Cleanup
			s_logger.debug("SimpleService calling fini code");
			fini();
		} 
	}
	
	/** Create a SimpleService with the default waiting period
	 */
	public SimpleService() {
		m_msDelay = DEFAULT_PERIOD;
		launch();
	}

	/**
	 * Create a SimpleService and specify the desired waiting period
	 * @param msdelay	duration in milliseconds the thread will
	 * 					sleep between two successive loop call
	 */
	public SimpleService(int msdelay) {
		m_msDelay = msdelay;
		launch();
	}

	private synchronized void launch() {
		if ( s_executor == null ) {
			s_executor = Executors.newCachedThreadPool();
		}
		s_logger.debug("Starting SimpleService Thread");
		m_thread = new ServiceThread();
		m_task = s_executor.submit(m_thread);
		s_serviceThreads.add(m_task);
		m_state = State.RUNNING;
	}


	/**
	 * Notify the thread that was initialized to start:
	 * 1) call the "init" method and 2) start to loop
	 * Note that the thread was indeed already started before 
	 * this call, but in a waiting state.
	 */
	public synchronized void start() {
		if ( m_state == State.STOPPED ) {
			launch();
		}
		// Simply call notify... However,
		// because the start method is synchronized, 
		// it grabs the object monitor first, which 
		// is required to notify a thread.
		s_logger.debug("starting the service");
		synchronized(m_thread) {
			m_thread.notify();
		}
		m_state = State.STARTED;
	}

	/**
	 * Terminate the thread.
	 * After the loop has been interrupted, the "fini" method
	 * will be called, then the thread will stop.
	 */
	public synchronized void cancel() {
		s_logger.debug("terminating the service");
		m_state = State.STOPPED;
		m_task.cancel(true);
		if ( m_task.isDone() ) {
			s_logger.debug("service thread is terminated");
			s_serviceThreads.remove(m_task);
			if ( s_serviceThreads.isEmpty() ) {
				s_logger.debug("no more services, killing the executor...");
				s_executor.shutdown();
				if ( s_executor.isShutdown() ) {
					s_executor = null;
				}
			}
		}
		s_logger.debug("interrupt has been called on the service thread");
	}

	public synchronized boolean isLooping() {
		return m_state==State.STARTED;
	}
	
	public boolean isThreadRunning() {
		return !m_task.isDone();
	}

	/**
	 * Notify all SimpleServices to cancel their threads,
	 * and frees the executor.
	 * @return true if everything went smoothly
	 */
	public static boolean shutdown() {
		s_logger.debug("shuting down all remaining services...");
		List<Runnable> still_active = s_executor.shutdownNow();
		if ( still_active.size() > 0 ) {
			s_logger.warn("Executor still have active tasks!");
		}
		try {
			s_executor.awaitTermination(200, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			s_logger.warn("Some services did not termintate in time!");
			return false;
		}
		s_logger.debug("clean shutdown of all service :-)");
		return true;
	}
	
	public abstract void loop() throws InterruptedException;

	public abstract int init();

	public abstract int fini();
}
