package com.drs.cyberpunk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.drs.cyberpunk.CyberpunkGame;
import com.drs.cyberpunk.Utility;

public class SplashScreen implements Screen {

    private Stage stage;
    private Image logoImage = null;
    private Texture logoTexture = null;
    
    @SuppressWarnings("unused")
	private static final String TAG = SplashScreen.class.getSimpleName();
    private static final String logoFilenamePath = "data/images/Digital_Ronin_Studios_512x512.png";
	
	public SplashScreen(){
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        
        if( logoTexture == null ){
        	logoTexture = Utility.getTextureAsset(logoFilenamePath);
        	if( logoTexture != null){
        		registerImage();
        	}
        }
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport( width, height, true );		
	}

	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		Utility.loadTextureAsset(logoFilenamePath);
		logoTexture = Utility.getTextureAsset(logoFilenamePath);		
		
		if( logoTexture != null ){
			 registerImage();
		}
		

	}
	
	private void registerImage(){
		logoImage = new Image(logoTexture);
		
		logoImage.setPosition(Gdx.graphics.getWidth()/2-logoImage.getWidth()/2, Gdx.graphics.getHeight()/2-logoImage.getHeight()/2);
		
		logoImage.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().mainMenuScreen);
				return true;
			}

			}
		);
		
		
		logoImage.addAction( Actions.sequence( Actions.fadeOut( 0.001f ), Actions.fadeIn( 2f ), Actions.delay(0f) ) );
		stage.addActor(logoImage);
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
		stage.dispose();
		stage = null;
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
		Utility.unloadAsset(logoFilenamePath);
		stage.dispose();
	}

	
	
	
}
