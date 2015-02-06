package com.drs.cyberpunk.entities;

import com.badlogic.gdx.math.Vector2;
import com.drs.cyberpunk.enums.MapLocationEnum;


public interface IPlayer {
	
	public enum UPDATECREDITS {
		ADD, DEBIT, NOTHING
		}
	
	public void updateHealthBarDamage(int damage);
	public void updateHealthBarHeal(int heal);	
	
	public void playSelectionSound();
	  
	public void setIsCurrentMapInCyberspace(boolean isCyberspace);
	public boolean getIsCurrentMapInCyberspace();
	  
	public Vector2 getLastCoordinates(MapLocationEnum map);
	public void setLastCoordinates(MapLocationEnum map, Vector2 coordinates);
	  
	public int getNumberOfCredits();
	public void updateCredits(int amount, UPDATECREDITS action);
}
