package com.drs.cyberpunk;

import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.drs.cyberpunk.entities.Entity;
import com.drs.cyberpunk.entities.Entity.Direction;
import com.drs.cyberpunk.entities.Player;
import com.drs.cyberpunk.enums.MapLocationEnum;
import com.drs.cyberpunk.listeners.WorldRenderListener;
import com.drs.cyberpunk.maps.GameMap;
import com.drs.cyberpunk.screens.ActionMenuScreen;



public class WorldRenderer implements WorldRenderListener{

	private final static String TAG = WorldRenderer.class.getSimpleName();
	
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private OrthographicCamera hudCamera;
	private SpriteBatch hudSpriteBatch;
	
	private BitmapFont font;
	
	private Rectangle cameraBoundingBox;
	
	private GameHUD gameHUD;
	private final float unitScale  = 1f;
	
	//cursor
	private Texture cursorTexture;
	private Vector3 rawCursorPosition;
	private Vector3 hudCursorPosition;
	private Vector3 worldCursorPosition;
	
	private SpriteBatch cursorSpriteBatch;
	
	ShapeRenderer shapeRenderer;
	
	private GameMap currentMap = null;
	
	private ActionMenuScreen actionMenu;
	
	private static class VIEWPORT {
		static float viewportWidth;
		static float viewportHeight;
		static float virtualWidth;
		static float virtualHeight;
		static float physicalWidth;
		static float physicalHeight;
	}	
		
	private ShakeCamera shakeCamera;
	private AlarmCamera alarmCamera;
	
	private static WorldRenderer worldRenderer = null;
	
	public static WorldRenderer getInstance(){
		if( worldRenderer == null ){
			worldRenderer = new WorldRenderer();
		}
		return worldRenderer;
	}
	
	private WorldRenderer(){
	}
	
	
	public void hide(){
		Utility.unloadAsset(Assets._CURSOR_PATH);
		Gdx.input.setCursorCatched(false);
	}
	
	public void show(){
		//Gdx.app.debug(TAG, "Construction" );
		
		font = new BitmapFont();
		
		//Debug
		shapeRenderer = new ShapeRenderer();	
		cameraBoundingBox = new Rectangle();
		
		initializeWorld();
		
		//Create the hud
		hudSpriteBatch = new SpriteBatch();	
	
		//cursor
		//Create cursor
		cursorSpriteBatch = new SpriteBatch();
		Utility.loadTextureAsset(Assets._CURSOR_PATH);
		
		rawCursorPosition = new Vector3();
		hudCursorPosition = new Vector3();
		worldCursorPosition = new Vector3();
		
		shakeCamera = new ShakeCamera(VIEWPORT.viewportWidth, VIEWPORT.viewportHeight, 30.0f);
		shakeCamera.reset();
		
		alarmCamera = new AlarmCamera();
		alarmCamera.setAlertMessagePosition((int)VIEWPORT.viewportWidth/2-100, (int)VIEWPORT.viewportHeight-10);
		
		Gdx.input.setCursorCatched(true);
		Gdx.input.setCursorPosition((int)VIEWPORT.viewportWidth / 2, (int)VIEWPORT.viewportHeight / 2);
	}
	
	public void dispose(){
		//current map dispose
		WorldEntities.getInstance().dispose();
		currentMap.dispose();
		gameHUD.dispose();
		if( renderer != null){
			renderer.dispose();			
		}

		Utility.unloadAsset(Assets._CURSOR_PATH);
	}
	
	public Rectangle getCurrentCameraBoundingBox(){
		//Update the current bounding box
		float quadrantWidth = camera.viewportWidth / 2;
		float quadrantHeight = camera.viewportHeight /2;
		float minX = WorldEntities.getInstance().getPlayer().getCurrentPosition().x - quadrantWidth - WorldEntities.getInstance().getPlayer().WIDTH;
		float minY = WorldEntities.getInstance().getPlayer().getCurrentPosition().y - quadrantHeight - WorldEntities.getInstance().getPlayer().HEIGHT;
		float width = camera.viewportWidth + WorldEntities.getInstance().getPlayer().WIDTH;
		float height = camera.viewportHeight + WorldEntities.getInstance().getPlayer().HEIGHT;
		
		cameraBoundingBox.set( minX,minY,width,height);
		return cameraBoundingBox;
	}
	
	public void setSize(int width, int height){
		setupViewport(width, height);
		gameHUD.resize((int)VIEWPORT.physicalWidth, (int)VIEWPORT.physicalHeight);
	}
	
	public GameHUD getHUD(){
		return gameHUD;
	}
	
	public ActionMenuScreen getActionMenu(){
		return actionMenu;
	}
	
	public void setCurrentMapLocation(MapLocationEnum map){
		WorldEntities.getInstance().hide();
		
		
		if( renderer != null ){
			renderer.dispose();
			renderer = null;
		}
		
		if( currentMap != null ){
			//Save the location
			Player player = WorldEntities.getInstance().getPlayer();
			
			if( player.isAlive() ){
				Gdx.app.debug(TAG, "Player is alive: Coordinates: " + player.getCurrentPosition().x + "," + player.getCurrentPosition().y );
				player.setLastCoordinates(currentMap.getMapName(), player.getCurrentPosition().cpy());					
			}else{
				//Reset to default location if player has died
				Vector2 playerStart = new Vector2();
				playerStart.x = currentMap.getPlayerStart().getX();
				playerStart.y = currentMap.getPlayerStart().getY();
				Gdx.app.debug(TAG, "Player has died: Resetting coordinates to default: " + playerStart.x + "," + playerStart.y);
				player.setLastCoordinates(currentMap.getMapName(), playerStart);	
			}
			
			//Purge old messages
			MessageQueue.getInstance().purgeMessageQueue();
			
			currentMap.dispose();
			currentMap = null;
		}

		currentMap = GameMap.mapFactory(map);
		
		if( currentMap.isCyberspace() ){
			gameHUD.resetHealthBar();
			gameHUD.resetAlertBar();
			
			gameHUD.isHealthBarVisible(true);
			gameHUD.isAlertBarVisible(true);
			
			gameHUD.setIsLoadEnabled(false);
		}else{
			gameHUD.resetHealthBar();
			gameHUD.resetAlertBar();
			
			gameHUD.isHealthBarVisible(false);
			gameHUD.isAlertBarVisible(false);
			gameHUD.setIsLoadEnabled(true);
		}
		
        TiledMap tiledMap = currentMap.getMap();
        if( tiledMap == null ){
        	Gdx.app.debug(TAG, "Map is not loaded!");
        	return;
        }
        
		renderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);	
		renderer.setView(camera);

		WorldEntities.getInstance().createEntities(map);
		WorldEntities.getInstance().updatePlayerMapStartCoordinates(map);
		WorldEntities.getInstance().show();
	}
	
	public GameMap getCurrentMap(){
		return currentMap;
	}
	
	public MapLocationEnum getCurrentMapLocation(){
		return currentMap.getMapName();
	}

	
	private void initializeWorld(){
		//camera setup	
		setupViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//get the current size		
		camera = new OrthographicCamera(VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		camera.setToOrtho(false, VIEWPORT.viewportWidth,VIEWPORT.viewportHeight);
		camera.update();
		
		hudCamera = new OrthographicCamera(VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		hudCamera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		hudCamera.update();
		
		gameHUD = new GameHUD(hudCamera);
		gameHUD.setWorldRenderListener(this);
		gameHUD.show();
		
		actionMenu = new ActionMenuScreen(hudCamera);
		actionMenu.hide();
	}
	
	public void render(float delta){
		//updates
		if( currentMap != null ){
			currentMap.update(delta);			
		}
		
		if( gameHUD != null ){
			gameHUD.update(delta);
		}
		
		if( alarmCamera != null ){
			alarmCamera.update(delta);
		}
		
		if(cursorTexture == null && Utility.isAssetLoaded(Assets._CURSOR_PATH)){
			cursorTexture = Utility.getTextureAsset(Assets._CURSOR_PATH);	
		}

		WorldEntities.getInstance().update(delta);
		MessageQueue.getInstance().update(delta);
		
		//renderer.getSpriteBatch().disableBlending();
		if( renderer == null && Utility.isAssetLoaded(currentMap.getMapFilenamePath())){
	        TiledMap tiledMap = currentMap.getMap();
	        if( tiledMap != null ){
				renderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);	
				renderer.setView(camera);
				
				WorldEntities.getInstance().createEntities(currentMap.getMapName());
				WorldEntities.getInstance().updatePlayerMapStartCoordinates(currentMap.getMapName());
				WorldEntities.getInstance().show();
	        }else{
	        	Gdx.app.debug(TAG, "Map is not loaded!");
	        }
		}
		
		//@TODO Add loading screen here
		if( Utility.updateAssetLoading() == false){
			return;
		}
		
		//update camera position
		if( !shakeCamera.getIsCameraShaking() ){
			//Gdx.app.debug(TAG, "Camera is NOT Shaking");
			camera.position.set(WorldEntities.getInstance().getPlayer().getCurrentPosition().x, WorldEntities.getInstance().getPlayer().getCurrentPosition().y, 0f);
		}else{
			shakeCamera.setOriginCameraCenter(WorldEntities.getInstance().getPlayer().getCurrentPosition().x, WorldEntities.getInstance().getPlayer().getCurrentPosition().y);
			Vector2 shakeCoords = shakeCamera.getShakeCameraCenter();
			camera.position.x = shakeCoords.x;
			camera.position.y = shakeCoords.y;
		}
		camera.update();
		
		if( renderer != null ) {
			renderer.getSpriteBatch().enableBlending();
			renderer.getSpriteBatch().setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//renderer.getSpriteBatch().setBlendFunction(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA); //MULTIPLY blend mode, darkens layer underneath
			//renderer.getSpriteBatch().setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); //NORMAL blend mode
			//renderer.getSpriteBatch().setBlendFunction(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR); //SCREEN blend mode; Lightens the layer underneath
			renderer.setView(camera);
			
			renderer.getSpriteBatch().begin();	
			
			drawMapLayer(currentMap.getMapBackgroundLayer() );
			
			drawMapLayer(currentMap.getMapGroundLayer() );

			drawEntities(delta, false);
			
			drawPlayer(delta);
			
			drawEntities(delta, true);
			
			drawEntityEffects(delta);
			
			drawMapLayer(currentMap.getMapCollisionLayer() );
			
			drawLevelParticleEffects(delta);
			
			drawMapLayer(currentMap.getMapWallCollisionLayer());

			drawMapLayer(currentMap.getMapForegroundLayer());
			
			drawLightMapLayer();	 
	
			drawAlarmCamera();
			
			renderer.getSpriteBatch().end();			
		}
		
		drawAlarmCameraCountdown();
		
		//drawRay(WorldEntities.getInstance().getPlayer().getSelectionRay());

		gameHUD.render(delta);
		
		actionMenu.render(delta);
		
		drawCursor();
		
		//drawDebug();
		
		MessageQueue.getInstance().processMessage();
	}
	
	@Override
	public void addMessageToTerminal(String message){
		gameHUD.addTerminalMessage(message);
	}
	
	private void drawAlarmCameraCountdown(){
		if( !alarmCamera.isAlarmActivated() ){
			return;
		}
		
        // set the projection matrix
        hudSpriteBatch.setProjectionMatrix(hudCamera.combined);
        
		hudSpriteBatch.begin();
		alarmCamera.drawCountDown(hudSpriteBatch);
		hudSpriteBatch.end();	
	}
	
	private void drawAlarmCamera(){
		if( !alarmCamera.isAlarmActivated() ){
			return;
		}
        	
		alarmCamera.drawAlarm(renderer.getSpriteBatch());		
	}
	
	private void setCurrentRawCursorPosition(){
		if( cursorTexture == null ) return;
		rawCursorPosition.set(Gdx.input.getX(), Gdx.input.getY()+cursorTexture.getHeight(), 0f);
	}
	
	private Vector3 getCurrentCursorHUDPosition(){
		setCurrentRawCursorPosition();
		hudCursorPosition = rawCursorPosition;
		hudCamera.unproject(hudCursorPosition);
		return hudCursorPosition;
	}
	
	private Vector3 getCurrentCursorWorldPosition(){
		setCurrentRawCursorPosition();
		worldCursorPosition = rawCursorPosition;
		camera.unproject(worldCursorPosition);
		return worldCursorPosition;
	}
	
	
	private void drawCursor(){
		if( cursorTexture != null ){
			Vector3 cursorPosition = getCurrentCursorHUDPosition();
			
			cursorSpriteBatch.begin();
			cursorSpriteBatch.draw(cursorTexture, cursorPosition.x, cursorPosition.y);
			cursorSpriteBatch.end();			
		}

	}
	
	@SuppressWarnings("unused")
	private void drawDebug(){
        // set the projection matrix
        hudSpriteBatch.setProjectionMatrix(hudCamera.combined);
  	
        TiledMap map = currentMap.getMap();
        if( map == null ) return;
        
		//Debug for FPS
		TiledMapTileLayer mapLayer =  (TiledMapTileLayer)map.getLayers().get(currentMap.getMapCollisionLayer());
		
		font.setColor(Color.GREEN.cpy());
		
		hudSpriteBatch.begin();
		
		//hudSpriteBatch.draw( gameHUD.getTexture(), 0, 0, VIEWPORT.viewportWidth, gameHUD.getTexture().getHeight() );

		font.draw(hudSpriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 
				0, VIEWPORT.viewportHeight-25);		
		
		font.draw(hudSpriteBatch, "Current Cell: (" + (int)(WorldEntities.getInstance().getPlayer().getCurrentPosition().x/mapLayer.getTileWidth()) + " , " +
				+ (int)(WorldEntities.getInstance().getPlayer().getCurrentPosition().y/mapLayer.getTileHeight()) + ")", 
				0, VIEWPORT.viewportHeight-40);
		
		
		font.draw(hudSpriteBatch, "Current Position: (" + (int)(WorldEntities.getInstance().getPlayer().getCurrentPosition().x) + " , " +
				+ (int)(WorldEntities.getInstance().getPlayer().getCurrentPosition().y) + ")", 
				0, VIEWPORT.viewportHeight-55);
		
		font.draw(hudSpriteBatch, "Direction:" + WorldEntities.getInstance().getPlayer().getCurrentDirection() + " State:" + WorldEntities.getInstance().getPlayer().getState().toString(), 
				0, VIEWPORT.viewportHeight-70);
		
		hudSpriteBatch.end();
		
	}
	
	private void drawLevelParticleEffects(float delta){
		//Draw Level Effects
		Collection<ParticleEffect> effects = WorldEntities.getInstance().getParticleEffectEntities();
		
		if( effects != null ){
			for( ParticleEffect effect: effects){
				effect.draw(renderer.getSpriteBatch(), delta);
			}
		}
	}
	
	private void drawEntityEffects(float delta){
		Collection<Entity> actors = WorldEntities.getInstance().getEntities(); 
		
		if( actors == null ){
			return;
		}
		
		//draw effects last
		for(Entity actor: actors){
			Sprite frameSprite = actor.getFrameSprite();
			
			if( frameSprite == null || frameSprite.getTexture() == null){
				Gdx.app.debug(TAG, "Entity Sprite is NULL");
				continue;
			}
			
			
			actor.drawStatusMessage(renderer.getSpriteBatch(), delta, Direction.UP);
			actor.drawDamageEffect(renderer.getSpriteBatch(), delta);				


			
			if( actor.isDrawDeathEffectEnabled() == true){
				actor.drawDeathEffect(renderer.getSpriteBatch(), delta);
			}
		}
		
		if( WorldEntities.getInstance().getPlayer().isAlive() ){
			
			//Status
			WorldEntities.getInstance().getPlayer().drawStatusMessage(
					renderer.getSpriteBatch(), 
					delta,
					WorldEntities.getInstance().getPlayer().getCurrentDirection().getOpposite());
		
			//Damage Effect
			WorldEntities.getInstance().getPlayer().drawDamageEffect(renderer.getSpriteBatch(), delta);
		}
		
		if(  WorldEntities.getInstance().getPlayer().isDrawDeathEffectEnabled() == true){
			 WorldEntities.getInstance().getPlayer().drawDeathEffect(renderer.getSpriteBatch(), delta);
		}
		
	}
	
	private void drawPlayer(float delta){
		Sprite frameSprite = WorldEntities.getInstance().getPlayer().getFrameSprite();
		
		if( frameSprite == null || frameSprite.getTexture() == null){
			Gdx.app.debug(TAG, "Player Sprite is NULL");
			return;
		}
		
		frameSprite.setPosition( WorldEntities.getInstance().getPlayer().getCurrentPosition().x,  WorldEntities.getInstance().getPlayer().getCurrentPosition().y);
		
		//Update selection ray
		Vector3 cursorPosition = getCurrentCursorWorldPosition();
		Ray ray = WorldEntities.getInstance().getPlayer().getSelectionRay();
		ray.set(WorldEntities.getInstance().getPlayer().getCurrentPosition().x+(WorldEntities.getInstance().getPlayer().WIDTH/2), 
				WorldEntities.getInstance().getPlayer().getCurrentPosition().y+(WorldEntities.getInstance().getPlayer().HEIGHT/2), 
				0.0f, cursorPosition.x, cursorPosition.y, cursorPosition.z);
		
		renderer.getSpriteBatch().draw(frameSprite,  frameSprite.getX(),  frameSprite.getY(),  frameSprite.getOriginX() , frameSprite.getOriginY(), 
				frameSprite.getWidth(), frameSprite.getHeight(), frameSprite.getScaleX(), frameSprite.getScaleY(), frameSprite.getRotation(), 
				true);
	}
	
	private void drawEntities(float delta, boolean drawCollidable){		
		Collection<Entity> actors = WorldEntities.getInstance().getEntities(); 
		
		if( actors == null ){
			return;
		}
		
		//draw entities first
		for(Entity actor: actors){
			//we want to always draw the entities. Up to the levelentities to dispose/not render.
			
			Sprite frameSprite = actor.getFrameSprite();
			
			if( frameSprite == null || frameSprite.getTexture() == null){
				Gdx.app.debug(TAG, "Entity Sprite is NULL");
				continue;
			}
			
			if( !actor.isVisible() ){
				continue;
			}
			
			if( actor.isCollidable() == drawCollidable ){
				frameSprite.setPosition(actor.getCurrentPosition().x, actor.getCurrentPosition().y);
				
				/*
				font.draw(renderer.getSpriteBatch(), "Direction:" + actor.getCurrentDirection() + "Previous Direction:" + actor.getPreviousDirection() +  
						" State:" + actor.getState().toString(), 
						actor.getCurrentPosition().x, actor.getCurrentPosition().y);
				
				font.draw(renderer.getSpriteBatch(), "Velocity: (" + actor.getVelocity().x + "," + actor.getVelocity().y + ")", 
						actor.getCurrentPosition().x - 15, actor.getCurrentPosition().y -15);
						*/
				
				renderer.getSpriteBatch().draw(frameSprite,  frameSprite.getX(),  frameSprite.getY(),  frameSprite.getOriginX() , frameSprite.getOriginY(), 
						frameSprite.getWidth(), frameSprite.getHeight(), frameSprite.getScaleX(), frameSprite.getScaleY(), frameSprite.getRotation(), 
						true);
				
				Sprite sprite = actor.updateSelectionState();
				if( sprite != null ){
					//Gdx.app.debug(TAG, "Drawing Sprite " + actor.getEntityID());
					renderer.getSpriteBatch().draw(sprite.getTexture(), sprite.getX(), sprite.getY());
				}
			}			
		}		
	}

	private void drawLightMapLayer(){
		Texture lightMap = currentMap.getMapLightmapTexture();
		
		if( lightMap != null) {
			renderer.getSpriteBatch().setBlendFunction(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA); //MULTIPLY blend mode, darkens layer underneath
			renderer.getSpriteBatch().draw(lightMap,0f,0f);
			renderer.getSpriteBatch().setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
	
	public boolean isPlayerCollisionWithEntities(){
		Collection<Entity> actors = WorldEntities.getInstance().getEntities(); 
		
		if( actors == null ){
			return false;
		}
		
		for(Entity actor: actors){
			if( actor.isCollidable() && WorldEntities.getInstance().getPlayer().getEntityBoundingBox().overlaps(actor.getEntityBoundingBox()) ){
				//Gdx.app.debug(TAG, "isPlayerCollisionWithEntities():: Player box is MIN " + entities.getPlayer().getEntityBoundingBox().x + "," + entities.getPlayer().getEntityBoundingBox().y
				//		+ " MAX " +  (entities.getPlayer().getEntityBoundingBox().x+ entities.getPlayer().getEntityBoundingBox().getWidth()) + "," + 
				//		(entities.getPlayer().getEntityBoundingBox().y + entities.getPlayer().getEntityBoundingBox().getHeight()));
				
				//Gdx.app.debug(TAG, "isPlayerCollisionWithEntities():: Entity box is MIN " + actor.getEntityBoundingBox().x + "," + actor.getEntityBoundingBox().y
				//		+ " MAX " +  (actor.getEntityBoundingBox().x+ actor.getEntityBoundingBox().getWidth()) + 
				//		"," +  (actor.getEntityBoundingBox().y+actor.getEntityBoundingBox().getHeight()));
				
				//Gdx.app.debug(TAG, "isPlayerCollisionWithEntities():: Collision with actor: " + actor.getCurrentPosition().x + "," + actor.getCurrentPosition().y);
				return true;
			}
		}
		
		return false;
	}
	
	
	public boolean isCollisionWithPlayer(Entity entity){
        return isCollisionWithPlayer(entity.getEntityBoundingBox());
	}
	
	public boolean isCollisionWithMap(Entity entity){
		entity.setBoundingBoxSize(.3f);
		return isCollisionWithMap(entity.getEntityBoundingBox());
	}
	
	public boolean isCollisionWithPlayer(Rectangle boundingBox){
        
		if( WorldEntities.getInstance().getPlayer().getEntityBoundingBox().overlaps(boundingBox) ){
			//Gdx.app.debug(TAG, "isCollisionWithPlayer::Player box is MIN " + entities.getPlayer().getEntityBoundingBox().x + "," + entities.getPlayer().getEntityBoundingBox().y
			//		+ " MAX " +  (entities.getPlayer().getEntityBoundingBox().x + entities.getPlayer().getEntityBoundingBox().getWidth()) + 
			//		"," + (entities.getPlayer().getEntityBoundingBox().y + entities.getPlayer().getEntityBoundingBox().getHeight()));
			
			//Gdx.app.debug(TAG, "isCollisionWithPlayer::Entity box is MIN " + boundingBox.x + "," + boundingBox.y
			//		+ " MAX " +  (boundingBox.x + boundingBox.width) + "," 
			//		+  (boundingBox.y + boundingBox.height));
			
			//Gdx.app.debug(TAG, "isCollisionWithPlayer::Collision with actor: " + boundingBox.x + "," + boundingBox.y);
			return true;
		}
		
		return false;
	}

	public boolean isSpawnCollisionWithEntity(Rectangle boundingBox){
		Collection<Entity> actors = WorldEntities.getInstance().getEntities(); 
		
		if( actors == null ){
			return false;
		}
		
		for(Entity actor: actors){
			//Need to filter out the entity with itself first
			if( !actor.isAlive()){
				Gdx.app.debug(TAG, "isSpawnCollisionWithEntity: actor is dead");
				continue;
			}
			
			
			if( actor.isCollidable() && boundingBox.overlaps(actor.getEntityBoundingBox()) ){
				//Gdx.app.debug(TAG, "isCollisionWithEntity::Player box is MIN " + boundingBox.x + "," + boundingBox.y
				//		+ " MAX " +  (boundingBox.x+ boundingBox.width) + 
				//		"," + (boundingBox.y + boundingBox.height));
				
				//Gdx.app.debug(TAG, "isCollisionWithEntity::Entity box is MIN " + actor.getEntityBoundingBox().x + "," + actor.getEntityBoundingBox().y
				//		+ " MAX " +  (actor.getEntityBoundingBox().x+ actor.getEntityBoundingBox().getWidth()) + 
				//		"," +  (actor.getEntityBoundingBox().y+ actor.getEntityBoundingBox().getHeight()));
				
				//Gdx.app.debug(TAG, "isCollisionWithEntity::Collision with actor: " + actor.getCurrentPosition().x + "," + actor.getCurrentPosition().y);
				return true;
			}
		}
	
		return false;
	}
	
	public boolean isCollisionWithEntity(Entity entity){
		Collection<Entity> actors = WorldEntities.getInstance().getEntities(); 
		
		if( actors == null ){
			return false;
		}
		
		for(Entity actor: actors){
			//Need to filter out the entity with itself first
			if( entity.getEntityID().equalsIgnoreCase(actor.getEntityID()) ||  !actor.isAlive()){
				//Gdx.app.debug(TAG, "isCollisionWithEntity:: Entity found itself");
				continue;
			}
			
			
			if( actor.isCollidable() && entity.getEntityBoundingBox().overlaps(actor.getEntityBoundingBox()) ){
				//Gdx.app.debug(TAG, "isCollisionWithEntity::Player box is MIN " + boundingBox.x + "," + boundingBox.y
				//		+ " MAX " +  (boundingBox.x+ boundingBox.width) + 
				//		"," + (boundingBox.y + boundingBox.height));
				
				//Gdx.app.debug(TAG, "isCollisionWithEntity::Entity box is MIN " + actor.getEntityBoundingBox().x + "," + actor.getEntityBoundingBox().y
				//		+ " MAX " +  (actor.getEntityBoundingBox().x+ actor.getEntityBoundingBox().getWidth()) + 
				//		"," +  (actor.getEntityBoundingBox().y+ actor.getEntityBoundingBox().getHeight()));
				
				//Gdx.app.debug(TAG, "isCollisionWithEntity::Collision with actor: " + actor.getCurrentPosition().x + "," + actor.getCurrentPosition().y);
				return true;
			}
		}
	
		return false;
	}
	
	public boolean isCollisionWithMap(Rectangle boundingBox){
		TiledMap map = currentMap.getMap();
		if( map == null ) return false;
		
		TiledMapTileLayer mapLayer =  (TiledMapTileLayer)map.getLayers().get(currentMap.getMapCollisionLayer());
		
		String mapCollisionObjectLayer = currentMap.getMapCollisionObjectLayer();
		
		if( mapCollisionObjectLayer != null ){
			MapLayer collisionLayer =  (MapLayer)map.getLayers().get(mapCollisionObjectLayer);
			boolean isCollision = isPixelCollisionWithMapLayer(boundingBox, mapLayer, collisionLayer);
			
			if( isCollision ){
				return true;
			}		
		}
		
		String mapCollisionLayer = currentMap.getMapWallCollisionLayer();
		
		if( mapCollisionLayer != null ){
			TiledMapTileLayer mapWallLayer =  (TiledMapTileLayer)map.getLayers().get(mapCollisionLayer);
			boolean isWallCollision = isCollisionWithMapLayer(boundingBox, mapWallLayer);
			
			if( isWallCollision ){
				return true;
			}		
		}

		
		return false;
	}
	
	private boolean isCollisionWithMapLayer(Rectangle boundingBox, TiledMapTileLayer mapLayer){
		if( mapLayer == null ){
			return false;
		}
		
		Cell cell;

		
		//Need to cover 8 cases to make sure we get all checks. The 4 corners, as well as midpoints of the sides.		
		float mapTileWidth = mapLayer.getTileWidth();
		float mapTileHeight = mapLayer.getTileHeight();
		
		//Bottom Left corner
		cell = mapLayer.getCell((int)(boundingBox.x/mapTileWidth), (int)(boundingBox.y/mapTileHeight));

		if( cell != null ){
			//Gdx.app.debug(TAG, "Collision BOTTOM LEFT CORNER: (" + boundingBox.x + "," + boundingBox.y + ")");
			return true;
		}
		
		//Top Right corner
		cell = mapLayer.getCell((int)((boundingBox.x + boundingBox.width)/mapTileWidth), 
				(int)((boundingBox.y+ boundingBox.height)/mapTileHeight));
			
		if(cell != null){
			//Gdx.app.debug(TAG, "Collision TOP RIGHT CORNER: (" + (boundingBox.x + boundingBox.width) + "," + (boundingBox.y+ boundingBox.height) + ")");
			return true;
		}
	
		//Bottom Right corner
		cell = mapLayer.getCell((int)((boundingBox.x + boundingBox.width)/mapTileWidth), 
				(int)((boundingBox.y)/mapTileHeight));
			
		if(cell != null){
			//Gdx.app.debug(TAG, "Collision BOTTOM RIGHT CORNER: (" + (boundingBox.x + boundingBox.width) + "," + boundingBox.y + ")");
			return true;
		}
		
		//Top Left corner
		cell = mapLayer.getCell((int)((boundingBox.x)/mapTileWidth), 
				(int)((boundingBox.y+ boundingBox.height)/mapTileHeight));
			
		if(cell != null){
			//Gdx.app.debug(TAG, "Collision TOP LEFT CORNER: (" + boundingBox.x + "," + (boundingBox.y+ boundingBox.height) + ")");
			return true;
		}
		return false;
	}
	
	private boolean isPixelCollisionWithMapLayer(Rectangle boundingBox, TiledMapTileLayer mapLayer, MapLayer collisionLayer){
		if( mapLayer == null ){
			return false;
		}
		
		Cell cell;
		
		//Need to cover 8 cases to make sure we get all checks. The 4 corners, as well as midpoints of the sides.
		
		float mapTileWidth = mapLayer.getTileWidth();
		float mapTileHeight = mapLayer.getTileHeight();
		
		//Bottom Left corner
		cell = mapLayer.getCell((int)(boundingBox.x/mapTileWidth), (int)(boundingBox.y/mapTileHeight));

		if( cell != null ){
			//Gdx.app.debug(TAG, "Collision BOTTOM LEFT CORNER: (" + boundingBox.x + "," + boundingBox.y + ")");

			Rectangle rectangle = null;
			
			for( MapObject object: collisionLayer.getObjects()){

				if(object instanceof RectangleMapObject) {
					rectangle = ((RectangleMapObject)object).getRectangle();
					if( boundingBox.overlaps(rectangle) ){
						return true;
					}
				}
			}
		}
		
		//Top Right corner
		cell = mapLayer.getCell((int)((boundingBox.x + boundingBox.width)/mapTileWidth), 
				(int)((boundingBox.y+ boundingBox.height)/mapTileHeight));
			
		if(cell != null){
			//Gdx.app.debug(TAG, "Collision TOP RIGHT CORNER: (" + (boundingBox.x + boundingBox.width) + "," + (boundingBox.y+ boundingBox.height) + ")");
			Rectangle rectangle = null;
			
			for( MapObject object: collisionLayer.getObjects()){

				if(object instanceof RectangleMapObject) {
					rectangle = ((RectangleMapObject)object).getRectangle();
					if( boundingBox.overlaps(rectangle) ){
						return true;
					}
				}
			}
		}
	
		//Bottom Right corner
		cell = mapLayer.getCell((int)((boundingBox.x + boundingBox.width)/mapTileWidth), 
				(int)((boundingBox.y)/mapTileHeight));
			
		if(cell != null){
			//Gdx.app.debug(TAG, "Collision BOTTOM RIGHT CORNER: (" + (boundingBox.x + boundingBox.width) + "," + boundingBox.y + ")");
			Rectangle rectangle = null;
			
			for( MapObject object: collisionLayer.getObjects()){

				if(object instanceof RectangleMapObject) {
					rectangle = ((RectangleMapObject)object).getRectangle();
					if( boundingBox.overlaps(rectangle) ){
						return true;
					}
				}
			}
		}

		//Top Left corner
		cell = mapLayer.getCell((int)((boundingBox.x)/mapTileWidth), 
				(int)((boundingBox.y+ boundingBox.height)/mapTileHeight));
			
		if(cell != null){
			//Gdx.app.debug(TAG, "Collision TOP LEFT CORNER: (" + boundingBox.x + "," + (boundingBox.y+ boundingBox.height) + ")");
			Rectangle rectangle = null;
			
			for( MapObject object: collisionLayer.getObjects()){

				if(object instanceof RectangleMapObject) {
					rectangle = ((RectangleMapObject)object).getRectangle();
					if( boundingBox.overlaps(rectangle) ){
						return true;
					}
				}
			}
		}

		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean isPixelCollisionDetected(TiledMapTile tile, float sourceX, float sourceY, Rectangle boundingBoxTarget){
		//Get the pixmap data from GPU
		if( !tile.getTextureRegion().getTexture().getTextureData().isPrepared() ){
			tile.getTextureRegion().getTexture().getTextureData().prepare();		
		}
		
		//Gdx.app.debug(TAG, "Cell Tile ID is: " +  tile.getId());
		
		Pixmap srcPixmap = tile.getTextureRegion().getTexture().getTextureData().consumePixmap();
		
		Gdx.app.debug(TAG, "Pixmap Region XY " + tile.getTextureRegion().getRegionX() + "," + tile.getTextureRegion().getRegionY() );
		Gdx.app.debug(TAG, "Pixmap Region WH " + tile.getTextureRegion().getRegionWidth() + "," + tile.getTextureRegion().getRegionHeight() );
		//Gdx.app.debug(TAG, "Pixmap size: width:" + srcPixmap.getWidth() + " height:" + srcPixmap.getHeight() );
		
		Vector2 topLeftCorner = new Vector2( boundingBoxTarget.getX(),boundingBoxTarget.getY()+boundingBoxTarget.getHeight());
		Vector2 bottomRightCorner = new Vector2(boundingBoxTarget.getX()+boundingBoxTarget.getWidth(), boundingBoxTarget.getY());
		
		Gdx.app.debug(TAG, "BoundingBox " + boundingBoxTarget.x + "," +boundingBoxTarget.y + " width: " + boundingBoxTarget.getWidth() + " height: "+ boundingBoxTarget.getHeight());
		
		//Gdx.app.debug(TAG, "BoundingBox " + boundBoxSource.x + "," +boundBoxSource.y);
		//Gdx.app.debug(TAG, "TopLeftCorner " + topLeftCorner.x  + "," +topLeftCorner.y);
		//Gdx.app.debug(TAG, "BottomRightCorner " + bottomRightCorner.x  + "," +bottomRightCorner.y);
		//Gdx.app.debug(TAG, "Top X " + ( (boundBoxSource.x - topLeftCorner.x) * boundingBoxTarget.getWidth()));
		//Gdx.app.debug(TAG, "Bottom X " + (bottomRightCorner.x - topLeftCorner.x));
		
		float x = ((sourceX - topLeftCorner.x) * boundingBoxTarget.getWidth()) / (bottomRightCorner.x - topLeftCorner.x);
		float y = ((sourceY - topLeftCorner.y) * boundingBoxTarget.getHeight()) / (bottomRightCorner.y - topLeftCorner.y);
		
		Gdx.app.debug(TAG, "Mapping Pixel Location: x" + (int)x + "," + (int)y);
		
		
		
		int pixel = srcPixmap.getPixel((int)x,(int)y);
		int R = ((pixel & 0xff000000));
		int G = ((pixel & 0x00ff0000));
		int B = ((pixel & 0x0000ff00));
		int A = ((pixel & 0x000000ff));
		
		//upload pixmap back
		Texture texture = new Texture( srcPixmap );
		tile.getTextureRegion().getTexture().getTextureData().disposePixmap();
		tile.getTextureRegion().setTexture(texture); 
		
		
		//If transparent, no collision
		if( A == 0 ){
			Gdx.app.debug(TAG, "Pixel at : " + sourceX + "," + sourceY + " is ALPHA" + " is R" + R + " G" + G + " B" +B + " A"+A  );
			return false;
		}else{
			Gdx.app.debug(TAG, "Pixel at : " + sourceX + "," + sourceY + " is NOT ALPHA!!!!" + " is R" + R + " G" + G + " B" +B + " A"+A  );
			return true;
		}
	}
	
	public void selectEntity(Vector3 mouseCoordinates){
		//Gdx.app.debug(TAG, " Mouse coordinates coming in: (" + mouseCoordinates.x + "," + mouseCoordinates.y + ")");
		//Convert screen coordinates to world coordinates
		camera.unproject(mouseCoordinates);
		Collection<Entity> actors = WorldEntities.getInstance().getEntities(); 
		
		if( actors == null ){
			return;
		}
		
		Entity selectedEntity = gameHUD.getCurrentSelectedEntity();
		if( selectedEntity != null ){
			selectedEntity.setIsSelected(false);
		}
		gameHUD.setCurrentSelectedEntity(null);
		
		for(Entity actor: actors){
			if( actor.getEntityBoundingBox().contains(mouseCoordinates.x,mouseCoordinates.y) ){
				if( actor.isAlive() && WorldEntities.getInstance().getPlayer().isSelectionRayDistanceWithinThreshold()){
					actor.setIsSelected(true);
					gameHUD.setCurrentSelectedEntity(actor);
					WorldEntities.getInstance().getPlayer().playSelectionSound();
					//Gdx.app.debug(TAG, "SELECTED!!!");
				}
				break;
			}
		}
	}
	
	public void actionOnEntity(Vector3 mouseCoordinates){
		//Gdx.app.debug(TAG, " Mouse coordinates coming in: (" + mouseCoordinates.x + "," + mouseCoordinates.y + ")");
		//Convert screen coordinates to world coordinates
		Collection<Entity> actors = WorldEntities.getInstance().getEntities(); 
		
		if( actors == null ){
			return;
		}
		
		
		//get selected entity
		for(Entity actor: actors){
			if( actor.isSelected() && actor.isActionMenuEnabled() ){
				//Bring up action menu for this object
				Vector3 cursorPosition = getCurrentCursorHUDPosition();
				actionMenu.setPosition(cursorPosition.x, cursorPosition.y);
				actionMenu.updateActionMenu(actor);
				actionMenu.show();
				break;
			}
		}
	
	}
	
	private void drawMapLayer(String layerName){
		try{
		    TiledMap map = currentMap.getMap();    
		    if( map == null ) return;
		    
			//First, try loading as tiledLayer
			TiledMapTileLayer tiledMapLayer = (TiledMapTileLayer)map.getLayers().get(layerName);
			if( tiledMapLayer != null){
				renderer.renderTileLayer(tiledMapLayer);
				renderer.setView(camera);
				return;
			}else{
				Gdx.app.debug(TAG, "Could not load map layer " +  layerName + " from current map location " + currentMap.getMapName().toString());
			}
		}catch(NullPointerException np){
			//We simply don't draw the layer if it doesn't exist
			Gdx.app.debug(TAG, "Could not load map layer " +  layerName + " from current map location " + currentMap.getMapName().toString());
		}finally{

		}
	}
	
	@SuppressWarnings("unused")
	private void drawMapObjects(String layerName){
		try{
	        TiledMap map = currentMap.getMap();
	        if( map == null ) return;
	        
			MapLayer mapLayer = map.getLayers().get(layerName);
			if( mapLayer != null){
				MapObjects mapObjects = mapLayer.getObjects();
				for( MapObject object : mapObjects){
					//This method is not currently implemented
					//renderer.renderObject(object);
					renderObject(object);
				}
				return;
			}else{
				Gdx.app.debug(TAG, "Could not load map objects " +  layerName + " from current map location " + currentMap.getMapName().toString());
			}
		}catch(NullPointerException np){
			//We simply don't draw the layer if it doesn't exist
			Gdx.app.debug(TAG, "Could not load map objects " +  layerName + " from current map location " + currentMap.getMapName().toString());
		}finally{

		}
	}
	
	public void drawRay(Ray ray){
		ShapeType shapeType = ShapeType.Line;
		Color objColor = Color.RED;
		
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.setColor(objColor.r, objColor.g, objColor.b, 1.0f);
		
		//draw calls
		shapeRenderer.begin(shapeType);
		shapeRenderer.line(ray.origin, ray.direction);
		shapeRenderer.end();
		
	    // set the projection matrix
		//debug
		
        hudSpriteBatch.setProjectionMatrix(hudCamera.combined);
		font.setColor(Color.GREEN.cpy());
		
		hudSpriteBatch.begin();
		font.draw(hudSpriteBatch, "Distance: " + ray.origin.dst(ray.direction), 
				0, VIEWPORT.viewportHeight-25);		
		hudSpriteBatch.end();
		
	}
	
	public void renderObject (MapObject object) {
		Color objColor = object.getColor();
		float objAlpha = object.getOpacity();
		boolean isVisible = object.isVisible();
		ShapeType shapeType = ShapeType.Filled;
		String shapeTypeProperty;
		
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		shapeRenderer.setColor(objColor.r, objColor.g, objColor.b, objAlpha);
	
		if( false == isVisible){
			return;
		}
		
		//Check properties for the shape type: i.e,  points, lines, or filled. Default is filled.
		if( object.getProperties().containsKey("ShapeType") ){
			shapeTypeProperty = (String)object.getProperties().get("ShapeType");
			
			if( shapeTypeProperty.equalsIgnoreCase("Point")){
				shapeType = ShapeType.Point;
			}else if(shapeTypeProperty.equalsIgnoreCase("Line")){
				shapeType = ShapeType.Line;
			}else if(shapeTypeProperty.equalsIgnoreCase("Filled")){
				shapeType = ShapeType.Filled;
			}else{
				shapeType = ShapeType.Filled;
			}
		}
			
		shapeRenderer.begin(shapeType);
		
		if(object instanceof CircleMapObject) {
			Circle circle = ((CircleMapObject)object).getCircle();
			shapeRenderer.circle(circle.x, circle.y, circle.radius);
		}else if(object instanceof EllipseMapObject) {
			Ellipse ellipse = ((EllipseMapObject)object).getEllipse();
			shapeRenderer.ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
		}else if(object instanceof PolygonMapObject) {
			Polygon polygon = ((PolygonMapObject)object).getPolygon();
			shapeRenderer.polygon(polygon.getVertices());
		}else if(object instanceof PolylineMapObject) {
			Polyline polyline = ((PolylineMapObject)object).getPolyline();
			shapeRenderer.polyline(polyline.getVertices());
		}else if(object instanceof RectangleMapObject) {
			Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
			shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		}else if(object instanceof TextureMapObject) {
			TextureMapObject texture = (TextureMapObject)object;
			renderer.getSpriteBatch().draw(texture.getTextureRegion(), texture.getX(), texture.getY(), texture.getOriginX(), 
					texture.getOriginY(), texture.getTextureRegion().getRegionWidth(), texture.getTextureRegion().getRegionHeight(), 
					texture.getScaleX(), texture.getScaleY(), texture.getRotation());
		}
		else {
			throw new IllegalArgumentException("This shape is not supported");
		}

		shapeRenderer.end();
	}

	private void setupViewport(int width, int height){
	   	//Make the viewport a percentage of the total display area
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;
    	
    	//Current viewport dimensions
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
    	
    	//pixel dimensions of display
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();
    	
    	//aspect ratio for current viewport
    	float aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);
    	
    	//update viewport if there could be skewing
    	if( VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= aspectRatio){
    		//Letterbox left and right
    		VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
    		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
    	}else{
    		//letterbox above and below
    		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
    		VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
    	}	
    	
    	Gdx.app.debug(TAG, "WorldRenderer: virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")" );
    	Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")" );
    	Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")" );
	}

	@Override
	public void changeMapEvent(String mapName) {
		setCurrentMapLocation(MapLocationEnum.valueOf(mapName));
	}
	
	@Override
	public void startShakingCamera(){
		shakeCamera.reset();
		shakeCamera.startShaking();
	}
	
	@Override
	public void alertTriggered(){
		alarmCamera.startAlarm();
	}
	
	@Override
	public void alertStopped(){
		alarmCamera.stopAlarm();
	}
	
	public boolean isAlarmActivated(){
		return alarmCamera.isAlarmTriggeredandCountdownFinished();
	}
	
	@Override
	public void updateDamagePlayerHealthUI(int damage){
		gameHUD.setHealthBarDamage(damage);
	}
	
	@Override
	public void updateHealPlayerHealthUI(int heal){
		gameHUD.setHealthBarHeal(heal);
	}
	
	@Override
	public void updatePlayerMaxHealth(int maxHealth){
		gameHUD.setMaxHealthBar(maxHealth);
	}

	@Override
	public void updateIncreaseCyberspaceAlertUI(int increase){
		gameHUD.setAlertIncrease(increase);
	}
	
	@Override
	public void updateDecreaseCyberspaceAlertUI(int decrease){
		gameHUD.setAlertDecrease(decrease);
	}
	
	
}
