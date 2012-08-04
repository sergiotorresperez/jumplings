package net.garrapeta.demo.actor;


import java.util.ArrayList;

import net.garrapeta.demo.JumplingsWorld;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class DebrisActor extends JumplingActor {

	// ----------------------------------------------------------- Constantes
	
	/**
	 *  Z-Index del actor
	 */
	public final static int Z_INDEX = -10;
	
	public final static Filter DEBRIS_FILTER;
	
	public  final static int    DEBRIS_FILTER_BIT     	= 0x00004;
	
	/** Tiempo que permanece el actor en pantalla, en ms */
	private final static int    DEFAULT_LONGEVITY			= 2500;
	
	// ------------------------------------------------ Variables est�ticas
		
	protected Bitmap bitmap;
	
	protected Paint paint;
	
	protected int alpha;
	
	// --------------------------------------------- Variables de instancia
	
	float longevity = DEFAULT_LONGEVITY;
	
	float lifeTime = longevity;
	
	// --------------------------------------------------- Inicializaci�n est�tica
	
	static  {
		
		DEBRIS_FILTER  = new Filter();
		
		DEBRIS_FILTER.categoryBits = DebrisActor.DEBRIS_FILTER_BIT;
		
		DEBRIS_FILTER.maskBits     = WallActor.WALL_FILTER_BIT   | 
									 WallActor.FLOOR_FILTER_BIT  |
									 DebrisActor.DEBRIS_FILTER_BIT;
		
	}
	
	// ---------------------------------------------------------- Constructor
	
	public DebrisActor(JumplingsWorld jWorld, Body body, Bitmap bitmap) {
		this(jWorld, body);
		this.bitmap = bitmap;
		this.paint = new Paint();
	}
	
	public DebrisActor(JumplingsWorld jWorld, Body body) {
		super(jWorld, Z_INDEX);
		// el cuerpo viene del actor enemigo
		this.mainBody = body;
		initPhysics(null);
	}

	// ------------------------------------------------------------- M�todos

	@Override
	protected void initBodies(PointF worldPos) {
		addBody(mainBody);
		
		// se cambia el filtro
		ArrayList<Fixture> fs = mainBody.getFixtureList();
		int l2 = fs.size();
		for (int i2 = 0; i2 < l2; i2++) {
			fs.get(i2).setFilterData(DEBRIS_FILTER);
		}
	}
	
	protected void drawBitmaps(Canvas canvas) {
		paint.setAlpha(alpha);
		jWorld.drawBitmap(canvas, mainBody, bitmap, paint);
	}
	
	@Override
	public void doLogic(float gameTimeStep) {
		lifeTime = Math.max(0, lifeTime - gameTimeStep);
		if (lifeTime <= 0) {
			gameWorld.removeActor(this);
		}
		alpha = (int) (255 * lifeTime / longevity);
	}

}

