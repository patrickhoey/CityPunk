package com.drs.cyberpunk.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.drs.cyberpunk.MessageQueue;
import com.drs.cyberpunk.Utility;
import com.drs.cyberpunk.enums.ActionsEnum;
import com.drs.cyberpunk.items.InventoryItem;
import com.drs.cyberpunk.items.SoftwareItem;
import com.drs.cyberpunk.items.SoftwareItem.Functions;



public class DataWall extends Entity implements ICyberspace{
	
	private static final String TAG = DataWall.class.getSimpleName();
	
	//Need to figure out a good number. Range is 1-7?, 1-10?
	//Number of CPU's is strength
	@SuppressWarnings("unused")
	private int strength = 1;
	private String analysisData;
	
	public DataWall(){
		super();
		existsOffScreen = true;
		isHackable = false;
		isActionMenuEnabled = false;
		
		maxHealth = 200;
		setCurrentHealth(maxHealth);
		
		this.imagePath = dataWallBitmap;
		super.loadWalkingAnimation(1, 1, 1);
		
		isActionMenuEnabled = true;
		analysisData = new String(_ENTITY_NAME + CYBERSPACE_ENTITY.DATAWALL + "\n" + _WEAKNESS + InventoryItem._ICEPICK);
		selectionRayDistanceThreshold = POWER_MAX_DISTANCE;
	}
	
	@Override
	public void shutDown(){
		updateDamage(maxHealth);
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
	@SuppressWarnings("unchecked")
	public void loadInventoryItems(){
		Json reader = new Json();
		Array<InventoryItem> items = reader.fromJson(Array.class, InventoryItem.class, Gdx.files.internal("data/items/inventoryitems_DataWall.json"));
		
		for(InventoryItem item: items){
			inventoryItems.put(item.getItemID(), item);
		}
	}
	
	
	@Override
	public void receivedInventoryItemAction(Entity sender, ACTION action, InventoryItem item){
		
		if( !isAlive() ){
			return;
		}
		//Gdx.app.debug(TAG, "ENTITY Received Inventory ITEM action: ");
		
		switch(action){
		case ATTACK:
			//Can only be attacked by software
			if( !(item instanceof SoftwareItem)){
				Gdx.app.debug(TAG, "NOT a softwareitem...");
				return;
			}else{
				SoftwareItem softwareItem = (SoftwareItem)item;
				
				//Only offensive software works on datawalls
				if( softwareItem.getFunction() != Functions.OFFENSIVE_UTILITY
						&& !softwareItem.getItemID().equalsIgnoreCase(InventoryItem._ICEPICK)  ){
					return;
				}
				
				//Always chance of creating noise
				if( Utility.isRollSuccessful(.80f)){
					updateIncreaseAlert(10);
				}
				
				if( Utility.isRollSuccessful(.33f)){
					updateIncreaseAlert(25);
				}				
				
				
				//We have determined that this is an attack with offensive software
				calculateDamage(softwareItem);
				
				//Send message back
				//There is a percentage chance of attacking
				if( Utility.isRollSuccessful(.50f) ){
					softwareItem.playActivatedSound();
					MessageQueue.getInstance().addMessageToQueue(MessageQueue.createMessage(
							this.getEntityID(), 
							sender.getEntityID(), 
							ACTION.ATTACK.toString(), 
							InventoryItem._CRASH));			
				}

			}
		case NOTHING:
		default:
			
		}
	}
	
	private void calculateDamage(SoftwareItem item){
		//special cases for different types of software can go here
		//such as software that it is impervious to		
		int damage = item.getSoftwareMajorVersion()+item.getSoftwarePatchVersion();
		
		entityMessageQueue.addMessageToQueue(EntityStatusQueue.DAMAGE_MESSAGE_HEADER+String.valueOf(damage));
		defaultDamageEffect.reset();
		
		updateDamage(damage);
	}
	
	
	
}
