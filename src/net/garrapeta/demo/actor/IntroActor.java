package net.garrapeta.demo.actor;

import net.garrapeta.demo.JumplingsApplication;
import net.garrapeta.demo.JumplingsWorld;
import net.garrapeta.demo.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

public class IntroActor extends JumplingActor {

	// ----------------------------------------------------------- Constantes
	public  final static float  DEFAULT_RADIUS = BASE_RADIUS * 3f;
	
	/**
	 *  Z-Index del actor
	 */
	public final static int Z_INDEX = 0;
	
	
	// ------------------------------------------------- Variables estáticas
	
	// vivo
	protected final static Bitmap BMP_INTRO_BODY;
	
	protected final static Bitmap BMP_INTRO_FOOT_RIGHT;
	protected final static Bitmap BMP_INTRO_FOOT_LEFT;
	
	protected final static Bitmap BMP_INTRO_HAND_RIGHT;
	protected final static Bitmap BMP_INTRO_HAND_LEFT;
	
	protected final static Bitmap BMP_INTRO_EYE_RIGHT;
	protected final static Bitmap BMP_INTRO_EYE_LEFT;

	// ----------------------------------------------- Variables de instancia
	
	AnthropomorphicHelper ah;
	
	// Bitmaps del actor vivo
	protected Bitmap bmpBody;
	
	protected Bitmap bmpFootRight;
	protected Bitmap bmpFootLeft;
	
	protected Bitmap bmpHandRight;
	protected Bitmap bmpHandLeft;
	
	protected Bitmap bmpEyeRight;
	protected Bitmap bmpEyeLeft;
	
	// ----------------------------------------------- Inicialización estática
	
	static {
	
		Resources r = JumplingsApplication.getInstance().getResources();
		
		// Vivo
		BMP_INTRO_BODY		= BitmapFactory.decodeResource(r, R.drawable.intro_body);
		
		BMP_INTRO_FOOT_RIGHT	= BitmapFactory.decodeResource(r, R.drawable.intro_foot_right);
		BMP_INTRO_FOOT_LEFT	= BitmapFactory.decodeResource(r, R.drawable.intro_foot_left);
		
		BMP_INTRO_HAND_RIGHT	= BitmapFactory.decodeResource(r, R.drawable.intro_hand_right);
		BMP_INTRO_HAND_LEFT	= BitmapFactory.decodeResource(r, R.drawable.intro_hand_left);
		
		BMP_INTRO_EYE_RIGHT	= BitmapFactory.decodeResource(r, R.drawable.intro_eye_right);
		BMP_INTRO_EYE_LEFT	= BitmapFactory.decodeResource(r, R.drawable.intro_eye_left);
		
	}
	
	// ---------------------------------------------------- Métodos estáticos
	
	
	// --------------------------------------------------- Constructor
	
	public IntroActor(JumplingsWorld jWorld, PointF worldPos) {
		super(jWorld, Z_INDEX);
		radius = IntroActor.DEFAULT_RADIUS;
		
		ah = new AnthropomorphicHelper(this);		
		
		initPhysics(worldPos);
		
		// vivo
		ah.initBitmaps(BMP_INTRO_BODY, 
				       BMP_INTRO_FOOT_RIGHT,
				       BMP_INTRO_FOOT_LEFT,
				       BMP_INTRO_HAND_RIGHT,
				       BMP_INTRO_HAND_LEFT,
				       BMP_INTRO_EYE_RIGHT,
				       BMP_INTRO_EYE_LEFT);
	}


	// --------------------------------------------- Métodos heredados
	
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
			f.setFilterData(CONTACT_FILTER);
			circleShape.dispose();
			
		}
		
		ah.createLimbs(worldPos, radius);		
	}

	@Override
	public void doLogic(float gameTimeStep) {

	}
	
	
	public void draw(Canvas canvas) {
		ah.draw(canvas);
	}
	
}
