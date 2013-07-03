import ioio.lib.api.DigitalOutput;
import ioio.lib.api.DigitalOutput.Spec.Mode;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOSwingApp;

import java.awt.Window;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.UIManager;

// Example of connection: http://mitchtech.net/android-ioio-servo-control/

public class SwingServo extends IOIOSwingApp implements ChangeListener {

//	private int CENTER_PULSE_WIDTH = 1500;
//	private int PULSE_RANGE = 1000;
	private int CENTER_PULSE_WIDTH = 1500;
	private int PULSE_RANGE = 2000;
	private int SERVO_OFFSET = -60;
	private int SERVO_PIN = 10;
	private volatile int m_pulseWidth;
	
	public static void main(String[] args) throws Exception {
		new SwingServo().go(args);
	}

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {
			private PwmOutput m_servoPwmOutput;
			private final int PWM_FREQ = 100;

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				m_servoPwmOutput = ioio_.openPwmOutput(new DigitalOutput.Spec(SERVO_PIN, Mode.OPEN_DRAIN), PWM_FREQ);
				m_servoPwmOutput.setPulseWidth(CENTER_PULSE_WIDTH + SERVO_OFFSET);
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				m_servoPwmOutput.setPulseWidth(m_pulseWidth);
				Thread.sleep(20);
			}
		};
	}

	@Override
	protected Window createMainWindow(String[] args) {
		// Use native look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		JFrame frame = new JFrame("HelloIOIOSwing");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JSlider slider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
		slider.setMajorTickSpacing(90);
        slider.setMinorTickSpacing(30);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        
        frame.getContentPane().add(slider);
        
		// Display the window.
		frame.setSize(300, 100);
		frame.setLocationRelativeTo(null); // center it
		frame.setVisible(true);

		return frame;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
//        if (!source.getValueIsAdjusting()) {
            int pos = (int)source.getValue();
    		System.out.println("stateChanged -> " + pos);
            m_pulseWidth = CENTER_PULSE_WIDTH + SERVO_OFFSET + PULSE_RANGE*pos/(source.getMaximum()-source.getMinimum());
//            System.out.println("pulseWidth = " + m_pulseWidth);
//        }    		
	}

}
