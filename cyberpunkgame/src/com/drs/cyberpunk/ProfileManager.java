package com.drs.cyberpunk;

import java.util.Enumeration;
import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class ProfileManager {

	private Hashtable<String,FileHandle> profileMap = null;
	private static ProfileManager profileMgr = null;
	Array<String> filenames;
	
	private ProfileManager(){
		profileMap = new Hashtable<String,FileHandle>();
		profileMap.clear();
		filenames = new Array<String>();
	}
	
	public static final ProfileManager getInstance(){
		if( profileMgr == null){
			profileMgr = new ProfileManager();
		}
		return profileMgr;
	}
	
	public void processAllProfiles(){
		if(profileMap==null){
			profileMap = new Hashtable<String,FileHandle>();
		}
		
		boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();
		if( isLocAvailable ){
			FileHandle[] files = Gdx.files.local(".").list(Utility.SAVEGAME_SUFFIX);
				
			for(FileHandle file: files) {
				profileMap.put(file.nameWithoutExtension(), file);
				}
		}else{
			//TODO: try external directory here
			//HTML5 does not support Local
			return;
		}
	}
	
	public void addProfile(String profileName, boolean overwrite){
		if(profileMap==null){
			profileMap = new Hashtable<String,FileHandle>();
		}
		String fullFilename = profileName+Utility.SAVEGAME_SUFFIX;
		
		boolean exists = Gdx.files.external(fullFilename).exists();

		//If we cannot overwrite and the file exists, exit
		if( exists && overwrite == false ){
			return;
		}
		
		boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();
		if( isLocAvailable ){
			FileHandle file = Gdx.files.local(fullFilename);
			file.writeString("", false);
			profileMap.put(file.nameWithoutExtension(), file);
			
		}else{
			//TODO: try external directory here
			//HTML5 does not support Local
			return;
		}
	}
	
	public Hashtable<String,FileHandle> getProfileMap(){
		return profileMap;
	}
	
	public FileHandle getProfileFilehandle(String key){
		return profileMap.get(key);
	}
	
	public Array<String> getProfileList(){
		Enumeration<String> list = profileMap.keys();
		filenames.clear();
		
		
		for (; list.hasMoreElements();)
			filenames.add(list.nextElement());
		
		return filenames;
	}
	
	
}
