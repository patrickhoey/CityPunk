package com.drs.cyberpunk;

import java.text.DecimalFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class AlarmCamera {
	
	private static final String TAG = AlarmCamera.class.getSimpleName();
	
	private boolean lightUp = true;
	
	private boolean alarmActivated = false;
	private Color color;
	private final float MAXALPHA = .98f;
	private final float MINALPHA = .02f;
	private final float INCREMENTALPHA = .02f;
	
	private Color oldColor;
	
	private float totalTimeActivated = 0;
	private float countDownStart = 20;
	private float currentTime = -1;
	private BitmapFont messageQueueFont;
	
	private DecimalFormat df;
	
	private Vector2 messagePosition;
	
	private SoundEffect alarmSound;
	
	public AlarmCamera(){	
		color = Color.RED.cpy();
		alarmSound = new SoundEffect(Assets._ALARM_SOUND);
		alarmSound.setConcurrentPlay(false);
		
		this.messageQueueFont = new BitmapFont(
				Gdx.files.internal(Assets._MONKIRTA_PURSUIT_36_FNT),
				Gdx.files.internal(Assets._MONKIRTA_PURSUIT_36_PNG),
				false, true);
		messagePosition = new Vector2();
		
		df = new DecimalFormat("#.##"); 		
	}
	
	public void update(float delta){
		alarmSound.update(delta);
		
		if( alarmActivated ){
			totalTimeActivated += delta;
			alarmSound.play();
		}else{
			alarmSound.stop();
		}
	}
	
	public void drawAlarm(SpriteBatch batch){
		//If no more messages, remove from the message queue
		if( alarmActivated == false){
			return;
		}
		
		if(color.a <= MINALPHA){
			color.a = MINALPHA;
			lightUp = true;
		}
		
		if(color.a >= MAXALPHA){
			color.a = MAXALPHA;
			lightUp = false;
		}
		
		if( lightUp ){
			color.a += INCREMENTALPHA;
		}else{
			color.a -= INCREMENTALPHA;
		}
		
		batch.setColor(color.r,color.g, color.b, color.a);

		currentTime = MathUtils.clamp(countDownStart - totalTimeActivated, 0.0f, countDownStart);
		
		if( currentTime == 0.0f ){
			batch.setColor(oldColor.r,oldColor.g, oldColor.b, oldColor.a);
		}

	}
	
	public void drawCountDown(SpriteBatch hudBatch){
		//If no more messages, remove from the message queue
		if( alarmActivated == false){
			return;
		}
		
		String output;
		
		if( currentTime >= 10){
			output = "00:00:"+ df.format(currentTime);
		}else{
			output = "00:00:0"+ df.format(currentTime);
		}
		
		
		messageQueueFont.draw(hudBatch, output , messagePosition.x, messagePosition.y);
	}
	
	public void setAlertMessagePosition(Vector2 position){
		this.messagePosition = position;
	}
	
	public void setAlertMessagePosition(float x, float y){
		this.messagePosition.x = x;
		this.messagePosition.y = y;
	}
	
	public boolean isAlarmActivated(){
		return alarmActivated;
	}
	
	public boolean isAlarmTriggeredandCountdownFinished(){
		if( alarmActivated == true && currentTime == 0){
			return true;
		}else{
			return false;
		}
	}
	
	public void startAlarm(){
		Gdx.app.debug(TAG, "Started Alarm...");
		alarmActivated = true;
		color = Color.RED.cpy();
		color.a = MAXALPHA;
		oldColor = Color.WHITE.cpy();
		totalTimeActivated = 0;
		currentTime = -1;
	}
	
	public void stopAlarm(){
		Gdx.app.debug(TAG, "Stopped Alarm...");
		alarmActivated = false;
		totalTimeActivated = 0;
		currentTime = -1;
	}

}

