package com.IYYX.cardboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class StartupActivity extends Activity {
	Button startButton;
	EditText username,contactUsername;
	protected void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setContentView(R.layout.startup_ui);
		startButton=(Button)findViewById(R.id.startButton);
		//someButton=(Button)findViewById(R.id.XXXXXXXXXXXX);
		username=(EditText)findViewById(R.id.startupUsername);
		//contactUsername=(EditText)findViewById(R.id.startupContactUsername);
		startButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.putExtra("myName", username.getText().toString());
				intent.putExtra("contactName",contactUsername.getText().toString());
				callMainActivity(intent,MainActivity.class);
			}
		});
		/*someButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent=new Intent();
				callMainActivity(intent,SipSettings.class);
			}
		});*/
	}
	void callMainActivity(Intent src, Class<?> activity) {
		Intent intent=new Intent(this,activity);
		intent.putExtras(src);
		this.startActivity(intent);
	}
}
