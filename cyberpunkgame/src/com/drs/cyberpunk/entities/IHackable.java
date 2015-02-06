package com.drs.cyberpunk.entities;

import java.util.HashMap;

public interface IHackable {
	
	public static final String ENUM_SECURITYLEVEL_BLUEHOST = "Blue Host";
	public static final String ENUM_SECURITYLEVEL_GREENHOST = "Green Host";
	public static final String ENUM_SECURITYLEVEL_ORANGEHOST = "Orange Host";
	public static final String ENUM_SECURITYLEVEL_REDHOST = "Red Host";
	public static final String ENUM_SECURITYLEVEL_BLACKHOST = "Black Host";
	public static final String ENUM_SECURITYLEVEL_UNKNOWNHOST = "Unknown Host";
	public static final String ENUM_SECURITYLEVEL_NONE = "None";
	
	
	public enum SECURITYLEVEL {
		BLUEHOST(ENUM_SECURITYLEVEL_BLUEHOST), //Public Service Databases; PUBLIC
		GREENHOST(ENUM_SECURITYLEVEL_GREENHOST), //Average Systems; NORMAL
		ORANGEHOST(ENUM_SECURITYLEVEL_ORANGEHOST), //Confidential Data; Factory Controller; NORMAL
		REDHOST(ENUM_SECURITYLEVEL_REDHOST), // Most secure; "Top Secret" data; Mission Critical Process Controls; SECURE
		BLACKHOST(ENUM_SECURITYLEVEL_BLACKHOST), //Systems that don't have a color; Illegal security; SECURE
		UNKNOWN(ENUM_SECURITYLEVEL_UNKNOWNHOST), //Cannot determine security level
		NONE(ENUM_SECURITYLEVEL_NONE); //Doesn't have a security level/Not hackable
		
	  private final String name;
	  private static HashMap<String,SECURITYLEVEL> map = null;
	  
	  SECURITYLEVEL(String stringName) {
		  name = stringName;
	  }
	  
	  public String toString(){
		  return name;
		  }
	  
	  public static SECURITYLEVEL toValue(String name){
		if( map == null ){
			map = new HashMap<String,SECURITYLEVEL>();
			
			for (SECURITYLEVEL action : SECURITYLEVEL.values()){
				map.put(action.toString(), action);
			}
		}
		return map.get(name);	  
	  }
		
	}
	
	public static final String _TERMINAL_MESSAGE = "SECURITY LEVEL: ";
	
	public SECURITYLEVEL getSecurityLevel();
	public void setSecurityLevel(String level);

}
