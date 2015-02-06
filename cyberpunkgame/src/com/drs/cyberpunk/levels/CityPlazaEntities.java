package com.drs.cyberpunk.levels;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.drs.cyberpunk.Assets;
import com.drs.cyberpunk.WorldRenderer;
import com.drs.cyberpunk.entities.Entity;
import com.drs.cyberpunk.entities.Entity.Direction;
import com.drs.cyberpunk.entities.Entity.State;
import com.drs.cyberpunk.entities.EntityPool;
import com.drs.cyberpunk.entities.GangThugNPC;
import com.drs.cyberpunk.entities.UmbrellaNPC;

public class CityPlazaEntities extends LevelEntities {

	private static final String TAG = CityPlazaEntities.class.getSimpleName();
	
	private EntityPool<UmbrellaNPC> umbrellaPool;
	private EntityPool<GangThugNPC> gangThugPool;
	private Entity atmMachine;
	
	private final int maximumUmbrellaPopulation = 25;
	private final int maximumGangThugPopulation = 5;
	
	private boolean loadedStaticEntities = false;
	
	public CityPlazaEntities(){
		super();

		//create static entities		
		atmMachine = new Entity(Assets._ATM_ENTITY);
		atmMachine.setStatic(true);
		entityMap.put(atmMachine.getEntityID(), atmMachine);
			
		//Add all game object pools here
		umbrellaPool = new EntityPool<UmbrellaNPC>(UmbrellaNPC.class,maximumUmbrellaPopulation);
		umbrellaPool.spawnAllEntities();
		
		for(UmbrellaNPC npc: umbrellaPool.getEntitiesInPool()){
			entityMap.put(npc.getEntityID(), npc);			
		}
		
		//Add all game object pools here
		gangThugPool = new EntityPool<GangThugNPC>(GangThugNPC.class,maximumGangThugPopulation);
		gangThugPool.spawnAllEntities();
		
		for(GangThugNPC npc: gangThugPool.getEntitiesInPool()){
			entityMap.put(npc.getEntityID(), npc);			
		}

	}
	
	public void update(float delta){
		//Check for dead entities
		umbrellaPool.freeDeadEntities();
		gangThugPool.freeDeadEntities();
		
		//refresh population of entities
		umbrellaPool.maintainPopulation();
		gangThugPool.maintainPopulation();
		
		spawnEntities(delta);
		
		updateEntityPositions(delta);
		
	}

	private void loadStaticEntities(){
		if( loadedStaticEntities == true ){
			return;
		}
		
		int numberSpawnPoints = WorldRenderer.getInstance().getCurrentMap().getStaticEntitySpawnPoints().size();
		
		if( numberSpawnPoints == 0){
			Gdx.app.debug(TAG, "Map is not loaded. No static entity spawn points...");
			return;
		}
		
		//setup npc's		
		for(RectangleMapObject mapObject: WorldRenderer.getInstance().getCurrentMap().getStaticEntitySpawnPoints()){
			String typeName = (String)mapObject.getProperties().get("Type");
			if( typeName == null){
				continue;
			}
			if( typeName.equalsIgnoreCase(ILevelEntities.ATM_START)){
				
				if( atmMachine.needsInit() ){
					atmMachine.setVelocity(new Vector2(0,0));
					atmMachine.setDirection(Direction.RIGHT);
					atmMachine.setInit(false);
					
					atmMachine.init(mapObject.getRectangle().getX(), mapObject.getRectangle().getY());
					}
			}else if(typeName.equalsIgnoreCase(ILevelEntities.FIRE_EFFECT_START)){
				ParticleEffect fireEffect = new ParticleEffect();
				fireEffect.load(Gdx.files.internal(Assets.barrelFireParticleEffect), Gdx.files.internal(Assets.EFFECTS_DIR));
				
				fireEffect.setPosition(
						mapObject.getRectangle().getX()+(mapObject.getRectangle().getWidth()/2), 
						mapObject.getRectangle().getY()+(mapObject.getRectangle().getHeight()/2)
						);
				
				for(ParticleEmitter emitter: fireEffect.getEmitters()){
					emitter.setContinuous(true);
				}

				particleEffects.add(fireEffect);
			}else if(typeName.equalsIgnoreCase(ILevelEntities.GRATE_SMOKE_EFFECT_START)){
				ParticleEffect smokeEffect = new ParticleEffect();
				smokeEffect.load(Gdx.files.internal(Assets.grateSmokeParticleEffect), Gdx.files.internal(Assets.EFFECTS_DIR));
				
				for(ParticleEmitter emitter: smokeEffect.getEmitters()){
					emitter.getSpawnHeight().setHigh(mapObject.getRectangle().getHeight());
					emitter.getSpawnWidth().setHigh(mapObject.getRectangle().getWidth());
					emitter.setContinuous(true);
				}
				//Gdx.app.debug(TAG, "Smoke Particle Width/Height: (" + mapObject.getRectangle().getWidth() + "," + mapObject.getRectangle().getHeight() + ")");
				//Gdx.app.debug(TAG, "Smoke Particle Position: (" + mapObject.getRectangle().getX() + "," + mapObject.getRectangle().getY() + ")");
				
				smokeEffect.setPosition(
						mapObject.getRectangle().getX()+(mapObject.getRectangle().getWidth()/2), 
						mapObject.getRectangle().getY()+(mapObject.getRectangle().getHeight()/2)
						);

				particleEffects.add(smokeEffect);
			}
		}
		
		loadedStaticEntities = true;
	}
	@Override
	public void spawnEntities(float delta){
		int numberSpawnPoints = WorldRenderer.getInstance().getCurrentMap().getNumberNPCSpawnPoints();
		
		if( numberSpawnPoints == 0 ){
			return;
		}
		
		Iterator<Entity> iter = entityMap.values().iterator();
		
		while(iter.hasNext()){
			numberSpawnPoints = WorldRenderer.getInstance().getCurrentMap().getNumberNPCSpawnPoints();
			Entity actor = iter.next();
			
			if( actor.isStatic() ){
				continue;
			}
			
			//If not, then spawn
			if( actor.needsInit() ){
				//Find a location to spawn
				float startX = 0;
				float startY = 0;
				boolean foundPosition = false;
				Rectangle spawnPoint;
				
				actor.setIsAlive(true);
				actor.setCollidable(false);
				actor.setIsVisible(false);
				
				while( foundPosition == false && numberSpawnPoints != 0){
					numberSpawnPoints--;
					
					spawnPoint = WorldRenderer.getInstance().getCurrentMap().getNextNPCSpawnPoint();
					
					if( spawnPoint == null){
						continue;
					}
					
					if( !isPotentialSpawnPointBlocked(spawnPoint)){
						startX = spawnPoint.x;
						startY = spawnPoint.y;
						foundPosition = true;
						if( actor instanceof GangThugNPC){
							actor.setVelocity(new Vector2(70, 70));
						}else{
							actor.setVelocity(new Vector2(MathUtils.random(20, 70), MathUtils.random(20, 70)));
						}

						actor.setDirection(actor.getCurrentDirection().getRandomNext());
						
						//After finding the location position, set the init
						actor.init(startX, startY);
						
						boolean isBlocked = isEntitySpawnPointBlocked(actor, delta);
						
						if( isBlocked ){
							actor.setInit(true);
						}else{
							actor.setCollidable(true);
							actor.setIsVisible(true);
							actor.setState(State.WALKING);
							actor.setNextPositionToCurrent();
						}

						//Gdx.app.debug(TAG, "Found good spawning position");
					}
				}
			}
		}
		
		//Add new objects if needed
		umbrellaPool.spawnAllEntities();
		gangThugPool.spawnAllEntities();
		
		loadStaticEntities();
	}
	
	public void updateEntityPositions(float delta){		
		Iterator<Entity> iter = entityMap.values().iterator();
		
		while(iter.hasNext()){
			Entity actor = iter.next();
			if( actor.isReadyToBeRemoved() ){
				//Gdx.app.debug(TAG, "Could not spawn. Killing...");
				iter.remove();
				actor.dispose();
				continue;
			}
			
			actor.update(delta);
			
			//Gdx.app.debug(TAG, "ACTOR position is: (" + actor.getCurrentPosition().x + "," + actor.getCurrentPosition().y + ")");
			//Gdx.app.debug(TAG, "ACTOR position AFTER COLLISION (BOTTOM RIGHT) is: (" + (actor.getCurrentPosition().x + actor.getEntityBoundingBox().width) + "," + actor.getCurrentPosition().y + ")");
			//Gdx.app.debug(TAG, "ACTOR direction is: (" + actor.getCurrentDirection() + ")");
			//Gdx.app.debug(TAG, "ACTOR position (TOP RIGHT)is: (" + (actor.getCurrentPosition().x + actor.getEntityBoundingBox().width) + "," + 
			//(actor.getCurrentPosition().y + actor.getEntityBoundingBox().height) + ")");
			
			if( actor.isStatic() || actor.needsInit() ){
				continue;
			}
			
			actor.calculateNextPosition(actor.getCurrentDirection(),delta);
			actor.setState(State.WALKING);
			
			//Gdx.app.debug(TAG, "Entity BEFORE is: " + actor.getState().toString());
			
			boolean isMapCollision = WorldRenderer.getInstance().isCollisionWithMap(actor);
			
			boolean isPlayerCollision = WorldRenderer.getInstance().isCollisionWithPlayer(actor);
			
			boolean isEntityCollision = WorldRenderer.getInstance().isCollisionWithEntity(actor);
			
	
			//These are kept separate for now in case we want to do different things based on the type of collision
			if( isPlayerCollision ){
				actor.setDirection(actor.getCurrentDirection().getOpposite());
			}else if( isMapCollision ){
				actor.setDirection(actor.getCurrentDirection().getRandomNext());
			}else if( isEntityCollision ){
				actor.setDirection(actor.getCurrentDirection().getNext());
			}else{
				actor.setIsVisible(true);
				actor.setNextPositionToCurrent();
			}
			
		}
		
	}
}
