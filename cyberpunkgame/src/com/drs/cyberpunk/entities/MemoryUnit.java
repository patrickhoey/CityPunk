package com.drs.cyberpunk.entities;

import com.badlogic.gdx.math.MathUtils;
import com.drs.cyberpunk.MessageQueue;
import com.drs.cyberpunk.Utility;
import com.drs.cyberpunk.enums.ActionsEnum;
import com.drs.cyberpunk.items.InventoryItem;
import com.drs.cyberpunk.items.SoftwareItem;
import com.drs.cyberpunk.items.SoftwareItem.Functions;



public class MemoryUnit extends Entity implements ICyberspace{
	
	@SuppressWarnings("unused")
	private static final String TAG = MemoryUnit.class.getSimpleName();
	
	//Need to figure out a good number. Range is 1-7?, 1-10?
	//Number of CPU's is strength
	@SuppressWarnings("unused")
	private int strength = 1;
	
	//Data integrity is the health points of the data wall
	private int dataIntegrity = 5;
	
	private boolean isShutDown = false;
	
	private int credits = 0;
	private String analysisData;
	
	public MemoryUnit(){
		super();
		existsOffScreen = true;
		isHackable = false;
		isActionMenuEnabled = false;
		
		//16x16 is for a page of 1024x1024 with 64x64 sprites
		this.imagePath = memoryUnitBitmap;
		super.loadWalkingAnimation(16, 16, 60);
		state = State.ANIMATED;
		
		credits = MathUtils.random(5, 50);
		
		isActionMenuEnabled = true;
		analysisData = new String(_ENTITY_NAME + CYBERSPACE_ENTITY.MEMORY_UNIT + "\n" + _WEAKNESS + InventoryItem._DOWNLOAD);
		selectionRayDistanceThreshold = POWER_MAX_DISTANCE;
	}
	
	@Override
	public void shutDown(){
		isShutDown = true;
		super.loadWalkingAnimation(1, 13, 16, 16, 1);
		state = State.IDLE;
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
	public void receivedInventoryItemAction(Entity sender, ACTION action, InventoryItem item){
		switch(action){
		case ATTACK:
			//Can only be attacked by software
			if( !(item instanceof SoftwareItem)){
				return;
			}else{
				SoftwareItem softwareItem = (SoftwareItem)item;
				
				if( softwareItem.getFunction() != Functions.SYSTEM_OPERATIONAL &&
						!softwareItem.getItemID().equalsIgnoreCase(InventoryItem._DOWNLOAD)){
					return;
				}
				
				//If no money, return
				if( credits == 0 ){
					return;
				}
				
				setIsEntityDamaged(true);
				defaultDamageEffect.reset();
				
				//User is using download
				if( !isShutDown && Utility.isRollSuccessful(.66f)){
					updateIncreaseAlert(10);
				}
				
				if( Utility.isRollSuccessful(.50f)){
					softwareItem.playActivatedSound();
					MessageQueue.getInstance().addMessageToQueue(
							MessageQueue.createMessage(
							this.getEntityID(), 
							sender.getEntityID(), 
							ACTION.GIVE_CREDIT.toString(), 
							String.valueOf(credits))
							);
					credits = 0;
					super.loadWalkingAnimation(3, 12, 16, 16, 1);
					state = State.IDLE;
					isShutDown = true;
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
