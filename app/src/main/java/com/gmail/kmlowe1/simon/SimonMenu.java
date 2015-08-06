package com.gmail.kmlowe1.simon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class SimonMenu extends Activity implements OnClickListener{
	
	private ImageView logo;
	private int counter = 0;
	private boolean run = true;
	private BackgroundThread bt;
	private int logoId[] = { R.drawable.simon1, R.drawable.simon2, 
			                 R.drawable.simon3, R.drawable.simon4, 
			                 R.drawable.simon5 };	
	private Button start;
	private Button about;
	private Button exit;
	private int fontSize = 12;
	
	@SuppressWarnings("deprecation")
	private void setFontSize(){
		int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		WindowManager w = getWindowManager();
		Display d = w.getDefaultDisplay();
		int width = d.getWidth();
		int height = d.getHeight();
		switch(screenSize){
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			fontSize = 34;
			break;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:		
			fontSize = 28;
			break;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			if(width > 240 && height > 432){
				fontSize = 18;
			}else{
				fontSize = 15;
			}
			break;
		default:
			fontSize = 10;
		}
	}
	
	private void setMenuScreen(){
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(),"font/Android Italic.ttf");
		
		start = (Button) findViewById(R.id.play);
		start.setTypeface(tf);
		start.setTextSize(fontSize);
		start.setOnClickListener(this);
		
		about = (Button) findViewById(R.id.about);
		about.setTypeface(tf);
		about.setTextSize(fontSize);
		about.setOnClickListener(this);
		
		exit = (Button) findViewById(R.id.exit);
		exit.setTypeface(tf);
		exit.setTextSize(fontSize);
		exit.setOnClickListener(this);
		
		logo = (ImageView) findViewById(R.id.logoImage);
		beta();
		runLogo();		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simon_menu);
		setFontSize();
		setMenuScreen();
	}
	
    public void runLogo(){
    	Handler handler = new Handler();
    	if(bt == null){
    		bt = new BackgroundThread();
    		handler.postDelayed(new Runnable(){
				@Override
				public void run() {
					new Thread(bt).start();
				}
    		}, 2000);
    	}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Thread.currentThread().interrupt();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	runLogo();
    }
    
    
	class BackgroundThread implements Runnable{
		@Override
		public void run() {
			while(run){
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						logo.setImageResource(logoId[counter]);
						if(counter == 4){
							counter = 1;
						}else{
							counter++;
						}
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}    

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.play:
			Intent play = new Intent(this, MainActivity.class);
			startActivityForResult(play, 1);
			onResume();
			break;
		case R.id.about:
			about();
			break;
		case R.id.exit:
			finish();
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {                                                   // override the back key
		if (keyCode == KeyEvent.KEYCODE_BACK) {                                                               // if the keyCode is equal to the back key
			run = false;
			finish();
			return true;                                                                                      // return true
		}
		return super.onKeyDown(keyCode, event);
	}	
	
	public void about() {
		newDialogInstance(
				"Android Simon, version 1.0" + "\nAuthor:  Kevin M. Lowe"
						+ "\nkmlowe1@gmail.com"
						+ "\nCopywrite 2014, All rights reserved"
						+ "\n"
						+ "\n\nDedicated to my wife Sibba..."
						+ "Thank you for being so patient "
						+ "with me during this project.",
				"About Android Simon.", false).show();
	}

	private AlertDialog.Builder newDialogInstance(String msg, String title,
			boolean yesNo) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setIcon(R.drawable.ic_launcher);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(msg);
		alertBuilder.setCancelable(!yesNo);

		return alertBuilder;
	}
	
	public void beta() {
    	AlertDialog.Builder beta = new AlertDialog.Builder(this);                                            // create an instance of a AlertDialog object
    	beta.setTitle(getResources().getString(R.string.betaTitle));                                         // set the title text of the box
    	beta.setMessage(getResources().getString(R.string.betaMsg));
    	beta.setIcon(R.drawable.ic_launcher);                                                                // set the Icon for the dialog box
    	beta.setCancelable(false);                                                                           // set cancelable to false
    	beta.setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() { // set the positive button text and listener
		
			@Override
			public void onClick(DialogInterface dialog, int id) {                                             // positive button click
				
			}
		});
    	AlertDialog alertDialog = beta.create();                                                             // call the create method
    	alertDialog.show();                                                                                   // show the box
    }		
	
}
