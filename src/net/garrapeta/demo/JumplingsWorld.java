package net.garrapeta.demo;

import net.garrapeta.demo.actor.WallActor;
import net.garrapeta.demo.wave.Wave;
import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.box2d.Box2DWorld;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Mundo del juego
 * 
 * @author GaRRaPeTa
 */
public class JumplingsWorld extends Box2DWorld {

	// -------------------------------------------------------- Constantes
	
	// ------------------------------------------------------------ Variables
	
	public JumplingsActivity jActivity;
	
	/** Wave actual */
	Wave wave;
	
	
	// centro de la pantalla
	float centerX;
	float centerY;
	

	
	/** Límites del mundo. Normalmente concidirán con el viewport, pero no es obligatorio */
	public RectF worldBoundaries;
	
	//----------------------------------------------------------- Constructor
	
	public JumplingsWorld(JumplingsActivity jActivity, GameView gameView) {
		super(jActivity, gameView);
		this.jActivity = jActivity;
		worldBoundaries = new RectF();
		
		// FIXME: Chapuza para evitar problema del cuelgue al inicial el juego
		view.setSyncDrawing(false);
	}
	
	// ----------------------------------------------------- Métodos de World
	
	public void create() {

		Log.i(JumplingsApplication.LOG_SRC,"create " + this);
		
		this.setTimeFactor(1);
		
		
		// Paredes  ----------------------------------------------------------------- 
		
		float wallsMargin = 0;		
		this.viewport.setViewportHeightInWorldUnits(14);
		
		RectF vb = viewport.getBoundaries();
		worldBoundaries.left   = vb.left 	+ wallsMargin;
		worldBoundaries.bottom = vb.bottom 	+ wallsMargin;
		worldBoundaries.right  = vb.right 	- wallsMargin;
		worldBoundaries.top    = vb.top 	- wallsMargin;		
		
		
		// pared superior
		addActor(new WallActor(this, 
									 new PointF(0, 0),
				                     new PointF(worldBoundaries.left, 	worldBoundaries.top),  
				                     new PointF(worldBoundaries.right,	worldBoundaries.top),
				                     false, false));
		
		// pared inferior - FLOOR
		addActor(new WallActor(this,
				                     new PointF(0, 0),  
					                 new PointF(worldBoundaries.left,	worldBoundaries.bottom),  
					                 new PointF(worldBoundaries.right,	worldBoundaries.bottom),
				                     true, false));
		
		// pared izquierda
		addActor(new WallActor(this,
				                     new PointF(0,0 ),
					                 new PointF(worldBoundaries.left, 	worldBoundaries.bottom),  
					                 new PointF(worldBoundaries.left, 	worldBoundaries.top),
				                     false, false));			

		// pared derecha
		addActor(new WallActor(this,
					                new PointF(0, 0),
					                new PointF(worldBoundaries.right, 	worldBoundaries.bottom),  
					                new PointF(worldBoundaries.right, 	worldBoundaries.top ),
					                false, false));
		
		
		// Paredes de seguridad --------------------------------------------------------------- 
		
		// tiene que ser negativo
		float securityMargin = - Wave.ENEMY_OFFSET * 3;		
		
		float securityLeft   = worldBoundaries.left		+ securityMargin;
		float securityBottom = worldBoundaries.bottom 	+ securityMargin;
		float securityRight  = worldBoundaries.right 	- securityMargin;
		float securityTop    = worldBoundaries.top 		- securityMargin;		
		
		
		// pared de seguridad superior
		addActor(new WallActor(this, 
									 new PointF(0, 0),
				                     new PointF(securityLeft, 	securityTop),  
				                     new PointF(securityRight,	securityTop),
				                     false, true));
		
		// pared de seguridad inferior
		addActor(new WallActor(this,
				                     new PointF(0, 0),  
					                 new PointF(securityLeft,	securityBottom),  
					                 new PointF(securityRight,	securityBottom),
				                     false, true));
		
		// pared de seguridad izquierda
		addActor(new WallActor(this,
				                     new PointF(0,0 ),
					                 new PointF(securityLeft, 	securityBottom),  
					                 new PointF(securityLeft, 	securityTop),
				                     false, true));			

		// pared de seguridad derecha
		addActor(new WallActor(this,
					                new PointF(0, 0),
					                new PointF(securityRight, 	securityBottom),  
					                new PointF(securityRight, 	securityTop),
					                false, true));
			
		
	}
	

	
	
	
	@Override
	public synchronized void processFrame(float gameTimeStep, float physicsTimeStep) {
		
		// FIXME: Chapuza para evitar problema del cuelgue al inicial el juego
		if (!view.isSyncDrawing() && currentPhysicsMillis() > 100) {
			view.setSyncDrawing(true);
		}
		
		
		
		// La generación de enemigos, regeneración de vida, comprobación de satisfacción
		// de condiciones de victoria derrota, etc, se delega en el wave-
		wave.processFrame(gameTimeStep, physicsTimeStep);
		
		

	}
	

	@Override
	protected void drawWorld(Canvas canvas) {
		drawWorldBackground(canvas);
		drawActors(canvas);
	}
	
	private void drawWorldBackground(Canvas canvas) {
		canvas.drawARGB(255, 255, 255, 255);
	}

	@Override
	public synchronized void surfaceChanged(float width, float height) {
		super.surfaceChanged(width, height);
    	
		Log.i(JumplingsApplication.LOG_SRC,"surfaceChanged " + this);
		
    	if (!jActivity.isWorldStarted()) {    		
    		jActivity.startWorld();
    	}

	}
	

	// -------------------------------------------------------- Métodos propios
	


	
	public void onWaveStarted() {
		Log.i(JumplingsApplication.LOG_SRC, "Wave started");
	}
	
	public void onWaveCompleted() {
		Log.i(JumplingsApplication.LOG_SRC, "Wave completed");
	}
	
	
	public void drawBitmap(Canvas canvas, Body body, Bitmap bitmap) {
		drawBitmap(canvas, body, bitmap, null);
	}
	
	/**
	 * Dibuja el bitmap centrado en el body
	 * @param canvas
	 * @param body
	 * @param bitmap
	 */
	public final void drawBitmap(Canvas canvas, Body body, Bitmap bitmap, Paint paint) {
		Vector2 worldPos = body.getWorldCenter();
		
		PointF screenPos = viewport.worldToScreen(worldPos.x, worldPos.y);
		canvas.save();
		
		canvas.translate(screenPos.x, screenPos.y);

		
		canvas.rotate(- (float)Math.toDegrees(body.getAngle()));

		// TODO: ¿No se puede especificar el punto de anclaje de otra manera?
		canvas.translate(-bitmap.getWidth() / 2 , -bitmap.getHeight() / 2 );
		
		Matrix m = new Matrix();
    	canvas.drawBitmap(bitmap, m, paint);
        canvas.restore();
	}
}
