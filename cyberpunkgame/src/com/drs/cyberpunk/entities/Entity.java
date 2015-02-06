package com.drs.cyberpunk.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.collision.Ray;
import com.drs.cyberpunk.Assets;
import com.drs.cyberpunk.Script;
import com.drs.cyberpunk.ScriptManager;
import com.drs.cyberpunk.SoundEffect;
import com.drs.cyberpunk.Utility;
import com.drs.cyberpunk.WorldRenderer;
import com.drs.cyberpunk.enums.ActionsEnum;
import com.drs.cyberpunk.items.InventoryItem;
import com.drs.cyberpunk.listeners.WorldRenderListener;


public class Entity implements Poolable,IEntities,IEntityInteraction,IHackable {
	
	private static final String TAG = Entity.class.getSimpleName();
	
	private Vector2 velocity;
	
	private SECURITYLEVEL level = SECURITYLEVEL.NONE;

	
	public float WIDTH = 64;
	public float HEIGHT = 64;
	
	private int walkingAnimRows = 0;
	private int walkingAnimCols = 0;
	private int walkingAnimFrames = 0;
	private int walkingAnimStartRowIndex = 0;
	private int walkingAnimStartColIndex = 0;
	private Texture walkingCycle = null;
	
	private String entityID;
	
	private int idleKeyFrame = 0;
	
	private boolean isAlive;
	protected boolean existsOffScreen = false;
	protected boolean isHackable = false;
	protected boolean isActionMenuEnabled = false;
	protected ArrayList<ActionsEnum> actionItems;
	private boolean drawDeathEffect = false;
	
	protected HashMap<String, InventoryItem> inventoryItems;
	
	protected Rectangle boundingBox;
	private Ray selectionRay;
	protected float selectionRayDistanceThreshold = 100;
	
	protected EntityStatusQueue entityMessageQueue;
	
	private boolean needsInit = true;
	
	private boolean isSelected = false;
	private boolean updatedSelectionState = false;
	private Sprite selectionSprite = null;
	
	private boolean isStatic = false;
	
	private boolean isCollidable = true;
	private boolean isVisible = true;
	
	protected int maxHealth = 10;
	private int currentHealth = 0;
	
	protected ParticleEffect defaultDamageEffect;
	protected boolean isEntityDamaged = false;
	protected ParticleEffect defaultDeathEffect;

	protected State state = State.IDLE;
	protected float frameTime = 0f;	
	protected Animation walkAnimation;
	protected String imagePath;
	private String loadedWalkAnimationImagePath;
	
	protected Sprite frameSprite;
	private Direction currentDirection = Direction.LEFT;
	private Direction previousDirection = Direction.UP;

	protected float rotationDegrees = 0;
	protected Vector2 nextPlayerPosition;
	protected Vector2 currentPlayerPosition;
	
	protected SoundEffect entityDamagedSound = null;
	private SoundEffect analysis;
	
	//script
	private Script entityScript = null;
	
	//listeners
	WorldRenderListener worldRender = null;
	
	public enum State {
		IDLE, WALKING, DYING, ANIMATED, ANIMATE_ONCE,ANIMATE_ONCE_REVERSE, PAUSE
	}
	
	public enum Direction {
		UP,RIGHT,DOWN,LEFT;
		
		public Direction getNext() {
			//Gdx.app.debug(TAG, "Current Direction: " + Direction.values()[(ordinal()) % Direction.values().length] );
			//Gdx.app.debug(TAG, "Current Direction: " + ordinal() );
			//Gdx.app.debug(TAG, "Next Direction: " + Direction.values()[(ordinal()+1) % Direction.values().length] );
			return Direction.values()[(ordinal()+1) % Direction.values().length];
		}
		
		public Direction getRandomNext() {
			return Direction.values()[MathUtils.random(Direction.values().length-1)];
		}
		
		public Direction getOpposite() {
			if( this == LEFT){
				return RIGHT;
			}else if( this == RIGHT){
				return LEFT;
			}else if( this == UP){
				return DOWN;
			}else{
				return UP;
			}
		}
		
	}
	
	public Entity(){
		initEntity();
	}
	
	public Entity(String entityType){
		initEntity();
		entityScript = ScriptManager.getInstance().scriptFactory(entityType);
		if( entityScript != null ){
			entityScript.create(this);
		}
	}
	
	public void initEntity(){
		//Gdx.app.debug(TAG, "Construction" );
		
		this.entityID = UUID.randomUUID().toString();
		
		this.entityMessageQueue = new EntityStatusQueue();
		
		this.frameSprite = new Sprite();		
		this.nextPlayerPosition = new Vector2();
		this.currentPlayerPosition = new Vector2();
		this.boundingBox = new Rectangle();
		this.velocity = new Vector2(100f,100f);
		
		this.defaultDamageEffect = new ParticleEffect();
		defaultDamageEffect.load(Gdx.files.internal(Assets.defaultDamageParticleEffect), Gdx.files.internal(Assets.EFFECTS_DIR));
		
		this.defaultDeathEffect = new ParticleEffect();
		defaultDeathEffect.load(Gdx.files.internal(Assets.defaultDeathParticleEffect), Gdx.files.internal(Assets.EFFECTS_DIR));
		
		inventoryItems = new HashMap<String,InventoryItem>();		

		selectionRay = new Ray(new Vector3(), new Vector3());
		
		entityDamagedSound = new SoundEffect(_entity_damaged);
		
		setWorldRenderListener(WorldRenderer.getInstance());
		
		setIsAlive(true);
		
		actionItems = new ArrayList<ActionsEnum>();
		
		analysis = new SoundEffect(_analysis);
	}
	
	@Override
	public boolean isVisible(){
		return isVisible;
	}
	
	@Override
	public void setIsVisible(boolean isVisible){
		  this.isVisible = isVisible;
	}
	
	@Override
	public void reset() {
		frameTime = 0f;
		state = State.IDLE;
		rotationDegrees = 0;
		setIsAlive(false);
		needsInit = true;
		entityID = UUID.randomUUID().toString();
		WIDTH = 64;
		HEIGHT = 64;
	}
	
	@Override
	public String getEntityID(){
		return entityID;
	}
	
	@Override
	public boolean isEntityDamaged(){
		return isEntityDamaged;
	}
	
	@Override
	public void setIsEntityDamaged(boolean isDamaged){
		this.isEntityDamaged = isDamaged;
	}
	
	@Override
	public boolean needsInit(){
		return needsInit;
	}
	
	@Override
	public void setInit(boolean needsInit){
		this.needsInit = needsInit;
	}
	
	@Override
	public boolean isSelected(){
		return isSelected;
	}
	
	@Override
	public void setIsSelected(boolean isSelected){
		this.isSelected = isSelected;
	}
	
	@Override
	public Vector2 getVelocity(){
		return new Vector2(velocity);
	}
	@Override
	public void setVelocity(Vector2 velocity){
		this.velocity = velocity;
	}
	
	@Override
	public boolean isHackable(){
		return isHackable;
	}
	
	@Override
	public void setIsHackable(boolean isHackable){
		this.isHackable = isHackable;
	}
	
	@Override
	public void updateDamage(int damageAmount){
		entityDamagedSound.play();

		currentHealth -= damageAmount;
		
		if( currentHealth <= 0){
			setIsAlive(false);
			currentHealth = 0;
		}
	}
	
	@Override
	public void setCurrentHealth(int health){
		this.currentHealth = health;
	}
	
	@Override
	public boolean isActionMenuEnabled(){
		return isActionMenuEnabled;
	}
	
	@Override
	public void setIsActionMenuEnabled(boolean isEnabled){
		this.isActionMenuEnabled = isEnabled;
	}
	
	@Override
	public void updateIncreaseAlert(int increase){
		if( worldRender == null ) return;
		worldRender.updateIncreaseCyberspaceAlertUI(increase);
	}
	
	@Override
	public void updateDecreaseAlert(int decrease){
		if( worldRender == null ) return;
		worldRender.updateDecreaseCyberspaceAlertUI(decrease);
	}
	
	
	@Override
	public ArrayList<ActionsEnum> getActionItems(){
		return actionItems;
	}
	
	@Override
	public void addActionItemToEnd(String actionStr){
		ActionsEnum action = ActionsEnum.valueOf(actionStr);
		if( action == null) return;
		actionItems.add(action);
	}
	
	@Override
	public void addActionItemToBeginning(String actionStr){
		ActionsEnum action = ActionsEnum.valueOf(actionStr);
		if( action == null) return;
		actionItems.add(0,action);
	}
	
	@Override
	public void runActionItem(String actionStr){
		entityScript.runActionItem(this, ActionsEnum.valueOf(actionStr));
		return;
	}
	
	@Override
	public void receivedInventoryItemAction(Entity sender, ACTION action, InventoryItem item){
		return;
	}
	
	@Override
	public void setWorldRenderListener(WorldRenderListener worldRender){
		this.worldRender = worldRender;
	}
	
	@Override
	public WorldRenderListener getWorldRenderListener(){
		return worldRender;
	}
	
	@Override
	public void worldRenderMapChange(String mapName){
		if( worldRender == null ) return;
		worldRender.changeMapEvent(mapName);
	}
	
	@Override
	public int getMaxHealth(){
		return this.maxHealth;
	}
	
	@Override
	public void update(float delta){
		frameTime += delta;
		
		loadTextures();
		
		TextureRegion currentFrame = getCurrentFrame(delta);
		
		playSoundBasedOnFrameIndex(delta);
		
		if( currentFrame == null || frameSprite == null){
			Gdx.app.debug(TAG, "Sprite/currentFrame is null for nextPosition" );
			return;
		}
		
		//Gdx.app.debug(TAG, "Current Region Width: " + currentFrame.getRegionWidth() + " and height: " + currentFrame.getRegionHeight()  );

		frameSprite.setRegion(currentFrame);
		
		//Gdx.app.debug(TAG, "FrameSprite Region Width: " + frameSprite.getRegionWidth() + " and height: " + frameSprite.getRegionHeight()  );
		
		//Sounds go here

		entityDamagedSound.update(delta);		
		analysis.update(delta);
	}
	
	public void playAnalysisSound(){
		analysis.play();
	}
	
	@Override
	public Ray getSelectionRay(){
		return selectionRay;
	}
	
	@Override
	public boolean isSelectionRayDistanceWithinThreshold(){
		float distance = selectionRay.origin.dst(selectionRay.direction);
		if( distance <= selectionRayDistanceThreshold){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public boolean isRayDistanceWithinThreshold(Rectangle target){
		selectionRay.set(boundingBox.x, boundingBox.y, 0.0f, target.x, target.y, 0.0f);
		
		float distance = selectionRay.origin.dst(selectionRay.direction);
		if( distance <= selectionRayDistanceThreshold){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public float getSelectionThreshold(){
		return selectionRayDistanceThreshold;
	}
	
	@Override
	public void playSoundBasedOnFrameIndex(float delta){
	}
	
	@Override 
	public void loadInventoryItems(){
	}
	
	@Override
	public Collection<InventoryItem> getInventoryItems(){
		return inventoryItems.values();
	}

	@Override
	public void drawDamageEffect(SpriteBatch batch, float delta){
		if( !isAlive() ){
			return;
		}
		defaultDamageEffect.setPosition((currentPlayerPosition.x+(WIDTH/2)), (currentPlayerPosition.y+(HEIGHT/2)));
		defaultDamageEffect.draw(batch, delta);
	}
	
	@Override
	public void drawDeathEffect(SpriteBatch batch, float delta){
		defaultDeathEffect.setPosition((currentPlayerPosition.x+(WIDTH/2)), (currentPlayerPosition.y+(HEIGHT/2)));
		defaultDeathEffect.draw(batch, delta);	
		if( defaultDeathEffect.isComplete() ){
			drawDeathEffect = false;
		}

	}
	
	@Override
	public void drawStatusMessage(SpriteBatch batch, float delta, Direction direction){
		entityMessageQueue.drawStatusMessage(batch, this, direction);
	}
	
	@Override
	public boolean isReadyToBeRemoved(){
		return isAlive == false &&
				entityMessageQueue.isProcessingMessage() == false &&
				drawDeathEffect == false;
	}
	
	@Override
	public InventoryItem getInventoryItem(String itemID){
		return inventoryItems.get(itemID);
	}
	
	@Override
	public boolean isDrawDeathEffectEnabled(){
		return drawDeathEffect;
	}
	
	@Override
	public void drawDeathEffectEnabled(boolean isEnabled){
		this.drawDeathEffect = isEnabled;
	}
	
	public TextureRegion getCurrentFrame(float delta){
		
		if( walkAnimation == null ){
			Gdx.app.debug(TAG, "Current animation is NULL..." );
			return null;
		}
		
		TextureRegion currentFrame = null;
		
		if( state == State.WALKING ){
			currentFrame = walkAnimation.getKeyFrame(frameTime, false);
		}else if( state == State.ANIMATED){
			if( (walkAnimation.getPlayMode() == Animation.NORMAL ||
					walkAnimation.getPlayMode() == Animation.REVERSED) &&
					walkAnimation.isAnimationFinished(frameTime))
			{
				//If we are playing once (normal or reversed) and we are done, set to idle
				state = State.IDLE;
				walkAnimation.setPlayMode(Animation.NORMAL);
				currentFrame = walkAnimation.getKeyFrame(idleKeyFrame, false);
			}else{
				currentFrame = walkAnimation.getKeyFrame(frameTime, false);
			}
		}else if( state == State.ANIMATE_ONCE){
			walkAnimation.setPlayMode(Animation.NORMAL);
			frameTime = 0f;
			state = State.ANIMATED;
			currentFrame = walkAnimation.getKeyFrame(frameTime, false);
		}else if( state == State.ANIMATE_ONCE_REVERSE ){
			walkAnimation.setPlayMode(Animation.REVERSED);
			frameTime = 0f;
			state = State.ANIMATED;
			currentFrame = walkAnimation.getKeyFrame(frameTime, false);
		}else if( state == State.IDLE ){
			currentFrame = walkAnimation.getKeyFrame(idleKeyFrame, false);
		}
		
		return currentFrame;
	}
	
	public int getCurrentFrameIndex(){
		int keyFrameIndex = -1;
		
		if( walkAnimation == null ){
			Gdx.app.debug(TAG, "Current animation is NULL..." );
			return keyFrameIndex;
		}

		if( state == State.WALKING || state == State.ANIMATED){
			keyFrameIndex = walkAnimation.getKeyFrameIndex(frameTime);
		}else if( state == State.IDLE ){
			keyFrameIndex = 0;
		}
		
		return keyFrameIndex;
	}
	
	public Sprite updateSelectionState(){
		if( !isSelected ){
			//Dispose of unmanaged Texture
			if( selectionSprite != null && selectionSprite.getTexture() != null ){
				Utility.dispose(selectionSprite.getTexture());	
			}

			selectionSprite = null;
			updatedSelectionState = false;
		}else if( updatedSelectionState ){
			selectionSprite.setPosition(frameSprite.getX(), frameSprite.getY());
		}else{
			selectionSprite = new Sprite(frameSprite);
			
			int x = frameSprite.getRegionX();
			int y = frameSprite.getRegionY();
			int width = frameSprite.getRegionWidth();
			int height = frameSprite.getRegionHeight();
			//Gdx.app.debug(TAG, "Current Region : " + x + "," + y + " width:" + width + " height:" + height);
	
			//Get the pixmap data from GPU
			selectionSprite.getTexture().getTextureData().prepare();
			Pixmap srcPixmap = selectionSprite.getTexture().getTextureData().consumePixmap();
			
			//Create the destination pixmap to bind to the texture
			Pixmap destPixmap = new Pixmap(width, height, Format.RGBA8888);

			
			//Once OpenGL 2.0 is enabled, we should use shaders instead			
				for(int row = y, destRow=0; row < height+y; row++,destRow++){
					for(int col = x, destCol=0; col < width+x; col++,destCol++){
					int pixel = srcPixmap.getPixel(col, row);
					int A = ((pixel & 0x000000ff));
					if( A != 0 ){
						//Gdx.app.debug(TAG, "Pixel at : " + col + "," + row );
						destPixmap.drawPixel(destCol, destRow, Color.rgba8888( .72f, 0.9f, 1.0f, 0.55f));
					}
				}
			}
			
			//Upload modified image data to GPU
			Texture texture = new Texture( destPixmap );
			srcPixmap.dispose();
			destPixmap.dispose();

			selectionSprite.setTexture(texture);
			
			//Gdx.app.debug(TAG, "Selected Sprite! : " + this.getEntityID() );
			updatedSelectionState = true;
		}
		return selectionSprite;
	}
	
	@Override
	public void init(float startX, float startY){
		this.currentPlayerPosition.x = startX;
		this.currentPlayerPosition.y = startY;
		
		this.nextPlayerPosition.x = startX;
		this.nextPlayerPosition.y = startY;
		
		setIsAlive(true);

		loadInventoryItems();
		
		needsInit = false;

		//Gdx.app.debug(TAG, "Calling INIT" );
	}
	
	@Override
	public boolean isAlive(){
		return isAlive;
	}
	
	@Override 	  
	public void setIsAlive(boolean isAlive){
		if( isAlive == false ){
			entityMessageQueue.purgeEntityStatusQueue();
			drawDeathEffect = true;
			defaultDeathEffect.reset();
		}else{
			drawDeathEffect = false;
			currentHealth = maxHealth;
			isEntityDamaged = false;
		}
		
		this.isAlive = isAlive;
	}
	
	@Override
	public boolean existsOffScreen(){
		  return existsOffScreen;
	}
	
	@Override
	public void setExistsOffScreen(boolean existsOffscreen){
		this.existsOffScreen = existsOffscreen;
	}
	
	@Override
	public Rectangle getEntityBoundingBox(){
		//Update the current bounding box
		if( frameSprite == null ){
			Gdx.app.debug(TAG, "Framesprite for getEntityBoundingBox() is NULL");
			return boundingBox;
		}
		
		//Gdx.app.debug(TAG, "GETTING Bounding Box: " + boundingBox.getX() + "," + boundingBox.getY() + "width " + boundingBox.getWidth() + " height " + boundingBox.getHeight());
		
		return boundingBox;
	}
	
	public void setBoundingBoxSize(float percentageReduced){
		//Update the current bounding box		
		float width;
		float height;
		float xOffset;
		float yOffset;
		
		float reductionAmount = 1.0f - percentageReduced; //.8f for 20% (1 - .20)
		
		if( reductionAmount > 0 && reductionAmount < 1){
			width = WIDTH * reductionAmount; //reduce by 20%
			height = HEIGHT * reductionAmount; //reduce by 20%
		}else{
			width = WIDTH; 
			height = HEIGHT;
		}
		
		if( width == 0 || height == 0){
			Gdx.app.debug(TAG, "Width and Height are 0!! " + width + ":" + height);		
		}

		xOffset = (WIDTH - width)/2;
		yOffset =  (HEIGHT - height)/2;
		
		//Gdx.app.debug(TAG, "Reduction amount: " + width + ":" + height);
		//Gdx.app.debug(TAG, "Regular amount: " + WIDTH + ":" + HEIGHT);
		//Gdx.app.debug(TAG, "Offset amount: " + xOffset + "," + yOffset);
	
		float minX = nextPlayerPosition.x + xOffset;
		float minY = nextPlayerPosition.y + yOffset;
		
		boundingBox.set( minX,minY,width,height);
		//Gdx.app.debug(TAG, "SETTING Bounding Box: " + minX + "," + minY + "width " + width + " height " + height);
	}
	
	private void loadTextures(){
		//Walking animation
		if(	walkingCycle == null && Utility.isAssetLoaded(imagePath)){
			walkingCycle = Utility.getTextureAsset(imagePath);
			loadedWalkAnimationImagePath = imagePath;
			
			if( walkingCycle == null ){
				Gdx.app.debug(TAG, "Walking Texture is null" );
				return;
			}
			
			TextureRegion[] walkCycleFrames = getFramesfromImage(walkingCycle);
			
	        walkAnimation = new Animation(0.11f, walkCycleFrames);
	        walkAnimation.setPlayMode(Animation.LOOP); 
	        
	        //get the first frame so we can render something
	        TextureRegion currentFrame = walkAnimation.getKeyFrame(idleKeyFrame, false);
	        
			if( currentFrame == null ){
				Gdx.app.debug(TAG, "Current frame is null" );
				return;
			}
			
	        frameSprite.setRegion(currentFrame);
	        frameSprite.setOrigin(currentFrame.getRegionWidth()/2, currentFrame.getRegionHeight()/2);
	        frameSprite.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
	        
	        //We are doing a 1 pixel for every unit for the game
	        WIDTH = 1f * currentFrame.getRegionWidth();
	        HEIGHT = 1f * currentFrame.getRegionHeight();
	        
	        //Now that the Height and Width are set, we need to set the boundingbox
	        setBoundingBoxSize(0f);
		}
	}
	
	public void loadWalkingAnimation(int numRows, int numColumns, int totalFrames)
	{
		walkingAnimStartRowIndex = 0;
		walkingAnimStartColIndex = 0;
		
		initWalkingAnim(numRows, numColumns, totalFrames);
	}
	
	public void loadWalkingAnimation(int startRowIndex, int startColIndex, int numRows, int numColumns, int totalFrames)
	{
		walkingAnimStartRowIndex = startRowIndex;
		walkingAnimStartColIndex = startColIndex;
		
		initWalkingAnim(numRows, numColumns, totalFrames);
	}
	
	private void initWalkingAnim(int numRows, int numColumns, int totalFrames){
		walkingAnimRows = numRows;
		walkingAnimCols = numColumns;
		walkingAnimFrames = totalFrames;
		
		Utility.unloadAsset(loadedWalkAnimationImagePath);
		walkingCycle = null;
		
		Utility.loadTextureAsset(imagePath);
	}
	
	@Override
	public void setEntityImagePath(String imagePath){
		this.imagePath = imagePath;		  
	}
	
	public void dispose(){
		Utility.unloadAsset(imagePath);
		entityDamagedSound.dispose();			
	}
	
	public void setState(State state){
		this.state = state;
	}
	
	public State getState(){
		return state;
	}
	
	public Sprite getFrameSprite(){
		return frameSprite;
	}
	
	protected TextureRegion[] getFramesfromImage(Texture sourceImage){	
		//Handle walking animation of main character
		final int sourceCycleRow = walkingAnimRows;
		final int sourceCycleCol = walkingAnimCols;

		int frameWidth = sourceImage.getWidth()  / sourceCycleCol;
		int frameHeight = sourceImage.getHeight() / sourceCycleRow;
		
		TextureRegion[][] temp = TextureRegion.split(sourceImage, frameWidth, frameHeight);  
		
		TextureRegion[] textureFrames = new TextureRegion[walkingAnimFrames];

		int index = 0;
        for (int i = walkingAnimStartRowIndex; i < sourceCycleRow && index < walkingAnimFrames; i++) {
                for (int j = walkingAnimStartColIndex; j < sourceCycleCol && index < walkingAnimFrames; j++) {
                	//Gdx.app.debug(TAG, "Got frame " + i + "," + j + " from " + sourceImage);
                	TextureRegion region = temp[i][j];
                	if( region == null ){
                		Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j + " from " + sourceImage);
                	}
                	textureFrames[index] = region;
                	index++;
                }
        }
        
        return textureFrames;
	}
	
	public Vector2 getNextPosition(){
		return nextPlayerPosition;
	}
	
	public void setNextPosition(float nextPositionX, float nextPositionY){
		this.nextPlayerPosition.x = nextPositionX;
		this.nextPlayerPosition.y = nextPositionY;
	}
	
	public Vector2 getCurrentPosition(){
		return currentPlayerPosition;
	}
	
	public void setCurrentPosition(float currentPositionX, float currentPositionY){
		this.currentPlayerPosition.x = currentPositionX;
		this.currentPlayerPosition.y = currentPositionY;
	}
	
	public void setDirection(Direction direction){
		this.previousDirection = this.currentDirection;
		this.currentDirection = direction;
		
		//Look into the appropriate variable when changing position
		//@TODO Seems can't use stored delta, need to get a new one everytime

		switch (currentDirection) {
		case DOWN : rotationDegrees = 0;
		break;
		case LEFT : rotationDegrees = 270;
		break;
		case UP : rotationDegrees = 180;
		break;
		case RIGHT : rotationDegrees = 90;
		break;
		default:
			break;
		}
		
		frameSprite.setRotation(rotationDegrees);
	}
	
	public Direction getCurrentDirection(){
		return currentDirection;
	}
	
	public Direction getPreviousDirection(){
		return previousDirection;
	}

	
	public void setNextPositionToCurrent(){
		if( state == State.PAUSE){
			return;
		}

		frameSprite.setX(nextPlayerPosition.x);
		frameSprite.setY(nextPlayerPosition.y);
		setCurrentPosition(nextPlayerPosition.x, nextPlayerPosition.y);
		//Gdx.app.debug(TAG, "NOT BLOCKED: Setting nextPosition as Current: (" + nextPlayerPosition.x + "," + nextPlayerPosition.y + ")"  );		

	}
	
	
	public void calculateNextPosition(Direction currentDirection, float deltaTime){
		if( state == State.PAUSE){
			return;
		}
		
		
		float testX = 0f;
		float testY = 0f;
					
		testX = currentPlayerPosition.x;
		testY = currentPlayerPosition.y;
		
		//Gdx.app.debug(TAG, "calculateNextPosition:: Current Position: (" + currentPlayerPosition.x + "," + currentPlayerPosition.y + ")"  );
		//Gdx.app.debug(TAG, "calculateNextPosition:: Current Direction: " + currentDirection  );
		
		velocity.scl(deltaTime);
		
		switch (currentDirection) {
		case LEFT : 
		testX -=  velocity.x;
		break;
		case RIGHT :
		testX += velocity.x;
		break;
		case UP : 
		testY += velocity.y;
		break;
		case DOWN : 
		testY -= velocity.y;
		break;
		default:
			break;
		}
		
		nextPlayerPosition.x = testX;
		nextPlayerPosition.y = testY;
		
		//velocity
		velocity.scl(1/deltaTime);		
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public int getIdleKeyFrame() {
		return idleKeyFrame;
	}

	public void setIdleKeyFrame(int idleKeyFrame) {
		this.idleKeyFrame = idleKeyFrame;
	}

	public boolean isCollidable() {
		return isCollidable;
	}

	public void setCollidable(boolean isCollidable) {
		this.isCollidable = isCollidable;
	}

	@Override
	public SECURITYLEVEL getSecurityLevel() {
		return level;
	}

	@Override
	public void setSecurityLevel(String level) {
		this.level = SECURITYLEVEL.toValue(level);
	}
	
	
}
