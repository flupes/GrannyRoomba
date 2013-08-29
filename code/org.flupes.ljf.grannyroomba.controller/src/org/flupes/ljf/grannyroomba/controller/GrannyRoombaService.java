package org.flupes.ljf.grannyroomba.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;

import org.flupes.ljf.grannyroomba.IServo;
import org.flupes.ljf.grannyroomba.ServoStub;
import org.flupes.ljf.grannyroomba.net.Server;
import org.flupes.ljf.grannyroomba.net.ServoServer;

public class GrannyRoombaService extends Service {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	protected Server m_servoService;
	protected IServo m_servoImpl;

	@Override
	public void onCreate() {
		s_logger.debug("GrannyRoombaService.onCreate");
		m_servoImpl = new ServoStub(-45, 90, 45);
		m_servoService = new ServoServer(3333, m_servoImpl);
	}

	@Override
	public void onDestroy() {
		s_logger.debug("GrannyRoombaService.onDestroy");
		m_servoService.cancel();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		s_logger.debug("GrannyRoombaService.onStart");
		
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
		m_servoService.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		s_logger.warn("onBind should not be called!");
		return null;
	}
}
