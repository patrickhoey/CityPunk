package com.drs.cyberpunk;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.openal.OpenALSound;

public class SoundEffect implements Sound {

	@SuppressWarnings("unused")
	private static final String TAG = SoundEffect.class.getSimpleName();
	private Sound sound = null;
	private float soundDelta = 0.0f;
	private float soundDuration = 0.0f;
	private long soundID = -1;
	private boolean isConcurrentPlayEnabled = true;
	private String soundFilePath;
	
	public SoundEffect(String filePath) {
		this.soundFilePath = filePath;
		
		Utility.loadSoundAsset(filePath);
		
		//Gdx.app.debug(TAG, "Sound Effect " + filePath + " with duration: " + soundDuration);
	}
	
	public void update(float delta){
		soundDelta += delta;
		
		
		if( sound == null ){
			sound = Utility.getSoundAsset(soundFilePath);			
		}
		
		//Get duration of sound
		if( sound != null && soundDuration == 0.0f){
			soundDuration = ((OpenALSound)sound).duration();				
		}	

		//Gdx.app.debug(TAG, "Delta: " + delta);
		//Gdx.app.debug(TAG, "SoundTime: " + soundDelta);
	}
	
	public void setConcurrentPlay(boolean isConcurrentPlayEnabled){
		this.isConcurrentPlayEnabled = isConcurrentPlayEnabled;
	}
	
	@Override
	public long play(float volume, float pitch, float pan) {
		if( sound == null ){
			sound = Utility.getSoundAsset(soundFilePath);			
		}
		
		//Get duration of sound
		if( sound != null && soundDuration == 0.0f){
			soundDuration = ((OpenALSound)sound).duration();				
		}
		
		if( sound == null ) {
			return 0;
		}
		
		if( isConcurrentPlayEnabled ){
			soundID = sound.play(volume, pitch, pan);
		}else{
			if( soundDelta > soundDuration || soundID == -1){
				soundID = sound.play(volume, pitch, pan);
				soundDelta = 0.0f;
				}			
		}

		return soundID;
	}
	
	@Override
	public long play() {
		soundID = this.play(1);
		return soundID;
	}
	
	@Override
	public long play(float volume) {
		soundID = this.play(volume, 1, 0);
		return soundID;
	}

	@Override
	public long loop() {
		if( sound == null ) return 0;
		return sound.loop();
	}

	@Override
	public long loop(float volume) {
		if( sound == null ) return 0;
		return sound.loop(volume);
	}

	@Override
	public long loop(float volume, float pitch, float pan) {
		if( sound == null ) return 0;
		return sound.loop(volume, pitch, pan);
	}

	@Override
	public void stop() {
		if( sound == null ) return;
		sound.stop();
	}

	@Override
	public void pause() {
		if( sound == null ) return;
		sound.pause();
	}

	@Override
	public void resume() {
		if( sound == null ) return;
		sound.resume();
	}

	@Override
	public void dispose() {
		if( sound == null ) return;
		Utility.unloadAsset(soundFilePath);
	}

	@Override
	public void stop(long soundId) {
		if( sound == null ) return;
		sound.stop(soundId);
	}

	@Override
	public void pause(long soundId) {
		if( sound == null ) return;
		sound.pause(soundId);
	}

	@Override
	public void resume(long soundId) {
		if( sound == null ) return;
		sound.resume(soundId);
	}

	@Override
	public void setLooping(long soundId, boolean looping) {
		if( sound == null ) return;
		sound.setLooping(soundId, looping);
	}

	@Override
	public void setPitch(long soundId, float pitch) {
		if( sound == null ) return;
		sound.setPitch(soundId, pitch);
	}

	@Override
	public void setVolume(long soundId, float volume) {
		if( sound == null ) return;
		sound.setVolume(soundId, volume);
	}

	@Override
	public void setPan(long soundId, float pan, float volume) {
		if( sound == null ) return;
		sound.setPan(soundId, pan, volume);
	}

	@Override
	public void setPriority(long soundId, int priority) {
		if( sound == null ) return;
		sound.setPriority(soundId, priority);
	}

}
