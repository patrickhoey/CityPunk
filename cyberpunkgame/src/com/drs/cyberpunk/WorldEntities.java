package com.drs.cyberpunk;

import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.drs.cyberpunk.entities.Entity;
import com.drs.cyberpunk.entities.Entity.State;
import com.drs.cyberpunk.entities.Player;
import com.drs.cyberpunk.enums.MapLocationEnum;
import com.drs.cyberpunk.levels.LevelEntities;


public class WorldEntities {

	private static final String TAG = WorldEntities.class.getSimpleName();

	private LevelEntities levelEntities;
	
	//The player controlled hero
	private Player player = null;
	private static WorldEntities worldEntities = null;
	
	private WorldEntities(){
		getPlayer();
	}
	
	public static WorldEntities getInstance(){
		if( worldEntities == null ){
			worldEntities = new WorldEntities();
		}
		return worldEntities;
	}
	
	public Player getPlayer(){
		if( player == null ){
			player = new Player();
			player.init(0.0f, 0.0f);
		}
		return player;
	}
	
	public void update(float delta){
		if( levelEntities != null ){
			levelEntities.update(delta);			
		}

		player.update(delta);
		
		WorldRenderer.getInstance().getCurrentMap().updateSoundScapeJukeBox(delta);
	}
	
	public Collection<Entity> getEntities(){
		if( levelEntities != null ){
			return levelEntities.getEntities();		
		}else{
			return null;
		}
	}
	
	public Collection<ParticleEffect> getParticleEffectEntities(){
		if( levelEntities != null ){
			return levelEntities.getParticleEffectEntities();	
		}else{
			return null;
		}
	}
	
	public void hide(){
		PlayerController.hide();
		player.setState(State.PAUSE);
		if( WorldRenderer.getInstance().getCurrentMap() != null ){
			WorldRenderer.getInstance().getCurrentMap().stopBackgroundMusic();
		}

	}
	
	public void show(){
		player.setState(State.IDLE);
		if( WorldRenderer.getInstance().getCurrentMap() != null ){
			WorldRenderer.getInstance().getCurrentMap().playLoadingSound();
			WorldRenderer.getInstance().getCurrentMap().startBackgroundMusic();
		}
	}
	
	public void updatePlayerMapStartCoordinates(MapLocationEnum mapLocation){
		Vector2 lastCoordinates = player.getLastCoordinates(mapLocation);
		
		if( lastCoordinates == null ){
			Gdx.app.debug(TAG, "Loading default coordinates: X:" + WorldRenderer.getInstance().getCurrentMap().getPlayerStart().getX() + "," + " Y:" + WorldRenderer.getInstance().getCurrentMap().getPlayerStart().getY() );
			player.setCurrentPosition(WorldRenderer.getInstance().getCurrentMap().getPlayerStart().getX(), WorldRenderer.getInstance().getCurrentMap().getPlayerStart().getY());
			player.setNextPosition(WorldRenderer.getInstance().getCurrentMap().getPlayerStart().getX(), WorldRenderer.getInstance().getCurrentMap().getPlayerStart().getY());
		}else{
			Gdx.app.debug(TAG, "Loading Last coordinates: X:" + lastCoordinates.x + "," + " Y:" + lastCoordinates.y );
			player.setCurrentPosition(lastCoordinates.x, lastCoordinates.y);
			player.setNextPosition(lastCoordinates.x, lastCoordinates.y);
		}
	}
	
	public void createEntities(MapLocationEnum mapLocation){
		levelEntities = LevelEntities.mapEntitiesFactory(mapLocation);
		
		//setup listeners for player
		player.setWorldRenderListener(WorldRenderer.getInstance());
		player.setIsCurrentMapInCyberspace(WorldRenderer.getInstance().getCurrentMap().isCyberspace());
		
		if( levelEntities == null){
			Gdx.app.debug(TAG, "No entities created!!" );
			return;
		}
	}
	
	public void dispose(){
		player.dispose();
		levelEntities.dispose();
	}
	
}
