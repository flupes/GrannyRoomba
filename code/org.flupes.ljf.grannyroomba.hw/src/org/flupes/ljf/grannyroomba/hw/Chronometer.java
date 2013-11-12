package org.flupes.ljf.grannyroomba.hw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chronometer {

	protected static final boolean CHECK_TIMING = true;	
	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	private long m_start;
	private long m_stop;
	private long m_duration;
	private String m_name;

	public Chronometer(String name) {
		m_name = name;
	}
	
	public void start() { 
		if ( CHECK_TIMING ) {
			m_start = System.nanoTime();
		}
	}
	
	public void stop() {
		if ( CHECK_TIMING ) {
			m_stop = System.nanoTime();
			m_duration = (m_stop-m_start)/1000000;
		}
	}
	
	public long duration() {
		return m_duration;
	}
	
	public void show() {
		if ( CHECK_TIMING ) {
			s_logger.warn("Timer [" + m_name + "] -> " + m_duration + "ms");
		}
	}
	
	public void show(long enough) {
		if ( CHECK_TIMING && m_duration>enough ) {
			s_logger.warn("Timer [" + m_name + "] -> " + m_duration + "ms");
		}
	}
}
