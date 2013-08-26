package org.flupes.ljf.grannyroomba.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
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
		m_servoImpl = new ServoStub();
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
		m_servoService.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		s_logger.warn("onBind should not be called!");
		return null;
	}
}
