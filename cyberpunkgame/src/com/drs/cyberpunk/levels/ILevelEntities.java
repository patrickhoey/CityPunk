package com.drs.cyberpunk.levels;

import java.util.Collection;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.drs.cyberpunk.entities.Entity;

public interface ILevelEntities {		
	
	public static final String PLAYER_START = "Player_Start";
	public static final String NPC_SPAWN_POINT = "NPC_Start";
	public static final String STATIC_ENTITY_START = "Static_Entity_Start";
	
	public static final String FIRE_EFFECT_START = "Fire_Effect_Start";
	public static final String GRATE_SMOKE_EFFECT_START = "Grate_Smoke_Start";
	public static final String ATM_START = "ATM";
	
	public static final String DATAWALL_START = "DataWall_Start";
	public static final String CPU_START = "CPU_Start";
	public static final String CODE_GATE_START = "Code_Gate_Start";
	public static final String MEMORY_UNIT_START = "Memory_Unit_Start";
	public static final String CYBERSPACE_DOOR_START = "Door_Start";
	public static final String CYBERSPACE_EXIT = "Exit";
	
	public void dispose();
	
	public Collection<Entity> getEntities();
	public Entity getEntity(String entityID);
	
	public Collection<ParticleEffect> getParticleEffectEntities();
	
	public void update(float delta);
	public void spawnEntities(float delta);
}
