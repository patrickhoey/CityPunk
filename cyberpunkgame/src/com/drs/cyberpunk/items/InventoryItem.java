package com.drs.cyberpunk.items;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.drs.cyberpunk.Utility;

public class InventoryItem implements IInventoryItem, Comparable<InventoryItem> {
	
	private static final String TAG = InventoryItem.class.getSimpleName();
	
	public static final String _UPLOAD = 		"upload";
	public static final String _DOWNLOAD =  	"download";
	public static final String _ICEPICK =  		"icepick";
	public static final String _ERASE =  		"erase";
	public static final String _CRUMBLE =  		"crumble";
	public static final String _CRASH =  		"crash";
	public static final String _DECOY = 		"decoy";
	public static final String _RECON =  		"recon";
	public static final String _STEALTH = 		"stealth";
	public static final String _SHIELD =  		"shield";
	public static final String _DECRYPT = 		"decrypt";
	public static final String _DATAFILE =  	"datafile";
	public static final String _DATAFILE2 =  	"datafile2";
	
	//Small image that represents software in inventory
	private Image itemImage = null;
	
	//Serialize:
	private String itemLongDescription;
	private String itemShortDescription;
	private String itemID; 	//Unique identification for this specific instance of software
	private String itemImagePath;
	private Texture itemImageTexture = null;

	private String itemDisplay;
	private String itemAttributeDisplay;  
	
	public final static String inventoryItemDisplayHeader = 
			Utility.padRight("Name", 10) +
			"Description";  
	
	public InventoryItem(){
	}
	
	
	@Override
	public String getItemLongDescription() {
		return itemLongDescription;
	}

	@Override
	public void setItemLongDescription(String itemDescription) {
		this.itemLongDescription = itemDescription;
	}
	
	@Override
	public String getItemShortDescription() {
		return itemShortDescription;
	}

	@Override
	public void setItemShortDescription(String itemDescription) {
		this.itemShortDescription = itemDescription;
	}

	
	@Override
	public Image getItemImage() {
		if( itemImage == null ){
			itemImageTexture = Utility.getTextureAsset(itemImagePath);
			//create the image
			if( itemImageTexture == null ) return null;
			itemImage = new Image(itemImageTexture);
		}
		return itemImage;
	}

	@Override
	public void setItemImage(Image itemImage) {
		this.itemImage = itemImage;
	}
	
	@Override
	public String getItemImagePath(){
		return itemImagePath;
	}
	
	@Override
	public void setItemImagePath(String itemImagePath){
		this.itemImagePath = itemImagePath;
	}
	
	
	@Override
	public String getItemID(){
		return itemID;
	}
	
	@Override
	public void setItemID(String itemID){
		this.itemID = itemID;
	}
	
	@Override
	public void write(Json json) {
		json.writeValue("itemLongDescription", itemLongDescription);
		json.writeValue("itemShortDescription", itemShortDescription);
		json.writeValue("itemID", itemID);
		json.writeValue("itemImagePath", itemImagePath);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		if( jsonData.get("itemLongDescription") != null ) {
			itemLongDescription = jsonData.get("itemLongDescription").asString();			
		}

		if( jsonData.get("itemShortDescription") != null ){
			itemShortDescription = jsonData.get("itemShortDescription").asString();		
		}

		if( jsonData.get("itemID") != null ){
			String itemDataString = jsonData.get("itemID").asString();
			if( itemDataString.isEmpty() ){
				itemID = UUID.randomUUID().toString();
			}else{
				itemID = itemDataString;	
			}
	
		}
		
		if( jsonData.get("itemImagePath") != null ){
			itemImagePath = jsonData.get("itemImagePath").asString();			
		}
		
		if( jsonData.get("itemShortDescription") != null ){
			itemDisplay = Utility.padRight(jsonData.get("itemShortDescription").asString(), 10);
		}
		if( jsonData.get("itemLongDescription") != null ) {
			itemDisplay += jsonData.get("itemLongDescription").asString();
		}
		
		itemAttributeDisplay = "NONE";
		
		Utility.loadTextureAsset(itemImagePath);
		
		itemImageTexture = Utility.getTextureAsset(itemImagePath);		
		
		if( itemImageTexture == null ){
			Gdx.app.debug(TAG, "Texture is null" );
			return;
		}
		
		//create the image
		itemImage = new Image(itemImageTexture);
	}


	@Override
	public String getItemDisplayString() {
		return itemDisplay;
	}

	@Override
	public void setItemDisplayString(String displayString) {
		this.itemDisplay = displayString;
	}

	@Override
	public String getDisplayAttributes() {
		return itemAttributeDisplay;
	}


	@Override
	public void setDisplayAttributes(String attributes) {
		this.itemAttributeDisplay = attributes;
	}


	@Override
	public int compareTo(InventoryItem item) {
		return this.itemID.compareTo(item.getItemID());
	}
	
}
