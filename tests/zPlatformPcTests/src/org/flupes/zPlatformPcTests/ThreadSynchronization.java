package org.flupes.zPlatformPcTests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadSynchronization {

	private ExecutorService m_exec;
	private Runnable m_monitor;
	
	private class TelemetryThread implements Runnable {

		public TelemetryThread(Runnable monitor) {
			m_monitor = monitor;
		}
		
		@Override
		public void run() {
			for (int i=1; i<12; i++) {
				System.out.println("TelemetryThread loop "+i);
				synchronized (m_monitor) {
					System.out.println("notify monitor thread!");
					m_monitor.notify();
				}
				System.out.println("TelemetryThread sleep for 1s");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.err.println("TelemetryThread sleep was interrupted");
				}
			}
			m_exec.shutdownNow();
		}
		
	}

	private class MonitorThread implements Runnable {

		private int counter = 1;
		@Override
		public void run() {
			while ( !m_exec.isShutdown() ) {
				synchronized(this) {
//				synchronized(m_monitor) {
					try {
						System.out.println("MonitorThread wait to be awaken...");
						wait();
					} catch (InterruptedException e) {
						System.err.println("MonitorThread inerrupted");
					}
				}
				System.out.println("MonitorThread does work for 3s / counter="+counter);
				counter++;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					System.err.println("MonitorThread work was interrupted");
				}
			}
		}
		
	}
	
	ThreadSynchronization() {
		m_exec = Executors.newFixedThreadPool(2);
		m_monitor = new MonitorThread();
		m_exec.execute(m_monitor);
		Runnable telem = new TelemetryThread(m_monitor);
		m_exec.execute(telem);
	}
	
	public static void main(String[] args) {
		new ThreadSynchronization();
	}

}
