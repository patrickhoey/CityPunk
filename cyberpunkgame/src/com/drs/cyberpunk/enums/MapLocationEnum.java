package com.drs.cyberpunk.enums;

import java.util.HashMap;

public class MapLocationEnum implements Comparable<Object>{
	private static HashMap<String,MapLocationEnum> map = null;
	private static int nextOrdinal = 0;
	
	public static final MapLocationEnum CITYPLAZA001 = new MapLocationEnum("City Plaza");
	public static final MapLocationEnum SUBGRID001 = new MapLocationEnum("Sub Grid");
	public static final MapLocationEnum NONE = new MapLocationEnum("None");
	
	private final int ordinal = nextOrdinal++;
	private final String name;

	private MapLocationEnum(String name){
		this.name = name;
		if( map == null ){
			map = new HashMap<String,MapLocationEnum>();
			}
		map.put(name, this);
		}
	
	public String toString(){
		return name;
	}
	
	public int compareTo(Object o){
		if( o == null ) return -1;
		return ordinal - ((MapLocationEnum)o).ordinal;
	}
	
	public static MapLocationEnum valueOf(String name){
		if( name == null ) return null;
		return map.get(name);
	}
}
