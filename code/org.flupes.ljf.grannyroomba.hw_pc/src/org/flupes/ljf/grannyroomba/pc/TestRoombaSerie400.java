package org.flupes.ljf.grannyroomba.pc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.hw.RoombaSeries400;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOConnectionManager.Thread;
import ioio.lib.util.pc.IOIOConsoleApp;

public class TestRoombaSerie400 extends IOIOConsoleApp {

	private RoombaSeries400 m_roomba;

	private DigitalOutput m_heartBeatLed;
	private boolean m_beatState;
	private long m_lastTime;

	private boolean m_scheduleRequest = false;
	private boolean m_scheduleSpot = false;

	private Uart m_uart;
	private InputStream m_input;

	private static boolean s_listen = true;
	
	public static void main(String[] args) throws Exception {
		Logger logger = Logger.getLogger("grannyroomba");
		logger.setLevel(Level.DEBUG);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(appender);
		new TestRoombaSerie400().go(args);
	}

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {

			@Override
			protected void setup() throws ConnectionLostException,
			InterruptedException {
				m_heartBeatLed = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
				m_beatState = true;
				m_lastTime = System.currentTimeMillis();
				m_roomba = new RoombaSeries400(ioio_);

				if ( s_listen ) {
					m_uart = ioio_.openUart(new DigitalInput.Spec(11), null,
							57600, Uart.Parity.NONE, Uart.StopBits.ONE);
					m_input = m_uart.getInputStream();
				}
				m_roomba.connect();

			}

			@Override
			public void loop()
					throws ConnectionLostException, InterruptedException {
				// do something
				if ( m_scheduleRequest ) {
					System.out.println("get sensors");
					m_roomba.getPowerInfo();
					m_scheduleRequest = false;
				}
				if ( m_scheduleSpot ) {
					System.out.println("spot");
					m_roomba.spot();
					m_scheduleSpot = false;
				}
				if ( s_listen ) {
					int b;
					int n;
					try {
						n = m_input.available();
						for ( int i=n; i>0; i--) {
							b = m_input.read();
							if ( b == -1 ) break;
							System.out.println("got: " + b);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				beat();
				Thread.sleep(10);
			}

			@Override
			public void disconnected() {
				System.err.println("IOIO Board Disconnected!");
			}

			@Override
			public void incompatible() {
				System.err.println("IOIO Board Not Compatible!");
			}
		};
	}

	@Override
	protected void run(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		boolean abort = false;
		String line;
		while (!abort && (line = reader.readLine()) != null) {
			if (line.equals("a")) {
				m_roomba.power();
			}
			else if ( line.equals("d")) {
				m_roomba.baseDrive(200, 0x8000);
			}
			else if ( line.equals("s")) {
				m_roomba.spot();
			}
			else if ( line.equals("p")) {
				m_scheduleRequest = true;
			} else if (line.equals("q")) {
				abort = true;
			} else {
				System.out.println("Unknown input. d=drive, s=spot, p=powerinfo, q=quit.");
			}
		}
	}

	private void beat() {
		long currentTime = System.currentTimeMillis();
		try {
			if ( m_beatState ) {
				if ( (currentTime-m_lastTime) > 800 ) {
					m_lastTime = currentTime;
					m_heartBeatLed.write(false);
					m_beatState = false;
				}
			}
			else {
				if ( (currentTime-m_lastTime) > 200 ) {
					m_lastTime = currentTime;
					m_heartBeatLed.write(true);
					m_beatState = true;
				}
			}
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

