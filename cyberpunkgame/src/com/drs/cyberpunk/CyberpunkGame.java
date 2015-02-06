package com.drs.cyberpunk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.drs.cyberpunk.screens.CreditScreen;
import com.drs.cyberpunk.screens.GameScreen;
import com.drs.cyberpunk.screens.InventoryScreen;
import com.drs.cyberpunk.screens.LoadGameScreen;
import com.drs.cyberpunk.screens.MainMenuScreen;
import com.drs.cyberpunk.screens.NewGameScreen;
import com.drs.cyberpunk.screens.SplashScreen;


public class CyberpunkGame extends Game {

	private static final String TAG = CyberpunkGame.class.getSimpleName();

	public SplashScreen logoSplash;
	public GameScreen gameScreen;
	public MainMenuScreen mainMenuScreen;
	public CreditScreen creditScreen;
	public InventoryScreen inventoryScreen;
	public LoadGameScreen loadGameScreen;
	public NewGameScreen newGameScreen;
	
	private static CyberpunkGame game = null;
	
	private CyberpunkGame(){
	}
	
	public static final CyberpunkGame getInstance(){
		if( game == null){
			game = new CyberpunkGame();
		}
		return game;
	}

	@Override
	public void create() {
		//Gdx.app.debug(TAG, "Create()" );
		Assets.load();
		
		logoSplash = new SplashScreen();
		mainMenuScreen = new MainMenuScreen();
		creditScreen = new CreditScreen();
		gameScreen = new GameScreen();
		loadGameScreen = new LoadGameScreen();
		newGameScreen = new NewGameScreen();

		
		
		setScreen(logoSplash);
	}
	
	@Override
	public void render () {
		super.render();
		
		boolean isLoadingFinished = Utility.updateAssetLoading();

		if( isLoadingFinished == false){
			Gdx.app.debug(TAG, "Completed percentage: " + Utility.loadCompleted()*100 + " of " + Utility.numberAssetsQueued() + " total items" );
		}

	}
	
}
