package net.garrapeta.jumplings.scenario;

import net.garrapeta.jumplings.R;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.wave.CampaignSurvivalWave;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 
 * Clase para pintar el escenario
 * @author GaRRaPeTa
 */
public class Scenario {
	
	// ------------------------------------------------ Constantes
	
	/** Tiempo que tarda la animaci'on de fade out al desaparecer, en ms */
	private final static int FADE_OUT_TIME = CampaignSurvivalWave.AFTER_WAVE_SWITCH_REALTIME;

	
	// ------------------------------------ Variables de instancia

	JumplingsGameWorld dWorld;
	
	// cielo
	Layer layerBg0;
	// monta�as
	Layer layerBg1;
	// nubes
	Layer layerBg2;

	/** si el escenario est� desapareciendo */
	public boolean fadingOut = false;

	/** Tiempo que le queda al escenario para terminar de desaparecer */
	public float fadingOutRemainigTime = FADE_OUT_TIME;
	
	private Paint paint 	 = new Paint();	
	
	// ----------------------------------------------- Constructor
	

	/**
	 * @param dWorld
	 */
	public Scenario(JumplingsGameWorld dWorld) {
		this.dWorld = dWorld;
		
		// Inicializaci�n de las layers
		Resources r = dWorld.jgActivity.getResources();
		{
			Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.bg_blue_sky);
			int maxHeight = (int) (dWorld.view.getHeight() * 1.5);
			layerBg0 = new Layer(this, bmp, maxHeight, 0, 0, 2, 0, true, true);
		}
		{
			Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.bg_green_hills);
			int maxHeight = (int) (dWorld.view.getHeight() * 2);
			float initYPos = dWorld.view.getHeight() - bmp.getHeight();
			layerBg1 = new Layer(this, bmp, maxHeight, 0, initYPos, 0, 0, false, false);
		}
		{
			Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.bg_clouds);
			int maxHeight = (int) (dWorld.view.getHeight() * 2.7);
			float initYPos = -maxHeight + bmp.getHeight();
			layerBg2 = new Layer(this, bmp, maxHeight, 0, initYPos, 3, 0, true, false);
		}
	}
	
	/**
	 * Reseteo
	 */
	public void reset() {
		layerBg0.reset();
		layerBg1.reset();
		layerBg2.reset();
	}
	
	public void setProgress(float progress) {
		layerBg0.setProgress(progress);
		layerBg1.setProgress(progress);
		layerBg2.setProgress(progress);
	}
	
	public void onGameOver() {
		layerBg0.onGameOver();
		layerBg1.onGameOver();
		layerBg2.onGameOver();
	}
	
	// --------------------------------------------- M�todos propios
	
	public void processFrame(float gameTimeStep) {
		if (fadingOut) {
			fadingOutRemainigTime = Math.max(0, fadingOutRemainigTime - gameTimeStep);
			int alpha = (int) (255 * fadingOutRemainigTime / FADE_OUT_TIME);
			paint.setAlpha(alpha);
		}
		layerBg0.processFrame(gameTimeStep);
		layerBg1.processFrame(gameTimeStep);
		layerBg2.processFrame(gameTimeStep);
	}
	

	public void draw(Canvas canvas) {
		if (JumplingsApplication.DRAW_SCENARIO) {				
			layerBg0.draw(canvas, paint);
			layerBg1.draw(canvas, paint);
			layerBg2.draw(canvas, paint);
		}
	}

}

