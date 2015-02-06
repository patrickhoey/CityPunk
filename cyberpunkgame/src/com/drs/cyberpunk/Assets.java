package com.drs.cyberpunk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable{

	//Scripts
	public static final String _ATM_ENTITY = "data/scripts/atm.lua";
	
	public static final String _CREDITS_PATH = "data/text/credits.txt";
	public static final String _CURSOR_PATH = "data/images/cursor.png";
	public static final String _MENU_STARTUP = "data/sounds/right_context_menu_sound.mp3";
	public static final String _MENU_STARTUP_HOVER = "data/sounds/right_context_menu_hover_sound.mp3";
	
	public static final String _ALARM_SOUND = "data/sounds/alarm_sound.wav";

	 //Particle Effects
	public static final String EFFECTS_DIR = "data/effects";
	public static final String defaultDamageParticleEffect = "data/effects/sparks.p";
	public static final String defaultDeathParticleEffect = "data/effects/death_smoke.p";
	public static final String barrelFireParticleEffect = "data/effects/barrel_fire.p";
	public static final String grateSmokeParticleEffect = "data/effects/grate_smoke.p";
	
	//Fonts
	public static final String _MONKIRTA_PURSUIT_36_FNT = "data/fonts/monkirta_pursuit_36.fnt";
	public static final String _MONKIRTA_PURSUIT_36_PNG = "data/fonts/monkirta_pursuit_36.png";
	
	public static Skin skin;
	public static AssetManager _AssetManager;
		
	public static void load(){
		skin = new Skin();
		skin.addRegions(new TextureAtlas(Gdx.files.internal("data/skins/uiskin.atlas")));
		skin.load(Gdx.files.internal("data/skins/uiskin.json"));
		
		_AssetManager = new AssetManager();
		
	}


	@Override
	public void dispose() {
		_AssetManager.dispose();		
	}
	
}
