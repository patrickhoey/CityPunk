package com.drs.cyberpunk.entities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.drs.cyberpunk.entities.Entity.Direction;

public class EntityStatusQueue {
	
	private static final String TAG = EntityStatusQueue.class.getSimpleName();

	public static final String DAMAGE_MESSAGE = "DAMAGE";
	public static final String HEAL_MESSAGE = "HEAL";
	public static final String ADD_CREDITS_MESSAGE = "CREDITS";
	public static final String MESSAGE_TOKEN = "::";
	public static final String DAMAGE_MESSAGE_HEADER = DAMAGE_MESSAGE+MESSAGE_TOKEN+"-";
	public static final String HEAL_MESSAGE_HEADER = HEAL_MESSAGE+MESSAGE_TOKEN+"+";
	public static final String ADD_CREDIT_HEADER = ADD_CREDITS_MESSAGE+MESSAGE_TOKEN+"$";
	private static final int DECAY_MAX = 75;
	private static final int DECAY_RATE = 5;
	
	//private List<String> messageQueue;
	private List<String> messageQueue;
	private BitmapFont messageQueueFont;

	//entity specific attributes
	private String currentMessage;
	private int messageDecay = DECAY_MAX;
	private Vector2 currentMessagePosition;	
	private boolean isProcessingMessage = false;
	private Color damageColor = Color.WHITE.cpy();
	private Color healColor = Color.WHITE.cpy();
	private Color textColor = Color.WHITE.cpy();
	private Color creditsColor = Color.GREEN.cpy();
	
	public EntityStatusQueue(){
		this.messageQueue = Collections.synchronizedList(new LinkedList<String>());
		this.messageQueueFont = new BitmapFont();
		currentMessagePosition = new Vector2();
	}
	
	public void purgeEntityStatusQueue(){
		Gdx.app.debug(TAG, "Purge: Purging entity status queue of " + messageQueue.size() + " items...");
		messageQueue.clear();
		isProcessingMessage = false;
	}
	
	public void drawStatusMessage(SpriteBatch batch, Entity target, Direction direction){
		//Gdx.app.debug(TAG, "DRAW: ProcessingMessage is: " + isProcessingMessage + " with queue of " + messageQueue.size() + " items...");
		
		//If no more messages, remove from the message queue
		if( isProcessingMessage == false){
			return;
		}
		
		//Gdx.app.debug(TAG, "DRAW: ProcessingMessage is: " + isProcessingMessage + " with queue of " + messageQueue.size() + " items...");
		
		if( messageQueue.isEmpty() && messageDecay < 0 ){
			isProcessingMessage = false;
			return;
		}
		
		if( !messageQueue.isEmpty() && messageDecay < 0){
			currentMessagePosition.set(target.getCurrentPosition().x+(target.getFrameSprite().getHeight()/2), target.getCurrentPosition().y+(target.getFrameSprite().getWidth()+5));
			messageDecay = DECAY_MAX;
			messageQueueFont.setScale(1.0f);
			String message = messageQueue.remove(0);
			String[] messages = message.split(MESSAGE_TOKEN);
			String color = messages[0];
			
			if( color.equalsIgnoreCase(DAMAGE_MESSAGE) ){
				getDamageColor().a = 1.0f;
				messageQueueFont.setColor(getDamageColor());
			}else if(color.equalsIgnoreCase(HEAL_MESSAGE) ){
				getHealColor().a = 1.0f;
				messageQueueFont.setColor(getHealColor());
			}else if(color.equalsIgnoreCase(ADD_CREDITS_MESSAGE)){
				creditsColor.a = 1.0f;
				messageQueueFont.setColor(creditsColor);
			}else{
				getTextColor().a = 1.0f;
				messageQueueFont.setColor(getTextColor());
			}
			
			currentMessage = messages[1]; 
			messageQueueFont.setScale(2);
		}
		
		if( currentMessage != null && !currentMessage.isEmpty()){
			switch(direction){
			case LEFT:
				messageQueueFont.draw(batch, currentMessage, currentMessagePosition.x, currentMessagePosition.y);
				currentMessagePosition.set(currentMessagePosition.x-1, currentMessagePosition.y);
				break;
			case RIGHT:
				messageQueueFont.draw(batch, currentMessage, currentMessagePosition.x, currentMessagePosition.y);
				currentMessagePosition.set(currentMessagePosition.x+1, currentMessagePosition.y);
				break;
			case UP:
				messageQueueFont.draw(batch, currentMessage,  currentMessagePosition.x, currentMessagePosition.y);
				currentMessagePosition.set(currentMessagePosition.x, currentMessagePosition.y+1);
				break;
			case DOWN:
				messageQueueFont.draw(batch, currentMessage, currentMessagePosition.x, currentMessagePosition.y);
				currentMessagePosition.set(currentMessagePosition.x, currentMessagePosition.y-1);
				break;
			}	
		}
		
		Color color = messageQueueFont.getColor();
		messageQueueFont.setColor(color.r,color.g, color.b, (color.a <= 0)? 0 : color.a -.02f);
		messageDecay -= DECAY_RATE;
	}
	
	public void addMessageToQueue(String message){
		isProcessingMessage = true;
		messageQueue.add(message);
		//Gdx.app.debug(TAG, "ADD: ProcessingMessage is: " + isProcessingMessage + " with queue of " + messageQueue.size() + " items...");
	}
	
	public boolean isProcessingMessage(){
		return isProcessingMessage;
	}

	public Color getDamageColor() {
		return damageColor;
	}

	public void setDamageColor(Color damageColor) {
		this.damageColor = damageColor;
	}

	public Color getHealColor() {
		return healColor;
	}

	public void setHealColor(Color healColor) {
		this.healColor = healColor;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
}
