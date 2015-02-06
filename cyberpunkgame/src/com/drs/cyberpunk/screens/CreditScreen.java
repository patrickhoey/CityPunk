package com.drs.cyberpunk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.drs.cyberpunk.Assets;
import com.drs.cyberpunk.CyberpunkGame;

public class CreditScreen implements Screen {
    private Stage stage;
    private ScrollPane scrollPane;
		
	public CreditScreen(){
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
        
        scrollPane.setScrollY(scrollPane.getScrollY()+delta*20);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport( width, height, true );		
	}

	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		//Get text
		FileHandle file = Gdx.files.internal(Assets._CREDITS_PATH);
		String textString = file.readString();
		
		Label text = new Label(textString, Assets.skin);
	    text.setAlignment(Align.top | Align.left);
	    text.setWrap(true);
	    
	    scrollPane = new ScrollPane(text);
		    
	    scrollPane.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().mainMenuScreen);
				return true;
			}

			}
		);
	      
	    Table table = new Table();
	    table.center();
	    table.setFillParent(true);
		table.defaults().width(Gdx.graphics.getWidth());	
	    table.add(scrollPane);
	      
	    stage.addActor(table);
	}
		
	/*
	Runnable onSplashFinishedRunnable = new Runnable() {
		@Override
		public void run() {
			game.setScreen(game.gameScreen);
		}
		};
		*/

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
