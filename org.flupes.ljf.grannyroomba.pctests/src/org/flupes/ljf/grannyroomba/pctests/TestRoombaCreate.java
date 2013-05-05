package org.flupes.ljf.grannyroomba.pctests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.DigitalOutput.Spec.Mode;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOConnectionManager.Thread;
import ioio.lib.util.pc.IOIOConsoleApp;

public class TestRoombaCreate extends IOIOConsoleApp {

	private static final int CMD_START = 128;
	private static final int CMD_CONTROL = 130;
	private static final int CMD_DEMO = 136;
	
	private DigitalOutput m_heartBeatLed;
	private boolean m_beatState;
	private long m_lastTime;

	private Uart m_uart;
	private InputStream m_input;
	private OutputStream m_output;
	private static Logger s_logger = Logger.getLogger("grannyroomba");

	private static boolean s_listen = true;

	public static void main(String[] args) throws Exception {
		s_logger.setLevel(Level.DEBUG);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		s_logger.addAppender(appender);
		new TestRoombaCreate().go(args);
	}

	private void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeByte(int b) 
			throws ConnectionLostException {
		try {
			m_output.write( b );
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
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

				m_uart = ioio_.openUart(null,
						new DigitalOutput.Spec(6, Mode.OPEN_DRAIN),
						57600, Uart.Parity.NONE, Uart.StopBits.ONE);
				m_output = m_uart.getOutputStream();

				if ( s_listen ) {
					m_uart = ioio_.openUart(new DigitalInput.Spec(11), null,
							57600, Uart.Parity.NONE, Uart.StopBits.ONE);
					m_input = m_uart.getInputStream();
				}

				s_logger.info("start");
				writeByte(CMD_START);
				delay(500);
				s_logger.info("demo");
				writeByte(CMD_DEMO);
				delay(40);
				writeByte(4);
			}

			@Override
			public void loop()
					throws ConnectionLostException, InterruptedException {

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
		};
	}

	@Override
	protected void run(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		boolean abort = false;
		String line;
		while (!abort && (line = reader.readLine()) != null) {
			if (line.equals("q")) {
				abort = true;
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
