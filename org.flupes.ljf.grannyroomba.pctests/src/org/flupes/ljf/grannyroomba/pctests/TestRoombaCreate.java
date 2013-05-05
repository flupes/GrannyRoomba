package org.flupes.ljf.grannyroomba.pctests;

import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.flupes.ljf.grannyroomba.hw.RoombaCreate;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.DigitalOutput.Spec.Mode;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOConnectionManager.Thread;
import ioio.lib.util.pc.IOIOSwingApp;


public class TestRoombaCreate extends IOIOSwingApp {


	private RoombaCreate m_roomba;

	private DigitalOutput m_heartBeatLed;
	private boolean m_beatState;
	private long m_lastTime;

	private Uart m_uart;
	private InputStream m_input;
	private OutputStream m_output;
	private static Logger s_logger = Logger.getLogger("grannyroomba");

	private static boolean s_listen = false;

	private boolean m_uiOpened;


	public static void main(String[] args) throws Exception {
		s_logger.setLevel(Level.DEBUG);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		s_logger.addAppender(appender);

		new TestRoombaCreate().go(args);
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

				m_roomba = new RoombaCreate(ioio_);
				m_roomba.connect();

				if ( s_listen ) {
					m_uart = ioio_.openUart(new DigitalInput.Spec(11), null,
							57600, Uart.Parity.NONE, Uart.StopBits.ONE);
					m_input = m_uart.getInputStream();
				}

				m_roomba.startTelemetry();
				//				m_roomba.demo(4);

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
	protected Window createMainWindow(String args[]) {
		// Use native look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		JFrame frame = new JFrame("Test Roomba Create Key Input");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setSize(400, 400);
		frame.setLocationRelativeTo(null); // center it
		frame.setVisible(true);

		frame.addKeyListener( 
				new KeyAdapter() { 
					public void keyPressed(KeyEvent e) {
						switch ( e.getKeyCode() ) {
						case KeyEvent.VK_UP: 
							s_logger.debug("UP pressed");
							break;
						case KeyEvent.VK_DOWN:
							s_logger.debug("DOWN pressed");
							break;
						case KeyEvent.VK_LEFT: 
							s_logger.debug("LEFT pressed");
							break;
						case KeyEvent.VK_RIGHT:
							s_logger.debug("RIGHT pressed");
							break;
						case KeyEvent.VK_SPACE:
							s_logger.debug("SPACE pressed");
							break;
						default:
							s_logger.debug("Key " + e.getKeyChar() + " not processed");
						}
					}
				} 
				);
		
		return frame;
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
