package org.flupes.ljf.grannyroomba.pctests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.MessageBox;

public class GetSystemProperties {

	public static void main(String[] args) {
		String host = System.getProperties().getProperty("host");
		int port = Integer.getInteger("port", 0);
		String msgText;
		if ( host == null ) {
			msgText = "Could not get host property!";
		}
		else {
			msgText = "Got property host = "+host+" / port="+port;
		}
		Display display = new Display( );
		Shell shell = new Shell (display);
		MessageBox msgBox = new MessageBox(shell, SWT.OK);
		msgBox.setMessage(msgText);
		msgBox.open();	
		
		display.dispose();

	}

}
