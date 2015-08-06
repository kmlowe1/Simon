package com.gmail.kmlowe1.simon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SimonSplash extends Activity{
	
	private TextView loading;
	
	private void setSplashScreen(){
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(),"font/Android Hollow.ttf");
		
		loading = (TextView) findViewById(R.id.loading);
		loading.setTypeface(tf);
		loading.setTextSize(40);
		loading.setTextColor(0xffe8e8e8);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.simon_splash);
		setSplashScreen();
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				startActivity(new Intent(SimonSplash.this, SimonMenu.class));
				SimonSplash.this.finish();	
			}
		}, 3000L);
	}
}
