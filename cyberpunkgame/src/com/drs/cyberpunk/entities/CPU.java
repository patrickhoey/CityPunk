package com.drs.cyberpunk.entities;

import com.badlogic.gdx.math.Rectangle;
import com.drs.cyberpunk.SoundEffect;
import com.drs.cyberpunk.Utility;
import com.drs.cyberpunk.items.InventoryItem;
import com.drs.cyberpunk.items.SoftwareItem;
import com.drs.cyberpunk.items.SoftwareItem.Functions;
import com.drs.cyberpunk.entities.ICyberspace;
import com.drs.cyberpunk.enums.ActionsEnum;



public class CPU extends Entity implements ICyberspace{
	
	@SuppressWarnings("unused")
	private static final String TAG = CPU.class.getSimpleName();
	
	//Need to figure out a good number. Range is 1-7?, 1-10?
	//Number of CPU's is strength
	@SuppressWarnings("unused")
	private int strength = 1;
	
	//Data integrity is the health points of the data wall
	private int dataIntegrity = 5;
	
	private boolean isCrashed = false;
	
	private SoundEffect powerDown;
	private String analysisData;
	
	public CPU(){
		super();
		existsOffScreen = true;
		isHackable = false;
		isActionMenuEnabled = false;
		
		this.imagePath = cpuBitmap;
		super.loadWalkingAnimation(16, 16, 90);
		state = State.ANIMATED;
		
		powerDown = new SoundEffect(_cpu_powerdown);
		powerDown.setConcurrentPlay(false);
		
		isActionMenuEnabled = true;
		analysisData = new String(_ENTITY_NAME + CYBERSPACE_ENTITY.CPU + "\n" + _WEAKNESS + InventoryItem._CRASH);
		selectionRayDistanceThreshold = POWER_MAX_DISTANCE;
	}
	
	@Override
	public void shutDown(){
		powerDown.play();
		isCrashed = true;
		super.loadWalkingAnimation(5, 10, 16, 16, 1);
		state = State.IDLE;
	}
	
	public boolean isPoweredDown(){
		return isCrashed;
	}
	
	public boolean isPowerSource(Rectangle targetBoundingBox){
		if( targetBoundingBox == null ) return false;
		
		return isRayDistanceWithinThreshold(targetBoundingBox);
	}
	
	@Override
	public void runActionItem(String actionStr){
		ActionsEnum action = ActionsEnum.valueOf(actionStr);
		if( action == null) return;

		if( action.compareTo(ActionsEnum.EXIT) == 0 ){
			return;
		}else if( action.compareTo(ActionsEnum.ANALYZE) == 0 ){
			worldRender.addMessageToTerminal(analysisData);
			return;
		}
	}
	
	@Override
	public void update(float delta){
		super.update(delta);
		powerDown.update(delta);
	}
	
	@Override
	public void receivedInventoryItemAction(Entity sender, ACTION action, InventoryItem item){
		switch(action){
		case ATTACK:
			//Can only be attacked by software
			if( !(item instanceof SoftwareItem)){
				return;
			}else{
				SoftwareItem softwareItem = (SoftwareItem)item;
				
				//Only offensive software works on datawalls
				if( softwareItem.getFunction() != Functions.OFFENSIVE_UTILITY
						&& !softwareItem.getItemID().equalsIgnoreCase(InventoryItem._CRASH)  ){
					return;
				}
				
				if( isCrashed ){
					return;
				}
				
				setIsEntityDamaged(true);
				defaultDamageEffect.reset();
				
				//Always chance of creating noise
				if( Utility.isRollSuccessful(.80f)){
					updateIncreaseAlert(10);
				}
				
				if( Utility.isRollSuccessful(.25f)){
					shutDown();
				}	
				
				
			}
		case NOTHING:
		default:
			
		}
	}
	
	@SuppressWarnings("unused")
	private void calculateDamage(SoftwareItem item){
		//special cases for different types of software can go here
		//such as software that it is impervious to
		int damage = item.getSoftwareMajorVersion()+item.getSoftwarePatchVersion();
		dataIntegrity -= damage;
		
		if( dataIntegrity <= 0){
			setIsAlive(false);
		}
	}
	
}
