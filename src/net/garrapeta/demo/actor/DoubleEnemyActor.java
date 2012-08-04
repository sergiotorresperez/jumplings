package net.garrapeta.demo.actor;

import net.garrapeta.demo.JumplingsApplication;
import net.garrapeta.demo.JumplingsGameWorld;
import net.garrapeta.demo.R;
import net.garrapeta.gameengine.Viewport;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class DoubleEnemyActor extends EnemyActor {

	// --------------------------------------------------------------- Constantes
	
	public final static float  DEFAULT_RADIUS = BASE_RADIUS * 1.1f;
	
	public final static float  HEIGHT_RESTORATION_FACTOR = 1f / 3f;
		
	public final static short JUMPER_CODE_DOUBLE   	   = 1;
	
	// ------------------------------------------------------ Variables estáticas
	
	// vivo
	protected final static Bitmap BMP_ORANGE_BODY;
	
	protected final static Bitmap BMP_ORANGE_FOOT_RIGHT;
	protected final static Bitmap BMP_ORANGE_FOOT_LEFT;
	
	protected final static Bitmap BMP_ORANGE_HAND_RIGHT;
	protected final static Bitmap BMP_ORANGE_HAND_LEFT;
	
	// debris
	protected final static Bitmap BMP_DEBRIS_ORANGE_BODY;
	
	protected final static Bitmap BMP_DEBRIS_ORANGE_FOOT_RIGHT;
	protected final static Bitmap BMP_DEBRIS_ORANGE_FOOT_LEFT;
	
	protected final static Bitmap BMP_DEBRIS_ORANGE_HAND_RIGHT;
	protected final static Bitmap BMP_DEBRIS_ORANGE_HAND_LEFT;
	
	// ---------------------------------------------------------------- Variables
	

	// ------------------------------------------------------ Métodos estáticos
	
	static {
		Resources r = JumplingsApplication.getInstance().getResources();
		
		// Alive
		BMP_ORANGE_BODY					= BitmapFactory.decodeResource(r, R.drawable.orange_double_body);
		
		BMP_ORANGE_FOOT_RIGHT			= BitmapFactory.decodeResource(r, R.drawable.orange_foot_right);
		BMP_ORANGE_FOOT_LEFT			= BitmapFactory.decodeResource(r, R.drawable.orange_foot_left);
		
		BMP_ORANGE_HAND_RIGHT			= BitmapFactory.decodeResource(r, R.drawable.orange_hand_right);
		BMP_ORANGE_HAND_LEFT			= BitmapFactory.decodeResource(r, R.drawable.orange_hand_left);
		
		// Debris
		BMP_DEBRIS_ORANGE_BODY			= BitmapFactory.decodeResource(r, R.drawable.orange_debris_double_body);
		
		BMP_DEBRIS_ORANGE_FOOT_RIGHT	= BitmapFactory.decodeResource(r, R.drawable.orange_debris_foot_right);
		BMP_DEBRIS_ORANGE_FOOT_LEFT		= BitmapFactory.decodeResource(r, R.drawable.orange_debris_foot_left);
		
		BMP_DEBRIS_ORANGE_HAND_RIGHT	= BitmapFactory.decodeResource(r, R.drawable.orange_debris_hand_right);
		BMP_DEBRIS_ORANGE_HAND_LEFT		= BitmapFactory.decodeResource(r, R.drawable.orange_debris_hand_left);
		
	}		
		
	static double getDoubleEnemyActorHitCount() {
		return 2;
	}
	
	// --------------------------------------------------------------- Constructor

	/**
	 * @param gameWorld
	 */
	public DoubleEnemyActor(JumplingsGameWorld jgWorld, PointF worldPos) {
		super(jgWorld, worldPos);
		
		this.code = DoubleEnemyActor.JUMPER_CODE_DOUBLE;
		
		this.radius = DoubleEnemyActor.DEFAULT_RADIUS;
		
		initPhysics(worldPos);
		
		// vivo
		ah.initBitmaps(BMP_ORANGE_BODY, 
			       	   BMP_ORANGE_FOOT_RIGHT,
			           BMP_ORANGE_FOOT_LEFT,
			           BMP_ORANGE_HAND_RIGHT,
			           BMP_ORANGE_HAND_LEFT,
			           BMP_EYE_0_RIGHT,
			           BMP_EYE_0_LEFT);

		// debris
		bmpDebrisBody			= BMP_DEBRIS_ORANGE_BODY;
		
		bmpDebrisFootRight		= BMP_DEBRIS_ORANGE_FOOT_RIGHT;
		bmpDebrisFootLeft		= BMP_DEBRIS_ORANGE_FOOT_LEFT;
		
		bmpDebrisHandRight		= BMP_DEBRIS_ORANGE_HAND_RIGHT;
		bmpDebrisHandLeft		= BMP_DEBRIS_ORANGE_HAND_LEFT;
		
		bmpDebrisEyeRight		= BMP_DEBRIS_EYE_0_RIGHT;
		bmpDebrisEyeLeft		= BMP_DEBRIS_EYE_0_LEFT;
	}
	
	@Override
	protected void initBodies(PointF worldPos) {
		// Cuerpo
		{
			// Create Shape with Properties
			PolygonShape polygonShape = new PolygonShape();
			Vector2[] vertices = new Vector2[] {
									new Vector2(0, radius),
									new Vector2(- radius , 0),
									new Vector2(0, -radius),
									new Vector2(radius, 0)
								};
			polygonShape.set(vertices);
			
			mainBody = jgWorld.createBody(this, worldPos, true);
			mainBody.setBullet(true);
			
			// Assign shape to Body
			Fixture f = mainBody.createFixture(polygonShape, 1.0f);
			f.setFilterData(CONTACT_FILTER);
			polygonShape.dispose();
			
		}
		
		ah.createLimbs(worldPos, radius);
	}
	
	// -------------------------------------------------------- Métodos propios
	
	private final float getRestorationInitVy(float posY) {
		float maxHeight = posY + HEIGHT_RESTORATION_FACTOR * (jgWorld.worldBoundaries.top -  posY);
		return (float) getInitialYVelocity(maxHeight);
	}
	

	@Override
	public void onHitted() {
		
		EnemyActor son = null;
		float xVel = 0;
		Vector2 pos = null;
	
		
		pos = mainBody.getWorldCenter();
		son = new DoubleSonEnemyActor(jgWorld, Viewport.vector2ToPointF(pos)); 
		
		xVel = mainBody.getLinearVelocity().x;
				
		jgWorld.addActor(son);

		float yVel = getRestorationInitVy(pos.y);
		son.setLinearVelocity(xVel / 2, yVel);
			
		
		super.onHitted();
	}



}
