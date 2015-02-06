package com.drs.cyberpunk;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;

public class ScriptManager {
	private static ScriptManager manager = null;
	private static final String TAG = ScriptManager.class.getSimpleName();
	private HashMap<String, Script> scriptMap;
	
	private ScriptManager(){
		scriptMap = new HashMap<String,Script>();
	}
	
	public static final ScriptManager getInstance(){
		if( manager == null){
			manager = new ScriptManager();
		}
		return manager;
	}
	
	public Script scriptFactory(String scriptFileName) {
		if( scriptFileName == null || scriptFileName.isEmpty() ){
			Gdx.app.debug(TAG, "Script filename is NULL");
			return null;
		}

		Script script = null;
		
		script = scriptMap.get(scriptFileName);
		
		if( script != null ){
			Gdx.app.debug(TAG, "Script already initialized : " + scriptFileName.toString());
			return script;
		}

		script = new Script();
		boolean isInitialized = script.initScript(scriptFileName);
		
		if( isInitialized == false ){
			Gdx.app.debug(TAG, "Unable to find script with filename : " + scriptFileName.toString());
			return null;
		}else{
			scriptMap.put(scriptFileName, script);
		}
		
		return script;
	}
    
    

}
