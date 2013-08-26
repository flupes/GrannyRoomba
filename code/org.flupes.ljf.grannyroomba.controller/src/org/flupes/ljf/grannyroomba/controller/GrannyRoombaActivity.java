package org.flupes.ljf.grannyroomba.controller;

import org.flupes.grannyroomba.robotcontroller.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GrannyRoombaActivity extends Activity {

	TextView m_text;
	
	protected static Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_granny_roomba);
	
		m_text = (TextView) findViewById(R.id.editTextArea);
		m_text.setText("This is a multiline\ntext field called a TextView\nAnd it can have scrollbars...\n");
		// required to enable scrolling
		m_text.setMovementMethod(new ScrollingMovementMethod());
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.granny_roomba, menu);
		return true;
	}

	public void buttonClicked(View v) {
		ToggleButton button = (ToggleButton)v;
		if ( button != null ) {
			if ( button.isChecked() ) {
				m_text.append("Current state is ON\n");
				s_logger.info("Trying to start the service...");
				startService(new Intent(this, GrannyRoombaService.class));
			}
			else {
				m_text.append("Current state is OFF\n");
				s_logger.info("GrannyRoomba", "Now stopping the service...");
				stopService(new Intent(this, GrannyRoombaService.class));
			}
		}
	}

}
