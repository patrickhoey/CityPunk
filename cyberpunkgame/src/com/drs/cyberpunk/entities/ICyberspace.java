package com.drs.cyberpunk.entities;

public interface ICyberspace {

	public enum CYBERSPACE_ENTITY {
		CODE_GATE,
		CPU,
		DATAWALL,
		MEMORY_UNIT
	}
	
	public final static float POWER_MAX_DISTANCE = 500f;
	
	public void shutDown();
	
}
