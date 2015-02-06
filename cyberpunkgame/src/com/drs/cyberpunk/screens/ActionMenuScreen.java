package com.drs.cyberpunk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.drs.cyberpunk.Assets;
import com.drs.cyberpunk.SoundEffect;
import com.drs.cyberpunk.entities.Entity;

public class ActionMenuScreen implements Screen {

	private static final String TAG = ActionMenuScreen.class.getSimpleName();
	private List listItems;
	
	private Window window;
	private Stage stage;
    private Camera camera;
    private Entity currentEntity;
    private SoundEffect menuStart;
    private SoundEffect menuSelectionHover;
    
    private ClickListener clickListener;
    private int lastHoverSelectionIndex;
		
	public ActionMenuScreen(Camera camera){
		this.camera = camera;
		Gdx.app.debug(TAG, "Initializing Action Menu..." );
		init();
	}

	public void init() {	
		stage = new Stage();
		stage.setCamera(camera);
					
		listItems = new List(new String[]{"Initializing..."}, Assets.skin, "actionmenu");
		
		listItems.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if( listItems.getSelection() == null ) return true;
				String selection = listItems.getSelection();
				listItems.setSelectedIndex(-1);
				currentEntity.runActionItem(selection);
				window.setVisible(false);
				return true;
			}
			
			}
		);
		
		listItems.addListener(clickListener = new ClickListener() {
			@Override
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				if (pointer != -1 ) return;
				listItems.setSelectedIndex(-1);
				lastHoverSelectionIndex = -1;
			}
			}
		);
		
		listItems.setSelectable(true);
		listItems.setSelectedIndex(-1);
		
		menuStart = new SoundEffect(Assets._MENU_STARTUP);
		
		menuSelectionHover = new SoundEffect(Assets._MENU_STARTUP_HOVER);
		
		window = new Window("Actions", Assets.skin, "inventory");
		window.setMovable(false);
		window.setModal(true);
		
		window.add(listItems).center();
		
		stage.addActor(window);
		
		window.debug();
	}
	
	private int getItemListIndex(List list, Vector2 coords){
		int selectedIndex = -1;
		selectedIndex = (int)((list.getHeight() - coords.y) / list.getItemHeight());
		selectedIndex = Math.max(0, selectedIndex);
		selectedIndex = Math.min(list.getItems().length - 1, selectedIndex);
		return selectedIndex;
	}
	
	public void updateActionMenu(Entity entity){
		currentEntity = entity;
		listItems.setItems(entity.getActionItems().toArray());
		listItems.setSelectedIndex(-1);
		window.pack();
	}
	
	public Stage getStage(){
		return stage;
	}

	public void setPosition(float x, float y){
		window.setPosition(x, y);
		//Gdx.app.debug(TAG, "SET POSITION: x: " +  window.getX() + " y: " + window.getY() );
	}
	
	@Override
	public void render(float delta) {
        stage.act(delta);
        stage.draw();
        
        
        if( window.isVisible() ){
	        int mouseX = Gdx.input.getX();
	    	int mouseY = Gdx.input.getY();
	    	Vector2 temp = new Vector2(mouseX, mouseY);
	    	
	    	stage.screenToStageCoordinates(temp);
	    	listItems.stageToLocalCoordinates(temp);
	    	
	        if( clickListener.isOver(listItems, temp.x,temp.y) ){
	        	//Gdx.app.debug(TAG, "SET POSITION: x: " +  temp.x + " y: " + temp.y );
	        	
	        	int index = getItemListIndex(listItems, temp);
	        	listItems.setSelectedIndex(index);
	       	
	        	if( lastHoverSelectionIndex != index){
	            	menuSelectionHover.play();
	            	lastHoverSelectionIndex = index;
	        	}
	
	        }      	
        }

        //Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport( width, height, false );
	}

	@Override
	public void show() {
		menuStart.play();
		window.setVisible(true);
	}

	@Override
	public void hide() {
		window.setVisible(false);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
