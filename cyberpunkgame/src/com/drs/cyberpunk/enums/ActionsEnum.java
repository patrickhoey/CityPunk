package com.drs.cyberpunk.enums;

import java.util.HashMap;

public class ActionsEnum implements Comparable<Object>{
	private static HashMap<String,ActionsEnum> map = null;
	private static int nextOrdinal = 0;

	public static final ActionsEnum EXIT = new ActionsEnum("EXIT");
	public static final ActionsEnum ANALYZE = new ActionsEnum("ANALYZE");
	public static final ActionsEnum JACK_IN = new ActionsEnum("JACK IN");
	
	private final int ordinal = nextOrdinal++;
	private final String name;

	private ActionsEnum(String name){
		this.name = name;
		if( map == null ){
			map = new HashMap<String,ActionsEnum>();
			}
		map.put(name, this);
		}
	
	public String toString(){
		return name;
	}
	
	public int compareTo(Object o){
		if( o == null ) return -1;
		return ordinal - ((ActionsEnum)o).ordinal;
	}
	
	public static ActionsEnum valueOf(String name){
		if( name == null ) return null;
		return map.get(name);
	}
}
