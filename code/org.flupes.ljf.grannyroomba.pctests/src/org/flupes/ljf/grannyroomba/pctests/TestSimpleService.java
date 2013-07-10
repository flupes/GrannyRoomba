package org.flupes.ljf.grannyroomba.pctests;

import java.util.concurrent.TimeUnit;

import org.flupes.ljf.grannyroomba.SimpleService;

public class TestSimpleService {

	class HelloService extends SimpleService {

		private int m_counter;
		private String m_name;
		private int m_period;
		
		HelloService(String name, int period) {
			super(1);
			m_name = name;
			m_period = period;
		}
		
		@Override
		public int loop() throws InterruptedException {
			m_counter++;
			System.out.println("Hello [" + m_name + "] : " +m_counter);
			TimeUnit.SECONDS.sleep(m_period);
			return 0;
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

	}

	TestSimpleService() {
		SimpleService a = new HelloService("pomme", 1);
		SimpleService b = new HelloService("poire", 2);
		SimpleService c = new HelloService("prune", 1);
		try {
			a.start();
			Thread.sleep(3000);
			b.start();
			c.start();
			Thread.sleep(3000);
			c.cancel();
			Thread.sleep(6000);
			SimpleService.terminateAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	
	public static void main(String[] args) {
		new TestSimpleService();
	}

}
