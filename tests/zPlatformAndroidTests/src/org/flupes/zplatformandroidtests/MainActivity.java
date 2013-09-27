package org.flupes.zplatformandroidtests;

import org.flupes.zplatformandroidtests.R;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	protected static org.slf4j.Logger s_logger = LoggerFactory.getLogger("grannyroomba");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    s_logger.trace("this is a trace message");
	    s_logger.debug("this is a debug message");
	    s_logger.info("this is an info message");
	    s_logger.error("this is an error message");
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
