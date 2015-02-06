package com.drs.cyberpunk.entities;

import com.drs.cyberpunk.SoundEffect;
import com.drs.cyberpunk.Utility;
import com.drs.cyberpunk.enums.ActionsEnum;
import com.drs.cyberpunk.items.InventoryItem;
import com.drs.cyberpunk.items.SoftwareItem;
import com.drs.cyberpunk.items.SoftwareItem.Functions;


public class CodeGate extends Entity implements ICyberspace{
	@SuppressWarnings("unused")
	private static final String TAG = CodeGate.class.getSimpleName();
	
	private SoundEffect unlockGate;
	private boolean isUnlocked = false;
	
	//Need to figure out a good number. Range is 1-7?, 1-10?
	//Number of CPU's is strength
	@SuppressWarnings("unused")
	private int strength = 1;
	
	private String analysisData;
	
	public CodeGate(){
		super();
		existsOffScreen = true;
		isHackable = false;
		isActionMenuEnabled = false;
		
		this.imagePath = codeGateClosedBitmap;
		super.loadWalkingAnimation(16, 16, 60);
		state = State.ANIMATED;
		
		unlockGate = new SoundEffect(_codegate_unlock);
		unlockGate.setConcurrentPlay(false);
		
		isActionMenuEnabled = true;
		analysisData = new String(_ENTITY_NAME + CYBERSPACE_ENTITY.CODE_GATE + "\n" + _WEAKNESS + InventoryItem._DECRYPT);
		selectionRayDistanceThreshold = POWER_MAX_DISTANCE;
	}
	
	@Override
	public void shutDown(){
		isUnlocked = true;
		unlockGate.play();
		super.loadWalkingAnimation(3, 12, 16, 16, 1);
		state = State.IDLE;
		setCollidable(false);
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
		unlockGate.update(delta);
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
				if( softwareItem.getFunction() != Functions.SYSTEM_OPERATIONAL &&
						!softwareItem.getItemID().equalsIgnoreCase(InventoryItem._DECRYPT)){
					return;
				}
				
				if( isUnlocked ){
					return;
				}
				
				setIsEntityDamaged(true);
				defaultDamageEffect.reset();
			
				//User is using decrypt
				if( Utility.isRollSuccessful(.66f)){
					updateIncreaseAlert(10);
				}
				
				if( Utility.isRollSuccessful(.33f)){
					shutDown();
				}	
				
			}
		case NOTHING:
		default:
			
		}
	}
	

	
}
