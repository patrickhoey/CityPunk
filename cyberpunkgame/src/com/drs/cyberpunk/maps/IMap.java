package com.drs.cyberpunk.maps;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.drs.cyberpunk.enums.MapLocationEnum;


public interface IMap {
	
	//Assets
	public static final String _GHOSTS_AMBIENT = "data/music/ghosts.mp3";
	public static final String _AMBIENT_ALLEY_SOUND = "data/sounds/cityplaza_ambient_MODIFIED.wav";
	public static final String _AMBIENT_CYBERSPACE_SOUND = "data/sounds/cyberspace_ambience.wav";
	
	public static final String _AIRSHIP_FLYBY_DISTANT = "data/sounds/airship_flyby_distant.mp3";
	public static final String _CAR_HORN_DISTANT = "data/sounds/carhorn_distant.mp3";
	public static final String _CHINESE_FEMALE_VOICE_EFFECTS = "data/sounds/chinese_female_voice_effects.mp3";
	public static final String _DOG_BARKING_DISTANT = "data/sounds/dog_barking_distant.mp3";
	public static final String _FIRE_TRUCK_SIREN_DISTANT = "data/sounds/firetrucksiren_distant.mp3";
	public static final String _FLYBY_2_DISTANT = "data/sounds/fly_by_2_distant.mp3";
	public static final String _FLYBY_DISTANT = "data/sounds/fly_by_distant.mp3";
	public static final String _METRO_TRAIN_PASSBY_DISTANT = "data/sounds/metro_train_passby_distant.mp3";
	public static final String _MOTORCYCLES_DISTANT = "data/sounds/motorcycles_distant.mp3";
	public static final String _POLICE_SIRENS_DISTANT = "data/sounds/PoliceSirens_Distant.mp3";
	public static final String _POLICE_SIREN_DISTANT  = "data/sounds/police_siren_distant.mp3";
	
	//cyberspace
	public static final String _DARK_DISTORTION = "data/sounds/SFX_DarkDistortion.mp3";
	public static final String _TELEMETRY_INFO = "data/sounds/SFX_ComputerTelemetryInformationSwell.mp3";
	public static final String _CODE_STREAM_MATRIX_1 = "data/sounds/ScienceFictionDigitalCodeStreamMatrix1.mp3";
	public static final String _CODE_STREAM_MATRIX_2 = "data/sounds/ScienceFictionDigitalCodeStreamMatrix2.mp3";
	public static final String _CODE_STREAM_MATRIX_3 = "data/sounds/ScienceFictionDigitalCodeStreamMatrix3.mp3";
	public static final String _SCIFI_FORCEFIELD = "data/sounds/scifi_force_field.mp3";
	public static final String _LOADING_SOUND_CYBERSPACE = "data/sounds/uploading_cyberspace.wav";
	
	
	//Map layers
	//Keys
	public final static String MAP_FILENAME_PATH = "MAP_FILENAME_PATH";
	public final static String MAP_BACKGROUND_LAYER = "MAP_BACKGROUND_LAYER";
	public final static String MAP_COLLISION_LAYER = "MAP_COLLISION_LAYER";
	public final static String MAP_COLLISION_OBJECT_LAYER = "MAP_COLLISION_OBJECT_LAYER";
	public final static String MAP_WALL_COLLISION_LAYER = "MAP_WALL_COLLISION_LAYER";
	public final static String MAP_FOREGROUND_LAYER = "MAP_FOREGROUND_LAYER";
	public final static String MAP_SPAWN_POINT_LAYER = "MAP_SPAWN_POINT_LAYER";
	public final static String MAP_LIGHTMAP_LAYER = "MAP_LIGHTMAP_LAYER";
	public final static String MAP_GROUND_LAYER = "MAP_GROUND_LAYER";
	
	public String getMapFilenamePath();
	public String getMapBackgroundLayer();
	public String getMapForegroundLayer();
	public String getMapCollisionLayer();
	public String getMapCollisionObjectLayer();
	public String getMapWallCollisionLayer();
	public String getMapLightmapLayerPath();
	public String getMapSpawnPointsLayer();
	public String getMapGroundLayer();
	
	public MapLocationEnum getMapName();
	public void setMapName(MapLocationEnum map);
	
	public Rectangle getPlayerStart();
	
	public Rectangle getNextNPCSpawnPoint();
	public int getNumberNPCSpawnPoints();
	public ArrayList<RectangleMapObject> getNPCSpawnPoints();
	

	public ArrayList<RectangleMapObject> getStaticEntitySpawnPoints();
	public int getNumberStaticEntitySpawnPoints();
	
	public void setBackgroundMusic(Map<String,String> music);
	
	public void setSoundScapeJukeBox(ArrayList<String> sounds);
	public void updateSoundScapeJukeBox(float delta);
	public void stopBackgroundMusic();
	public void startBackgroundMusic();
	public void playLoadingSound();
	
	public boolean isCyberspace();
}
