package net.garrapeta.jumplings.actor;

import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

public class RoundEnemyActor extends EnemyActor { 
	
	
	// ---------------------------------------------------- Constantes
	
	public  final static float  DEFAULT_RADIUS = BASE_RADIUS * 1f;
	
	public final static short JUMPER_CODE_SIMPLE    	   = 0;
	
	// ------------------------------------------------------ Variables est�ticas
	
	// vivo
	protected final static Bitmap BMP_RED_BODY;
	
	protected final static Bitmap BMP_RED_FOOT_RIGHT;
	protected final static Bitmap BMP_RED_FOOT_LEFT;
	
	protected final static Bitmap BMP_RED_HAND_RIGHT;
	protected final static Bitmap BMP_RED_HAND_LEFT;
	
	// debris
	protected final static Bitmap BMP_DEBRIS_RED_BODY;
	
	protected final static Bitmap BMP_DEBRIS_RED_FOOT_RIGHT;
	protected final static Bitmap BMP_DEBRIS_RED_FOOT_LEFT;
	
	protected final static Bitmap BMP_DEBRIS_RED_HAND_RIGHT;
	protected final static Bitmap BMP_DEBRIS_RED_HAND_LEFT;
	
	// ---------------------------------------------------- Variables

	// --------------------------------------------------------- Inicializaci�n est�tica
	
	static {
		Resources r = JumplingsApplication.getInstance().getResources();
		
		// Vivo
		BMP_RED_BODY		= BitmapFactory.decodeResource(r, R.drawable.red_body);
		
		BMP_RED_FOOT_RIGHT	= BitmapFactory.decodeResource(r, R.drawable.red_foot_right);
		BMP_RED_FOOT_LEFT	= BitmapFactory.decodeResource(r, R.drawable.red_foot_left);
		
		BMP_RED_HAND_RIGHT	= BitmapFactory.decodeResource(r, R.drawable.red_hand_right);
		BMP_RED_HAND_LEFT	= BitmapFactory.decodeResource(r, R.drawable.red_hand_left);
		
		// Debris
		BMP_DEBRIS_RED_BODY			= BitmapFactory.decodeResource(r, R.drawable.red_debris_body);
		
		BMP_DEBRIS_RED_FOOT_RIGHT	= BitmapFactory.decodeResource(r, R.drawable.red_debris_foot_right);
		BMP_DEBRIS_RED_FOOT_LEFT	= BitmapFactory.decodeResource(r, R.drawable.red_debris_foot_left);
		
		BMP_DEBRIS_RED_HAND_RIGHT	= BitmapFactory.decodeResource(r, R.drawable.red_debris_hand_right);
		BMP_DEBRIS_RED_HAND_LEFT	= BitmapFactory.decodeResource(r, R.drawable.red_debris_hand_left);

	}		
	
	// ---------------------------------------------------------------- Constructor
	
	public RoundEnemyActor(JumplingsGameWorld jgWorld, PointF worldPos) {
		super(jgWorld, worldPos);
		
		this.code = RoundEnemyActor.JUMPER_CODE_SIMPLE;
		
		this.radius = RoundEnemyActor.DEFAULT_RADIUS;
		initPhysics(worldPos);
		
		// vivo
		ah.initBitmaps(BMP_RED_BODY, 
				       BMP_RED_FOOT_RIGHT,
				       BMP_RED_FOOT_LEFT,
				       BMP_RED_HAND_RIGHT,
				       BMP_RED_HAND_LEFT,
				       BMP_EYE_0_RIGHT,
				       BMP_EYE_0_LEFT);
		
		// debris
		bmpDebrisBody			= BMP_DEBRIS_RED_BODY;
		
		bmpDebrisFootRight		= BMP_DEBRIS_RED_FOOT_RIGHT;
		bmpDebrisFootLeft		= BMP_DEBRIS_RED_FOOT_LEFT;
		
		bmpDebrisHandRight		= BMP_DEBRIS_RED_HAND_RIGHT;
		bmpDebrisHandLeft		= BMP_DEBRIS_RED_HAND_LEFT;
		
		bmpDebrisEyeRight		= BMP_DEBRIS_EYE_0_RIGHT;
		bmpDebrisEyeLeft		= BMP_DEBRIS_EYE_0_LEFT;
	}
	
	// ------------------------------------------------------ M�todos heredados
	

	// -------------------------------------------------------- M�todos Propios
	
	@Override
	protected void initBodies(PointF worldPos) {
	
		// Cuerpo
		{
			// Create Shape with Properties
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(radius);
			mainBody = jgWorld.createBody(this, worldPos, true);
			mainBody.setBullet(true);
			
			// Assign shape to Body
			Fixture f = mainBody.createFixture(circleShape, 1.0f);
			f.setFilterData(CONTACT_FILTER);
			circleShape.dispose();
			
		}
		
		ah.createLimbs(worldPos, radius);
	}



}
