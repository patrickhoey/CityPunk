package com.drs.cyberpunk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.drs.cyberpunk.Assets;
import com.drs.cyberpunk.CyberpunkGame;
import com.drs.cyberpunk.ProfileManager;

public class LoadGameScreen implements Screen {
    private Stage stage;
    private ScrollPane scrollPane;
    
	private TextButton loadButton;
	private TextButton backButton;

	private List listItems;
	
	public LoadGameScreen(){
		
	}
	
	@Override
	public void render(float delta) {
		if( delta == 0){
			return;
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        //Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport( width, height, true );		
	}

	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		loadButton = new TextButton("Load",Assets.skin);
		loadButton.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ){
				String fileName = listItems.getSelection();
				if( fileName != null ){
					FileHandle file = ProfileManager.getInstance().getProfileFilehandle(fileName);
					if( file != null ){
						//@TODO actually load from the file
						CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().gameScreen);
					}
				}
				return true;
			}
		}
		);
		
		
		
		backButton = new TextButton("Back",Assets.skin);
		
		backButton.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ){
				CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().mainMenuScreen);
				return true;
			}
		}
		);
		
		//Get text
		ProfileManager.getInstance().processAllProfiles();
		Array<String> list = ProfileManager.getInstance().getProfileList();
	    
		listItems = new List(list.toArray(), Assets.skin, "inventory");
		
	    scrollPane = new ScrollPane(listItems);
	    scrollPane.setOverscroll(false, false);
	    scrollPane.setFadeScrollBars(false);
	    scrollPane.setScrollingDisabled(true, false);
	    scrollPane.setScrollbarsOnTop(true);
	    
	      
	    Table table = new Table();
	    table.debug();
	    table.center();
	    table.setFillParent(true);
		//table.defaults().width(Gdx.graphics.getWidth());
		table.padBottom(loadButton.getHeight());
	    table.add(scrollPane).center();
	      
		Table bottomTable = new Table();
		bottomTable.debug();
		//bottomTable.defaults().uniform();
		bottomTable.setHeight(loadButton.getHeight());
		bottomTable.setWidth(Gdx.graphics.getWidth());
		bottomTable.center();
		bottomTable.add(loadButton).padRight(50);
		bottomTable.add(backButton);
		
	    stage.addActor(table);
	    stage.addActor(bottomTable);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
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
		stage.clear();
		scrollPane = null;
		stage.dispose();
	}

}
