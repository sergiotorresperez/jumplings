package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.Box2DActor;
import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.jumplings.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

public class IntroActor extends JumplingActor implements IBumpable {

    // ----------------------------------------------------------- Constantes
    public final static float DEFAULT_RADIUS = BASE_RADIUS * 3f;

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 0;

    // ------------------------------------------------- Variables est�ticas

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

    private AnthropomorphicDelegate mAnthtopoDelegate;
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

    public IntroActor(JumplingsWorld jWorld, PointF worldPos) {
        super(jWorld, IntroActor.DEFAULT_RADIUS, Z_INDEX, worldPos);
        mAnthtopoDelegate = new AnthropomorphicDelegate(this);
        mBumpDelegate = new BumpDelegate(this);
        init(worldPos);
    }

    // --------------------------------------------- M�todos heredados

    @Override
    protected void initBodies(PointF worldPos) {

        // Cuerpo
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(mRadius);
            mainBody = mJWorld.createBody(this, worldPos, true);
            mainBody.setBullet(true);

            // Assign shape to Body
            Fixture f = mainBody.createFixture(circleShape, 1.0f);
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
    public void onBumpedChanged(boolean bumped) {
        mBumpDelegate.onBumped(bumped, this, mAnthtopoDelegate);
    }

    @Override
    public void onBeginContact(Body thisBody, Box2DActor other, Body otherBody, Contact contact) {
        super.onBeginContact(thisBody, other, otherBody, contact);
        mBumpDelegate.onBeginContact(mEntered, thisBody, other, otherBody, contact);
    }

}
