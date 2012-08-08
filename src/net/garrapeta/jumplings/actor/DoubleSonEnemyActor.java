package net.garrapeta.jumplings.actor;

import net.garrapeta.jumplings.R;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

public class DoubleSonEnemyActor extends EnemyActor { 
	
	
	// ---------------------------------------------------- Constantes
	
	public  final static float  DEFAULT_RADIUS = BASE_RADIUS * 1f;
	
	public final static short JUMPER_CODE_DOUBLE_SON   	   = 2;
	
	// ------------------------------------------------------ Variables est�ticas
	
	// vivo
	protected final static Bitmap BMP_ORANGE_SIMPLE_BODY;
	
	// debris
	protected final static Bitmap BMP_DEBRIS_ORANGE_SIMPLE_BODY;

	
	// ---------------------------------------------------- Variables

	// --------------------------------------------------- Inicializaci�n est�tica
	
	// --------------------------------------------------------- Inicializaci�n est�tica
	
	static {
		Resources r = JumplingsApplication.getInstance().getResources();
		
		// Vivo
		BMP_ORANGE_SIMPLE_BODY			= BitmapFactory.decodeResource(r, R.drawable.orange_simple_body);
		
		// Debris
		BMP_DEBRIS_ORANGE_SIMPLE_BODY	= BitmapFactory.decodeResource(r, R.drawable.orange_debris_simple_body);

	}		
	
	// ---------------------------------------------------------------- Constructor
	
	public DoubleSonEnemyActor(JumplingsGameWorld jgWorld, PointF worldPos) {
		super(jgWorld, worldPos);
		this.code = DoubleSonEnemyActor.JUMPER_CODE_DOUBLE_SON;
		
		this.radius = DoubleSonEnemyActor.DEFAULT_RADIUS;
	
		initPhysics(worldPos);
		
		// vivo
		ah.initBitmaps(BMP_ORANGE_SIMPLE_BODY, 
				       DoubleEnemyActor.BMP_ORANGE_FOOT_RIGHT,
				       DoubleEnemyActor.BMP_ORANGE_FOOT_LEFT,
				       DoubleEnemyActor.BMP_ORANGE_HAND_RIGHT,
				       DoubleEnemyActor.BMP_ORANGE_HAND_LEFT,
		               BMP_EYE_0_RIGHT,
		               BMP_EYE_0_LEFT);
		
		// debris
		bmpDebrisBody			= BMP_DEBRIS_ORANGE_SIMPLE_BODY;
		
		bmpDebrisFootRight		= DoubleEnemyActor.BMP_DEBRIS_ORANGE_FOOT_RIGHT;
		bmpDebrisFootLeft		= DoubleEnemyActor.BMP_DEBRIS_ORANGE_FOOT_LEFT;
		
		bmpDebrisHandRight		= DoubleEnemyActor.BMP_DEBRIS_ORANGE_HAND_RIGHT;
		bmpDebrisHandLeft		= DoubleEnemyActor.BMP_DEBRIS_ORANGE_HAND_LEFT;
		
		bmpDebrisEyeRight		= BMP_DEBRIS_EYE_0_RIGHT;
		bmpDebrisEyeLeft		= BMP_DEBRIS_EYE_0_LEFT;
	}
	
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
