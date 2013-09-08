package com.garrapeta.jumplings.actor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.garrapeta.gameengine.Box2DActor;
import com.garrapeta.jumplings.JumplingsWorld;
import com.garrapeta.jumplings.R;

public class IntroActor extends JumplingActor<JumplingsWorld> implements IBumpable {

    // ----------------------------------------------------------- Constantes
    public final static float DEFAULT_RADIUS = BASE_RADIUS * 3f;

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 0;

    // ------------------------------------------------- Variables estáticas

    // vivo
    protected final static int BMP_INTRO_BODY_ID = R.drawable.intro_body;

    protected final static int BMP_INTRO_FOOT_RIGHT_ID = R.drawable.intro_foot_right;
    protected final static int BMP_INTRO_FOOT_LEFT_ID = R.drawable.intro_foot_left;

    protected final static int BMP_INTRO_HAND_RIGHT_ID = R.drawable.intro_hand_right;
    protected final static int BMP_INTRO_HAND_LEFT_ID = R.drawable.intro_hand_left;

    protected final static int BMP_INTRO_EYE_RIGHT_OPENED_ID = R.drawable.intro_eye_right_opened;
    protected final static int BMP_INTRO_EYE_LEFT_OPENED_ID = R.drawable.intro_eye_left_opened;

    protected final static int BMP_INTRO_EYE_RIGHT_CLOSED_ID = R.drawable.intro_eye_right_closed;
    protected final static int BMP_INTRO_EYE_LEFT_CLOSED_ID = R.drawable.intro_eye_left_closed;
    // ----------------------------------------------- Variables de instancia

    private AnthropomorphicDelegate<JumplingsWorld> mAnthtopoDelegate;
    private BumpDelegate mBumpDelegate;

    // Bitmaps del actor vivo
    protected Bitmap bmpBody;

    protected Bitmap bmpFootRight;
    protected Bitmap bmpFootLeft;

    protected Bitmap bmpHandRight;
    protected Bitmap bmpHandLeft;

    protected Bitmap bmpEyeRight;
    protected Bitmap bmpEyeLeft;

    // ----------------------------------------------- Inicializaci�n est�tica

    // --------------------------------------------------- Constructor

    public IntroActor(JumplingsWorld world) {
        super(world, Z_INDEX);
        mRadius = IntroActor.DEFAULT_RADIUS;
        mAnthtopoDelegate = new AnthropomorphicDelegate<JumplingsWorld>(this);
        mBumpDelegate = new BumpDelegate(this);
    }

    // --------------------------------------------- M�todos heredados
    
    public void init(PointF worldPos) {
    	super.init(worldPos);
    	mBumpDelegate.reset(mAnthtopoDelegate);
    }
    
    @Override
    protected void initBodies(PointF worldPos) {

        // Cuerpo
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(mRadius);
            mMainBody = getWorld().createBody(this, worldPos, true);
            mMainBody.setBullet(true);

            // Assign shape to Body
            Fixture f = mMainBody.createFixture(circleShape, 1.0f);
            f.setFilterData(CONTACT_FILTER);
            circleShape.dispose();

        }

        mAnthtopoDelegate.createAnthropomorphicLimbs(worldPos, mRadius);
    }

    @Override
    protected void initBitmaps() {
        // vivo
        mAnthtopoDelegate.initAnthropomorphicBitmaps(BMP_INTRO_BODY_ID, BMP_INTRO_FOOT_RIGHT_ID, BMP_INTRO_FOOT_LEFT_ID, BMP_INTRO_HAND_RIGHT_ID,
                BMP_INTRO_HAND_LEFT_ID, BMP_INTRO_EYE_RIGHT_OPENED_ID, BMP_INTRO_EYE_LEFT_OPENED_ID, BMP_INTRO_EYE_RIGHT_CLOSED_ID,
                BMP_INTRO_EYE_LEFT_CLOSED_ID);
    }

    @Override
    public void processFrame(float gameTimeStep) {
        super.processFrame(gameTimeStep);
        mBumpDelegate.processFrame(gameTimeStep);
    }

    @Override
    protected final void drawBitmaps(Canvas canvas) {
        mAnthtopoDelegate.drawAnthropomorphicBitmaps(canvas);
    }

    @Override
    public void onBumpChange(boolean bumped) {
        mBumpDelegate.onBumped(bumped, this, mAnthtopoDelegate);
    }

    @Override
    public void onBeginContact(Body thisBody, Box2DActor<JumplingsWorld> other, Body otherBody, Contact contact) {
        super.onBeginContact(thisBody, other, otherBody, contact);
        mBumpDelegate.onBeginContact(mEntered, thisBody, other, otherBody, contact);
    }

    @Override
    protected void free(JumplingsFactory factory) {
        getWorld().getFactory().free(this);
    }

    @Override
    protected void dispose() {
        super.dispose();
        mAnthtopoDelegate = null;
        mBumpDelegate = null;
        bmpBody = null;
        bmpFootRight = null;
        bmpFootLeft = null;

        bmpHandRight = null;
        bmpHandLeft = null;

        bmpEyeRight = null;
        bmpEyeLeft = null;
    }

}
