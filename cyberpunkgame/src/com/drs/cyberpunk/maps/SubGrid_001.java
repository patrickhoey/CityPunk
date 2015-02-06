package com.drs.cyberpunk.maps;

import com.drs.cyberpunk.SoundEffect;
import com.drs.cyberpunk.enums.MapLocationEnum;


public class SubGrid_001 extends GameMap {

	private final static String mapFilenamePath = "data/levels/SubGrid_001.tmx";
	private final static String mapBackgroundLayer = "location_002_background";
	private final static String mapCollisionLayer = "location_002_collision";
	private final static String mapCollisionObjectLayer = "location_002_collision_objects";
	private final static String mapWallCollisionLayer = "location_002_wall_collision";
	private final static String mapForegroundLayer = "location_002_foreground";
	private final static String mapSpawnPointsLayer = "location_002_spawnpoints";
	private final static String mapLightmapLayer = "data/levels/location_002_lightmap.png";
	private final static String mapGroundLayer = "location_002_ground";
	
	private SoundEffect loadingSound;
	
	private MapLocationEnum mapLocation;
	
	public SubGrid_001(MapLocationEnum maplocation){
		
		mapLayers.put(MAP_FILENAME_PATH, mapFilenamePath);
		mapLayers.put(MAP_BACKGROUND_LAYER, mapBackgroundLayer);
		mapLayers.put(MAP_COLLISION_LAYER, mapCollisionLayer);
		mapLayers.put(MAP_COLLISION_OBJECT_LAYER, mapCollisionObjectLayer);
		mapLayers.put(MAP_WALL_COLLISION_LAYER, mapWallCollisionLayer);
		mapLayers.put(MAP_FOREGROUND_LAYER, mapForegroundLayer);
		mapLayers.put(MAP_SPAWN_POINT_LAYER, mapSpawnPointsLayer);
		mapLayers.put(MAP_LIGHTMAP_LAYER, mapLightmapLayer);
		mapLayers.put(MAP_GROUND_LAYER, mapGroundLayer);
		
		this.mapLocation = maplocation;
		
		isCyberspaceLocation = true;
		this.loadingSound = new SoundEffect(_LOADING_SOUND_CYBERSPACE);
		
		loadAssets();
	}
	
	@Override
	public MapLocationEnum getMapName(){
		return mapLocation;
	}
	
	@Override
	public void setMapName(MapLocationEnum map){
		this.mapLocation = map;
	}
	
	@Override
	public void playLoadingSound(){
		loadingSound.play();
	}
	
	@Override
	public void update(float delta){
		super.update(delta);
		loadingSound.update(delta);
	}
}