package com.drs.cyberpunk.screens;

import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.drs.cyberpunk.Assets;
import com.drs.cyberpunk.Utility;
import com.drs.cyberpunk.WorldEntities;
import com.drs.cyberpunk.items.InventoryItem;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class InventoryScreen extends Window {

	@SuppressWarnings("unused")
	private static final String TAG = InventoryScreen.class.getSimpleName();
	private static final String _INVENTORY_TEXTURE_PATH = "data/images/inventory_screen.png";
	
	private Texture inventoryTexture;
	private Image currentSelectedItemImage = null;
	private Image inventoryBackgroundImage = null;

	
	private Array<InventoryItem> inventoryItems;
	private Array<String> inventoryItemStrings;
	
	private List listItems;
	private ScrollPane scrollItems;
	private Label itemHeader;
	private Label selectionInformation;
	private ImageTextButton selectionInfoDisplay;
	private TextButton loadSoftware;
	private TextButton unloadSoftware;
	
	private Label credits;
	private static final String _CREDITS = "Credits: ";
	
	private String currentSelectionStr;
	
	public InventoryScreen(){
		super("Inventory", Assets.skin);
		init();
	}

	public void init() {		
		Collection<InventoryItem> collection = WorldEntities.getInstance().getPlayer().getInventoryItems();
		InventoryItem[] items = collection.toArray(new InventoryItem[collection.size()]);
		inventoryItems = new Array<InventoryItem>(items);
		inventoryItems.sort();
		
		inventoryItemStrings = new Array<String>();
		for(InventoryItem item: inventoryItems){
			inventoryItemStrings.add(item.getItemDisplayString());
		}
		
		listItems = new List(inventoryItemStrings.toArray(), Assets.skin, "inventory");
		
		listItems.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				updateSelectionInformation();
				}
			});
		
		scrollItems = new ScrollPane(listItems, Assets.skin, "inventoryPane");
		itemHeader = new Label(InventoryItem.inventoryItemDisplayHeader, Assets.skin, "inventory");

		selectionInformation = new Label("", Assets.skin, "inventory");	
		selectionInformation.setWrap(true);
		
		selectionInfoDisplay = new ImageTextButton(selectionInformation.getText().toString(), Assets.skin);
		selectionInfoDisplay.setDisabled(true);
		selectionInfoDisplay.setWidth(Gdx.graphics.getWidth()*.33f-6); //1/3 screen
		selectionInfoDisplay.setHeight(Gdx.graphics.getHeight()*.66f); //1/3 screen
		selectionInfoDisplay.setPosition(Gdx.graphics.getWidth()-((selectionInfoDisplay.getWidth()+this.getPadRight())), 
				Gdx.graphics.getHeight()-(selectionInfoDisplay.getHeight()+this.getPadTop()));
		
		Utility.loadTextureAsset(_INVENTORY_TEXTURE_PATH);
		inventoryTexture = Utility.getTextureAsset(_INVENTORY_TEXTURE_PATH);		
		
		if( inventoryTexture != null ){
			inventoryBackgroundImage = new Image(inventoryTexture);
			inventoryBackgroundImage.setWidth(Gdx.graphics.getWidth());
			inventoryBackgroundImage.setHeight(Gdx.graphics.getHeight());
			this.addActor(inventoryBackgroundImage);
		}
		
		scrollItems.setWidth(Gdx.graphics.getWidth()*.66f); //2/3ds of the screen
		scrollItems.setHeight(Gdx.graphics.getHeight()*.66f); //2/3ds of the screen
		scrollItems.setPosition(this.getPadLeft(), Gdx.graphics.getHeight()-((scrollItems.getHeight()+this.getPadTop()+itemHeader.getHeight())));
		scrollItems.setOverscroll(false, false);
		
		loadSoftware = new TextButton("Load", Assets.skin, "inventory");
		loadSoftware.setHeight(50);
		loadSoftware.setWidth(100);
		loadSoftware.setPosition(this.getPadLeft(), Gdx.graphics.getHeight()-(scrollItems.getHeight()+this.getPadTop()+itemHeader.getHeight()+loadSoftware.getHeight()));
		
		unloadSoftware = new TextButton("Unload", Assets.skin, "inventory");
		unloadSoftware.setHeight(50);
		unloadSoftware.setWidth(100);
		unloadSoftware.setPosition(loadSoftware.getX()+loadSoftware.getWidth()+20, loadSoftware.getY());
		
		itemHeader.setPosition(this.getPadLeft(), Gdx.graphics.getHeight()-itemHeader.getHeight()-this.getPadTop());

		selectionInfoDisplay.add(selectionInformation).center().left().width(selectionInfoDisplay.getWidth()).height(selectionInfoDisplay.getHeight()*.66f);
		
		credits = new Label(_CREDITS + 0, Assets.skin, "inventory");
		credits.setPosition(unloadSoftware.getX()+(unloadSoftware.getWidth()*2),unloadSoftware.getY()+(unloadSoftware.getHeight()/2-credits.getHeight()/2));
		
		this.addActor(itemHeader);
		this.addActor(scrollItems);
		this.addActor(loadSoftware);
		this.addActor(unloadSoftware);
		this.addActor(credits);
		this.addActor(selectionInfoDisplay);
		
		updateSelectionInformation();
	}
	
	public void updateCreditField(){
		credits.setText(_CREDITS + WorldEntities.getInstance().getPlayer().getNumberOfCredits());
	}
	
	public TextButton getLoadButton(){
		return loadSoftware;
	}
	
	public TextButton getUnloadButton(){
		return unloadSoftware;
	}
	
	public InventoryItem getCurrentSelectedItem(){
		int index = listItems.getSelectedIndex();
		if( index >= 0){
			InventoryItem selectionItem = inventoryItems.get(index);
			return selectionItem;
		}
		return null;
	}
	
	private void updateSelectionInformation(){
		int index = listItems.getSelectedIndex();
		if( index >= 0){
			InventoryItem selectionItem = inventoryItems.get(index);
			Image image = selectionInfoDisplay.getImage();
			currentSelectedItemImage = selectionItem.getItemImage();
			
			if( image != null && currentSelectedItemImage != null){
				image.setDrawable(currentSelectedItemImage.getDrawable());			
			}
			
			currentSelectionStr = "Name: " + selectionItem.getItemShortDescription() + "\n";
			currentSelectionStr += "Description: " + selectionItem.getItemLongDescription() + "\n";
			currentSelectionStr += "\nAttributes: " + "\n\n";
			currentSelectionStr += selectionItem.getDisplayAttributes();

			selectionInformation.setText(currentSelectionStr);
		}
	}
	
	public void update(float delta){
		int index = listItems.getSelectedIndex();
		if( index >= 0 && currentSelectedItemImage == null){
			updateSelectionInformation();
		}	
		
		if( inventoryTexture == null ){
			inventoryTexture = Utility.getTextureAsset(_INVENTORY_TEXTURE_PATH);
			if( inventoryTexture == null) {
				return;
			}
			inventoryBackgroundImage = new Image(inventoryTexture);
			inventoryBackgroundImage.setWidth(Gdx.graphics.getWidth());
			inventoryBackgroundImage.setHeight(Gdx.graphics.getHeight());
			this.addActorBefore(itemHeader, inventoryBackgroundImage);
		}
		
		updateCreditField();
	}
	
	
}




