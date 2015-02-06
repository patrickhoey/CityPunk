package com.drs.cyberpunk;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.math.MathUtils;

public class JukeBox {

	private LinkedList<SoundEffect> currentPlayList;
	private float minimumPlayTimeRange = 0.5f;
	private float maximumPlayTimeRange = 10.0f;
	private float playTime = 0.0f;
	private float currentDelta = 0.0f;
	private boolean isStarted = false;
	
	public JukeBox(){
		currentPlayList = new LinkedList<SoundEffect>();		
	}
	
	public void start(){
		isStarted = true;
	}
	
	public void stop(){
		isStarted = false;
		for(SoundEffect sound: currentPlayList){
			sound.stop();
		}
	}
	
	public void unload(){
		stop();
		for(SoundEffect sound: currentPlayList){
			//Overriden, actually unloading from AssetManager
			sound.dispose();
		}
	}
	
	public void setPlayList(ArrayList<String> soundList){
		for(String sound: soundList){
			SoundEffect soundEffect = new SoundEffect(sound);
			soundEffect.setConcurrentPlay(false);
			
			currentPlayList.add(soundEffect);
		}
	}
	
	public void update(float delta){
		if( currentPlayList.isEmpty() || isStarted == false){
			return;
		}
		
		currentDelta += delta;
		
		for(SoundEffect soundEffect: currentPlayList){
			soundEffect.update(delta);
		}
		
		if( currentDelta >= playTime){
			currentDelta = 0.0f;
			playTime = MathUtils.random(minimumPlayTimeRange, maximumPlayTimeRange); 
			int randomIndex = MathUtils.random(0,currentPlayList.size()-1);
			currentPlayList.get(randomIndex).play();
		}
	}

	public void setPlayTimeRange(float minimum, float maximum){
		this.minimumPlayTimeRange = minimum;
		this.maximumPlayTimeRange = maximum;
	}
	
	
	
}
