package com.gmail.kmlowe1.simon;

import java.util.Random;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	
	public enum player{ COMPUTER, PERSON; }                                                                   // ENUM player defined for the turn variable 
	private final int SIZE = 4;                                                                               // Constant int for array size
	private int randomMove;                                                                                   // integer variable for random int
	private int move = 0;                                                                                     // integer index for the saved move array
	private GameButton[] buttons = new GameButton[SIZE];                                                      // Array of GameButton Object
	private SoundPool pool= new SoundPool(0, AudioManager.STREAM_MUSIC, 0);                                   // SoundPool Object
	private int poolId[] = new int[buttons.length+1];                                                         // Array of SoundPool Id's
	private int moves[] = new int[1000];                                                                      // integer array for saved computer moves
	private player turn = player.COMPUTER;                                                                    // player variable for who's turn
	private int roundNumber = 0;                                                                              // integer variable to store the current round
	private BackgroundThread bt;                                                                              // instance of a BackgroundThread object
	private boolean run = true;                                                                               // boolean variable to check whether the thread is running
	private int numbMoves = 0;                                                                                // integer variable to keep track of the number of computer moves
	private int count = 0;                                                                                    // integer variable to keep track of the number of person moves
	private TextView gameTitle;                                                                               // instance of a textView object
	private TextView scoreBoard;                                                                              // instance of a TextView object
    private TextView circleMask;                                                                              // instance of a TextView object
	private int dialogBoxFlag = 0;                                                                            // integer variable to flag if a dialogbox is active on activity destruction
    private int titleFontSize = 30;                                                                           // integer variable for the font size of the title
    private int scoreFontSize = 15;                                                                           // integer variable for the font size of the score keeper

	private void setGameBoard(){                                                                              // SetButtons function sets up the gamebuttons

		loadSoundPool();                                                                                      // LoadPool functions called to set up soundPool
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(),"font/Android Scratch.ttf");                  // load a type face
		
		gameTitle = (TextView) findViewById(R.id.game_title);                                                 // inflate the game title text box
		gameTitle.setTextColor(0xffe8e8e8);                                                                   // set game title text color
		gameTitle.setTextSize(titleFontSize);                                                                 // set game title text size
		gameTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.CLIP_HORIZONTAL);                              // set game title text orientation
		gameTitle.setTypeface(tf);                                                                            // set game title type face
		gameTitle.setText(getResources().getString(R.string.gameTitle));                                                                           // set game title text
		
		scoreBoard = (TextView) findViewById(R.id.score_board);                                               // inflate the score board text box
		scoreBoard.setTextColor(0xff0066cc);                                                                  // set score board text color
		scoreBoard.setTextSize(scoreFontSize);                                                                // set score board text size
		scoreBoard.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);                           // set score board text orientation
		scoreBoard.setText(getResources().getString(R.string.round) + ": " + (roundNumber+1));                                                 // set score board text plus the round number
		
		int buttonId[] = { R.id.Button01, R.id.Button02,                                                      // Array of buttonIds for the gamebutton objects
						   R.id.Button03, R.id.Button04 };
		
		int buttonPressed[] = { R.drawable.green_pressed_bg, R.drawable.red_pressed_bg,                       // Array of drawable id's for buttons in pressed state
							    R.drawable.yellow_pressed_bg, R.drawable.blue_pressed_bg };
		
		for(int i = 0; i < buttons.length; i++){                                                              // for loop to set up gamebutton objects array
			buttons[i] = (GameButton) findViewById(buttonId[i]);                                              // inflate the button widgets
			buttons[i].setBackgroundResource(buttonPressed[i]);                                               // set initial drawable background
			buttons[i].setOnTouchListener(myTouchListener);							                          // set up the listener for each button object                                 
			buttons[i].setSound(poolId[i]);                                                                   // set the soundpool id for each button
			buttons[i].setIndex(i);                                                                           // set the button index number
		}
		for(int i = 0; i < moves.length; i++){                                                                // initialize the moves array and set the initial value to -1
			moves[i]= -1;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void setScoreboardSize(){                                                                         // function to check the screen size and adjust widgets accordingly
		int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;  // get the screen size and store it in the local variable screenSize
		WindowManager w = getWindowManager();                                                                 // create a instance of a WindowManager object
		Display d = w.getDefaultDisplay();                                                                    // create a display object
		int width = d.getWidth();                                                                             // get the width of the device display window
		int height = d.getHeight();                                                                           // get the height of the device window
		int size;
		circleMask = (TextView) findViewById(R.id.center_mask);                                               // inflate the TextView circleMask which is the center circle on the gameboard
	    LayoutParams params = circleMask.getLayoutParams();		                                              // LayoutParams variable params to store the layout parameters for the circleMask TextView
		switch(screenSize){                                                                                   // switch on the screenSize variable
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:                                                          // if the screen layout is XLarge
//			Toast.makeText(this, "XLarge, size: " + width + "X" + height,
//					   Toast.LENGTH_LONG).show();
			params.height = 700;                                                                              // assign the height value to the circleMask params
			params.width = 700;                                                                               // assign the width value to the circleMask params
  			circleMask.setLayoutParams(params);                                                               // set the new layout parameters to the circleMask object
			titleFontSize = 80;                                                                               // set the titleFontSize
			scoreFontSize = 40;                                                                               // set the scoreFontSize
			break;                                                                                            // break out of the switch
		case Configuration.SCREENLAYOUT_SIZE_LARGE:			                                                  // if the screen layout is Large
//			Toast.makeText(this, "Large, size: " + width + "X" + height,
//					   Toast.LENGTH_LONG).show();
			size = (height+width)/6;
			params.height = size;                                                                             // assign the height value to the circleMask params
			params.width = size;                                                                              // assign the width value to the circleMask params
			circleMask.setLayoutParams(params);                                                               // set the new layout parameters to the circleMask object
			titleFontSize = (height+width) / 40;                                                                               // set the titleFontSize
			scoreFontSize = (height+width) / 80;                                                                               // set the scoreFontSize
			break;                                                                                            // break out of the switch
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:                                                          // if the screen layout is Normal
//			Toast.makeText(this, "Normal, size: " + width + "X" + height,
//					   Toast.LENGTH_LONG).show();
			size = (height+width) / 6;
			params.height = size;
			params.width = size;
			circleMask.setLayoutParams(params);
			titleFontSize = (height+width) / 70;
			scoreFontSize = (height+width) / 110;
			break;                                                                                            // break out of the switch
		default:                                                                                              // if the screen layout is none of the above, default
			params.height = 50;                                                                               // assign the height value to the circleMask params
			params.width = 50;                                                                                // assign the width value to the circleMask params
			circleMask.setLayoutParams(params);                                                               // set the new layout parameters to the circleMask object
			titleFontSize = 15;                                                                               // set the titleFontSize
			scoreFontSize = 8;                                                                                // set the scoreFontSize
		}
	}	
	
	public void loadSoundPool(){                                                                              // loadPool function sets up the sounds
		int soundId[] = {R.raw.green, R.raw.red, R.raw.yellow, R.raw.blue, R.raw.gameover };                  // integer array of soundIds
		Context context = getApplicationContext();                                                            // set context variable for application context
		for(int i = 0; i < buttons.length+1; i++){                                                            // for loop to set up the sound pool array
			poolId[i] = pool.load(context, soundId[i], 1);                                                    // poolId is equal to the pool load
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState){                                                      // onSavedInstanceState saves the values during app destruction
		super.onSaveInstanceState(outState);                                                                  
		for(int i = 0; i < moves.length; i++){                                                                // for loop to run through the moves array
			outState.putInt("" + i,  moves[i]);                                                               // output the contents of the moves array to a outState bundle
		}
		outState.putInt("randomMove", randomMove);                                                            // output the randomMove variable to the outState bundle
		outState.putInt("move", move);                                                                        // output the move variable to the outState bundle
		outState.putInt("roundNumber", roundNumber);                                                          // output the roundNumber variable to the outState bundle
		outState.putBoolean("run", run);                                                                      // output the run variable to the outState bundle
		outState.putInt("numbMoves", numbMoves);                                                              // output the numbMoves variable to the outState bundle
		outState.putInt("count", count);                                                                      // output the count variable to the outState bundle
		outState.putInt("dialogBoxFlag", dialogBoxFlag);                                                      // output the dialogBoxFlag variable to the outState bundle
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){                                         // onRestoreInstanceState is called after the onCreate to access the saved state of
		super.onRestoreInstanceState(savedInstanceState);                                                     // after recovering from an app destruction
		for(int i = 0; i < moves.length; i++){                                                                // for loop to run through the moves array
			moves[i] = savedInstanceState.getInt("" + i);                                                     // set the contents of the the savedInstanceState to the moves array
		}
		randomMove = savedInstanceState.getInt("randomMove");                                                 // set randomMove variable to the contents of the savedInstanceState bundle
		move = savedInstanceState.getInt("move");                                                             // set move variable to the contents of the savedInstanceState bundle
		roundNumber = savedInstanceState.getInt("roundNumber");                                               // set roundNumber variable to the contents of the savedInstanceState bundle
		run = savedInstanceState.getBoolean("run");                                                           // set run variable to the contents of the savedInstanceState bundle
		numbMoves = savedInstanceState.getInt("numbMoves");                                                   // set numbMoves variable to the contents of the savedInstanceState bundle
		count = savedInstanceState.getInt("count");                                                           // set count variable to the contents of the savedInstanceState bundle
		if(savedInstanceState.getInt("dialogBoxFlag") == 1){                                                  // if dialogBoxFlag is equal to 1, then the startGame Dialog box was open on app destruction
			startGame();                                                                                      // call startGame function to set the startGame dialog box on restore
		}else if(savedInstanceState.getInt("dialogBoxFlag") == 2){                                            // if dialogBoxFlag is equal to 2, then the gameOver Dialog box was open on app destruction
			gameOver();                                                                                       // call gameOver function to set the gameOver dialog box on restore
		}else if(savedInstanceState.getInt("dialogBoxFlag") == 3){                                            // if dialogBoxFlag is equal to 3, then the exitGame Dialog box was open on app destruction
			exitGame();                                                                                       // call exitGame function to set the exitGame dialog box on restore
		}else if(savedInstanceState.getInt("dialogBoxFlag") == 4){                                            // if dialogBoxFlag is equal to 4, then the reset Dialog box was open on app destruction
			reset();                                                                                          // call reset function to set the reset dialog box on restore
		}else if(run == true){                                                                                // else, no dialog box was open on app destruction
			turn = player.COMPUTER;                                                                           // set turn equal to computer
			simon();                                                                                          // start simon
		}
	}
	
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//		Log.i("Thread", "onPause called, thread should be dead");
//		Thread.currentThread().interrupt();
//	}
//	
//	protected void onResume() {
//		super.onResume();
//		Log.i("Thread", "onResume called");
//		if(bt == null){
//			Log.i("Thread", "bt is null");
//			if(run && dialogBoxFlag != 1){
//				Log.i("Thread", "continue simon");
//				simon();
//			}else if(run){
//			
//			}
//		}
//	};

	View.OnTouchListener myTouchListener = new View.OnTouchListener(){                                        // OnTouchListener
		@Override
		public boolean onTouch(View v, MotionEvent event) {                                                   // onTouch function
			Handler handler = new Handler();                                                                  // define a local instance of a Handler object
			if(run == true){                                                                                  // check if the thread is running
				return true;                                                                                  // return true to eat the event (disable the buttons)
			}else if(run == false){                                                                           // check if the thread is running
				if(event.getAction() == MotionEvent.ACTION_DOWN){                                             // filter for specific screen motion
					final GameButton button = (GameButton) v;                                                 // initialize a gamebutton object based on the view id
					toggle(button);										                                      // toggle the button sound
					count++;                                                                                  // increment the count variable
					if(isItCorrect(button) && count == numbMoves){                                            // check if the button pressed is correct and is the last
						turn = player.COMPUTER;                                                               // set the turn variable to true
						run = true;                                                                           // set the run variable to true
						roundNumber++;                                                                        // increment the round number variable			
						scoreBoard.setText(getResources().getString(R.string.round) +": " + (roundNumber+1)); 								  // update the round number displayed in the score board text box
						count = 0;                                                                            // reset the count to zero person moves
						handler.postDelayed(new Runnable(){                                                   // create a new runnable

							@Override
							public void run() {                                                               // run after delay
								button.setPressed(false);                                                     // set the pressed state of the button to false
								simon();                                                                      // call the simon function
							}
						}, 100);                                                                              // delay by 100 milliseconds
					}else if(isItCorrect(button)){                                                            // else check if the button is correct
						return false;                                                                         // return false to display the button touch
					}else if(!isItCorrect(button)){                                                           // else if the button is incorrect
						scoreBoard.setText(getResources().getString(R.string.wrong));                                                    // set the score board text to indicate a wrong move
						gameOver();                                                                           // call game over
						return true;                                                                          // return true to eat the event (disable the buttons)
					}
				}
			}                                                                       
			return false;                                                                                     // return false
		}
	};

	public void gameOver(){                                                                                   // gameOver function
		killThread();                                                                                         // call stopThread function
		incorrectMove();                                                                                      // call incorrectMove function
	}
	
	public boolean isItCorrect(GameButton button){                                                            // isItCorrect function
		boolean correct;                                                                                      // Initialize local boolean variable		
		int buttonNumb = button.getIndex();                                                                   // set the local integer variable to the button number index
		if(buttonNumb == moves[count-1]){                                                                     // check the button number index against the moves array
			correct = true;                                                                                   // if it matches, set correct variable to true
		}else{
			correct = false;                                                                                  // if not a match, set correct variable to false
		}
		return correct;                                                                                       // return correct
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {                                                   // override the back key
		if (keyCode == KeyEvent.KEYCODE_BACK) {                                                               // if the keyCode is equal to the back key
			run = false;                                                                                      // set run to false to stop thread
			exitGame();                                                                                       // call the exitGame() function
			return true;                                                                                      // return true
		}
		return super.onKeyDown(keyCode, event);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {                                                      // onCreate 
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);                                                                 // set content to XML layout id
        setScoreboardSize();                                                                                  // call the scoreBoard size function to set the object size based on the screen size
        setGameBoard();                                                                                       // call the setGameBoard function to create the gameboard
        if(savedInstanceState != null){                                                                       // if the bundle contains a saved state
        	return;                                                                                           // return
        }
        startGame();                                                                                          // else call the startGame() function
    }
       
    public void kill(){                                                                                       // kill function 
		run = false;                                                                                          // set the run variable to false 
		killThread();                                                                                         // call the killThread function to kill the BackgroundThread if it isn't null
		dialogBoxFlag = 0;                                                                                    // set the dialogBoxFlag to 0;
		finish();			    	                                                                          // call finish to exit activity
    }
      
    public void restart(){                                                                                    // restart function to restart the game
		for(int i = 0; i < moves.length; i++){                                                                // for loop to reset the moves array elements to -1 
			moves[i] = -1;
		}
		numbMoves = 0;                                                                                        // reset numbMoves variable
		count = 0;                                                                                            // reset the count variable
		roundNumber = 0;                                                                                      // reset the roundNumber variable
		turn = player.COMPUTER;                                                                               // set the turn variable to computer
		run = true;                                                                                           // set the run variable to true
		dialogBoxFlag = 0;                                                                                    // reset the dialogBoxFlag variable
		startGame();    	                                                                                  // call the startGame function
    }
        
    public void simon(){
    	Handler handler = new Handler();
    	if(turn == player.COMPUTER){
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
    }
    
    public void makeSound(final int sound){                                                                   //makeSound function creates a new thread
		new Thread(new Runnable(){

			@Override
			public void run() {                                                                               //run will make the sound
				pool.play(sound, 1f, 1f, 1, 0, 1f);
			}
		}).start();
	}
    
    public int time(){
    	int time = 1000;
    	if(roundNumber > 5 && roundNumber <= 10){
    		time = 800;
    		return time;
    	}else if(roundNumber > 10 && roundNumber <= 15){
    		time = 600;
    		return time;
    	}else if(roundNumber > 15 && roundNumber <= 20){
    		time = 400;
    		return time;
    	}else if(roundNumber > 20){
    		time = 350;
    		return time;
    	}
    	return time;
    }
    
    class BackgroundThread implements Runnable{
    	Handler handler = new Handler();
    	Random random = new Random();
    	@Override
		public void run() {
			while (run == true){
				randomMove = random.nextInt(4);	
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						if(moves[move] >= 0){
							randomMove = moves[move];
							buttons[randomMove].setPressed(true);
							toggle(buttons[randomMove]);
							move++;
							Log.i("Thread", "Thread still running");
						}else{
							buttons[randomMove].setPressed(true);
							toggle(buttons[randomMove]);
							moves[move] = randomMove;
							run = false;
							turn = player.PERSON;
							numbMoves++;
							move = 0;
						}
					}
				});

				handler.postDelayed(new Runnable(){

					@Override
					public void run() {
						if(run == true){
							buttons[randomMove].setPressed(false);
						}else{
							buttons[randomMove].setPressed(false);
							killThread();
						}
					}
				}, 200);
				try{
					Thread.sleep(time());
				}catch(InterruptedException e){
					return;
				}
			}
		}
    }
    
    public void killThread(){
    	  if(bt !=null){
    	      bt = null;
    	  }
    	}

    public void toggle(GameButton button){                                                                    //toggle function changes background and plays sound
    	makeSound(button.getSound());
    }
    
    public void startGame(){                                                                                  // startGame function displays a dialog box
    	dialogBoxFlag = 1;                                                                                    // flag to indicate the dialog box was active(used for saved state)
    	AlertDialog.Builder start = new AlertDialog.Builder(this);                                            // create an instance of a AlertDialog object
    	start.setTitle(getResources().getString(R.string.ready));                                                             // set the title text of the box
    	start.setIcon(R.drawable.ic_launcher);                                                                // set the Icon for the dialog box
    	start.setCancelable(false);                                                                           // set cancelable to false
    	start.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {                                // set the positive button text and listener
			
			@Override
			public void onClick(DialogInterface dialog, int id) {                                             // positive button click
				scoreBoard.setText(getResources().getString(R.string.round) + ": " + (roundNumber+1));                                         // set the score board text
				dialogBoxFlag = 0;                                                                            // set the dialogbox flag to 0(used for saved state)
				simon();                                                                                      // call simon function
			}
		});
    	AlertDialog alertDialog = start.create();                                                             // call the create method
    	alertDialog.show();                                                                                   // show the box
    }
    
    public void incorrectMove(){
    	dialogBoxFlag = 2;
    	makeSound(poolId[4]);
    	AlertDialog.Builder end = new AlertDialog.Builder(this);
    	end.setTitle(getResources().getString(R.string.incorrectTitle));
    	end.setMessage(getResources().getString(R.string.incorrect1) + " " + roundNumber + " "
    			     + getResources().getString(R.string.incorrect2));
    	end.setIcon(R.drawable.ic_launcher);
    	end.setCancelable(false);
    	end.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				restart();
			}
    	});
    	end.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				kill();
			}
    	});
    	AlertDialog alertDialog = end.create();
    	alertDialog.show();
    }
    
    public void exitGame(){
    	dialogBoxFlag = 3;
    	AlertDialog.Builder exit = new AlertDialog.Builder(this);
    	exit.setTitle(getResources().getString(R.string.exitGameTitle));
    	exit.setIcon(R.drawable.ic_launcher);
    	exit.setCancelable(false);
    	exit.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				kill();
			}
    	});
    	
    	exit.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				reset();
			}
    		
    	});
    	AlertDialog alertDialog = exit.create();
    	alertDialog.show();
    }
    
    public void reset(){
    	dialogBoxFlag = 4;
    	AlertDialog.Builder reset = new AlertDialog.Builder(this);
    	reset.setTitle(getResources().getString(R.string.resetGameTitle));
    	reset.setIcon(R.drawable.ic_launcher);
    	reset.setCancelable(false);
    	reset.setPositiveButton(getResources().getString(R.string.restart), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				restart();
			}
    	});
    	reset.setNegativeButton(getResources().getString(R.string.continueGame), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;	
			}
    	});
    	AlertDialog alertDialog = reset.create();
    	alertDialog.show();
    }   
}