package org.flupes.ljf.grannyroomba.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;

import org.flupes.ljf.grannyroomba.ILocomotor;
import org.flupes.ljf.grannyroomba.IServo;
import org.flupes.ljf.grannyroomba.LocomotorStub;
import org.flupes.ljf.grannyroomba.ServoStub;
import org.flupes.ljf.grannyroomba.hw.IoioRoombaLocomotor;
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
	protected ILocomotor m_locoImpl;

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
			m_servoService = new ServoServer(3333, m_servoImpl);
			m_servoService.start();
			
			m_locoImpl = new LocomotorStub();
			m_locoService = new LocomotorServer(4444, m_locoImpl);
			m_locoService.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();	// IOIO things
		s_logger.info("GrannyRoombaService.onDestroy");
		m_servoService.cancel();
		m_locoService.cancel();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		super.onStart(intent, startid);	// IOIO things
		s_logger.info("GrannyRoombaService.onStart");

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (intent != null && intent.getAction() != null && intent.getAction().equals("stop")) {
			// User clicked the notification. Need to stop the service.
			nm.cancel(0);
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
				if ( ! m_debug ) {
					m_servoImpl = new IoioServo(10, ioio_, 1500, 2000, -180, 180);
					m_servoService = new ServoServer(3333, m_servoImpl);
					m_servoService.start();
					s_logger.warn("IOIO looper started the ServoService");
					
					RoombaCreate roomba = new RoombaCreate(ioio_);
					roomba.connect(2, 1);
					roomba.safeControl();
					roomba.startTelemetry();

					m_locoImpl = new IoioRoombaLocomotor(roomba);
					m_locoService = new LocomotorServer(4444, m_locoImpl);
					m_locoService.start();
					s_logger.warn("IOIO looper starte the LocomotorService");
				}
			}

			@Override
			public void loop() throws ConnectionLostException,
			InterruptedException {
				m_onboardLed.write(false);
				Thread.sleep(100);
				m_onboardLed.write(true);
				Thread.sleep(900);
				count += 1;
//				s_logger.info("loop #"+count);
			}
		};
	}

}
