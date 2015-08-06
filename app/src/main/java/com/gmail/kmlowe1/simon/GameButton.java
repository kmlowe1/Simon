package com.gmail.kmlowe1.simon;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class GameButton extends Button{
	
	private int sound;
	private int index;

	public GameButton(Context context) {
		super(context);
	}
	
	public GameButton(Context context, AttributeSet attribute){
		super(context, attribute);
	}

	public final int getSound() {
		return sound;
	}

	public void setSound(int sound) {
		this.sound = sound;
	}
	
	public final int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
}
