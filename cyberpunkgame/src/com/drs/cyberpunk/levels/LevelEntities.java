package com.drs.cyberpunk.levels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.drs.cyberpunk.WorldRenderer;
import com.drs.cyberpunk.entities.Entity;
import com.drs.cyberpunk.entities.Entity.State;
import com.drs.cyberpunk.enums.MapLocationEnum;


public abstract class LevelEntities implements ILevelEntities{

	private static final String TAG = LevelEntities.class.getSimpleName();
	protected HashMap<String, Entity> entityMap;
	protected ArrayList<ParticleEffect> particleEffects;
	
	public static LevelEntities mapEntitiesFactory(MapLocationEnum mapLocation) {
		Gdx.app.debug(TAG, "Returning object for: " + mapLocation.toString());
		LevelEntities levelEntities = null;
		if( mapLocation.toString().equalsIgnoreCase(MapLocationEnum.CITYPLAZA001.toString())){		
			levelEntities = new CityPlazaEntities();
		}else if( mapLocation.toString().equalsIgnoreCase(MapLocationEnum.SUBGRID001.toString())){		
			levelEntities = new SubGrid001Entities();
		}else{
			return null;
		}
		
		return levelEntities;
	}
	
	
	public LevelEntities(){	
		entityMap = new HashMap<String, Entity>();
		particleEffects = new ArrayList<ParticleEffect>();
	}
	
	protected boolean isCurrentPositionOnScreen(Vector2 position){
		//Gdx.app.debug(TAG, "CurrentCameraMINIMUMPosition: (" +  world.getCurrentCameraBoundingBox().min.x + "," +  world.getCurrentCameraBoundingBox().min.y + ")");
		//Gdx.app.debug(TAG, "CurrentCameraMAXPosition: (" +  world.getCurrentCameraBoundingBox().max.x + "," +  world.getCurrentCameraBoundingBox().max.y + ")");
		//Gdx.app.debug(TAG, "Current actor position (" + position.x + "," + position.y + "," + position.z + ")");
		return WorldRenderer.getInstance().getCurrentCameraBoundingBox().contains(position);
	}
	
	protected boolean isPotentialSpawnPointBlocked(Rectangle boundingBox){
	
		boolean isMapCollision = WorldRenderer.getInstance().isCollisionWithMap(boundingBox);
		
		boolean isPlayerCollision = WorldRenderer.getInstance().isCollisionWithPlayer(boundingBox);
		
		boolean isEntityCollision = WorldRenderer.getInstance().isSpawnCollisionWithEntity(boundingBox);
		
		return isMapCollision|isPlayerCollision|isEntityCollision;
	}
	
	protected boolean isEntitySpawnPointBlocked(Entity entity, float delta){
		
		entity.calculateNextPosition(entity.getCurrentDirection(),delta);
		entity.setState(State.WALKING);
		
		boolean isMapCollision = WorldRenderer.getInstance().isCollisionWithMap(entity);
		
		boolean isPlayerCollision = WorldRenderer.getInstance().isCollisionWithPlayer(entity);
		
		boolean isEntityCollision = WorldRenderer.getInstance().isCollisionWithEntity(entity);
		
		return isMapCollision|isPlayerCollision|isEntityCollision;
	}
	
	@Override
	 public Collection<Entity> getEntities(){
		return entityMap.values();
	}
	
	@Override
	public Collection<ParticleEffect> getParticleEffectEntities(){
		return particleEffects;
	}
	
	//caller better check for null
	@Override
	public Entity getEntity(String entityID){
		return entityMap.get(entityID);
	}
	
	@Override
	public void dispose(){
		Iterator<Entity> iter = entityMap.values().iterator();
		
		while(iter.hasNext()){
			Entity actor = iter.next();
			iter.remove();
			actor.dispose();
			continue;
		}
		
		Iterator<ParticleEffect> iterEffect = particleEffects.iterator();
		
		while(iterEffect.hasNext()){
			ParticleEffect effect = iterEffect.next();
			iterEffect.remove();
			effect.dispose();
			continue;
		}
		
	}
	
}
