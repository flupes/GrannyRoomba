package org.flupes.ljf.grannyroomba.pctests;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.SimpleService;

public class TestSimpleService {

	class HelloService extends SimpleService {

		private int m_counter;
		private String m_name;
		
		HelloService(String name, int period) {
			super(1000*period);
			m_name = name;
		}
		
		@Override
		public void loop() throws InterruptedException {
			m_counter++;
			System.out.println("Hello [" + m_name + "] : " +m_counter);
		}

		@Override
		public int init() {
			System.out.println("HelloService.init [" + m_name + "]");
			m_counter=0;
			return 0;
		}

		@Override
		public int fini() {
			System.out.println("HelloService.fini [" + m_name + "]");
			return 0;
		}

		public void printStatus() {
			System.out.println("HelloService ["+ m_name + "] is " 
					+ (isThreadRunning()?"running":"termintated"));
		}
	}

	TestSimpleService() {
		HelloService a = new HelloService("pomme", 1);
		SimpleService b = new HelloService("poire", 2);
		SimpleService c = new HelloService("prune", 1);
		try {
			a.start();
			Thread.sleep(3000);
			a.printStatus();
			b.start();
			c.start();
			Thread.sleep(3000);
			a.printStatus();
			c.cancel();
			Thread.sleep(3000);
			a.printStatus();
			a.cancel();
			b.cancel();
			a.printStatus();
			Thread.sleep(100);
			c.start();
			Thread.sleep(2000);
			c.cancel();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("grannyroomba");
		logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(appender);
		new TestSimpleService();
		// shutdown is not necessary because the SimpleService
		// static executor will auto destroy when no more threads are 
		// running
		// SimpleService.shutdown();
	}

}
