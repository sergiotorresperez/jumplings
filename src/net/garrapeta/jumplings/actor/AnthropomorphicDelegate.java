package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.module.BitmapManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

/**
 * Common implementation of those aspects of the anthropomorphic creatures,, to let objects
 * implementing that interface delegate in this.
 * 
 * @author garrapeta
 * 
 */
public class AnthropomorphicDelegate {
    // ---------------------------------------------------- Constantes

    protected static float feetRatio = 2.2f;
    protected static float feetJointFrequencyHz = 2.0f;
    protected static float feetJointdampingRatio = 0.7f;

    protected static float handRatio = 4;
    protected static float handJointFrequencyHz = 20.0f;
    protected static float handJointdampingRatio = 0.9f;

    protected static float eyeRatio = 3;
    protected static float eyeJointFrequencyHz = 2.0f;
    protected static float eyeJointdampingRatio = 0.6f;

    protected static float mouthJointFrequencyHz = 2.0f;
    protected static float mouthJointdampingRatio = 1f;

    public static final int EYE_STROKE_COLOR = Color.BLACK;
    public static final int EYE_FILL_COLOR = Color.WHITE;
    public static final int EYE_LINE_COLOR = Color.TRANSPARENT;

    protected final static float LIMBS_DENSITY = 1.4f;

    // ------------------------------------------- Variables de instancia

    private JumplingActor mActor;

    // Cuerpos
    Body rightFootBody;
    Body leftFootBody;

    Body rightHandBody;
    Body leftHandBody;

    Body rightEyeBody;
    Body leftEyeBody;

    Body mouthBody;

    // Bitmaps del actor vivo
    private Bitmap mBmpBody;

    private Bitmap mBmpFootRight;
    private Bitmap mBmpFootLeft;

    private Bitmap mBmpHandRight;
    private Bitmap mBmpHandLeft;

    private Bitmap mBmpEyeRightOpened;
    private Bitmap mBmpEyeLeftOpened;

    private Bitmap mBmpEyeRightClosed;
    private Bitmap mBmpEyeLeftClosed;

    private boolean mAreEyesOpened = true;

    // ----------------------------------------------- Constructor

    public AnthropomorphicDelegate(JumplingActor actor) {
        mActor = actor;
    }

    // ---------------------------------------------- M�todos

    public void createAnthropomorphicLimbs(PointF worldPos, float radius) {
        float feetWorldRadius = radius / feetRatio;

        // Pie izquierdo
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(feetWorldRadius);

            PointF anchor = new PointF(mActor.mainBody.getWorldCenter().x - feetWorldRadius - 0, mActor.mainBody.getWorldCenter().y - radius
                    - feetWorldRadius);

            leftFootBody = mActor.mJWorld.createBody(mActor, anchor, true);
            leftFootBody.setBullet(false);
            // Assign shape to Body
            Fixture f = leftFootBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = feetJointFrequencyHz;
            jointDef.dampingRatio = feetJointdampingRatio;

            // jointDef.collideConnected = true;

            jointDef.initialize(mActor.mainBody, leftFootBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.mJWorld.createJoint(mActor, jointDef);
        }

        // Pie derecho
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(feetWorldRadius);

            PointF anchor = new PointF(mActor.mainBody.getWorldCenter().x + feetWorldRadius - 0, mActor.mainBody.getWorldCenter().y - radius
                    - feetWorldRadius);

            rightFootBody = mActor.mJWorld.createBody(mActor, anchor, true);
            // Assign shape to Body
            Fixture f = rightFootBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = feetJointFrequencyHz;
            jointDef.dampingRatio = feetJointdampingRatio;

            // jointDef.collideConnected = true;

            jointDef.initialize(mActor.mainBody, rightFootBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.mJWorld.createJoint(mActor, jointDef);
        }

        float handWorldRadius = radius / handRatio;

        // Mano izquierda
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(handWorldRadius);

            PointF anchor = new PointF(mActor.mainBody.getWorldCenter().x - radius - handWorldRadius - 0, mActor.mainBody.getWorldCenter().y
                    - handWorldRadius);

            leftHandBody = mActor.mJWorld.createBody(mActor, anchor, true);
            leftHandBody.setBullet(false);
            // Assign shape to Body
            Fixture f = leftHandBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = handJointFrequencyHz;
            jointDef.dampingRatio = handJointdampingRatio;

            jointDef.initialize(mActor.mainBody, leftHandBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.mJWorld.createJoint(mActor, jointDef);
        }

        // Mano derecha
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(handWorldRadius);

            PointF anchor = new PointF(mActor.mainBody.getWorldCenter().x + radius + handWorldRadius, mActor.mainBody.getWorldCenter().y
                    - handWorldRadius);

            rightHandBody = mActor.mJWorld.createBody(mActor, anchor, true);
            rightHandBody.setBullet(false);
            // Assign shape to Body
            Fixture f = rightHandBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = handJointFrequencyHz;
            jointDef.dampingRatio = handJointdampingRatio;

            jointDef.initialize(mActor.mainBody, rightHandBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.mJWorld.createJoint(mActor, jointDef);
        }

        float eyeWorldRadius = radius / eyeRatio;

        // Ojo izquierdo
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(eyeWorldRadius);

            PointF anchor = new PointF(mActor.mainBody.getWorldCenter().x - eyeWorldRadius * 1.5f, mActor.mainBody.getWorldCenter().y
                    + eyeWorldRadius);

            leftEyeBody = mActor.mJWorld.createBody(mActor, anchor, true);
            leftEyeBody.setBullet(false);
            // Assign shape to Body
            Fixture f = leftEyeBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = eyeJointFrequencyHz;
            jointDef.dampingRatio = eyeJointdampingRatio;

            jointDef.initialize(mActor.mainBody, leftEyeBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.mJWorld.createJoint(mActor, jointDef);
        }

        // Ojo derecho
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(eyeWorldRadius);

            PointF anchor = new PointF(mActor.mainBody.getWorldCenter().x + eyeWorldRadius * 1.5f, mActor.mainBody.getWorldCenter().y
                    + eyeWorldRadius);

            rightEyeBody = mActor.mJWorld.createBody(mActor, anchor, true);
            rightEyeBody.setBullet(false);
            // Assign shape to Body
            Fixture f = rightEyeBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = eyeJointFrequencyHz;
            jointDef.dampingRatio = eyeJointdampingRatio;

            jointDef.initialize(mActor.mainBody, rightEyeBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.mJWorld.createJoint(mActor, jointDef);
        }

    }

    public void initAnthropomorphicBitmaps(int bmpBodyId, int bmpFootRightId, int bmpFootLeftId, int bmpHandRightId, int bmpHandLeftId,
            int bmpEyeRightIdOpened, int bmpEyeLeftIdOpened, int bmpEyeRightIdClosed, int bmpEyeLeftIdClosed) {

        BitmapManager mb = mActor.mJWorld.getBitmapManager();
        mBmpBody = mb.getBitmap(bmpBodyId);
        mBmpFootRight = mb.getBitmap(bmpFootRightId);
        mBmpFootLeft = mb.getBitmap(bmpFootLeftId);
        mBmpHandRight = mb.getBitmap(bmpHandRightId);
        mBmpHandLeft = mb.getBitmap(bmpHandLeftId);
        mBmpEyeRightOpened = mb.getBitmap(bmpEyeRightIdOpened);
        mBmpEyeLeftOpened = mb.getBitmap(bmpEyeLeftIdOpened);
        mBmpEyeRightClosed = mb.getBitmap(bmpEyeRightIdClosed);
        mBmpEyeLeftClosed = mb.getBitmap(bmpEyeLeftIdClosed);

    }

    public void drawAnthropomorphicBitmaps(Canvas canvas) {
        mActor.mJWorld.drawBitmap(canvas, this.leftHandBody, mBmpHandLeft);
        mActor.mJWorld.drawBitmap(canvas, this.leftFootBody, mBmpFootLeft);

        mActor.mJWorld.drawBitmap(canvas, this.rightHandBody, mBmpHandRight);
        mActor.mJWorld.drawBitmap(canvas, this.rightFootBody, mBmpFootRight);

        mActor.mJWorld.drawBitmap(canvas, mActor.mainBody, mBmpBody);

        if (mAreEyesOpened) {
            mActor.mJWorld.drawBitmap(canvas, this.leftEyeBody, mBmpEyeLeftOpened);
            mActor.mJWorld.drawBitmap(canvas, this.rightEyeBody, mBmpEyeRightOpened);
        } else {
            mActor.mJWorld.drawBitmap(canvas, this.leftEyeBody, mBmpEyeLeftClosed);
            mActor.mJWorld.drawBitmap(canvas, this.rightEyeBody, mBmpEyeRightClosed);
        }
    }

    public void setEyesOpened(boolean areEyesOpened) {
        mAreEyesOpened = areEyesOpened;
    }

}
