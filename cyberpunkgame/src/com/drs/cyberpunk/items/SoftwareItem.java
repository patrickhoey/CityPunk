package com.drs.cyberpunk.items;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.drs.cyberpunk.SoundEffect;
import com.drs.cyberpunk.Utility;

public class SoftwareItem extends InventoryItem {
	
	//This enum represents what this software does
	static public enum Functions {
		OFFENSIVE_UTILITY, // breaks barriers, code gate/tollbooth, breaks sentry type ICE
		DEFENSIVE_UTILITY, //sets up stealth, decoys, evasion
		SYSTEM_OPERATIONAL, //issue commands, probe security
		SPECIAL_UTILITY,	//Misc.	
		NONE
	}	
	
	//Rolls against other software will be  (softwarePatchVersion + majorVersion + 1d10)
	
	//This is the amount of memory units this piece of software takes up in active memory and disk memory
	private int memoryUnitSize = 0;
	
	//The major version is the main version purchased (1 to 5)
	private int softwareMajorVersion = 0;
	
	//The patch version is the latest patch applied to software (0 to 5)
	//This represents whether this version is official or not; If not, then there are chances calculated that the software will crash every use
	//TODO: When creating your own upgrades to software, you could purchase a "certification patch" which upgrades your status to official
	//This will affect QUALITY of software
	//0 has crash bugs
	
	//1-5 determines the efficiency of the functions; The higher, the more optimal
	//Efficiency is the measure of the software in performing its functions; Low efficiency runs the functions suboptimally, while high efficiency runs
	//optimally; 1-5
	private int softwarePatchVersion = 0;
	
	//This enum represents what this software does
	private Functions function = Functions.NONE;

	private String softwareItemDisplay;
	private String softwareAttributeDisplay;  
	
	private SoundEffect activateSound = null;
	private String soundEffectName;
	
	public String getSoundEffect(){
		return soundEffectName;
	}
	
	public void setSoundEffect(String soundEffect){
		this.soundEffectName = soundEffect;
		activateSound = new SoundEffect(soundEffectName);
	}
	
	public Functions getFunction() {
		return function;
	}

	public void setFunction(Functions function) {
		this.function = function;
	}

	public int getMemoryUnitSize() {
		return memoryUnitSize;
	}

	public void setMemoryUnitSize(int memoryUnitSize) {
		this.memoryUnitSize = memoryUnitSize;
	}

	public int getSoftwareMajorVersion() {
		return softwareMajorVersion;
	}

	public void setSoftwareMajorVersion(int softwareMajorVersion) {
		this.softwareMajorVersion = softwareMajorVersion;
	}
	
	public void update(float delta){
		if( activateSound != null ){
			activateSound.update(delta);			
		}
	}
	
	public void playActivatedSound(){
		if( activateSound != null ){
			activateSound.play();		
		}
	}

	public int getSoftwarePatchVersion() {
		return softwarePatchVersion;
	}

	public void setSoftwarePatchVersion(int softwarePatchVersion) {
		this.softwarePatchVersion = softwarePatchVersion;
	}
	
	@Override
	public String getItemDisplayString(){
		return  softwareItemDisplay;
	}
	
	@Override
	public void setItemDisplayString(String displayString){
		this.softwareItemDisplay = displayString;
	}
	
	@Override
	public String getDisplayAttributes(){
		return softwareAttributeDisplay;
	}
	
	@Override
	public void setDisplayAttributes(String attributes){
		this.softwareAttributeDisplay = attributes;
	}

	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("memoryUnitSize", memoryUnitSize);
		json.writeValue("softwareMajorVersion", softwareMajorVersion);
		json.writeValue("softwarePatchVersion", softwarePatchVersion);
		json.writeValue("function", function);
		json.writeValue("soundeffectname", soundEffectName);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		if( jsonData.get("memoryUnitSize") != null ) {
			memoryUnitSize = jsonData.get("memoryUnitSize").asInt();			
		}
		if( jsonData.get("softwareMajorVersion") != null ) {
			softwareMajorVersion = jsonData.get("softwareMajorVersion").asInt();			
		}
		if( jsonData.get("softwarePatchVersion") != null ) {
			softwarePatchVersion = jsonData.get("softwarePatchVersion").asInt();			
		}
		if( jsonData.get("function") != null ) {
			function = Functions.valueOf(jsonData.get("function").asString());			
		}
		if( jsonData.get("soundeffectname") != null ) {
			setSoundEffect(jsonData.get("soundeffectname").asString());			
		}
		
		//Create display readable format
		//placeholder
		if( jsonData.get("itemShortDescription") != null ){
			softwareItemDisplay = Utility.padRight(jsonData.get("itemShortDescription").asString(), 10);
		}
		if( jsonData.get("itemLongDescription") != null ) {
			softwareItemDisplay += jsonData.get("itemLongDescription").asString();
		}
		
		
		if( jsonData.get("memoryUnitSize") != null ) {
			softwareAttributeDisplay = "Memory Unit: " + jsonData.get("memoryUnitSize").asString() + "\n";
		}
		if( jsonData.get("softwareMajorVersion") != null ) {
			softwareAttributeDisplay += "Version: " + jsonData.get("softwareMajorVersion").asString() + "\n";
		}
		if( jsonData.get("softwarePatchVersion") != null ) {
			softwareAttributeDisplay += "Patch: " + jsonData.get("softwarePatchVersion").asString() + "\n";
		}
		if( jsonData.get("function") != null ) {
			softwareAttributeDisplay += "Function: " + jsonData.get("function").asString() + "\n";
		}		


		//System.out.println(softwareItemDisplay);

	}
	

	
}
