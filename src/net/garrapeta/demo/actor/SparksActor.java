package net.garrapeta.demo.actor;

import net.garrapeta.demo.JumplingsApplication;
import net.garrapeta.demo.JumplingsWorld;
import net.garrapeta.demo.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class SparksActor extends JumplingActor {

	// ----------------------------------------------------------- Constantes
	public  final static float  DEFAULT_RADIUS = BASE_RADIUS * 1.2f;
	
	/**
	 *  Z-Index del actor
	 */
	public final static int Z_INDEX = 0;
	

	public  final static int    SPARKS_FILTER_BIT     = 0x00020;
	
	private final static Filter SPARKS_FILTER;
	
	// ------------------------------------------------- Variables est�ticas
	
	// Vivo
	protected final static Bitmap[] bmpsSparkles;
	

	// ----------------------------------------------- Variables de instancia
	
	float longevity;
	
	float lifeTime;
	
	protected int alpha;
	
	protected Bitmap bmpSparkle;
	
	protected Paint paint;
	
	// ----------------------------------------------- Inicializaci�n est�tica
	
	static {
		SPARKS_FILTER = new Filter();
		
		SPARKS_FILTER.categoryBits = SPARKS_FILTER_BIT;
		
		SPARKS_FILTER.maskBits     = SPARKS_FILTER_BIT   | 
				 					 WallActor.WALL_FILTER_BIT;
		
		Resources r = JumplingsApplication.getInstance().getResources();

		// vivo
		bmpsSparkles     = new Bitmap[4];
		bmpsSparkles[0]	= BitmapFactory.decodeResource(r, R.drawable.sparks_big_0);
		bmpsSparkles[1]	= BitmapFactory.decodeResource(r, R.drawable.sparks_big_1);
		bmpsSparkles[2]	= BitmapFactory.decodeResource(r, R.drawable.sparks_big_2);
		bmpsSparkles[3]	= BitmapFactory.decodeResource(r, R.drawable.sparks_big_3);
		
	}
	
	// ---------------------------------------------------- M�todos est�ticos
	
	
	// --------------------------------------------------- Constructor
	
	public SparksActor(JumplingsWorld jWorld, PointF worldPos, int longevity) {
		super(jWorld, Z_INDEX);
		
		this.longevity = this.lifeTime = longevity;
		
		radius = SparksActor.DEFAULT_RADIUS;
		
		bmpSparkle = bmpsSparkles[(int) (Math.random() * bmpsSparkles.length)];
		
		this.paint = new Paint();
		
		initPhysics(worldPos);		
	}


	// --------------------------------------------- M�todos heredados
	
	@Override
	protected void initBodies(PointF worldPos) {
	
		// Cuerpo
		{
			// Create Shape with Properties
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(radius);
			mainBody = jWorld.createBody(this, worldPos, true);
			mainBody.setBullet(true);
			
			// Assign shape to Body
			Fixture f = mainBody.createFixture(circleShape, 1.0f);
			f.setFilterData(SPARKS_FILTER);
			circleShape.dispose();
		}
		
	}

	@Override
	public void doLogic(float gameTimeStep) {
		lifeTime = Math.max(0, lifeTime - gameTimeStep);
		if (lifeTime <= 0) {
			gameWorld.removeActor(this);
		}
		alpha = (int) (255 * lifeTime / longevity);
	}
	
	
	@Override
	protected void drawBitmaps(Canvas canvas) {
		paint.setAlpha(alpha);
		jWorld.drawBitmap(canvas, this.mainBody, bmpSparkle, paint);
	}

	
}
