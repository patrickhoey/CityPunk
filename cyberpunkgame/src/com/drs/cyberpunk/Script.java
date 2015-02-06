package com.drs.cyberpunk;

import com.badlogic.gdx.Gdx;
import com.drs.cyberpunk.entities.Entity;
import com.drs.cyberpunk.enums.ActionsEnum;

import com.naef.jnlua.LuaState;

public class Script {
	
	private static final String TAG = Script.class.getSimpleName();
	
    @SuppressWarnings("unused")
	private String fileName;

    LuaState luaState = new LuaState(); 

    public Script(){
    }
    
    public boolean initScript(String scriptFileName){
    	this.fileName = scriptFileName;
    	
    	if( !Gdx.files.internal(scriptFileName).exists()){
    		Gdx.app.debug(TAG, "Script with filename : " + scriptFileName.toString() + " does not exist!");
    		return false;
    	}
    	
    	try{
    		luaState.openLibs();
    		luaState.load(
    				Gdx.files.internal(scriptFileName).read(), 
    				Gdx.files.internal(scriptFileName).file().getName(), 
    				"t");
            // Evaluate the chunk, thus defining the function 
            luaState.call(0, 0); // No arguments, no returns 
    	}
    	catch(Exception ex){
    		System.out.print(ex);
    		luaState.close();
    	}

    	return true;
    }
    
    public void create(Entity entity){
    	if( !luaState.isOpen() ){
    		Gdx.app.debug(TAG, "Script not available!");
    		return;
    	}
		luaState.openLibs();
		luaState.getGlobal("create");
		
		luaState.pushJavaObject(entity); // Push argument #1 
    
		// Call 
		luaState.call(1, 0); // 1 argument, 0 return 
    }
    
    public void runActionItem(Entity entity, ActionsEnum action){
    	if( !luaState.isOpen() ){
    		Gdx.app.debug(TAG, "Script not available!");
    		return;
    	}
		luaState.openLibs();
		luaState.getGlobal("runActionItem");
		
		luaState.pushJavaObject(entity); // Push argument #1
		luaState.pushJavaObject(action); // Push argument #1 
    		
		// Call 
		luaState.call(2, 0); // 2 arguments, 0 return
    }
	
	
}
