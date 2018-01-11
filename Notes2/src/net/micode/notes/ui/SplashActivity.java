package net.micode.notes.ui;

import net.micode.notes.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		PackageManager pm = getPackageManager();
		
		try {
			
		    PackageInfo pi = pm.getPackageInfo("com.lyt.android", 0);
		    TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
		    versionNumber.setText("Version " + pi.versionName);
		    
		} catch (NameNotFoundException e) {
		    e.printStackTrace();
		}

		new Handler().postDelayed(new Runnable(){

		@Override
		public void run() {
			
		    Intent intent = new Intent(SplashActivity.this,NotesListActivity.class);
		    startActivity(intent);
		    SplashActivity.this.finish();
		    
		}

		}, 2500);
	}

}


