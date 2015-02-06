package com.drs.cyberpunk.entities;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.drs.cyberpunk.SoundEffect;
import com.drs.cyberpunk.enums.MapLocationEnum;
import com.drs.cyberpunk.items.InventoryItem;
import com.drs.cyberpunk.items.SoftwareItem;
import com.drs.cyberpunk.items.SoftwareItem.Functions;


public class Player extends Entity implements IPlayer {
	
	private static final String TAG = Player.class.getSimpleName();

	private SoundEffect leftFootStep;
	private SoundEffect rightFootStep;
	private SoundEffect leftFootStepCyberspace;
	private SoundEffect rightFootStepCyberspace;
	private SoundEffect selectionSound;
	private SoundEffect playerDeath;
	private SoundEffect addCredits;
	
	private HashMap<MapLocationEnum, Vector2> lastMapLocation;
	
	private boolean isCyberspace = false;
	
	private int credits = 100;
	
	public Player(){
		super();
		existsOffScreen = true;
		
		this.imagePath = characterMaleWalkingAnim;
		super.loadWalkingAnimation(16, 16, 8);
		
		lastMapLocation = new HashMap<MapLocationEnum, Vector2>(); 
		
		maxHealth = 100;
		setCurrentHealth(maxHealth);
		
		entityMessageQueue.setDamageColor(Color.RED.cpy());
		entityMessageQueue.setHealColor(Color.GREEN.cpy());
		
		leftFootStep = new SoundEffect(_leftFootWalk);
		leftFootStep.setConcurrentPlay(false);
		
		leftFootStepCyberspace = new SoundEffect(_leftCyberspaceFootWalk);
		leftFootStepCyberspace.setConcurrentPlay(false);
		
		rightFootStep = new SoundEffect(_rightFootWalk);
		rightFootStep.setConcurrentPlay(false);
		
		rightFootStepCyberspace = new SoundEffect(_rightCyberspaceFootWalk);
		rightFootStepCyberspace.setConcurrentPlay(false);
		
		selectionSound = new SoundEffect(_selectionSound);
		
		entityDamagedSound = new SoundEffect(_player_damaged);
		
		playerDeath = new SoundEffect(_player_death_cyberspace);
		playerDeath.setConcurrentPlay(false);
		
		addCredits = new SoundEffect(_add_credits);
		
		selectionRayDistanceThreshold = 100;
	}	
	
	@Override
	public void playSelectionSound(){
		selectionSound.play();
	}
	
	@Override
	public Vector2 getLastCoordinates(MapLocationEnum map){
		Vector2 lastCoordinates = lastMapLocation.get(map);
		
		if( lastCoordinates == null ){
			return null;
		}else{
			return lastCoordinates;
		}
	}
	
	@Override
	public void setLastCoordinates(MapLocationEnum map, Vector2 coordinates){
		//Don't save if we don't have a real location
		if( coordinates.x == 0.0f && coordinates.y == 0.0f ){
			return;
		}
		
		float saveX = coordinates.x;
		float saveY = coordinates.y;

		coordinates.set(saveX, saveY);
		
		Gdx.app.debug(TAG, "setLastCoordinates; Saving map " + map.toString() + " x " + saveX + "," + saveY  + "y");
		
		lastMapLocation.put(map, coordinates);
	}
	
	@Override
	public void setIsCurrentMapInCyberspace(boolean isCyberspace){
		this.isCyberspace = isCyberspace;
	}
	
	@Override
	public boolean getIsCurrentMapInCyberspace(){
		return isCyberspace;
	}
	
	@Override
	public void update(float delta){
		super.update(delta);
		
		//This is called every update cycle
		//Call update on all effects here
		leftFootStep.update(delta);
		rightFootStep.update(delta);
		leftFootStepCyberspace.update(delta);
		rightFootStepCyberspace.update(delta);
		selectionSound.update(delta);
		playerDeath.update(delta);
		addCredits.update(delta);
	}
	
	@Override
	public void playSoundBasedOnFrameIndex(float delta){	
		int currentFrameIndex = getCurrentFrameIndex();
		//Currently, walking animation is 8 frames (indexed 0-7)
		//Left foot is frame #3
		//Right foot is frame #7
		if( currentFrameIndex == 2 ){
			if( isCyberspace ){
				leftFootStepCyberspace.play();
			}else{
				leftFootStep.play();
			}
	
		}else if( currentFrameIndex == 6 ){
			if( isCyberspace ){
				rightFootStepCyberspace.play();
			}else{
				rightFootStep.play();
			}
		}
		
		//Gdx.app.debug(TAG, "Current Frame: " + currentFrameIndex );
	}
	
	@Override
	public void updateHealthBarDamage(int damage){
		worldRender.updateDamagePlayerHealthUI(damage);
	}
	
	@Override
	public void updateHealthBarHeal(int heal){
		worldRender.updateHealPlayerHealthUI(heal);
	}
	
	@Override 
	@SuppressWarnings("unchecked")
	public void loadInventoryItems(){
		Json reader = new Json();
		Array<InventoryItem> items = reader.fromJson(Array.class, InventoryItem.class, Gdx.files.internal("data/items/inventoryitems.json"));
		
		for(InventoryItem item: items){
			inventoryItems.put(item.getItemID(), item);
		}
	}
	
	@Override
	public int getNumberOfCredits(){
		return credits;
	}
	
	@Override
	public void updateCredits(int amount, UPDATECREDITS action){
		switch(action){
		case ADD:
			credits += amount;
			entityMessageQueue.addMessageToQueue(EntityStatusQueue.ADD_CREDIT_HEADER+String.valueOf(amount));
			addCredits.play();
			break;
		case DEBIT:
			credits -= amount;
			break;
		case NOTHING:
			break;
		default:
			break;
		}
	}
	
	@Override
	public void receivedInventoryItemAction(Entity sender, ACTION action, InventoryItem item){
		
		//Gdx.app.debug(TAG, "PLAYER Received Inventory ITEM action: ");
		
		if( !isAlive() ){
			return;
		}
		
		switch(action){
		case ATTACK:
			//Can only be attacked by software
			if( !(item instanceof SoftwareItem)){
				return;
			}else{
				SoftwareItem softwareItem = (SoftwareItem)item;
				
				//Only offensive software works
				if( softwareItem.getFunction() != Functions.OFFENSIVE_UTILITY){
					return;
				}
				
				//We have determined that this is an attack with offensive software
				calculateDamage(softwareItem);
			}
		case NOTHING:
		default:
			
		}
	}
	
	private void calculateDamage(SoftwareItem item){
		//special cases for different types of software can go here
		//such as software that it is impervious to
		int damage = item.getSoftwareMajorVersion()+item.getSoftwarePatchVersion();

		updateHealthBarDamage(damage);
		
		//Gdx.app.debug(TAG, "DAMAGE TO PLAYER : " + damage);
		setIsEntityDamaged(true);
		worldRender.startShakingCamera();
		frameSprite.setColor(Color.WHITE.cpy());
		
		entityMessageQueue.addMessageToQueue(EntityStatusQueue.DAMAGE_MESSAGE_HEADER+String.valueOf(damage));
		//Gdx.app.debug(TAG, "RESETTING DAMAGE EFFECT...");
		defaultDamageEffect.reset();
		
		updateDamage(damage);
	}
	
	@Override 	  
	public void setIsAlive(boolean isAlive){
		super.setIsAlive(isAlive);
		
		if( !isAlive() && isCyberspace){
			playerDeath.play();
		}
	}
	
	@Override
	public void drawDamageEffect(SpriteBatch batch, float delta){
		if( !isAlive() || isEntityDamaged == false){
			return;
		}

		//Gdx.app.debug(TAG, "DRAWING DAMAGE EFFECT...");
		defaultDamageEffect.setPosition((currentPlayerPosition.x+(WIDTH/2)), (currentPlayerPosition.y+(HEIGHT)/2));
		defaultDamageEffect.draw(batch, delta);
		
		Color color = frameSprite.getColor();
		

		frameSprite.setColor(color.lerp(1.0f,0.0f,0.0f,(color.a <= 0)? .1f : color.a -.1f, 0.5f));
		
		if( color.a <= .1){
			setIsEntityDamaged(false);
			frameSprite.setColor(Color.RED.cpy().lerp(Color.WHITE.cpy(), 1.0f));
		}

	}
	
}
