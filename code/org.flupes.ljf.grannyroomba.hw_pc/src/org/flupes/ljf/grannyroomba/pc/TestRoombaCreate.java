package org.flupes.ljf.grannyroomba.pc;

import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;

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
	private static Logger s_logger = Logger.getLogger("grannyroomba");

	private static float SPEED_INCR = 0.2f;
	private static float SPIN_INCR = 0.2f;
	private static float MAX_VELOCITY = 500;
	private static float MAX_RADIUS = 2000;

	private static boolean s_listen = false;

	public static void main(String[] args) throws Exception {
		s_logger.setLevel(Level.TRACE);
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
//				m_roomba.connect();
				m_roomba.connect(2, 1);

				m_roomba.safeControl();
				
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
					private float speed = 0;
					private float spin = 0;
					private float prevSpeed = 0;
					public void keyPressed(KeyEvent e) {
						prevSpeed = speed;
						boolean newDrive = false; 
						switch ( e.getKeyCode() ) {
						case KeyEvent.VK_UP: 
							if ( speed < 1-SPEED_INCR/2 ) speed += SPEED_INCR;
							s_logger.trace("UP pressed -> speed="+speed+" / spin="+spin);
							newDrive=true;
							break;
						case KeyEvent.VK_DOWN:
							if ( speed > -1+SPEED_INCR/2 ) speed -= SPEED_INCR;
							s_logger.trace("DOWN pressed -> speed="+speed+" / spin="+spin);
							newDrive=true;
							break;
						case KeyEvent.VK_RIGHT:
							if ( spin < 1-SPIN_INCR/2 ) spin += SPIN_INCR;
							s_logger.trace("RIGHT pressed -> speed="+speed+" / spin="+spin);
							newDrive=true;
							break;
						case KeyEvent.VK_LEFT: 
							if ( spin > -1+SPIN_INCR/2 ) spin -= SPIN_INCR;
							s_logger.trace("LEFT pressed -> speed="+speed+" / spin="+spin);
							newDrive=true;
							break;
						case KeyEvent.VK_SPACE:
							speed = 0;
							spin = 0;
							s_logger.trace("SPACE pressed -> speed="+speed+" / spin="+spin);
							newDrive=true;
							break;
						case KeyEvent.VK_CONTROL:
							s_logger.trace("CONTROL pressed -> print telemetry");
							m_roomba.printRawTelemetry();
							break;
						default:
							s_logger.trace("Key " + e.getKeyChar() + " not processed");
						}
						// From a slow speed, small radius, we shoudl transition
						// to a slow point turn when speed becomes zero (without 
						// this check, small radius is transformed into high point
						// turn rate!)
						s_logger.trace("prevSpeed=" + prevSpeed + " / currSpeed=" + speed);
						if ( (Math.abs(prevSpeed)>SPEED_INCR/2) && (Math.abs(speed)<SPEED_INCR/2) ) {
							float absSpin = Math.abs(prevSpeed);
							if ( spin > SPIN_INCR/2 ) {
								spin = absSpin;
							}
							else if ( spin < -SPIN_INCR/2 ) {
								spin = -absSpin;
							}
							s_logger.trace("reset spin to :" + spin);
						}
						if ( newDrive ) {
							changeDrive(speed, spin);
						}
					}
				} 
				);

		return frame;
	}

	protected void changeDrive(float speed, float spin) {
		int velocity;
		int radius;
		if ( Math.abs(speed) < SPEED_INCR/2 ) {
			if ( Math.abs(spin) < SPIN_INCR/2 ) {
				velocity = 0;
				radius = 0x8000;
			}
			else {
				// Zero speed, this is a point turn
				if ( spin > 0 ) {
					radius = 0xFFFF;
				}
				else {
					radius = 0x0001; 
				}
				velocity = (int)(MAX_VELOCITY*Math.abs(spin));
			}
		}
		else {
			velocity = (int)(MAX_VELOCITY*speed);
			if ( Math.abs(spin) < SPIN_INCR/2 ) {
				// Zero spin, straight forward or backward move
				radius = 0x8000;
			}
			else {
				// We want the radius to decay exponentially, reduced by half
				// for each increment of the spin (starting at 2000).
				float steps = 1/SPIN_INCR;
				float factor = MAX_RADIUS / (float)Math.pow(2, steps-1);
				float absRadius = factor*(float)Math.pow(2, steps*(1-Math.abs(spin)));
				if ( spin > 0 ) {
					radius = -(int)absRadius;
				}
				else {
					radius = (int)absRadius;
				}
			}
		}
		try {
			m_roomba.drive(velocity, radius);
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
