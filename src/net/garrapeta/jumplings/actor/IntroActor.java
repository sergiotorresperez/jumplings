package net.garrapeta.jumplings.actor;

import net.garrapeta.jumplings.R;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsWorld;
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
	
	
	// ------------------------------------------------- Variables est�ticas
	
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
	
	// ----------------------------------------------- Inicializaci�n est�tica
	
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
	
	// ---------------------------------------------------- M�todos est�ticos
	
	
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
			f.setFilterData(CONTACT_FILTER);
			circleShape.dispose();
			
		}
		
		ah.createLimbs(worldPos, radius);		
	}

	@Override
	public void doLogic(float gameTimeStep) {

	}
	
	@Override
	public final void drawShapes(Canvas canvas) {
		ah.drawShapes(canvas);
	}

	@Override
	protected final void drawBitmaps(Canvas canvas) {
		ah.drawBitmaps(canvas);
	}
	
}
