package com.drs.cyberpunk.entities;

public class UmbrellaNPC extends Entity {

	@SuppressWarnings("unused")
	private static final String TAG = UmbrellaNPC.class.getSimpleName();
	
	public UmbrellaNPC(){
		super();
		
		existsOffScreen = true;
		
		this.imagePath = umbrellaBitmap;
		super.loadWalkingAnimation(1, 1, 1);
	}
	
}
