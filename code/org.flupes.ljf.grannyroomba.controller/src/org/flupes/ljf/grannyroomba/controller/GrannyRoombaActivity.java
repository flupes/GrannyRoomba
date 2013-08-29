package org.flupes.ljf.grannyroomba.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class GrannyRoombaActivity extends Activity {

	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, GrannyRoombaService.class));
		finish();
	}

}
