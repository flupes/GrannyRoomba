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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.flupes.ljf.grannyroomba.net.ServoClient;

public class KeyboardController {
	
	static Logger s_logger = Logger.getLogger("grannyroomba");

	// Static inner class because we do not create an instance of the
	// outer class since it is a main entry point
	static class ControlListener extends KeyAdapter {

		private ServoClient m_client;
		private Float m_position;
		private float m_increment;
		
		public ControlListener(ServoClient client) {
				m_client = client;
				m_increment = 5;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
		
			if ( m_position == null ) {
				m_position = m_client.getPosition();
			}
			boolean ret;
			switch (e.keyCode) {
			case SWT.PAGE_UP:
				m_position += m_increment;
				ret = m_client.setPosition(m_position);
				s_logger.info("PAGE_UP -> setPosition("+m_position+") => "+((ret)?"true":"false"));
				break;
			case SWT.PAGE_DOWN:
				m_position -= m_increment;
				ret = m_client.setPosition(m_position);
				s_logger.info("PAGE_DOWN -> setPosition("+m_position+") => "+((ret)?"true":"false"));
				break;
			case SWT.HELP:
				s_logger.info("HELP -> get config and state");
				float[] limits = m_client.getLimits(null);
				m_position = m_client.getPosition();
				if ( limits != null ) {
					s_logger.info("  low limit = " + limits[0]);
					s_logger.info("  high limit = " + limits[1]);
				}
				else {
					s_logger.warn("  not motor limits returned!");
				}
				if ( m_position != null ) {
					s_logger.info("  current position = " + m_position);
				}
				else {
					s_logger.warn("  invalid position returned!");
				}
			default:
				// just ignore silently
			}
		}

	}
	
	public static void main (String[] args) {
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
		ServoClient client = new ServoClient("172.16.0.39", 3333);
		ControlListener control = new ControlListener(client);
		shell.addKeyListener(control);
		client.connect();

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