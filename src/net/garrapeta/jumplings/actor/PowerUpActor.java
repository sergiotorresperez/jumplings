package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.jumplings.R;
import net.garrapeta.gameengine.Viewport;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;


public abstract class PowerUpActor extends MainActor {

	// ----------------------------------------------------------- Constantes
	public  final static float  DEFAULT_RADIUS = BASE_RADIUS * 1.05f;
	
	// ------------------------------------------------- Variables est�ticas
	
	// vivo
	protected final static Bitmap BMP_POWERUP_BG;
	
	// debris
	protected final static Bitmap BMP_DEBRIS_POWERUP_BG;
	
	// ---------------------------------------------- Variables de instancia
	
	protected Body iconBody;
	
	// Bitmaps del actor vivo
	protected Bitmap bmpBg;
	protected Bitmap bmpIcon;
	
	// Bitmaps del actor muerto (debris)
	protected Bitmap bmpDebrisBg;
	protected Bitmap bmpDebrisIcon;
	
	
	// --------------------------------------------------- Constructor
	
	public PowerUpActor(JumplingsGameWorld jgWorld, PointF worldPos) {
		super(jgWorld, worldPos, Z_INDEX);
		
		initPhysics(worldPos);
		
		this.radius = BladePowerUpActor.DEFAULT_RADIUS;
		
		// vivo
		bmpBg 	  	= BMP_POWERUP_BG;
		
		// debris
		bmpDebrisBg = BMP_DEBRIS_POWERUP_BG;
	}

	// ----------------------------------------------- Inicializaci�n est�tica
	
	static {
		
		Resources r = JumplingsApplication.getInstance().getResources();

		// vivo
		BMP_POWERUP_BG			= BitmapFactory.decodeResource(r, R.drawable.powerup_bg);
		
		// muerto
		BMP_DEBRIS_POWERUP_BG	= BitmapFactory.decodeResource(r, R.drawable.powerup_debris_bg);
	}
	// --------------------------------------------- M�todos heredados
	
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
		
		// Icon
		{
			// Create Shape with Properties
			PolygonShape polygonShape = new PolygonShape();
			polygonShape.setAsBox(radius, radius);
			PointF pos = new PointF(worldPos.x, worldPos.y);
			iconBody = jgWorld.createBody(this, pos, true);
			iconBody.setBullet(false);
			
			// Assign shape to Body
			Fixture f = iconBody.createFixture(polygonShape, 1.0f);
			f.setRestitution(AUX_BODIES_RESTITUTION);
			f.setFilterData(NO_CONTACT_FILTER);
			polygonShape.dispose();
			
			// Uni�n
			WeldJointDef jointDef = new WeldJointDef();

			
			jointDef.initialize(mainBody, 
					            iconBody,					            
								Viewport.pointFToVector2(pos));
			
			jgWorld.createJoint(this, jointDef);
		}
	}
	
	@Override
	protected ArrayList<JumplingActor> getDebrisBodies() {
		ArrayList<JumplingActor> debrisActors =  new ArrayList<JumplingActor>();
		
		// Main Body
		{
			Body body = mainBody;
			DebrisActor debrisActor = new DebrisActor(jgWorld,  body, bmpDebrisBg); 
			
			gameWorld.addActor(debrisActor);
			debrisActors.add(debrisActor);
		}
		
		// Icon
		{
			Body body = iconBody;
			DebrisActor debrisActor = new DebrisActor(jgWorld,  body, bmpDebrisIcon); 

			gameWorld.addActor(debrisActor);
			debrisActors.add(debrisActor);
		}
		
		return debrisActors;
	}

	@Override
	public void doLogic(float gameTimeStep) {
	}
	
	@Override
	protected void drawBitmaps(Canvas canvas) {
		jgWorld.drawBitmap(canvas, this.mainBody, 		bmpBg);
		jgWorld.drawBitmap(canvas, this.iconBody, 		bmpIcon);
	}
	
	// ------------------------------------------------ M�todos propios

}
