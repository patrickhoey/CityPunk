package com.drs.cyberpunk;

import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.drs.cyberpunk.entities.Entity;
import com.drs.cyberpunk.entities.IEntityInteraction.ACTION;
import com.drs.cyberpunk.items.InventoryItem;
import com.drs.cyberpunk.items.SoftwareItem;
import com.drs.cyberpunk.listeners.WorldRenderListener;
import com.drs.cyberpunk.screens.InventoryScreen;

public class GameHUD implements Screen{

	static int place = 0;
	private static final int NUMSLOTS = 8;
	
	private static final String TAG = GameHUD.class.getSimpleName();
	
	//Loaded software
	private Hashtable<Stack, SoftwareItem> loadedSoftware;
	
	private Texture hudTexture;
	private Texture defaultInventorySlotTexture;
	private Texture selectionInventorySlotTexture;
	
	private final static String hudFilenamePath = "data/images/cyberdeck.png";
	private final static String defaultInventorySlotPath = "data/images/inventory_slot.png";
	private final static String selectionInventorySlotPath = "data/images/inventory_slot_selection.png";
	private final static String BACKGROUND_IMAGE = "data/images/healthbar_background.png";
	private final static String HEALTHBAR_FOREGROUND_IMAGE = "data/images/healthbar_foreground.png";
	private final static String ALERTBAR_FOREGROUND_IMAGE = "data/images/alertbar_foreground.png";

	private Stage stage;
    //private TextButton inventoryButton;
    private Camera camera;
   
    private Image cyberDeckImage;

    private Label text;
    private String currentMessage;
    private InventoryScreen inventory;
    private Button cyberDeckButton;
    private Table inventoryLoaded;
    
    private Table terminalTable;
    
    //Inventory slots
    private final String defaultSlotImageName = "default_image";
    private final String inventorySelection = "inventory_selection";
    private Image selectionInventorySlotImage;
    
    private Entity currentSelectedEntity;
    
    private static final String HUD = "foreground_hud";
    private static final String INVENTORY = "background_inventory";
    
    private HealthBar healthBar;
    private HealthBar alertBar;
    
    private boolean barsInitialized = false;
    
    private boolean alertTriggered = false;
    private WorldRenderListener worldRender = null;
    
    //Groups
	Group bg;
	Group fg;
    
	public GameHUD(Camera camera){
		this.camera = camera;
		Gdx.app.debug(TAG, "Initializing HUD..." );
	}
	
	public Stage getStage(){
		return stage;
	}
	
	public void setIsLoadEnabled(boolean load){
		if( load ){
			inventory.getLoadButton().setDisabled(false);
			inventory.getUnloadButton().setDisabled(false);
		}else{
			inventory.getLoadButton().setDisabled(true);
			inventory.getUnloadButton().setDisabled(true);
		}
	}
	
	public void setCurrentSelectedEntity(Entity entity){
		this.currentSelectedEntity = entity;
	}
	
	public Entity getCurrentSelectedEntity(){
		return currentSelectedEntity;
	}
	
	@Override
	public void render(float delta) {

		if( !currentMessage.isEmpty() ){
			String message = text.getText() + "\n" + currentMessage;
			
			String[] sentences = message.split("\n");
			//Gdx.app.debug(TAG, "Number of sentences: " + sentences.length );
			
			int totalDisplayedSentences = 6;
			
			if( sentences.length > totalDisplayedSentences){
				message = "";
				//Take the last 9 sentences
				int offset = (sentences.length - totalDisplayedSentences) - 1;
				for(; offset < sentences.length; offset++){
					message += "\n" + sentences[offset];
				}
			}
			
			text.setText(message);	
			currentMessage = "";
		}
		
		if( currentSelectedEntity != null){
			float distance = currentSelectedEntity.getCurrentPosition().dst(WorldEntities.getInstance().getPlayer().getCurrentPosition());
			
			if( distance > WorldEntities.getInstance().getPlayer().getSelectionThreshold()){
				currentSelectedEntity.setIsSelected(false);
				currentSelectedEntity = null;				
			}

		}

        stage.act(delta);
        stage.draw();
		//Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport( width, height, false );
	}
	
	public void addTerminalMessage(String message){
		currentMessage = message;
	}
	
	public InventoryScreen getInventoryScreen(){
		return inventory;
	}

	public void dispose() {
		Utility.unloadAsset(hudFilenamePath);
		Utility.unloadAsset(defaultInventorySlotPath);
		Utility.unloadAsset(selectionInventorySlotPath);
		stage.dispose();	
	}

	@Override
	public void hide() {
		Utility.unloadAsset(hudFilenamePath);
		Utility.unloadAsset(defaultInventorySlotPath);
		Utility.unloadAsset(selectionInventorySlotPath);
	}
	
	private void getLoadedTextures(){
		if( hudTexture == null && Utility.isAssetLoaded(hudFilenamePath)){
			hudTexture = Utility.getTextureAsset(hudFilenamePath);		
			cyberDeckImage = new Image(hudTexture);	
			
			terminalTable.defaults().width(cyberDeckImage.getWidth()-100f);
			terminalTable.defaults().height(cyberDeckImage.getHeight()-36f);
		    //TODO: Figure out nicer way to place the scroll box for console
			terminalTable.add(text);
			terminalTable.right().bottom().padRight(35);
			terminalTable.padBottom(20);
			
			//Gdx.app.debug(TAG, "Terminal position " + terminalTable.getX() + "," + terminalTable.getY());

			cyberDeckButton.stack(cyberDeckImage, terminalTable).expand().fill();
			cyberDeckButton.pack();
			cyberDeckButton.setPosition(Gdx.graphics.getWidth() - cyberDeckButton.getWidth() , 0);
			
			//Gdx.app.debug(TAG, "Terminal position " + terminalTable.getX() + "," + terminalTable.getY());
		}
		
		if( selectionInventorySlotTexture == null && Utility.isAssetLoaded(selectionInventorySlotPath)){
			selectionInventorySlotTexture = Utility.getTextureAsset(selectionInventorySlotPath);
			selectionInventorySlotImage = new Image(selectionInventorySlotTexture);
			selectionInventorySlotImage.setName(inventorySelection);
			selectionInventorySlotImage.setVisible(false);
		}

		if( defaultInventorySlotTexture == null && Utility.isAssetLoaded(defaultInventorySlotPath)){
			defaultInventorySlotTexture = Utility.getTextureAsset(defaultInventorySlotPath);
			inventoryLoaded.left();
			inventoryLoaded.setWidth(8*64); //8 slots 64px wide 
			inventoryLoaded.setHeight(defaultInventorySlotTexture.getHeight());
			//How to set the background drawable
			inventoryLoaded.setBackground("default-scroll");
			inventoryLoaded.debug();
			addInventoryDefaultSlots();
		}
		
		if( hudTexture != null && healthBar.isInitialized() && barsInitialized == false ){
			healthBar.setPosition(cyberDeckButton.getX(), cyberDeckButton.getY()+cyberDeckButton.getHeight()+5);		
		
			if( alertBar.isInitialized() ){
				alertBar.setPosition(healthBar.getX(), healthBar.getY()+healthBar.getHeight()+5);
				barsInitialized = true;
			}
		}

	}
	
	public void setWorldRenderListener(WorldRenderListener worldRender){
		this.worldRender = worldRender;
	}
	
	public void update(float delta){
		healthBar.update(delta);
		alertBar.update(delta);		
		getLoadedTextures();
		inventory.update(delta);
		
		for(SoftwareItem item: loadedSoftware.values()){
			item.update(delta);
		}
	}
	
	@Override
	public void show() {
		Utility.loadTextureAsset(hudFilenamePath);
		Utility.loadTextureAsset(defaultInventorySlotPath);
		Utility.loadTextureAsset(selectionInventorySlotPath);
		
		loadedSoftware = new Hashtable<Stack,SoftwareItem>();
		
		bg = new Group();
		bg.setName(INVENTORY);
		fg = new Group();
		fg.setName(HUD);
		
		inventory = new InventoryScreen();
		
		cyberDeckButton = new Button(Assets.skin, "transparent");
		cyberDeckButton.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				//Check for availability; Disabled when in cyberspace

				if( inventory.isVisible() ){
					inventory.setVisible(false);
					selectionInventorySlotImage.setVisible(false);
				}else{
					inventory.setVisible(true);	
					selectionInventorySlotImage.setVisible(true);
				}

				return true;
			}

			}
		);
		
		//Gdx.app.debug(TAG, "GameHUD: Size: (" + Gdx.graphics.getWidth() + "," + Gdx.graphics.getHeight() + ")" );
		
		stage = new Stage();
		stage.setCamera(camera);	
		
		//Gdx.app.debug(TAG, "GameHUD: Texture Size: (" + hudTexture.getWidth() + "," + hudTexture.getHeight() + ")" );
		//Gdx.app.debug(TAG, "GameHUD: Image Size: (" + logoImage.getWidth() + "," + logoImage.getHeight() + ")" );
		
		terminalTable = new Table();
		terminalTable.center();
		terminalTable.setFillParent(true);
		terminalTable.debug();
		
	    currentMessage = new String();
	        
		text = new Label("", Assets.skin, "terminal-font", Color.GREEN.cpy());
		text.setText("Initializing...");
		text.setAlignment(Align.left);
		text.setWrap(true);
		
		//Setup inventory
		inventory.setWidth(stage.getWidth());
		inventory.setHeight(stage.getHeight());
		
		inventoryLoaded = new Table(Assets.skin);	
		
		//Setup listener here if the LOAD button was pressed
		
		//This load button ONLY loads software into the memory slots
		//Must check for this
		inventory.getLoadButton().addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(selectionInventorySlotImage == null) return true;
				
				InventoryItem item = inventory.getCurrentSelectedItem();
				SoftwareItem softwareItem;
				
				if( item instanceof SoftwareItem ){
					softwareItem = (SoftwareItem)inventory.getCurrentSelectedItem();
				}else{
					return true;
				}
				
				Image itemImage = softwareItem.getItemImage();
				
				Stack stack = (Stack)selectionInventorySlotImage.getParent();
				if ( stack == null ){
					return true;
				}
				
				boolean isSelection = isSelectedSlot(stack);
				
				if( itemImage != null && isSelection){
					//Register the associated item with the loadedSoftware container
					loadedSoftware.put(stack, softwareItem);
					stack.getChildren().pop();
					stack.add(itemImage);
					stack.add(selectionInventorySlotImage);
					}
				return true;
			}
			

			}
			
		);
		
		inventory.getUnloadButton().addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {			
				if( selectionInventorySlotImage == null ) return true;
				
				Stack stack = (Stack)selectionInventorySlotImage.getParent();
				if ( stack == null ){
					return true;
				}
				
				boolean isSelection = isSelectedSlot(stack);
				
				if( isSelection ){
					loadedSoftware.remove(stack);
					//Pop off selection
					stack.getChildren().pop();
					
					//Check to see if we have a default image, or one loaded
					Actor actor = stack.getChildren().peek();
					String name = actor.getName();
					if( name == null || !name.equalsIgnoreCase(defaultSlotImageName)){
						//Pop off item image
						stack.getChildren().pop();		
					}				
					
					}
				return true;
			}
			

			}
			
		);
		
		healthBar = new HealthBar(BACKGROUND_IMAGE, HEALTHBAR_FOREGROUND_IMAGE);
		healthBar.setMaxHealth(WorldEntities.getInstance().getPlayer().getMaxHealth());
		healthBar.setCurrentHealth(WorldEntities.getInstance().getPlayer().getMaxHealth());
		
		alertBar = new HealthBar(BACKGROUND_IMAGE, ALERTBAR_FOREGROUND_IMAGE);
		alertBar.setMaxHealth(100);
		alertBar.setCurrentHealth(0);
		
		// the order is important in the following two lines
		stage.addActor(bg); 
		stage.addActor(fg);
		
		//Add actors to the stage
		bg.addActor(inventory);
		
		fg.addActor(cyberDeckButton);
		fg.addActor(inventoryLoaded);
		fg.addActor(healthBar);
		fg.addActor(alertBar);
		
		//fg.print();
		
		inventory.setVisible(false);

	}
	
	public void resetHealthBar(){
		healthBar.setCurrentHealth(healthBar.getMaxHealth());
	}
	
	public void resetAlertBar(){
		alertTriggered = false;
		alertBar.setCurrentHealth(0);
	}
	
	public void isHealthBarVisible(boolean isVisible){
		healthBar.setVisible(isVisible);
	}
	
	public void isAlertBarVisible(boolean isVisible){
		alertBar.setVisible(isVisible);
	}
	
	public void setHealthBarDamage(int damage){
		healthBar.setHealthDamage(damage);
	}
	
	public void setHealthBarHeal(int heal){
		healthBar.setHealthHeal(heal);
	}
	
	public void setAlertIncrease(int increase){
		alertBar.setHealthHeal(increase);
		
		if( alertTriggered ) return;
		
		if( alertBar.getCurrentHealth() == alertBar.getMaxHealth()){
			worldRender.alertTriggered();
			alertTriggered = true;
		}
	}
	
	public void setAlertDecrease(int decrease){
		alertBar.setHealthDamage(decrease);
		
		if( !alertTriggered ) return;
		
		if( alertBar.getCurrentHealth() != alertBar.getMaxHealth()){
			worldRender.alertStopped();
			alertTriggered = false;
		}
	}
	
	public void setMaxHealthBar(int maxHealth){
		healthBar.setMaxHealth(maxHealth);
		healthBar.setCurrentHealth(maxHealth);
	}
	
	private boolean isSelectedSlot(Stack stack){
		if( stack == null ){
			return false;
		}
		Actor actor = stack.getChildren().peek();
		String name = actor.getName();
		if( name == null ){
			return false;
		}else{
			return name.equalsIgnoreCase(inventorySelection);				
		}

	}
	
	private void addInventoryDefaultSlots(){
		
		
		for( int i = 1; i <= NUMSLOTS; i++ ){
			Stack stack = new Stack();
			Image image = new Image(defaultInventorySlotTexture);
			image.setName(defaultSlotImageName);
			stack.addActor(image);
			inventoryLoaded.add(stack);
			
			//Setup listener here if the LOAD button was pressed
			stack.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					if( selectionInventorySlotImage == null ) return true;
					
					Stack stack = (Stack)event.getListenerActor();
					boolean isSelection = isSelectedSlot(stack);
					
					//Turns the selection on and off
					//Should only be available when inventory is up
					if(isSelection){
						stack.getChildren().pop();
					}else{
						stack.add(selectionInventorySlotImage);
					}
					
				SoftwareItem item = loadedSoftware.get(stack);
				if( item == null ){
					return true;
				}	
				
				item.playActivatedSound();
				
				Gdx.app.debug(TAG, "The software executed was: " + item.getItemShortDescription() );	
				
				//No current selection
				if( currentSelectedEntity != null && currentSelectedEntity.isAlive() ){
					MessageQueue.getInstance().addMessageToQueue(MessageQueue.createMessage(
							WorldEntities.getInstance().getPlayer().getEntityID(), 
							currentSelectedEntity.getEntityID(), 
							ACTION.ATTACK.toString(), 
							item.getItemID()));
				}
				return true;
			}
			}
			);
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
		
}
