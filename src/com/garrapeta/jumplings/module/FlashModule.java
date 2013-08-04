package com.garrapeta.jumplings.module;

import android.graphics.Color;

import com.garrapeta.gameengine.module.LeveledActionsModule;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.PermData;
import com.garrapeta.jumplings.actor.FlashActor;
import com.garrapeta.jumplings.actor.FlashActor.FlashData;

public class FlashModule  {

	private final static int FLASH_SHOT_DURATION = 100;
	private final static int FLASH_SHOT_ALPHA = 200;
	private final static int FLASH_SHOT_COLOR = Color.WHITE;
	private final static int FLASH_SHOT_PRIORITY = -1;

	private final static int FLASH_FAIL_DURATION = 1000;
	private final static int FLASH_FAIL_ALPHA = 230;
	private final static int FLASH_FAIL_COLOR = Color.BLACK;
    private final static int FLASH_FAIL_PRIORITY = 1;

    private final static int FLASH_LIFEUP_DURATION = 750;
    private final static int FLASH_LIFEUP_ALPHA = 180;
    private final static int FLASH_LIFEUP_COLOR = Color.rgb(255, 105, 180);
    private final static int FLASH_LIFEUP_PRIORITY = 2;

    private final static int FLASH_BLADE_DRAWN_DURATION = 750;
    private final static int FLASH_BLADE_DRAWN_ALPHA = 180;
    private final static int FLASH_BLADE_DRAWN_COLOR = Color.BLUE;
    private final static int FLASH_BLADE_DRAWN_PRIORITY = 0;

    private final static int BLADE_SWING_DURATION = 250;
    private final static int BLADE_SWING_ALPHA = 50;
    private final static int BLADE_SWING_COLOR = Color.WHITE;
    private final static int BLADE_SWING_PRIORITY = -1;
    
	public final static short ENEMY_SCAPED_KEY = 0;
	public final static short BOMB_EXPLODED_KEY = 1;
	public final static short LIFE_UP_KEY = 2;
	public final static short BLADE_DRAWN_KEY = 3;
	public final static short TAP_KEY = 4;
	public final static short BLADE_SWING_KEY = 5;
	
	private final static FlashData ENEMY_SCAPED_FLASH_DATA = new FlashData(FLASH_FAIL_COLOR, FLASH_FAIL_ALPHA, FLASH_FAIL_DURATION, FLASH_FAIL_PRIORITY);
	private final static FlashData BOMB_EXPLODED_DATA = new FlashData(FLASH_FAIL_COLOR, FLASH_FAIL_ALPHA, FLASH_FAIL_DURATION, FLASH_FAIL_PRIORITY);
	private final static FlashData LIFE_UP_DATA = new FlashData(FLASH_LIFEUP_COLOR, FLASH_LIFEUP_ALPHA, FLASH_LIFEUP_DURATION, FLASH_LIFEUP_PRIORITY);
	private final static FlashData BLADE_DRAWN_DATA = new FlashData(FLASH_BLADE_DRAWN_COLOR, FLASH_BLADE_DRAWN_ALPHA, FLASH_BLADE_DRAWN_DURATION, FLASH_BLADE_DRAWN_PRIORITY);
	private final static FlashData TAP_DATA = new FlashData(FLASH_SHOT_COLOR, FLASH_SHOT_ALPHA, FLASH_SHOT_DURATION, FLASH_SHOT_PRIORITY);
	private final static FlashData BLADE_SWING_DATA = new FlashData(BLADE_SWING_COLOR, BLADE_SWING_ALPHA, BLADE_SWING_DURATION, BLADE_SWING_PRIORITY);
    
	/** Flash actor used in flash effects */
    private final FlashActor mFlashActor;
    
    private final FlashModuleDelegate mDelegate;
    
	public FlashModule(short minimumLevel, JumplingsGameWorld jumplingsGameWorld) {
		mDelegate = new FlashModuleDelegate(minimumLevel);
        mFlashActor = new FlashActor(jumplingsGameWorld);
        mFlashActor.setInitted();
        jumplingsGameWorld.addActor(mFlashActor);
        
        mDelegate.create(PermData.CFG_LEVEL_SOME, ENEMY_SCAPED_KEY).add(ENEMY_SCAPED_FLASH_DATA);
        mDelegate.create(PermData.CFG_LEVEL_SOME, BOMB_EXPLODED_KEY).add(BOMB_EXPLODED_DATA);
        mDelegate.create(PermData.CFG_LEVEL_SOME, LIFE_UP_KEY).add(LIFE_UP_DATA);
        mDelegate.create(PermData.CFG_LEVEL_SOME, BLADE_DRAWN_KEY).add(BLADE_DRAWN_DATA);
        mDelegate.create(PermData.CFG_LEVEL_ALL, TAP_KEY).add(TAP_DATA);
        mDelegate.create(PermData.CFG_LEVEL_ALL, BLADE_SWING_KEY).add(BLADE_SWING_DATA);
        
	}
	
	public boolean flash(short key) {
		return mDelegate.executeOverOneResourceForKey(key);
	}

	private class FlashModuleDelegate extends LeveledActionsModule<FlashData, Void>  {
		
		private FlashModuleDelegate(short minimumLevel) {
			super(minimumLevel);
		}

		@Override
		protected void onExecute(FlashData flashData, Void... params) {
			mFlashActor.init(flashData);
		}
	}

}
