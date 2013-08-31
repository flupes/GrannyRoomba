package org.flupes.ljf.grannyroomba.pctests;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.flupes.ljf.grannyroomba.net.LocomotorClient;
import org.flupes.ljf.grannyroomba.net.ServoClient;
import org.flupes.ljf.grannyroomba.pctests.KeyboardController;

public class GrannyRoombaKeyboardUi {

	static Logger s_logger = Logger.getLogger("grannyroomba");

	protected static final boolean m_debug = false;
	
	public static void main(String[] args) {
		Display display = new Display( );

		Shell shell = new Shell (display);
		shell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		GridLayout gl = new GridLayout();
		gl.marginBottom = 4;
		gl.marginTop = 4;
		gl.marginLeft = 4;
		gl.marginRight = 4;
		shell.setLayout (gl);
		Image image = new Image (display, KeyboardController.class.getResourceAsStream ("grannyempty.jpg"));

		Group group = new Group(shell, SWT.NONE);
		GridData gd = new GridData();
		//	gd.horizontalAlignment = GridData.FILL_HORIZONTAL;
		//	gd.verticalAlignment = GridData.FILL_VERTICAL;
		//	gd.grabExcessHorizontalSpace = true;
		//	gd.grabExcessVerticalSpace = true;
		//	gd.horizontalAlignment = GridData.CENTER;
		gd.heightHint = 320;
		gd.widthHint = 240;
		group.setLayoutData(gd);
		//	group.setSize(240, 320);
		group.setBackgroundImage(image);

		// initialize logger
		s_logger.setLevel(Level.TRACE);
		Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
		s_logger.addAppender(appender);

		// Create client and keyboard dispatcher
		String host;
		int servoPort;
		int locoPort;
		if ( m_debug ) {
			host = "localhost";
			servoPort = 6666;
			locoPort = 7777;
		}
		else {
			host = "172.16.0.39";
			servoPort = 3333;
			locoPort = 4444;
		}
		ServoClient servoClient = new ServoClient(host, servoPort);
		LocomotorClient locoClient = new LocomotorClient(host, locoPort);
		
		KeyboardController kc = new KeyboardController(servoClient, locoClient);
		shell.addKeyListener(kc.controller());
		servoClient.connect();
		locoClient.connect();

		shell.pack ();
		//	shell.setSize(300, 400);
		shell.open ();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}

		image.dispose ();
		display.dispose ();
	}

}
