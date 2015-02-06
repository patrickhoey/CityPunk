package com.drs.cyberpunk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ShakeCamera {
	
	private static final String TAG = ShakeCamera.class.getSimpleName();
	
	private boolean isShaking = false;
	private Vector2 originCameraCenter;
	private float origShakeRadius = 30.0f;
	private float shakeRadius;
	private float randomAngle;
	private Vector2 offsetCameraCenter;
	private Vector2 currentCameraCenter;
	
	public ShakeCamera(float cameraViewportCoordsX, float cameraViewportCoordsY, float shakeRadius){
		this.shakeRadius = shakeRadius;
		this.origShakeRadius = shakeRadius;
		originCameraCenter = new Vector2();
		offsetCameraCenter = new Vector2();
		currentCameraCenter = new Vector2();
		setOriginCameraCenter(cameraViewportCoordsX/2f, cameraViewportCoordsY/2f);
		seedRandomAngle();
	}
	
	public boolean getIsCameraShaking(){
		return isShaking;
	}
	
	public void startShaking(){
		this.isShaking = true;
	}
	
	public void setOriginCameraCenter(float cameraCenterCoordsX, float cameraCenterCoordsY){
		this.originCameraCenter.x = cameraCenterCoordsX;
		this.originCameraCenter.y = cameraCenterCoordsY;
	}
	
	private void seedRandomAngle(){
		randomAngle = MathUtils.random(1, 360);
	}
	
	private void computeCameraOffset(){
		offsetCameraCenter.x =  MathUtils.sinDeg(randomAngle) * shakeRadius;
		offsetCameraCenter.y =  MathUtils.cosDeg(randomAngle) * shakeRadius;
	}
	
	private void computeCurrentCameraCenter(){
		currentCameraCenter.x = originCameraCenter.x + offsetCameraCenter.x;
		currentCameraCenter.y = originCameraCenter.y + offsetCameraCenter.y;
	}
	
	private void diminishShake(){
		if( shakeRadius < 2.0 ){
			Gdx.app.log(TAG, "DONE SHAKING: shakeRadius is: " + shakeRadius + " randomAngle is: " + randomAngle);
			isShaking = false;
			return;
		}
		
		//Gdx.app.log(TAG, "Current shakeRadius is: " + shakeRadius + " randomAngle is: " + randomAngle);
		
		isShaking = true;
		
		shakeRadius *= .9f;
		randomAngle += (150 + MathUtils.random(1, 60));
	}
	
	public void reset(){
		shakeRadius = origShakeRadius;
		isShaking = false;
		seedRandomAngle();
		computeCameraOffset();
	}
	
	public Vector2 getShakeCameraCenter(){
		computeCameraOffset();
		computeCurrentCameraCenter();
		diminishShake();
		return currentCameraCenter;
	}
}
