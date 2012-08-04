package net.garrapeta.demo.actor;


import net.garrapeta.demo.JumplingsGameWorld;
import net.garrapeta.gameengine.Actor;
import android.graphics.Canvas;
import android.graphics.Color;

public class FlashActor extends Actor {

	// ----------------------------------------------------------- Constantes
	
	/**
	 *  Z-Index del actor
	 */
	public final static int Z_INDEX = 10;

	public final static int    FLASH_SHOT_DURATION			= 100;
	public final static int    FLASH_SHOT_ALPHA		   		= 200;
	public final static int    FLASH_SHOT_COLOR		   		= Color.WHITE;
	
	public final static int    FLASH_ENEMY_SCAPED_DURATION	= 1500;
	public final static int    FLASH_ENEMY_SCAPED_ALPHA		= 230;
	public final static int    FLASH_ENEMY_SCAPED_COLOR		= Color.BLACK;
	
	public final static int    FLASH_BOMB_DURATION			= 2000;
	public final static int    FLASH_BOMB_ALPHA				= 255;
	public final static int    FLASH_BOMB_COLOR		   		= Color.WHITE;
	
	public final static int    FLASH_BOMB2_DURATION			= 100;
	public final static int    FLASH_BOMB2_ALPHA			= 255;
	public final static int    FLASH_BOMB2_COLOR		   	= Color.BLACK;
	
	public final static int    FLASH_LIFEUP_DURATION		= 750;
	public final static int    FLASH_LIFEUP_ALPHA			= 180;
	public final static int    FLASH_LIFEUP_COLOR		   	= Color.rgb(255, 105, 180);
	
	public final static int    FLASH_BLADE_DRAWN_DURATION	= 750;
	public final static int    FLASH_BLADE_DRAWN_ALPHA		= 180;
	public final static int    FLASH_BLADE_DRAWN_COLOR		= Color.BLUE;
	
	// ------------------------------------------------ Variables de instancia
	
	float longevity;
	
	float lifeTime;
	
	private float baseAlpha;
	private float baseRed;
	private float baseGreen;
	private float baseBlue;
	
	// ---------------------------------------------------------- Constructor
	
	public FlashActor(JumplingsGameWorld cWorld, int color, int alpha, int longevity) {
		super(cWorld, Z_INDEX);

		this.longevity = longevity;
		this.lifeTime  = longevity;
		this.baseAlpha = Color.alpha(color) * (alpha / 255f) ;
		this.baseRed = Color.red(color);
		this.baseGreen = Color.green(color);
		this.baseBlue = Color.blue(color);

	}

	// ------------------------------------------------------------- Métodos

	@Override
	public void doLogic(float gameTimeStep) {
		lifeTime = Math.max(0, lifeTime - gameTimeStep);
		if (lifeTime <= 0) {
			gameWorld.removeActor(this);
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		float alphaFactor = lifeTime / longevity;
		float finalAlpha  = alphaFactor * baseAlpha;
		canvas.drawARGB((int)finalAlpha, (int)baseRed, (int)baseGreen, (int)baseBlue);
	}

	@Override
	public boolean isPointInActor(float worldX, float worldY) {
		return false;
	}
}

