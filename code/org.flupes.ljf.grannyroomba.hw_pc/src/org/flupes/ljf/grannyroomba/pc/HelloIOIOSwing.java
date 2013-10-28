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

package org.flupes.ljf.grannyroomba.pc;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOSwingApp;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

public class HelloIOIOSwing extends IOIOSwingApp implements ActionListener {
	private static final String BUTTON_PRESSED = "bp";

	// Boilerplate main(). Copy-paste this code into any IOIOapplication.
	public static void main(String[] args) throws Exception {
		new HelloIOIOSwing().go(args);
	}

	protected boolean ledOn_;

	@Override
	protected Window createMainWindow(String args[]) {
		// Use native look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		JFrame frame = new JFrame("HelloIOIOSwing");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		JToggleButton button = new JToggleButton("LED");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setActionCommand(BUTTON_PRESSED);
		button.addActionListener(this);
		contentPane.add(Box.createVerticalGlue());
		contentPane.add(button);
		contentPane.add(Box.createVerticalGlue());

		// Display the window.
		frame.setSize(300, 100);
		frame.setLocationRelativeTo(null); // center it
		frame.setVisible(true);
		
		return frame;
	}

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {
			private DigitalOutput led_;

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				led_.write(!ledOn_);
				Thread.sleep(10);
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(BUTTON_PRESSED)) {
			ledOn_ = ((JToggleButton) event.getSource()).isSelected();
		}
	}
}
