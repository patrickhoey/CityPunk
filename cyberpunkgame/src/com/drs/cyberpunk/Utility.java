package com.drs.cyberpunk;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;

public final class Utility {
	
	   private static final String TAG = Utility.class.getSimpleName();
	   public static final String SAVEGAME_SUFFIX = ".sav";
	   
	   public static void unloadAsset(String assetFilenamePath){
			// once the asset manager is done loading
			if( Assets._AssetManager.isLoaded(assetFilenamePath) ){
				Assets._AssetManager.unload(assetFilenamePath);
			} else {
				//Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload: " + assetFilenamePath );
			}

	   }

	   //This is primarily for unmanaged textures (temporary in volatile memory)
	   public static void dispose(Texture texture){
		   if( texture != null ){
			   Gdx.app.debug(TAG, "Disposing of unmanaged texture...");
			   texture.dispose();
		   }
	   }
	   
	   public static float loadCompleted(){
		   return Assets._AssetManager.getProgress();
	   }
	   
	   public static int numberAssetsQueued(){
		   return Assets._AssetManager.getQueuedAssets();
	   }
	   
	   public static boolean updateAssetLoading(){
		   return Assets._AssetManager.update();
	   }
	   
	   public static boolean isAssetLoaded(String fileName){
		   return Assets._AssetManager.isLoaded(fileName);
	   }
	   
	   public static void loadTextureAsset(String textureFilenamePath){
		   if( textureFilenamePath == null || textureFilenamePath.isEmpty() ){
			   return;
		   }
			//load asset
		    InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();
		    
		    if( filePathResolver.resolve(textureFilenamePath).exists() ){
		    	Assets._AssetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));
		    	Assets._AssetManager.load(textureFilenamePath, Texture.class);	
		    }
		    else{
		    	Gdx.app.debug(TAG, "Texture doesn't exist!: " + textureFilenamePath );
		    }
		}
	   
	   public static Texture getTextureAsset(String textureFilenamePath){
			Texture texture = null;
			
			// once the asset manager is done loading
			if( Assets._AssetManager.isLoaded(textureFilenamePath) ){
				texture = Assets._AssetManager.get(textureFilenamePath,Texture.class);
			} else {
				//Gdx.app.debug(TAG, "Texture is not loaded: " + textureFilenamePath );
			}
			
			return texture;
	   }
	   
	   public static void loadSoundAsset(String soundFilenamePath){
		   if( soundFilenamePath == null || soundFilenamePath.isEmpty() ){
			   return;
		   }
		   
		   //load asset
		    InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();
		    
		    if( filePathResolver.resolve(soundFilenamePath).exists() ){
		    	Assets._AssetManager.setLoader(Sound.class, new SoundLoader(filePathResolver));
		    	Assets._AssetManager.load(soundFilenamePath, Sound.class);	
		    }
		    else{
		    	Gdx.app.debug(TAG, "Sound doesn't exist!: " + soundFilenamePath );
		    }
		}
	   
	   public static Sound getSoundAsset(String soundFilenamePath){
			Sound sound = null;
			
			// once the asset manager is done loading
			if( Assets._AssetManager.isLoaded(soundFilenamePath) ){
				sound = Assets._AssetManager.get(soundFilenamePath,Sound.class);
			} else {
				//Gdx.app.debug(TAG, "Sound is not loaded: " + soundFilenamePath );
			}

			return sound;
	   }
	   
	   public static void loadMusicAsset(String musicFilenamePath){
		   if( musicFilenamePath == null || musicFilenamePath.isEmpty() ){
			   return;
		   }
		   
		   //load asset
		    InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();
		    
		    if( filePathResolver.resolve(musicFilenamePath).exists() ){
		    	Assets._AssetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
		    	Assets._AssetManager.load(musicFilenamePath, Music.class);	
		    }
		    else{
		    	Gdx.app.debug(TAG, "Song doesn't exist!: " + musicFilenamePath );
		    }	
		}
	   
	   public static Music getMusicAsset(String musicFilenamePath){
			Music music = null;
			
			// once the asset manager is done loading
			if( Assets._AssetManager.isLoaded(musicFilenamePath) ){
				music = Assets._AssetManager.get(musicFilenamePath,Music.class);
			} else {
				//Gdx.app.debug(TAG, "Music is not loaded: " + musicFilenamePath );
			}
			
			return music;
	   }
	   
	   public static void loadMapAsset(String mapFilenamePath){
		   if( mapFilenamePath == null || mapFilenamePath.isEmpty() ){
			   return;
		   }
		   
		   //load asset
		    InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();
		    if( filePathResolver.resolve(mapFilenamePath).exists() ){
		    	Assets._AssetManager.setLoader(TiledMap.class, new TmxMapLoader(filePathResolver));
		    	Assets._AssetManager.load(mapFilenamePath, TiledMap.class);	
		    }
		    else{
		    	Gdx.app.debug(TAG, "Map doesn't exist!: " + mapFilenamePath );
		    }
		}
	   
	   public static TiledMap getMapAsset(String mapFilenamePath){
			TiledMap map = null;
			
			// once the asset manager is done loading
			if( Assets._AssetManager.isLoaded(mapFilenamePath) ){
				map = Assets._AssetManager.get(mapFilenamePath,TiledMap.class);
			} else {
				//Gdx.app.debug(TAG, "Map is not loaded: " + mapFilenamePath );
			}
			
			return map;
	   }
	   
	   public static String padRight(String s, int n){
		   return String.format("%1$-" + n + "s", s);
		   //System.out.println("'" + temp + "'" + " SIZE: " + temp.length());
	   }
	   
		public static String padLeft(String s, int n) {
			return String.format("%1$#" + n + "s", s);  
		}
		
		public static boolean isRollSuccessful(float chanceOfSuccess){
			float actualPercentage = MathUtils.random(0f, 1f);
			
			//Gdx.app.debug(TAG, "Percentage : " + chanceOfSuccess*100 + " against result: " +  actualPercentage );

			if(chanceOfSuccess > actualPercentage){
				return true;
			}else{
				return false;
			}
		}
		
}
