package com.garrapeta.jumplings.actor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.gameengine.Viewport;
import com.garrapeta.jumplings.JumplingsWorld;

/**
 * Common implementation of those aspects of the anthropomorphic creatures,, to
 * let objects implementing that interface delegate in this.
 * 
 * @author garrapeta
 * 
 */
public class AnthropomorphicDelegate<T extends JumplingsWorld> {
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

    private JumplingActor<T> mActor;

    // Cuerpos
    Body mRightFootBody;
    Body mLeftFootBody;

    Body mRightHandBody;
    Body mLeftHandBody;

    Body mRightEyeBody;
    Body mLeftEyeBody;

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

    public AnthropomorphicDelegate(JumplingActor<T> actor) {
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

            PointF anchor = new PointF(mActor.mMainBody.getWorldCenter().x - feetWorldRadius - 0, mActor.mMainBody.getWorldCenter().y - radius
                    - feetWorldRadius);

            mLeftFootBody = mActor.getWorld()
                                  .createBody(mActor, anchor, true);
            mLeftFootBody.setBullet(false);
            // Assign shape to Body
            Fixture f = mLeftFootBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = feetJointFrequencyHz;
            jointDef.dampingRatio = feetJointdampingRatio;

            // jointDef.collideConnected = true;

            jointDef.initialize(mActor.mMainBody, mLeftFootBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.getWorld()
                  .createJoint(mActor, jointDef);
        }

        // Pie derecho
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(feetWorldRadius);

            PointF anchor = new PointF(mActor.mMainBody.getWorldCenter().x + feetWorldRadius - 0, mActor.mMainBody.getWorldCenter().y - radius
                    - feetWorldRadius);

            mRightFootBody = mActor.getWorld()
                                   .createBody(mActor, anchor, true);
            // Assign shape to Body
            Fixture f = mRightFootBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = feetJointFrequencyHz;
            jointDef.dampingRatio = feetJointdampingRatio;

            // jointDef.collideConnected = true;

            jointDef.initialize(mActor.mMainBody, mRightFootBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.getWorld()
                  .createJoint(mActor, jointDef);
        }

        float handWorldRadius = radius / handRatio;

        // Mano izquierda
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(handWorldRadius);

            PointF anchor = new PointF(mActor.mMainBody.getWorldCenter().x - radius - handWorldRadius - 0, mActor.mMainBody.getWorldCenter().y
                    - handWorldRadius);

            mLeftHandBody = mActor.getWorld()
                                  .createBody(mActor, anchor, true);
            mLeftHandBody.setBullet(false);
            // Assign shape to Body
            Fixture f = mLeftHandBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = handJointFrequencyHz;
            jointDef.dampingRatio = handJointdampingRatio;

            jointDef.initialize(mActor.mMainBody, mLeftHandBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.getWorld()
                  .createJoint(mActor, jointDef);
        }

        // Mano derecha
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(handWorldRadius);

            PointF anchor = new PointF(mActor.mMainBody.getWorldCenter().x + radius + handWorldRadius, mActor.mMainBody.getWorldCenter().y - handWorldRadius);

            mRightHandBody = mActor.getWorld()
                                   .createBody(mActor, anchor, true);
            mRightHandBody.setBullet(false);
            // Assign shape to Body
            Fixture f = mRightHandBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = handJointFrequencyHz;
            jointDef.dampingRatio = handJointdampingRatio;

            jointDef.initialize(mActor.mMainBody, mRightHandBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.getWorld()
                  .createJoint(mActor, jointDef);
        }

        float eyeWorldRadius = radius / eyeRatio;

        // Ojo izquierdo
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(eyeWorldRadius);

            PointF anchor = new PointF(mActor.mMainBody.getWorldCenter().x - eyeWorldRadius * 1.5f, mActor.mMainBody.getWorldCenter().y + eyeWorldRadius);

            mLeftEyeBody = mActor.getWorld()
                                 .createBody(mActor, anchor, true);
            mLeftEyeBody.setBullet(false);
            // Assign shape to Body
            Fixture f = mLeftEyeBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = eyeJointFrequencyHz;
            jointDef.dampingRatio = eyeJointdampingRatio;

            jointDef.initialize(mActor.mMainBody, mLeftEyeBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.getWorld()
                  .createJoint(mActor, jointDef);
        }

        // Ojo derecho
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(eyeWorldRadius);

            PointF anchor = new PointF(mActor.mMainBody.getWorldCenter().x + eyeWorldRadius * 1.5f, mActor.mMainBody.getWorldCenter().y + eyeWorldRadius);

            mRightEyeBody = mActor.getWorld()
                                  .createBody(mActor, anchor, true);
            mRightEyeBody.setBullet(false);
            // Assign shape to Body
            Fixture f = mRightEyeBody.createFixture(circleShape, LIMBS_DENSITY);
            f.setFilterData(JumplingActor.NO_CONTACT_FILTER);
            circleShape.dispose();

            DistanceJointDef jointDef = new DistanceJointDef();

            jointDef.frequencyHz = eyeJointFrequencyHz;
            jointDef.dampingRatio = eyeJointdampingRatio;

            jointDef.initialize(mActor.mMainBody, mRightEyeBody, Viewport.pointFToVector2(anchor), Viewport.pointFToVector2(anchor));

            mActor.getWorld()
                  .createJoint(mActor, jointDef);
        }

    }

    public void initAnthropomorphicBitmaps(int bmpBodyId, int bmpFootRightId, int bmpFootLeftId, int bmpHandRightId, int bmpHandLeftId,
            int bmpEyeRightIdOpened, int bmpEyeLeftIdOpened, int bmpEyeRightIdClosed, int bmpEyeLeftIdClosed) {

        BitmapManager mb = mActor.getWorld()
                                 .getBitmapManager();
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
        mActor.getWorld()
              .drawBitmap(canvas, mLeftHandBody, mBmpHandLeft);
        mActor.getWorld()
              .drawBitmap(canvas, mLeftFootBody, mBmpFootLeft);

        mActor.getWorld()
              .drawBitmap(canvas, mRightHandBody, mBmpHandRight);
        mActor.getWorld()
              .drawBitmap(canvas, mRightFootBody, mBmpFootRight);

        mActor.getWorld()
              .drawBitmap(canvas, mActor.mMainBody, mBmpBody);

        if (mAreEyesOpened) {
            mActor.getWorld()
                  .drawBitmap(canvas, mLeftEyeBody, mBmpEyeLeftOpened);
            mActor.getWorld()
                  .drawBitmap(canvas, mRightEyeBody, mBmpEyeRightOpened);
        } else {
            mActor.getWorld()
                  .drawBitmap(canvas, mLeftEyeBody, mBmpEyeLeftClosed);
            mActor.getWorld()
                  .drawBitmap(canvas, mRightEyeBody, mBmpEyeRightClosed);
        }
    }

    public void setEyesOpened(boolean areEyesOpened) {
        mAreEyesOpened = areEyesOpened;
    }

}
