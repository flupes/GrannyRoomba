package org.flupes.grannyroomba.test.jmqandroid.helloserver;

import org.flupes.grannyroomba.test.jmqandroid.helloserver.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ZeroMqHelloServer extends Activity {

	TextView m_text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jzmq_server);
		m_text = (TextView) findViewById(R.id.textArea);
		m_text.setText("This is a multiline\ntext field called a TextView\nAnd it can have scrollbars...\n");
		// required to enable scrolling
		m_text.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jzmq_server, menu);
		return true;
	}
	
	public void buttonClicked(View v) {
		ToggleButton button = (ToggleButton)v;
		if ( button != null ) {
			if ( button.isChecked() ) {
				m_text.append("Current state is ON\n");
				Log.d("GrannyRoomba", "Trying to start the service...");
				startService(new Intent(this, HelloSimpleService.class));
			}
			else {
				m_text.append("Current state is OFF\n");
				Log.d("GrannyRoomba", "Now stopping the service...");
				stopService(new Intent(this, HelloSimpleService.class));
			}
		}
	}

}
