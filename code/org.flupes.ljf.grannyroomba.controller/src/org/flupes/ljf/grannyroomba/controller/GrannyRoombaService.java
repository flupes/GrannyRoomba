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

package org.flupes.ljf.grannyroomba.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;

import org.flupes.ljf.grannyroomba.ICreateLocomotor;
import org.flupes.ljf.grannyroomba.IServo;
import org.flupes.ljf.grannyroomba.CreateLocomotorStub;
import org.flupes.ljf.grannyroomba.ServoStub;
import org.flupes.ljf.grannyroomba.hw.IoioRoombaCreateLocomotor;
import org.flupes.ljf.grannyroomba.hw.IoioServo;
import org.flupes.ljf.grannyroomba.hw.RoombaCreate;
import org.flupes.ljf.grannyroomba.net.LocomotorServer;
import org.flupes.ljf.grannyroomba.net.ZmqServer;
import org.flupes.ljf.grannyroomba.net.ServoServer;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class GrannyRoombaService extends IOIOService {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	protected ZmqServer m_servoService;
	protected IServo m_servoImpl;
	
	protected ZmqServer m_locoService;
	protected ICreateLocomotor m_locoImpl;
	
	protected RoombaCreate m_roomba;

	private DigitalOutput m_greenLed;
	private DigitalOutput m_yellowLed;

	protected static final boolean m_debug = false;

	@Override
	public void onCreate() {
		super.onCreate();	// IOIO things
		/*
		ch.qos.logback.classic.Logger logger =
		        (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("grannyroomba");
		//set its Level to INFO. The setLevel() method requires a logback logger
		logger.setLevel(ch.qos.logback.classic.Level.TRACE);
		 */
		s_logger.info("GrannyRoombaService.onCreate");
		if ( m_debug ) {
			m_servoImpl = new ServoStub(-45, 90, 45);
			m_servoService = new ServoServer(3140, m_servoImpl);
			m_servoService.start();
			
			m_locoImpl = new CreateLocomotorStub();
			m_locoService = new LocomotorServer(3141, m_locoImpl);
			m_locoService.start();
		}
	}

	@Override
	public void onDestroy() {
		s_logger.info("GrannyRoombaService.onDestroy");
		m_servoService.cancel();
		m_locoService.cancel();
		m_roomba.shutdown();
		super.onDestroy();	// IOIO things
	}

	@Override
	public void onStart(Intent intent, int startid) {
		super.onStart(intent, startid);	// IOIO things
		s_logger.info("GrannyRoombaService.onStart");

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (intent != null && intent.getAction() != null && intent.getAction().equals("stop")) {
			// User clicked the notification. Need to stop the service.
			nm.cancel(0);
//			stopForeground(false);
			stopSelf();
		}
		else {
			// Service starting. Create a notification.
			Intent stopMsg = new Intent("stop", null, this, this.getClass());
			PendingIntent notifMsg = PendingIntent.getService(this,
					0, stopMsg, PendingIntent.FLAG_UPDATE_CURRENT);

			Notification notification = new Notification.Builder(this)
			.setContentTitle("GrannyRoomba service running...")
			.setContentText("Click Stop to terminate the GrannyRoomba service")
			.setSmallIcon(R.drawable.ic_launcher)
			.addAction(R.drawable.ic_launcher, "Stop", notifMsg)
			.setProgress(0, 0, true)
			.build();

			nm.notify(0, notification);
			// remove nm stuff to enable this
//			startForeground(3141, notification);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		s_logger.warn("onBind should not be called!");
		return null;
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BaseIOIOLooper() {
			private DigitalOutput m_onboardLed;			
			private int count = 0;

			@Override
			protected void setup() throws ConnectionLostException,
			InterruptedException {
				m_onboardLed = ioio_.openDigitalOutput(IOIO.LED_PIN);
				m_yellowLed = ioio_.openDigitalOutput(6);
				m_greenLed = ioio_.openDigitalOutput(12);
				
				if ( ! m_debug ) {
					m_servoImpl = new IoioServo(10, ioio_, 1500, 2000, -180, 180);
					m_servoService = new ServoServer(3140, m_servoImpl);
					m_servoService.start();
					s_logger.info("IOIO looper started the ServoService");
					
					m_roomba = new RoombaCreate(ioio_);
					m_roomba.connect(2, 1);

					m_locoImpl = new IoioRoombaCreateLocomotor(m_roomba);
					m_locoService = new LocomotorServer(3141, m_locoImpl);
					m_locoService.start();
					s_logger.info("IOIO looper started the LocomotorService");
				}
			}

			@Override
			public void loop() throws ConnectionLostException,
			InterruptedException {
				m_onboardLed.write(false);
				m_yellowLed.write(true);
				m_greenLed.write(false);
				Thread.sleep(100);
				m_onboardLed.write(true);
				m_yellowLed.write(false);
				m_greenLed.write(true);
				Thread.sleep(900);
				count += 1;
//				s_logger.info("loop #"+count);
			}
		};
	}

}
