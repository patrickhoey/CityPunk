package com.drs.cyberpunk.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;

public interface IInventoryItem extends Json.Serializable{

	public String getItemLongDescription();
	public void setItemLongDescription(String itemDescription);
	
	public String getItemShortDescription();
	public void setItemShortDescription(String itemDescription);
	
	public Image getItemImage();
	public void setItemImage(Image itemImage);
	
	public String getItemImagePath();
	public void setItemImagePath(String itemImagePath);
	
	public String getItemID();
	public void setItemID(String itemID);
	
	public String getItemDisplayString();
	public void setItemDisplayString(String displayString);
	
	public String getDisplayAttributes();
	public void setDisplayAttributes(String attributes);
}
