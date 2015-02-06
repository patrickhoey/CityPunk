package com.drs.cyberpunk.entities;

public class GangThugNPC extends Entity {

	@SuppressWarnings("unused")
	private static final String TAG = GangThugNPC.class.getSimpleName();
	
	public GangThugNPC(){
		super();
		
		existsOffScreen = true;
		
		this.imagePath = gangThugMaleWalkingAnim;
		super.loadWalkingAnimation(16, 16, 8);
	}
	
}
