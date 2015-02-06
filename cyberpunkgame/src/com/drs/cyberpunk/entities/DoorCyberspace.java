package com.drs.cyberpunk.entities;

import com.drs.cyberpunk.SoundEffect;

public class DoorCyberspace extends Entity{
	
	@SuppressWarnings("unused")
	private static final String TAG = DoorCyberspace.class.getSimpleName();
	
	private boolean isOpen = false;
	
	private SoundEffect doorOpen;
	private SoundEffect doorClose;
	
	public DoorCyberspace(){
		super();
		existsOffScreen = true;
		isHackable = false;
		isActionMenuEnabled = false;
		
		this.imagePath = doorCyberspaceBitmap;
		super.loadWalkingAnimation(16, 16, 6);
		state = State.IDLE;
		
		doorOpen = new SoundEffect(_cyberspace_door_open);
		doorClose = new SoundEffect(_cyberspace_door_close);
	}
	
	public void doorOpenPlay(){
		doorOpen.play();
	}
	
	public void doorClosePlay(){
		doorClose.play();
	}

	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public void update(float delta){
		super.update(delta);
		
		//This is called every update cycle
		//Call update on all effects here
		doorOpen.update(delta);
		doorClose.update(delta);
	}
	
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
		
		if( isOpen ){
			setCollidable(false);
			setIdleKeyFrame(5);
			//Gdx.app.debug(TAG, "Last frame cause it's open!");
		}else{
			setCollidable(true);
			setIdleKeyFrame(0);
			//Gdx.app.debug(TAG, "First frame cause it's closed!");
		}
	}


}
