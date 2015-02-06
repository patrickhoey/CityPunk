package com.drs.cyberpunk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.drs.cyberpunk.Assets;
import com.drs.cyberpunk.CyberpunkGame;
import com.drs.cyberpunk.ProfileManager;

public class NewGameScreen implements Screen {

	private Stage stage;
	
	//Buttons
	private Label profileName;
	
	private TextButton startButton;
	private TextButton backButton;
	
	private TextField profileText;
	
	private Dialog overwriteDialog;
	private Label overwriteLabel;
	private TextButton cancelButton;
	private TextButton overwriteButton;

	
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
		ProfileManager.getInstance().processAllProfiles();
		
		//Buttons
		profileName = new Label("Enter Profile Name: ", Assets.skin, "inventory");
		profileText = new TextField("", Assets.skin, "inventory");
		profileText.setMaxLength(20);
		
		overwriteDialog = new Dialog("Overwrite?", Assets.skin, "inventory");
		overwriteDialog.debug();
		overwriteLabel = new Label("Overwrite existing profile name?", Assets.skin, "inventory");
		cancelButton = new TextButton("Cancel", Assets.skin, "inventory");
		
		cancelButton.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ){
				overwriteDialog.hide();
				return true;
			}
		}
		);
		
		overwriteButton = new TextButton("Overwrite", Assets.skin, "inventory");
		
		overwriteButton.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ){
				String messageText = profileText.getText();
				ProfileManager.getInstance().addProfile(messageText,true);
				CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().gameScreen);
				return true;
			}
		}
		);
		

		overwriteDialog.setKeepWithinStage(true);
		overwriteDialog.setModal(true);
		overwriteDialog.setMovable(false);
		
		overwriteDialog.text(overwriteLabel);
		overwriteDialog.row();
		overwriteDialog.button(overwriteButton).bottom().left();
		overwriteDialog.button(cancelButton).bottom().right();
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		
		startButton = new TextButton("Start", Assets.skin);
		
		startButton.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ){
				String messageText = profileText.getText();
				//check to see if the current profile matches one that already exists
				boolean exists = false;

				exists = ProfileManager.getInstance().getProfileMap().containsKey(messageText);					

				if( exists ){
					//Pop up dialog for Overwrite
					overwriteDialog.show(stage);
				}else{
					ProfileManager.getInstance().addProfile(messageText,false);
					CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().gameScreen);
				}

				return true;
			}
		}
		);
		
		backButton = new TextButton("Back", Assets.skin);
		
		backButton.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ){
				CyberpunkGame.getInstance().setScreen(CyberpunkGame.getInstance().mainMenuScreen);
				return true;
			}
		}
		);
		
		Table topTable = new Table();
		topTable.setFillParent(true);
		topTable.add(profileName).center();
		topTable.add(profileText).center();
		
		Table bottomTable = new Table();
		bottomTable.debug();
		//bottomTable.defaults().uniform();
		bottomTable.setHeight(startButton.getHeight());
		bottomTable.setWidth(Gdx.graphics.getWidth());
		bottomTable.center();
		bottomTable.add(startButton).padRight(50);
		bottomTable.add(backButton);
		
	    stage.addActor(topTable);
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
		stage.dispose();
	}
	


}
