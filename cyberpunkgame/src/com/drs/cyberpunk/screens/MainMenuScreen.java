package com.drs.cyberpunk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.drs.cyberpunk.Assets;
import com.drs.cyberpunk.CyberpunkGame;
import com.drs.cyberpunk.Utility;

public class MainMenuScreen implements Screen {

	private Stage stage;
	
    private Image logoImage = null;
    private Texture logoTexture = null;
    private static final String logoFilenamePath = "data/images/citypunk_title.png";
    
    private Table table;
    
	//Buttons	
	TextButton newGameButton;
	TextButton loadGameButton;
	TextButton optionsButton;
	TextButton creditsButton;
	TextButton exitButton;
	
	public MainMenuScreen(){
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
        if( logoTexture == null ){
        	logoTexture = Utility.getTextureAsset(logoFilenamePath);
        	if( logoTexture != null){
        		registerImage();
        	}
        }
		
		stage.act(delta);
		stage.draw();		
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
		
		table = new Table();
		
		newGameButton = new TextButton("New Game",Assets.skin);
		loadGameButton = new TextButton("Load Game", Assets.skin);
		optionsButton = new TextButton("Options",Assets.skin);
		creditsButton = new TextButton("Credits",Assets.skin);
		exitButton = new TextButton("Exit",Assets.skin);
		
		newGameButton.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().newGameScreen);
				return true;
			}

			}
		);
		
		loadGameButton.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ){
				CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().loadGameScreen);
				return true;
			}
		}
		);
		
		exitButton.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.exit();
				return true;
			}

			}
		);
		
		creditsButton.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().creditScreen);
				return true;
			}

			}
		);
		
		//Set the buttons to be sized to their parent, which in this case is the stage
		table.setFillParent(true);
	}
	
	private void registerImage(){
		logoImage = new Image(logoTexture);
		logoImage.setPosition(0, Gdx.graphics.getHeight()-(logoImage.getHeight()));
	
		//Set the cells width default to the largest button, which in this case is the start button
		table.defaults().width(logoImage.getWidth());
		table.defaults().padBottom(20);
		
		table.add(logoImage);
		table.row();
		table.add(newGameButton);
		table.row();
		table.add(loadGameButton);
		table.row();
		table.add(optionsButton);
		table.row();
		table.add(creditsButton);
		table.row();
		table.add(exitButton);

		stage.addActor(table);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		stage.dispose();
		Utility.unloadAsset(logoFilenamePath);
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
		Utility.unloadAsset(logoFilenamePath);
	}
	
}



