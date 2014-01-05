package org.flupes.ljf.grannyroomba.stickclient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.flupes.ljf.grannyroomba.net.CreateLocomotorClient;
import org.flupes.ljf.grannyroomba.net.ServoClient;
//import java.io.PrintStream;
//import org.eclipse.ui.console.ConsolePlugin;
//import org.eclipse.ui.console.IOConsole;

public class TestJoystickClient {

	private static Logger s_logger = Logger.getLogger("grannyroomba");

	public static void main(String[] args) {

		String release = System.getProperties().getProperty("release");
		Appender appender;
		TTCCLayout layout = new TTCCLayout();
		layout.setDateFormat("ISO8601");
		if ( release != null && release.equalsIgnoreCase("true") ) {
			s_logger.setLevel(Level.DEBUG);
			String homeDir = System.getProperty("user.home");
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			String logFile = homeDir+"/granny_roomba_"+timeStamp+".txt";
			try {
				appender = new FileAppender(layout, logFile);
			} catch (IOException e) {
				System.err.println("Could not create log file: "+logFile);
				e.printStackTrace();
				return;
			}
		}
		else {
			s_logger.setLevel(Level.TRACE);
			appender = new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT);
		}
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

//		IOConsole consoles[] = new IOConsole[1];
//		consoles[0] = new IOConsole("console", null);
//        PrintStream consoleStream = new PrintStream(consoles[0].newOutputStream());
//        System.setOut(consoleStream);       
//        ConsolePlugin.getDefault().getConsoleManager().addConsoles(consoles);
        
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
			servo.disconnect();
			loco.disconnect();
			s_logger.info("JoystickClient terminated cleanly.");

		}
	}

}
