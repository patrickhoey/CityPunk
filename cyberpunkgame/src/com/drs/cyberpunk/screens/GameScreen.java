package com.drs.cyberpunk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.drs.cyberpunk.PlayerController;
import com.drs.cyberpunk.WorldRenderer;
import com.drs.cyberpunk.enums.MapLocationEnum;


public class GameScreen implements Screen, InputProcessor {

	private static final String TAG = GameScreen.class.getSimpleName();
	
	private InputMultiplexer inputMultiplexer;
	
	private PlayerController controller;
	
	public GameScreen(){
	}
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);	
		
		controller.update(delta);
		
		WorldRenderer.getInstance().render(delta);
	}

	@Override
	public void resize(int width, int height) {
		WorldRenderer.getInstance().setSize(width, height);
	}

	@Override
	public void show() {
		
		Gdx.app.debug(TAG, "show()" );
		
		WorldRenderer.getInstance().show();
		
		inputMultiplexer = new InputMultiplexer();
		
		//Load top level manager classes
		//Default level to load; If loading from savegame, need to pass that value in
		WorldRenderer.getInstance().setCurrentMapLocation(MapLocationEnum.CITYPLAZA001);
		controller = new PlayerController();
		
		//Add HUD to inputprocessor
		//We want the gameHUD to process events first
		inputMultiplexer.addProcessor(WorldRenderer.getInstance().getActionMenu().getStage());
		inputMultiplexer.addProcessor(WorldRenderer.getInstance().getHUD().getStage());
		inputMultiplexer.addProcessor(this);

		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		WorldRenderer.getInstance().hide();
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
		WorldRenderer.getInstance().dispose();
		controller.dispose();
		Gdx.input.setInputProcessor(null);
	}

	//Input processor stubs
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if( keycode == Keys.LEFT || keycode == Keys.A){
			controller.leftPressed();
		}
		if( keycode == Keys.RIGHT || keycode == Keys.D){
			controller.rightPressed();
		}
		if( keycode == Keys.UP || keycode == Keys.W){
			controller.upPressed();
		}
		if( keycode == Keys.DOWN || keycode == Keys.S){
			controller.downPressed();
		}
		if( keycode == Keys.Q){
			controller.quitPressed();
		}
		if( keycode == Keys.I){
			if( WorldRenderer.getInstance().getHUD().getInventoryScreen().isVisible() ){
				WorldRenderer.getInstance().getHUD().getInventoryScreen().setVisible(false);
			}else{
				WorldRenderer.getInstance().getHUD().getInventoryScreen().setVisible(true);
			}

		}
		
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if( keycode == Keys.LEFT || keycode == Keys.A){
			controller.leftReleased();
		}
		if( keycode == Keys.RIGHT || keycode == Keys.D){
			controller.rightReleased();
		}
		if( keycode == Keys.UP || keycode == Keys.W ){
			controller.upReleased();
		}
		if( keycode == Keys.DOWN || keycode == Keys.S){
			controller.downReleased();
		}
		if( keycode == Keys.Q){
			controller.quitReleased();
		}
		return true;
		// TODO Auto-generated method stub
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//Gdx.app.debug(TAG, "GameScreen: MOUSE DOWN........: (" + screenX + "," + screenY + ")" );
		
		if( button == Buttons.LEFT || button == Buttons.RIGHT ){
			controller.setClickedMouseCoordinates(screenX,screenY);
		}
		
		//left is selection, right is context menu
		if( button == Buttons.LEFT){
			controller.selectMouseButtonPressed(screenX, screenY);
		}
		if( button == Buttons.RIGHT){
			controller.doActionMouseButtonPressed(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//left is selection, right is context menu
		if( button == Buttons.LEFT){
			controller.selectMouseButtonReleased(screenX, screenY);
		}
		if( button == Buttons.RIGHT){
			controller.doActionMouseButtonReleased(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
