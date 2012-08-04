package net.garrapeta.demo.actor;

import net.garrapeta.gameengine.Viewport;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

public class AnthropomorphicHelper {
	// ---------------------------------------------------- Constantes

	protected static float feetRatio = 2.2f;
	protected static float feetJointFrequencyHz  = 2.0f;
	protected static float feetJointdampingRatio = 0.7f;
	
	protected static float handRatio = 4;
	protected static float handJointFrequencyHz  = 20.0f;
	protected static float handJointdampingRatio = 0.9f;
	
	protected static float eyeRatio = 3;
	protected static float eyeJointFrequencyHz  = 2.0f;
	protected static float eyeJointdampingRatio = 0.6f;
	
	protected static float mouthJointFrequencyHz  = 2.0f;
	protected static float mouthJointdampingRatio = 1f;
	
	public static final int EYE_STROKE_COLOR = Color.BLACK; 
	public static final int EYE_FILL_COLOR   = Color.WHITE;
	public static final int EYE_LINE_COLOR   = Color.TRANSPARENT;
	

	protected final static float LIMBS_DENSITY = 1.4f;
	
	
	// ------------------------------------------- Variables de instancia
	
	private JumplingActor actor;
	
	// Cuerpos
	Body rightFootBody;
	Body leftFootBody;
	
	Body rightHandBody;
	Body leftHandBody;
	
	Body rightEyeBody;
	Body leftEyeBody;
	
	Body mouthBody;
	
	// Bitmaps del actor vivo
	protected Bitmap bmpBody;
	
	protected Bitmap bmpFootRight;
	protected Bitmap bmpFootLeft;
	
	protected Bitmap bmpHandRight;
	protected Bitmap bmpHandLeft;
	
	protected Bitmap bmpEyeRight;
	protected Bitmap bmpEyeLeft;
	
	
	
	// ----------------------------------------------- Constructor
	
	public AnthropomorphicHelper(JumplingActor actor) {
		this.actor = actor;
	}
	
	// ---------------------------------------------- Métodos
	
	public void initBitmaps(Bitmap bmpBody,
							Bitmap bmpFootRight,
							Bitmap bmpFootLeft,
							Bitmap bmpHandRight,
							Bitmap bmpHandLeft,
							Bitmap bmpEyeRight,
							Bitmap bmpEyeLeft) {

		this.bmpBody = bmpBody;
		this.bmpFootRight = bmpFootRight;
		this.bmpFootLeft = bmpFootLeft;
		this.bmpHandRight = bmpHandRight;
		this.bmpHandLeft = bmpHandLeft;
		this.bmpEyeRight = bmpEyeRight;
		this.bmpEyeLeft = bmpEyeLeft;
	}
	
	protected void createLimbs(PointF worldPos, float radius) {
		float feetWorldRadius 	   = radius / feetRatio;
		
		// Pie izquierdo
		{
			// Create Shape with Properties
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(feetWorldRadius);
			
			PointF anchor = new PointF(actor.mainBody.getWorldCenter().x - feetWorldRadius - 0, 
					                   actor.mainBody.getWorldCenter().y - radius - feetWorldRadius);
			
			leftFootBody = actor.jWorld.createBody(actor, anchor, true);
			leftFootBody.setBullet(false);
			// Assign shape to Body
			Fixture f = leftFootBody.createFixture(circleShape, LIMBS_DENSITY);
			f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
			circleShape.dispose();
			
			DistanceJointDef jointDef = new DistanceJointDef();
			
			jointDef.frequencyHz  = feetJointFrequencyHz;
			jointDef.dampingRatio = feetJointdampingRatio;
			
//			jointDef.collideConnected = true;
			
			jointDef.initialize(actor.mainBody, 
								leftFootBody, 
								Viewport.pointFToVector2(anchor),					            
								Viewport.pointFToVector2(anchor));
			
			actor.jWorld.createJoint(actor, jointDef);
		}
		
		// Pie derecho
		{
			// Create Shape with Properties
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(feetWorldRadius);
			
			PointF anchor = new PointF(actor.mainBody.getWorldCenter().x + feetWorldRadius - 0, 
					                   actor.mainBody.getWorldCenter().y - radius - feetWorldRadius);
			
			rightFootBody = actor.jWorld.createBody(actor, anchor, true);
			// Assign shape to Body
			Fixture f = rightFootBody.createFixture(circleShape, LIMBS_DENSITY);
			f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
			circleShape.dispose();
			
			
			DistanceJointDef jointDef = new DistanceJointDef();
			
			jointDef.frequencyHz  = feetJointFrequencyHz;
			jointDef.dampingRatio = feetJointdampingRatio;
			
//			jointDef.collideConnected = true;
			
			jointDef.initialize(actor.mainBody, 
								rightFootBody, 
								Viewport.pointFToVector2(anchor),					            
								Viewport.pointFToVector2(anchor));
			
			actor.jWorld.createJoint(actor, jointDef);
		}
		
		float handWorldRadius 	   = radius / handRatio;
		
		// Mano izquierda
		{
			// Create Shape with Properties
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(handWorldRadius);
			
			PointF anchor = new PointF(actor.mainBody.getWorldCenter().x - radius - handWorldRadius - 0, 
					                   actor.mainBody.getWorldCenter().y - handWorldRadius);
			
			leftHandBody = actor.jWorld.createBody(actor, anchor, true);
			leftHandBody.setBullet(false);
			// Assign shape to Body
			Fixture f = leftHandBody.createFixture(circleShape, LIMBS_DENSITY);
			f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
			circleShape.dispose();
			
			
			DistanceJointDef jointDef = new DistanceJointDef();
			
			jointDef.frequencyHz  = handJointFrequencyHz;
			jointDef.dampingRatio = handJointdampingRatio;
			
			jointDef.initialize(actor.mainBody, 
							    leftHandBody, 
								Viewport.pointFToVector2(anchor),					            
								Viewport.pointFToVector2(anchor));
			
			actor.jWorld.createJoint(actor, jointDef);
		}
		
		// Mano derecha
		{
			// Create Shape with Properties
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(handWorldRadius);
			
			PointF anchor = new PointF(actor.mainBody.getWorldCenter().x + radius + handWorldRadius, 
					                   actor.mainBody.getWorldCenter().y - handWorldRadius);
			
			rightHandBody = actor.jWorld.createBody(actor, anchor, true);
			rightHandBody.setBullet(false);
			// Assign shape to Body
			Fixture f = rightHandBody.createFixture(circleShape, LIMBS_DENSITY);
			f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
			circleShape.dispose();
			
			
			DistanceJointDef jointDef = new DistanceJointDef();
			
			jointDef.frequencyHz  = handJointFrequencyHz;
			jointDef.dampingRatio = handJointdampingRatio;
			
			jointDef.initialize(actor.mainBody, 
							    rightHandBody, 
								Viewport.pointFToVector2(anchor),					            
								Viewport.pointFToVector2(anchor));
			
			actor.jWorld.createJoint(actor, jointDef);
		}
		
		
		float eyeWorldRadius 	   = radius / eyeRatio;
		
		// Ojo izquierdo
		{
			// Create Shape with Properties
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(eyeWorldRadius);
			
			PointF anchor = new PointF(actor.mainBody.getWorldCenter().x - eyeWorldRadius * 1.5f, 
					                   actor.mainBody.getWorldCenter().y + eyeWorldRadius);
			
			leftEyeBody = actor.jWorld.createBody(actor, anchor, true);
			leftEyeBody.setBullet(false);
			// Assign shape to Body
			Fixture f = leftEyeBody.createFixture(circleShape, LIMBS_DENSITY);
			f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
			circleShape.dispose();
			
			DistanceJointDef jointDef = new DistanceJointDef();
			
			jointDef.frequencyHz  = eyeJointFrequencyHz;
			jointDef.dampingRatio = eyeJointdampingRatio;
			
			jointDef.initialize(actor.mainBody, 
								leftEyeBody, 
								Viewport.pointFToVector2(anchor),					            
								Viewport.pointFToVector2(anchor));
			
			actor.jWorld.createJoint(actor, jointDef);
		}
		
		// Ojo derecho
		{
			// Create Shape with Properties
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(eyeWorldRadius);
			
			PointF anchor = new PointF(actor.mainBody.getWorldCenter().x + eyeWorldRadius * 1.5f, 
					                   actor.mainBody.getWorldCenter().y + eyeWorldRadius);
			
			rightEyeBody = actor.jWorld.createBody(actor, anchor, true);
			rightEyeBody.setBullet(false);
			// Assign shape to Body
			Fixture f = rightEyeBody.createFixture(circleShape, LIMBS_DENSITY);
			f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
			circleShape.dispose();
						
			DistanceJointDef jointDef = new DistanceJointDef();
			
			jointDef.frequencyHz  = eyeJointFrequencyHz;
			jointDef.dampingRatio = eyeJointdampingRatio;
			
			jointDef.initialize(actor.mainBody, 
								rightEyeBody, 
								Viewport.pointFToVector2(anchor),					            
								Viewport.pointFToVector2(anchor));
			
			actor.jWorld.createJoint(actor, jointDef);
		}
		
	}
	
	public void draw(Canvas canvas) {
		if (bmpBody == null) {
			actor.drawShapes(canvas);
		} else {
			actor.jWorld.drawBitmap(canvas, this.leftHandBody, 	bmpHandLeft);
			actor.jWorld.drawBitmap(canvas, this.leftFootBody, 	bmpFootLeft);
			
			actor.jWorld.drawBitmap(canvas, this.rightHandBody,	bmpHandRight);
			actor.jWorld.drawBitmap(canvas, this.rightFootBody, bmpFootRight);
			
			actor.jWorld.drawBitmap(canvas, actor.mainBody, 		bmpBody);
			
			actor.jWorld.drawBitmap(canvas, this.leftEyeBody, 	bmpEyeLeft);
			actor.jWorld.drawBitmap(canvas, this.rightEyeBody, 	bmpEyeRight);
			
		}
	}
}
