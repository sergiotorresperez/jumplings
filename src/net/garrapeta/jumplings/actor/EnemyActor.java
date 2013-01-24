package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.gameengine.Box2DActor;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

public abstract class EnemyActor extends MainActor implements IBumpable {

    // ---------------------------------------------------- Constantes

    // ------------------------------------------ Variables est�ticas

    // vivo
    protected final static int BMP_EYE_2_RIGHT_OPENED_ID = R.drawable.eye_2_right_opened;
    protected final static int BMP_EYE_2_LEFT_OPENED_ID = R.drawable.eye_2_left_opened;

    protected final static int BMP_EYE_0_RIGHT_OPENED_ID = R.drawable.eye_0_right_opened;
    protected final static int BMP_EYE_0_LEFT_OPENED_ID = R.drawable.eye_0_left_opened;

    protected final static int BMP_EYE_2_RIGHT_CLOSED_ID = R.drawable.eye_2_right_closed;
    protected final static int BMP_EYE_2_LEFT_CLOSED_ID = R.drawable.eye_2_left_closed;

    protected final static int BMP_EYE_0_RIGHT_CLOSED_ID = R.drawable.eye_0_right_closed;
    protected final static int BMP_EYE_0_LEFT_CLOSED_ID = R.drawable.eye_0_left_closed;

    // debris
    protected final static int BMP_DEBRIS_EYE_2_RIGHT_ID = BMP_EYE_2_RIGHT_OPENED_ID;
    protected final static int BMP_DEBRIS_EYE_2_LEFT_ID = BMP_EYE_2_LEFT_OPENED_ID;

    protected final static int BMP_DEBRIS_EYE_0_RIGHT_ID = BMP_EYE_0_RIGHT_OPENED_ID;
    protected final static int BMP_DEBRIS_EYE_0_LEFT_ID = BMP_EYE_0_LEFT_OPENED_ID;

    // ------------------------------------------ Variables de instancia

    protected AnthropomorphicDelegate mAnthtopoDelegate;
    protected BumpDelegate mBumpDelegate;

    // Bitmaps del actor muerto (debris)
    protected Bitmap mBmpDebrisBody;

    protected Bitmap mBmpDebrisFootRight;
    protected Bitmap mBmpDebrisFootLeft;

    protected Bitmap mBmpDebrisHandRight;
    protected Bitmap mBmpDebrisHandLeft;

    protected Bitmap mBmpDebrisEyeRight;
    protected Bitmap mBmpDebrisEyeLeft;

    // ---------------------------------------------------------- M�todos
    // est�ticos

    static double getSimpleEnemyBaseThread() {
        return 1;
    }

    // ----------------------------------------------------------------
    // Constructor

    /**
     * @param mJWorld
     * @param radius
     * @param worldPos
     */
    public EnemyActor(JumplingsGameWorld mJWorld, float radius, PointF worldPos) {
        super(mJWorld, worldPos, radius, Z_INDEX);
        mAnthtopoDelegate = new AnthropomorphicDelegate(this);
        mBumpDelegate = new BumpDelegate(this);
    }

    // ------------------------------------------- M�todos Heredados

    @Override
    protected final void drawBitmaps(Canvas canvas) {
        mAnthtopoDelegate.drawAnthropomorphicBitmaps(canvas);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (getWorldPos().y > mJWorld.mViewport.getWorldBoundaries().top) {
            // TODO: sample when enemy falls
            // mJWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_ENEMY_THROW);
        } else {
            mJWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_ENEMY_BOING);
        }
    }

    @Override
    public void onBeginContact(Body thisBody, Box2DActor other, Body otherBody, Contact contact) {
        super.onBeginContact(thisBody, other, otherBody, contact);
        mBumpDelegate.onBeginContact(mEntered, thisBody, other, otherBody, contact);
    }

    // ------------------------------------------------ M�todos propios

    @Override
    protected Body[] getMainBodies() {
        return new Body[] { mMainBody };
    }

    @Override
    protected void onScapedFromBounds() {
        mJgWorld.onEnemyScaped(this);
        super.onScapedFromBounds();
    }

    @Override
    public void onHitted() {
        mJgWorld.onEnemyKilled(this);
        super.onHitted();
    }

    @Override
    protected ArrayList<JumplingActor> getDebrisBodies() {
        ArrayList<JumplingActor> debrisActors = new ArrayList<JumplingActor>();

        // Main Body
        {
            Body body = mMainBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisBody);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left hand
        {
            Body body = mAnthtopoDelegate.leftHandBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisHandLeft);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right Hand
        {
            Body body = mAnthtopoDelegate.rightHandBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisHandRight);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left foot
        {
            Body body = mAnthtopoDelegate.leftFootBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisFootLeft);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right foot
        {
            Body body = mAnthtopoDelegate.rightFootBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisFootRight);
            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left Eye
        {
            Body body = mAnthtopoDelegate.leftEyeBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisEyeLeft);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right Eye
        {
            Body body = mAnthtopoDelegate.rightEyeBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisEyeRight);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        return debrisActors;
    }

    @Override
    public void processFrame(float gameTimeStep) {
        super.processFrame(gameTimeStep);
        mBumpDelegate.processFrame(gameTimeStep);
    }

    @Override
    public void onBumpedChanged(boolean bumped) {
        mBumpDelegate.onBumped(bumped, this, mAnthtopoDelegate);
    }

    @Override
    protected void dispose() {
        super.dispose();
        mAnthtopoDelegate = null;
        mBumpDelegate = null;
        mBmpDebrisBody = null;
        mBmpDebrisFootRight = null;
        mBmpDebrisFootLeft = null;

        mBmpDebrisHandRight = null;
        mBmpDebrisHandLeft = null;

        mBmpDebrisEyeRight = null;
        mBmpDebrisEyeLeft = null;
    }
}
