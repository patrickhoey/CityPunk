package com.drs.cyberpunk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class HealthBar extends Widget {

	private static final String TAG = HealthBar.class.getSimpleName();
	
	private Sprite backgroundBar = null;
	private Sprite foregroundBar = null;
	
	private String backgroundImagePath;
	private String foregroundImagePath;
	
	private Texture backgroundTexture = null;
	private Texture foregroundTexture = null;
	
	private int maxHealth = 0;
	private float currentPercentHealth = 0;
	private int currentHealth = 0;
	
	private boolean isInitialized = false;
	
	public HealthBar(String backgroundImage, String foregroundImage){
		this.backgroundImagePath = backgroundImage;
		this.foregroundImagePath = foregroundImage;
		
		Utility.loadTextureAsset(backgroundImagePath);
		backgroundTexture = Utility.getTextureAsset(backgroundImagePath);		
		
		if( backgroundTexture != null ){
			backgroundBar = new Sprite(backgroundTexture);
			setWidth(getPrefWidth());
			setHeight(getPrefHeight());
		}
		
		Utility.loadTextureAsset(foregroundImagePath);
		foregroundTexture = Utility.getTextureAsset(foregroundImagePath);		
		
		if( foregroundTexture != null ){
			foregroundBar = new Sprite(foregroundTexture);
		}
		
		if( backgroundTexture != null && foregroundTexture != null ){
			isInitialized = true;
		}
		
		Gdx.app.debug(TAG, "HealthBar init...");
	}
	
	public void update(float delta){
		
		if( backgroundTexture == null ){
			backgroundTexture = Utility.getTextureAsset(backgroundImagePath);		
			
			if( backgroundTexture != null ){
				backgroundBar = new Sprite(backgroundTexture);
				setWidth(getPrefWidth());
				setHeight(getPrefHeight());
			}
		}
		
		if( foregroundTexture == null ){
			foregroundTexture = Utility.getTextureAsset(foregroundImagePath);		
			
			if( foregroundTexture != null ){
				foregroundBar = new Sprite(foregroundTexture);
			}
		}
		
		if( isInitialized == false ){
			if( backgroundTexture != null && foregroundTexture != null ){
				isInitialized = true;
			}			
		}

	}
	
	public void setHealthDamage(int damage){
		
		if( damage <= 0 ){
			return;
		}
		
		int health = this.currentHealth;
		
		//Gdx.app.debug(TAG, "MaxHealth: " + maxHealth );
		//Gdx.app.debug(TAG, "DAMAGE: " + damage );
		
		health -= damage;
		
		//Gdx.app.debug(TAG, "Health: " + health );
		
		setCurrentHealth(health);
	}
	
	public void setHealthHeal(int healVal){
		
		if( healVal >= maxHealth){
			return;
		}
		
		int health = this.currentHealth;
		
		//Gdx.app.debug(TAG, "MaxHealth: " + maxHealth );
		//Gdx.app.debug(TAG, "DAMAGE: " + damage );
		
		health += healVal;
		
		//Gdx.app.debug(TAG, "Health: " + health );
		
		setCurrentHealth(health);
	}
	
	public void setMaxHealth(int maxHealth){
		this.maxHealth = maxHealth;
	}
	
	public int getMaxHealth(){
		return maxHealth;
	}
	
	public void setCurrentHealth(int currentHealth){
		this.currentHealth = MathUtils.clamp(currentHealth, 0, maxHealth);
		
		float tempPercent = (float) currentHealth / (float) maxHealth;
		
		//Gdx.app.debug(TAG, "Temp Percent: " + tempPercent );
		
		currentPercentHealth = MathUtils.clamp(tempPercent, 0, 100);
	}
	
	public int getCurrentHealth(){
		return currentHealth;
	}
	
	public float getPrefWidth () {
		if( backgroundBar == null ) return 0;
		return backgroundBar.getWidth();
	}
	
	public float getPrefHeight(){
		if( backgroundBar == null ) return 0;
		return backgroundBar.getHeight();
	}
	
	public void draw (SpriteBatch batch, float parentAlpha) {
		if( backgroundBar == null || foregroundBar == null ){
			return;
		}
		
		super.draw(batch, parentAlpha);

		backgroundBar.setPosition(getX(), getY());
		backgroundBar.draw(batch, parentAlpha);
		
		foregroundBar.setPosition(getX(), getY());
		
		//Need to also adjust size because otherwise the scale will be off
		foregroundBar.setSize(backgroundBar.getWidth()*currentPercentHealth, backgroundBar.getHeight());
		foregroundBar.setRegionWidth((int)(backgroundBar.getWidth()*currentPercentHealth));
		foregroundBar.draw(batch, parentAlpha);
	}

	public boolean isInitialized() {
		return isInitialized;
	}
	
}
