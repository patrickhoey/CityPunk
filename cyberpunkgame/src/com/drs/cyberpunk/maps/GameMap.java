package com.drs.cyberpunk.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.drs.cyberpunk.JukeBox;
import com.drs.cyberpunk.Utility;
import com.drs.cyberpunk.enums.MapLocationEnum;
import com.drs.cyberpunk.levels.ILevelEntities;


public abstract class GameMap implements IMap {

	private static final String TAG = GameMap.class.getSimpleName();
	
	private static final String _MUSIC_ = "music";
	private static final String _AMBIANT_SOUND = "ambiant_sound";
	
	private static float musicVolume = 0f;
	private static float ambientVolume = 1f;
	
	private ListIterator<RectangleMapObject> listIterator;

	protected boolean isCyberspaceLocation = false;
	
	protected TiledMap map = null;
	protected Texture lightMapLayer = null;
	
	protected HashMap<String,String> mapLayers;
	
	private HashMap<String,Music> backgroundMusic;
	private HashMap<String,Music> ambientSounds;
	
	private HashMap<String,String> queuedMusic;
	
	private JukeBox soundScapeJukeBox;
	
	private ArrayList<RectangleMapObject> npcSpawnPoints;
	private ArrayList<RectangleMapObject> staticEntitySpawnPoints;
	private Rectangle playerStart;
	
	public static GameMap mapFactory(MapLocationEnum mapLocation) {
		Gdx.app.debug(TAG, "Returning object for: " + mapLocation.toString());
		if( mapLocation.toString().equalsIgnoreCase(MapLocationEnum.CITYPLAZA001.toString())){	
			GameMap gameMap = new CityPlaza(mapLocation);
			
			//Add music
			HashMap<String,String> gameBackgroundMusic = new HashMap<String,String>();
			gameBackgroundMusic.put(_MUSIC_, _GHOSTS_AMBIENT);
			gameBackgroundMusic.put(_AMBIANT_SOUND, _AMBIENT_ALLEY_SOUND);	
			gameMap.setBackgroundMusic(gameBackgroundMusic);
			
			//Add soundScape
			ArrayList<String> sounds = new ArrayList<String>();
			sounds.add(_AIRSHIP_FLYBY_DISTANT);
			sounds.add(_CAR_HORN_DISTANT);
			sounds.add(_CHINESE_FEMALE_VOICE_EFFECTS);
			sounds.add(_DOG_BARKING_DISTANT);
			sounds.add(_FIRE_TRUCK_SIREN_DISTANT);
			sounds.add(_FLYBY_2_DISTANT);
			sounds.add(_FLYBY_DISTANT);
			sounds.add(_METRO_TRAIN_PASSBY_DISTANT);
			sounds.add(_MOTORCYCLES_DISTANT);
			sounds.add(_POLICE_SIRENS_DISTANT);
			sounds.add(_POLICE_SIREN_DISTANT);
			
			gameMap.setSoundScapeJukeBox(sounds);
			
			return gameMap;
		}else if(mapLocation.toString().equalsIgnoreCase(MapLocationEnum.SUBGRID001.toString())){
			GameMap gameMap = new SubGrid_001(mapLocation);
			
			//Add music
			HashMap<String,String> gameBackgroundMusic = new HashMap<String,String>();
			//gameBackgroundMusic.put(_MUSIC_, _GHOSTS_AMBIENT);
			gameBackgroundMusic.put(_AMBIANT_SOUND, _AMBIENT_CYBERSPACE_SOUND);	
			gameMap.setBackgroundMusic(gameBackgroundMusic);
			
			//Add soundScape
			ArrayList<String> sounds = new ArrayList<String>();
			
			sounds.add(_DARK_DISTORTION);
			sounds.add(_TELEMETRY_INFO);
			sounds.add(_CODE_STREAM_MATRIX_1);
			sounds.add(_CODE_STREAM_MATRIX_2);
			sounds.add(_CODE_STREAM_MATRIX_3);
			sounds.add(_SCIFI_FORCEFIELD);
			
			gameMap.setSoundScapeJukeBox(sounds);
			
			return gameMap;
		}else{
			return null;
		}
	}
	
	public GameMap(){
		
		mapLayers = new HashMap<String,String>();
        backgroundMusic = new HashMap<String,Music>();
        ambientSounds = new HashMap<String,Music>();
        soundScapeJukeBox = new JukeBox();
        
		npcSpawnPoints = new ArrayList<RectangleMapObject>();
		staticEntitySpawnPoints = new ArrayList<RectangleMapObject>();
		playerStart = new Rectangle();
		
		queuedMusic = new HashMap<String,String>();
	}
	
	protected void loadAssets(){
		//loading level
		Utility.loadMapAsset(mapLayers.get(MAP_FILENAME_PATH));	
		Utility.loadTextureAsset(mapLayers.get(MAP_LIGHTMAP_LAYER));
		
		getLoadedAssets();
	}
	
	private void getLoadedAssets(){
        //Map setup
		// once the asset manager is done loading
		if( map == null && Utility.isAssetLoaded(mapLayers.get(MAP_FILENAME_PATH)) ){
			map = Utility.getMapAsset(mapLayers.get(MAP_FILENAME_PATH));
			initSpawnPoints();
		}
		
		if( lightMapLayer == null && Utility.isAssetLoaded(mapLayers.get(MAP_LIGHTMAP_LAYER)) ){
			lightMapLayer = Utility.getTextureAsset(mapLayers.get(MAP_LIGHTMAP_LAYER));
		}
		
		if( queuedMusic.isEmpty() == false){
			updateQueuedMusic();
		}
	}
	
	public void update(float delta){
		getLoadedAssets();
	}
	
	private void processMusicType(String type, String filePath, Music song){
		if( type.equalsIgnoreCase(_MUSIC_)){
			backgroundMusic.put( filePath, song );
        }else if(type.equalsIgnoreCase(_AMBIANT_SOUND)){
        	ambientSounds.put( filePath, song );
        }else{
        	ambientSounds.put( filePath, song );
        }
	}
	
	private void updateQueuedMusic(){
		Iterator<Map.Entry<String, String>> iter = queuedMusic.entrySet().iterator();
		
		while(iter.hasNext()){
			Map.Entry<String,String> item = iter.next();
			
			Utility.loadMusicAsset(item.getValue());
			
			Music song = Utility.getMusicAsset(item.getValue());
			
			if( song == null ){
				continue;
			}
			
			processMusicType(item.getKey(), item.getValue(), song);
			
			Gdx.app.debug(TAG, "Processed queued music: " + item.getKey() +":" + item.getValue());
			
			iter.remove();
			
			Gdx.app.debug(TAG, "New queue size..." + queuedMusic.size());
		}
		
		startBackgroundMusic();
	}
	
	@Override
	public void setBackgroundMusic(Map<String,String> music){
		Iterator<Map.Entry<String, String>> iter = music.entrySet().iterator();
		
		while(iter.hasNext()){
			Map.Entry<String,String> item = iter.next();
			
			Utility.loadMusicAsset(item.getValue());
			
			Music song = Utility.getMusicAsset(item.getValue());
			
			if( song == null ){
				Gdx.app.debug(TAG, "Not loaded: " + item.getKey() +":" + item.getValue());
				queuedMusic.put(item.getKey(), item.getValue());
				Gdx.app.debug(TAG, "Adding to queue with new queue size..." + queuedMusic.size());
				continue;
			}
			
			processMusicType(item.getKey(), item.getValue(), song);
			
			iter.remove();
		}
	}
	
	@Override
	public void setSoundScapeJukeBox(ArrayList<String> sounds){
		soundScapeJukeBox.setPlayList(sounds);
	}
	
	@Override
	public void updateSoundScapeJukeBox(float delta){
		soundScapeJukeBox.update(delta);
	}
	
	public boolean isCyberspace(){
		return isCyberspaceLocation;
	}
	
	public TiledMap getMap(){
		return map;
	}
	
	public void dispose(){
		if( map != null ){
			map.dispose();			
		}		
		
		for(String songFilename: backgroundMusic.keySet()){
			Utility.unloadAsset(songFilename);
		}
		
		for(String songFilename: ambientSounds.keySet()){
			Utility.unloadAsset(songFilename);
		}
		
		soundScapeJukeBox.unload();
	}
	
	@Override
	public void stopBackgroundMusic(){
		for(Music song: backgroundMusic.values()){
			song.stop();
		}
		for(Music song: ambientSounds.values()){
			song.stop();
		}
		
		soundScapeJukeBox.stop();
	}
	
	@Override
	public void startBackgroundMusic(){
		for(Music song: backgroundMusic.values()){
			song.setLooping(true);
			song.setVolume(musicVolume);
			song.play();
		}
		
		for(Music song: ambientSounds.values()){
			song.setVolume(ambientVolume);
			song.setLooping(true);
			song.play();
		}
		
		soundScapeJukeBox.start();
	}
	
	private void initSpawnPoints(){
		npcSpawnPoints.clear();
		staticEntitySpawnPoints.clear();
		
		if( this == null){
			return;
		}
		
		Rectangle rectangle;
		String name;
		
		try{
	        TiledMap map = this.getMap();
	        
	        if( map == null ){
	        	Gdx.app.debug(TAG, "Map is not loaded");
	        	return;
	        }
	        
			MapLayer mapLayer = map.getLayers().get(this.getMapSpawnPointsLayer());
			if( mapLayer != null){
				MapObjects mapObjects = mapLayer.getObjects();
				for( MapObject object : mapObjects){
					if(object instanceof RectangleMapObject) {
						name = ((RectangleMapObject)object).getName();
						rectangle = ((RectangleMapObject)object).getRectangle();
						
						if( name != null && name.equalsIgnoreCase(ILevelEntities.PLAYER_START) ){
							playerStart = rectangle;
							//Gdx.app.debug(TAG, "PlayerStart: " + this.getMapName().toString() + " " + playerStart.x + "," + playerStart.y);
						}else if( name != null && name.equalsIgnoreCase(ILevelEntities.NPC_SPAWN_POINT) ){
							npcSpawnPoints.add((RectangleMapObject)object);
						}if( name != null && name.equalsIgnoreCase(ILevelEntities.STATIC_ENTITY_START) ){
							staticEntitySpawnPoints.add((RectangleMapObject)object);
						}else{
							//catch all misc. spawn points
							staticEntitySpawnPoints.add((RectangleMapObject)object);
						}

					}else{
						continue;
					}
				}
			}else{
				Gdx.app.debug(TAG, "Could not load map objects " +  this.getMapSpawnPointsLayer() + " from current map location ");
			}
		}catch(NullPointerException np){
			//We simply don't draw the layer if it doesn't exist
			Gdx.app.debug(TAG, "Could not load map objects " +  this.getMapSpawnPointsLayer() + " from current map location ");
		}finally{

		}
		
		listIterator = npcSpawnPoints.listIterator();
	
	}
	
	@Override
	public ArrayList<RectangleMapObject> getStaticEntitySpawnPoints(){
		return staticEntitySpawnPoints;
	}
	
	@Override
	public int getNumberStaticEntitySpawnPoints(){
		return staticEntitySpawnPoints.size();
	}
	
	@Override
	public Rectangle getPlayerStart(){
		return playerStart;
	}
	
	@Override
	public Rectangle getNextNPCSpawnPoint(){
		try {
			if( listIterator.hasNext() ){
				return listIterator.next().getRectangle();				
			}else{
				//reset to beginning of list
				while( listIterator.hasPrevious()){
					listIterator.previous();
				}
				if( listIterator.hasNext() ){
					return listIterator.next().getRectangle();	
				}
			}
		}catch(NoSuchElementException nse){
			Gdx.app.debug(TAG, "No element in the spawn point list...");
		}
		
		return null;
	}
	
	@Override
	public int getNumberNPCSpawnPoints(){
		return npcSpawnPoints.size();
	}
	
	public ArrayList<RectangleMapObject> getNPCSpawnPoints(){
		return npcSpawnPoints;
	}
	
	
	@Override
	public String getMapFilenamePath() {
		return mapLayers.get(MAP_FILENAME_PATH);
	}

	@Override
	public String getMapBackgroundLayer() {
		return mapLayers.get(MAP_BACKGROUND_LAYER);
	}

	@Override
	public String getMapForegroundLayer() {
		return mapLayers.get(MAP_FOREGROUND_LAYER);
	}

	@Override
	public String getMapCollisionLayer() {
		return mapLayers.get(MAP_COLLISION_LAYER);
	}
	
	@Override
	public String getMapCollisionObjectLayer() {
		return mapLayers.get(MAP_COLLISION_OBJECT_LAYER);
	}
	
	@Override
	public String getMapWallCollisionLayer() {
		return mapLayers.get(MAP_WALL_COLLISION_LAYER);
	}

	@Override
	public String getMapLightmapLayerPath() {
		return mapLayers.get(MAP_LIGHTMAP_LAYER);
	}
	
	@Override
	public String getMapSpawnPointsLayer(){
		return mapLayers.get(MAP_SPAWN_POINT_LAYER);
	}
	
	@Override
	public String getMapGroundLayer(){
		return mapLayers.get(MAP_GROUND_LAYER);
	}
	
	public Texture getMapLightmapTexture() {
		return lightMapLayer;
	}
	
}
