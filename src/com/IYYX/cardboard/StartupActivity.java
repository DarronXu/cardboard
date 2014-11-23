package com.IYYX.cardboard;

import com.IYYX.cardboard.myAPIs.TcpManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartupActivity extends Activity {
	Button SIPsettingsButton;
	Button startButton;
	EditText username;
	protected void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		instance=this;
		setContentView(R.layout.startup_ui);
		startButton=(Button)findViewById(R.id.startButton);
		SIPsettingsButton=(Button)findViewById(R.id.SIPsettingsButton);
		username=(EditText)findViewById(R.id.startupUsername);
		startButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {  
				InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
				runOnUiThread(new Runnable(){
					public void run(){
						Toast.makeText(instance, "Please Wait ... ", Toast.LENGTH_LONG).show();
					}
				});
				Intent intent=new Intent();
				intent.putExtra("myName", username.getText().toString());
				if (username.getText().toString().equals("")||username.getText().toString().equals(TcpManager.mMyName)) return;
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
	static StartupActivity instance;
	public static void connectionFailed() {
		instance.runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(instance, "Cannot connect to Server!\nCheck your network.", Toast.LENGTH_SHORT).show();
			}
		});
	}
	void callMainActivity(Intent src, Class<?> activity) {
		Intent intent=new Intent(this,activity);
		intent.putExtras(src);
		this.startActivity(intent);
	}
}
