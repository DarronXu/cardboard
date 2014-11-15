package com.IYYX.cardboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class StartupActivity extends Activity {
	Button SIPsettingsButton;
	Button startButton;
	EditText username;
	protected void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setContentView(R.layout.startup_ui);
		startButton=(Button)findViewById(R.id.startButton);
		SIPsettingsButton=(Button)findViewById(R.id.SIPsettingsButton);
		username=(EditText)findViewById(R.id.startupUsername);
		startButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.putExtra("myName", username.getText().toString());
				callMainActivity(intent,MainActivity.class);
			}
		});
		SIPsettingsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent=new Intent();
				callMainActivity(intent,SipSettings.class);
			}
		});
	}
	void callMainActivity(Intent src, Class<?> activity) {
		Intent intent=new Intent(this,activity);
		intent.putExtras(src);
		this.startActivity(intent);
	}
}
