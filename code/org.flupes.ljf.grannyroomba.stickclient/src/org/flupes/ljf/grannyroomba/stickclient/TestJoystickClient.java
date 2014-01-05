package org.flupes.ljf.grannyroomba.stickclient;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.flupes.ljf.grannyroomba.net.CreateLocomotorClient;
import org.flupes.ljf.grannyroomba.net.ServoClient;

public class TestJoystickClient {

	private static Logger s_logger = Logger.getLogger("grannyroomba");

	public static void main(String[] args) {
		s_logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		s_logger.addAppender(appender);

		String host = System.getProperties().getProperty("serverAddr");
		if ( host ==  null ) {
			host = "172.16.0.39";
		}
		int servoPort = Integer.getInteger("servoPort", 3140);
		int locoPort = Integer.getInteger("locoPort", 3141);

		Display display = new Display( );

		Shell shell = new Shell (display);
		
		Group group = new Group(shell, SWT.BORDER);
		group.setText("GrannyRoomba Status");
		group.pack();
		group.setSize(180, 60);

		ServoClient servo = new ServoClient(host, servoPort);
		CreateLocomotorClient loco = new CreateLocomotorClient(host, locoPort);

		JoystickClient stick = new JoystickClient(servo, loco);
		
		if ( stick.isConnected() ) {
			
			shell.pack ();
			shell.open ();
			
			while ( !shell.isDisposed() && stick.isConnected()) {
				if (!display.readAndDispatch ()) display.sleep();
			}
			s_logger.info("UI closed.");
			stick.stop();
			s_logger.info("Sitck canceled");

		}
	}

}
