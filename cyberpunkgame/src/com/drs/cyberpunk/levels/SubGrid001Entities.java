package com.drs.cyberpunk.levels;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.drs.cyberpunk.WorldEntities;
import com.drs.cyberpunk.WorldRenderer;
import com.drs.cyberpunk.entities.CPU;
import com.drs.cyberpunk.entities.CodeGate;
import com.drs.cyberpunk.entities.DataWall;
import com.drs.cyberpunk.entities.DoorCyberspace;
import com.drs.cyberpunk.entities.Entity;
import com.drs.cyberpunk.entities.Entity.Direction;
import com.drs.cyberpunk.entities.Entity.State;
import com.drs.cyberpunk.entities.ICyberspace;
import com.drs.cyberpunk.entities.MemoryUnit;
import com.drs.cyberpunk.enums.MapLocationEnum;


public class SubGrid001Entities extends LevelEntities {

	private static final String TAG = SubGrid001Entities.class.getSimpleName();

	private Rectangle cyberspaceExit = null;
	private boolean loadedEntities = false;
	
	private ArrayList<CPU> CPUIDs;
	
	public SubGrid001Entities(){
		super();
	
		loadedEntities = false;
		
		CPUIDs = new ArrayList<CPU>();
			
		Gdx.app.debug(TAG, "Loaded...");
	}
	
	public void update(float delta){	
		updateEntityPositions(delta);
	}
	
	private void loadStaticEntities(){
		if( loadedEntities == true ){
			return;
		}
		
		int numberSpawnPoints = WorldRenderer.getInstance().getCurrentMap().getNPCSpawnPoints().size();
		
		if( numberSpawnPoints == 0){
			return;
		}
		
		//setup npc's		
		for(RectangleMapObject mapObject: WorldRenderer.getInstance().getCurrentMap().getNPCSpawnPoints()){
			String typeName = (String)mapObject.getProperties().get("Type");
			if( typeName == null){
				continue;
			}
			if( typeName.equalsIgnoreCase(ILevelEntities.DATAWALL_START)){
				DataWall dataWall = new DataWall();
				if( dataWall.needsInit() ){
					dataWall.setVelocity(new Vector2(0,0));
					dataWall.setDirection(Direction.RIGHT);
					dataWall.setInit(false);
					dataWall.init(mapObject.getRectangle().getX(), mapObject.getRectangle().getY());
				}
				entityMap.put(dataWall.getEntityID(), dataWall);
			}else if(typeName.equalsIgnoreCase(ILevelEntities.CPU_START)){
				CPU cpu = new CPU();
				if( cpu.needsInit() ){
					cpu.setVelocity(new Vector2(0,0));
					cpu.setDirection(Direction.RIGHT);
					cpu.setInit(false);
					cpu.init(mapObject.getRectangle().getX(), mapObject.getRectangle().getY());
				}
				entityMap.put(cpu.getEntityID(), cpu);
				CPUIDs.add(cpu);
			}else if(typeName.equalsIgnoreCase(ILevelEntities.CODE_GATE_START)){
				CodeGate gate = new CodeGate();
				if( gate.needsInit() ){
					gate.setVelocity(new Vector2(0,0));
					gate.setDirection(Direction.RIGHT);
					gate.setInit(false);
					gate.init(mapObject.getRectangle().getX(), mapObject.getRectangle().getY());
				}
				entityMap.put(gate.getEntityID(), gate);
			}else if(typeName.equalsIgnoreCase(ILevelEntities.MEMORY_UNIT_START)){
				MemoryUnit unit = new MemoryUnit();
				if( unit.needsInit() ){
					unit.setVelocity(new Vector2(0,0));
					unit.setDirection(Direction.RIGHT);
					unit.setInit(false);
					unit.init(mapObject.getRectangle().getX(), mapObject.getRectangle().getY());
				}
				entityMap.put(unit.getEntityID(), unit);
			}else if(typeName.equalsIgnoreCase(ILevelEntities.CYBERSPACE_DOOR_START)){
				DoorCyberspace door = new DoorCyberspace();
				if( door.needsInit() ){
					door.setVelocity(new Vector2(0,0));
					door.setDirection(Direction.RIGHT);
					door.setInit(false);
					door.init(mapObject.getRectangle().getX(), mapObject.getRectangle().getY());
				}
				entityMap.put(door.getEntityID(), door);
			}else if( typeName.equalsIgnoreCase(ILevelEntities.CYBERSPACE_EXIT)){
				cyberspaceExit = mapObject.getRectangle();
			}
		}
		
		loadedEntities = true;
	}
	
	private void checkCPUPower(){
		
		Iterator<CPU> iterCPU = CPUIDs.iterator();
		
		while(iterCPU.hasNext()){
			CPU cpu = iterCPU.next();
			Entity entity = entityMap.get(cpu.getEntityID());
			
			//If CPU has been removed, don't continue
			if( entity == null){
				CPUIDs.remove(cpu);
				continue;
			}
		
			if(cpu.isPoweredDown()){
				//Iterate over all entities and disable them
				Iterator<Entity> iter = entityMap.values().iterator();
				
				while(iter.hasNext()){
					Entity actor = iter.next();
					if( actor instanceof ICyberspace){
						ICyberspace cyberspaceActor = (ICyberspace)actor;
						if( cpu.isPowerSource(actor.getEntityBoundingBox())){
							cyberspaceActor.shutDown();							
						}

					}
				}
				
				//done with CPU
				iterCPU.remove();
			}	
		}
	}
	
	public void updateEntityPositions(float delta){
		loadStaticEntities();
		
		//Player checks
		//Check if Alarm was triggered
		boolean alarmTriggered = WorldRenderer.getInstance().isAlarmActivated();
		if( alarmTriggered ){
			Gdx.app.debug(TAG, "Alarm triggered...");			
			WorldEntities.getInstance().getPlayer().setIsAlive(false);
			WorldRenderer.getInstance().alertStopped();
		}	
		
		boolean isPlayerReady = WorldEntities.getInstance().getPlayer().isReadyToBeRemoved();
		//If player has died
		if( isPlayerReady == true){
			Gdx.app.debug(TAG, "Died...");
			
			WorldRenderer.getInstance().changeMapEvent(MapLocationEnum.CITYPLAZA001.toString());
			WorldEntities.getInstance().getPlayer().setIsAlive(true);
			WorldRenderer.getInstance().alertStopped();
			return;
		}
		
		//See if player has hit the exit
		boolean atExit = WorldRenderer.getInstance().isCollisionWithPlayer(cyberspaceExit);
		
		if( atExit ){
			Gdx.app.debug(TAG, "Exited...");
	
			WorldRenderer.getInstance().changeMapEvent(MapLocationEnum.CITYPLAZA001.toString());
			WorldRenderer.getInstance().alertStopped();
			return;
		}
		
		checkCPUPower();
		
		//All entities
		Iterator<Entity> iter = entityMap.values().iterator();
		
		while(iter.hasNext()){
			Entity actor = iter.next();
			
			if( actor instanceof DoorCyberspace){
				DoorCyberspace door = (DoorCyberspace)actor;
				boolean isPlayerCollision = WorldRenderer.getInstance().isCollisionWithPlayer(door);
				
				if( isPlayerCollision ){
					//Gdx.app.debug(TAG, "Collision!! Door Open?" + door.isOpen() );
					if( door.isOpen() == false ){
						door.doorOpenPlay();
						door.setOpen(true);
						door.setState(State.ANIMATE_ONCE);						
					}
				}else{
					if( door.isOpen() == true ){
						door.doorClosePlay();
						door.setState(State.ANIMATE_ONCE_REVERSE);
						door.setOpen(false);
					}
				}
			}
			
			if( actor.isReadyToBeRemoved() ){
				iter.remove();
				actor.dispose();
				continue;
			}
			
			actor.update(delta);
			
		}
	}
	
	
	@Override
	public void spawnEntities(float delta){
		
	}
}
