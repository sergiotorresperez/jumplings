package net.garrapeta.demo.actor;

import net.garrapeta.demo.JumplingsGameWorld;
import net.garrapeta.gameengine.Viewport;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.RectF;


public class BulletActor extends HarmerActor {

	// ----------------------------------------------------- Constantes
	

	private final static float KILL_RADIUS    = 0.7f;
	private final static float BLAST_RADIUS   = 3;
	private final static float BLAST_FORCE    = 25;

	// ----------------------------------------- Variables de instancia
	
	protected Paint  paint;
	
	PointF worldPos;
	
	public float longevity;
	
	public float lifeTime = longevity;
	
	private float maxExplosionRadius;
	

	protected JumplingsGameWorld cWorld;
	
	private boolean firstFrame = true;
	
	private boolean alreadyKilled = false;

	// -------------------------------------------------- Constructores
	
	public BulletActor(JumplingsGameWorld cWorld, PointF worldPos, float maxRadius) {
		super(cWorld);
		this.cWorld = cWorld;
		this.worldPos = worldPos;
		this.maxExplosionRadius = maxRadius;
		
		paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setTextAlign(Align.CENTER);
		
		this.timestamp = System.currentTimeMillis();
		
		
	}

	// ----------------------------------------------- Métodos heredados
	
	@Override
	public void doLogic(float gameTimeStep) {
	
		// vida de la bala		
		lifeTime = Math.max(0, lifeTime - gameTimeStep);
		if (lifeTime <= 0) {
			gameWorld.removeActor(this);
		}
		
		super.doLogic(gameTimeStep);
		
		firstFrame = false;
	}
	
	@Override
	protected void effectOver(MainActor j) {
		if (firstFrame) {
			if (this.kills(j)) {
				j.onHitted();
			} else {
				// se aplica onda expansiva
				cWorld.applyForce(Viewport.pointFToVector2(worldPos), j.mainBody, BLAST_RADIUS, BLAST_FORCE);
			}
		}
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		int a = (int) ((lifeTime / longevity) * 255);
		paint.setAlpha(a);
		PointF screenPos = cWorld.viewport.worldToScreen(worldPos);
		float currentRadius = ((longevity - lifeTime) / longevity) * maxExplosionRadius;
		canvas.drawCircle(screenPos.x, screenPos.y, cWorld.viewport.worldUnitsToPixels(currentRadius), paint);
	}
	
	// --------------------------------------- Métodos propios


	private boolean hits(MainActor mainActor) {
		PointF pos = mainActor.getWorldPos();

		RectF otherRect = new RectF(pos.x - mainActor.radius, pos.y - mainActor.radius, pos.x + mainActor.radius, pos.y + mainActor.radius);
		RectF thisRect  = new RectF(worldPos.x - KILL_RADIUS, worldPos.y - KILL_RADIUS, worldPos.x + KILL_RADIUS, worldPos.y + KILL_RADIUS);
		return RectF.intersects(otherRect, thisRect);
	}

	
	private boolean kills(MainActor mainActor) {
		if (!alreadyKilled) {
			if (hits(mainActor)) {
				alreadyKilled = true;
				return true;
			}
		}
		return false;
	}
	
}
