package net.garrapeta.demo.actor;

import net.garrapeta.demo.JumplingsGameWorld;
import net.garrapeta.demo.Player;
import android.graphics.PointF;

public class ScoreTextActor extends TextActor {

	// ----------------------------------------------------- Constantes
	
	/**  Velocidad vertical, en unidades del mundo por segundo */
	public final static float DEFAULT_Y_VELOCITY = 15;
	
	/** Tiempo que permanece el actor en pantalla, en ms */
	public final static int    DEFAULT_LONGEVITY			= 250;
	
	/** Tiempo que permanece el actor en pantalla, en ms */
	public final static int    BASE_FONTSIZE				= 35;

	// ----------------------------------------- Variables de instancia

	// ----------------------------------------------------- Constructor
	
	public ScoreTextActor(JumplingsGameWorld cWorld, PointF worldPos, int score) {
		super(cWorld, worldPos);
		
		
		int level = (score / Player.BASE_POINTS);
		
		this.text = "+" + score;
		
		this.yVel = DEFAULT_Y_VELOCITY;
		
		
		this.longevity = DEFAULT_LONGEVITY + (level * 150);
		this.lifeTime = longevity;
		
		
		float textSize =  BASE_FONTSIZE  + (level * 3);
		paint.setTextSize(textSize);
		
	}

}
