package com.drs.cyberpunk.entities;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;

public class EntityPool<T extends Entity> extends Pool<T> {

	private static final String TAG = EntityPool.class.getSimpleName();
	
	private final Class<T> cls;
	
	private int maxPopulation;
	
	public EntityPool(Class<T> cls, int maxPopulation){
		this.cls = cls;
		this.maxPopulation = maxPopulation;
		
		createInitialPool();
	}
	
    // array containing the active entity NPC's
    private final ArrayList<T> activeEntities = new ArrayList<T>();
    private final ArrayList<T> notSpawnedEntities = new ArrayList<T>();

    @Override
    protected T newObject () {
    	T obj = null;
    	try {
			obj = cls.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
			return obj;
    }
    
	public void freeDeadEntities(){
        // free entities returning them to the pool:
		T npc;
        int len = activeEntities.size();
        
       // Gdx.app.debug(TAG, "The total active entity NPC's are: " + len);
        
        for (int i = len; --i >= 0;) {
        	npc = activeEntities.get(i);
            if (npc.isAlive() == false) {
            	//Gdx.app.debug(TAG, "Removing/Freeing dead entity...");
            	activeEntities.remove(i);
            	//Gdx.app.debug(TAG, "The total active entity NPC's (after removing dead entity): " + activeEntities.size());
            	this.free(npc);
            	//Gdx.app.debug(TAG, "The number of free objects in the pool are: " + this.getFree());
            }
        }

	}
	
	public ArrayList<T> getEntitiesInPool(){
		return activeEntities;
	}
	
	private void createInitialPool(){	
		
		for( int i = 0; i < maxPopulation; i++ ){
			//Check pools and refresh if low
			if( notSpawnedEntities.size() > maxPopulation){
				return;
			}
			
			T umbrellaNPC = this.obtain();
			notSpawnedEntities.add(umbrellaNPC);
			Gdx.app.debug(TAG, "Adding Umbrella NPC to pool. Current size: " + activeEntities.size() );
		}
	}
	
	public void maintainPopulation(){	
		
		int entitiesNeeded = maxPopulation - activeEntities.size() - notSpawnedEntities.size();
		
		for( int i = 0; i < entitiesNeeded; i++ ){
			//Check pools and refresh if low
			if( (activeEntities.size()+notSpawnedEntities.size()) > maxPopulation){
				return;
			}
			
			T umbrellaNPC = this.obtain();
			notSpawnedEntities.add(umbrellaNPC);
			//Gdx.app.debug(TAG, "Adding Umbrella NPC to pool. Current size: " + activeEntities.size() );
		}
	}
	
	public T spawnEntity(){
		T npc = null;
		
		if( notSpawnedEntities.size() == 0 ){
			return npc;
		}
		
		int lastPosition = notSpawnedEntities.size() - 1;
		npc = notSpawnedEntities.remove(lastPosition);
		activeEntities.add(npc);
		
		return npc;
	}
	
	public void spawnAllEntities(){
		Iterator<T> iter = notSpawnedEntities.iterator();
		
		while(iter.hasNext()){
			spawnEntity();
		}
	}
	
	public ArrayList<T> getUnspawnedEntities(){
		return notSpawnedEntities;
		//Gdx.app.debug(TAG, "Entities created but not spawned: " + notSpawnedEntities.size() + " Active entities: " +  activeEntities.size());
	}
	
	

	
}
