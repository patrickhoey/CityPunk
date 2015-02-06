package com.drs.cyberpunk.entities;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.drs.cyberpunk.entities.Entity.Direction;
import com.drs.cyberpunk.enums.ActionsEnum;
import com.drs.cyberpunk.items.InventoryItem;
import com.drs.cyberpunk.listeners.WorldRenderListener;



public interface IEntities{
	  
	  public static final String _ENTITY_NAME = "Name: ";
	  public static final String _WEAKNESS = "Weakness: ";
	
	  //all the asset paths for the entities
	  public static final String atmBitmap = "data/images/ATM_64x64.png";
	  public static final String umbrellaBitmap = "data/images/black_umbrella_64x64.png";
	  public static final String gangThugMaleWalkingAnim = "data/animation/mohawk_walk.png";
	  public static final String characterMaleWalkingAnim = "data/animation/character_walk.png";
	  public static final String dataWallBitmap = "data/images/datawall.png";
	  public static final String cpuBitmap = "data/images/cpu.png";
	  public static final String codeGateClosedBitmap = "data/images/codegate_closed.png";
	  public static final String memoryUnitBitmap = "data/images/memoryunit_64x64_animation.png";
	  public static final String doorCyberspaceBitmap = "data/images/doors.png";
	  
	  //Sounds
	  public static final String _selectionSound = "data/sounds/selection_sound.mp3";
	  //MP3's add a bit of silence in at the beginning. For precision, use wav files
	  public static final String _leftFootWalk = "data/sounds/left_footsteps_cement.wav";
	  public static final String _rightFootWalk = "data/sounds/right_footsteps_cement.wav";
	  public static final String _leftCyberspaceFootWalk = "data/sounds/cyberspace_footsteps_left.wav";
	  public static final String _rightCyberspaceFootWalk = "data/sounds/cyberspace_footsteps_right.wav";
	  public static final String _player_damaged = "data/sounds/player_damaged_cyberspace.mp3";
	  public static final String _entity_damaged = "data/sounds/cyberspace_entity_damage.mp3";
	  public static final String _cyberspace_door_open = "data/sounds/scifi_door_open.wav";
	  public static final String _cyberspace_door_close = "data/sounds/scifi_door_close.wav";
	  public static final String _player_death_cyberspace = "data/sounds/player_death_cyberspace.wav";
	  public static final String _codegate_unlock = "data/sounds/codegate_unlock.mp3";
	  public static final String _add_credits = "data/sounds/add_credits.mp3";
	  public static final String _cpu_powerdown = "data/sounds/cpu_powerdown.mp3";
	  public static final String _analysis = "data/sounds/analysis_scan.mp3";
	  
	  public void update (float delta);
	  
	  public void init(float posX, float posY);
	  
	  public String getEntityID();
	  
	  public boolean isAlive();
	  public void setIsAlive(boolean isAlive);
	  public boolean isDrawDeathEffectEnabled();
	  public void drawDeathEffectEnabled(boolean isEnabled);
	  
	  public boolean isReadyToBeRemoved();
	  
	  public void setEntityImagePath(String imagePath);
	  
	  public Vector2 getVelocity();
	  public void setVelocity(Vector2 velocity);
	  
	  public boolean needsInit();
	  public void setInit(boolean needsInit);
	  
	  public boolean existsOffScreen();
	  public void setExistsOffScreen(boolean existsOffscreen);
	   
	  public Rectangle getEntityBoundingBox();
	  
	  public ArrayList<ActionsEnum> getActionItems();
	  public void addActionItemToBeginning(String action);
	  public void addActionItemToEnd(String action);
	  public void runActionItem(String action);
	  
	  public boolean isActionMenuEnabled();
	  public void setIsActionMenuEnabled(boolean isEnabled);
	  
	  public void loadInventoryItems();
	  public InventoryItem getInventoryItem(String itemID);
	  public Collection<InventoryItem> getInventoryItems();
	
	  public void drawDamageEffect(SpriteBatch batch, float delta);
	  public void drawDeathEffect(SpriteBatch batch, float delta);
	  public void drawStatusMessage(SpriteBatch batch, float delta, Direction direction);
	  
	  public boolean isEntityDamaged();
	  public void setIsEntityDamaged(boolean isDamaged);
	  
	  public int getMaxHealth();
	  public void setCurrentHealth(int health);
	  public void updateDamage(int damageAmount);
	  
	  //listeners
	  public void setWorldRenderListener(WorldRenderListener worldRender);
	  public WorldRenderListener getWorldRenderListener();
	  public void worldRenderMapChange(String mapName);
	  
	  public void updateIncreaseAlert(int increase);
	  public void updateDecreaseAlert(int decrease);
	  
	  public void playSoundBasedOnFrameIndex(float delta);
	  public Ray getSelectionRay();
	  public boolean isSelectionRayDistanceWithinThreshold();
	  public float getSelectionThreshold();
	  public boolean isRayDistanceWithinThreshold(Rectangle target);
	  
	  public int getIdleKeyFrame();
	  public void setIdleKeyFrame(int idleKeyFrame);
	  
	  public boolean isCollidable();
	  public void setCollidable(boolean isCollidable);
	  
	  public boolean isVisible();
	  public void setIsVisible(boolean isVisible);
	  
		
	  public boolean isSelected();
	  public void setIsSelected(boolean isSelected);
	  
		
	  public boolean isHackable();
	  public void setIsHackable(boolean isHackable);
	 
}
