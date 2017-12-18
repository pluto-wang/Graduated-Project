package com.example.mic_client_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {
	private Button loginButton;
	private EditText name,num;	
	private int f,n,password=0;
	String account;
	
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		loginButton = (Button) findViewById(R.id.button1);
		name = (EditText) findViewById(R.id.account);
		num = (EditText) findViewById(R.id.password);		
		n=123;
		
		
		loginButton.setOnClickListener(new View.OnClickListener() {// login
			public void onClick(View v) {
				
				account = name.getText().toString();				
				password = Integer.parseInt(num.getText().toString());
				Intent i = new Intent(MainActivity.this,seating.class);
				
				if(password==n){
					f=1;
				}
				if(f==1){
					
					Bundle bundle = new Bundle();
					bundle.putString("account",account);	//send user's account 		
					i.putExtras(bundle);
					startActivity(i);
					finish();
					
				}
			  
				else if(f==0){
					Context context2 = getApplication();
					num.setText("");
					Toast.makeText(context2,"Your password is wrong",Toast.LENGTH_LONG).show();
				}
			}
		});
		

		}

}
