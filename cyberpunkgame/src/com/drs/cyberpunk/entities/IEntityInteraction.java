package com.drs.cyberpunk.entities;

import com.drs.cyberpunk.items.InventoryItem;

public interface IEntityInteraction {
	
	public static enum ACTION {
		ATTACK, GIVE_CREDIT, NOTHING
	}
	
	public void receivedInventoryItemAction(Entity sender, ACTION action, InventoryItem item);
}
