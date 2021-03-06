/*
 * GrannyRoomba - Telepresence robot based on a Roomba and Android tablet
 * Copyright (C) 2013 Lorenzo Flueckiger
 *
 * This file is part of GrannyRoomba.
 *
 * GrannyRoomba is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GrannyRoomba is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GrannyRoomba.  If not, see <http://www.gnu.org/licenses/>.
 */

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
