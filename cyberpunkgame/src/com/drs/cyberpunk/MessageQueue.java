package com.drs.cyberpunk;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.drs.cyberpunk.entities.Entity;
import com.drs.cyberpunk.entities.IEntityInteraction.ACTION;
import com.drs.cyberpunk.entities.IPlayer.UPDATECREDITS;
import com.drs.cyberpunk.items.InventoryItem;

public class MessageQueue {
	
	private static final String TAG = MessageQueue.class.getSimpleName();
	public static final String MESSAGE_TOKEN = "::";
	
	private List<String> messageQueue;
	
	private float currentDelta = 0;
	private final float deltaThreshold = .10f; //time to process messages
	private final int MESSAGE_HEADER_LENGTH = 4;
	
	private static MessageQueue entityMessageQueue = null;
	
	private MessageQueue(){
		this.messageQueue = Collections.synchronizedList(new LinkedList<String>());
	}
	
	public static MessageQueue getInstance(){
		if( entityMessageQueue == null ){
			entityMessageQueue = new MessageQueue();
		}
		return entityMessageQueue;
	}
	
	public static String createMessage(String senderID, String receiverID, String action, String inventoryItemID){
		return new String(
				senderID + MESSAGE_TOKEN 
				+ receiverID + MESSAGE_TOKEN 
				+ action + MESSAGE_TOKEN + 
				inventoryItemID);
	}
	
	public void addMessageToQueue(String message){
		messageQueue.add(message);
	}
	
	public void update(float delta){
		if( messageQueue.isEmpty() ) return;
		currentDelta += delta;
	}
	
	public void purgeMessageQueue(){
		Gdx.app.debug(TAG, "Purge: Purging message queue of " + messageQueue.size() + " items...");
		messageQueue.clear();
	}
	
	public void processMessage(){
		if( messageQueue.isEmpty() ) return;
		
		if( currentDelta < deltaThreshold){
			return;
		}
		
		currentDelta = 0;

		//Process next message
		String message = messageQueue.remove(0);
		
		//Gdx.app.debug(TAG, "Current message: " + message);
		
		String[] messages = message.split(MESSAGE_TOKEN);
		
		if( messages.length != MESSAGE_HEADER_LENGTH){
			//We have a bad message
			Gdx.app.debug(TAG, "Bad Message Header");
			return;
		}
		
		Entity sender = getEntity(messages[0]);
		if( sender == null ){
			Gdx.app.debug(TAG, "Sender NULL");
			return;
		}
		Entity target = getEntity(messages[1]);
		if( target == null ){
			Gdx.app.debug(TAG, "Target NULL");
			return;
		}
		
		ACTION action = ACTION.valueOf(messages[2]);
		String lastPartMessage = messages[3];
		
		if( lastPartMessage.isEmpty() ){
			return;
		}
				
		switch(action){
		case ATTACK:
			InventoryItem inventoryItem = sender.getInventoryItem(lastPartMessage);	
			target.receivedInventoryItemAction(sender, action, inventoryItem);
			break;
		case GIVE_CREDIT:
			int creditAmount = Integer.valueOf(lastPartMessage).intValue();
			WorldEntities.getInstance().getPlayer().updateCredits(creditAmount, UPDATECREDITS.ADD);
			break;
		case NOTHING:
			break;
		default:
			break;
		}
	}
	
	
	private Entity getEntity(String entityID){
		if( entityID == null || entityID.isEmpty() ){
			return null;
		}
		
		//First, see if ID is player
		if( WorldEntities.getInstance().getPlayer().getEntityID().equalsIgnoreCase(entityID) ){
			return WorldEntities.getInstance().getPlayer();
		}
		
		for( Entity entity: WorldEntities.getInstance().getEntities()){
			if( entity.getEntityID().equalsIgnoreCase(entityID) ){
				return entity;
			}
		}
		
		//If no player, and no entity, then the ID does not exist
		return null;
	}

}

