package com.garrapeta.jumplings.actor;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.garrapeta.gameengine.Box2DActor;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.R;

public abstract class EnemyActor extends MainActor {

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

    protected AnthropomorphicDelegate<JumplingsGameWorld> mAnthtopoDelegate;
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
     * @param mWorld
     * @param worldPos
     */
    public EnemyActor(JumplingsGameWorld world) {
        super(world,  Z_INDEX);
        mAnthtopoDelegate = new AnthropomorphicDelegate<JumplingsGameWorld>(this);
        mBumpDelegate = new BumpDelegate(this);
    }


    public void init(PointF worldPos) {
    	super.init(worldPos);
    	mBumpDelegate.reset(mAnthtopoDelegate);
    }
    
    @Override
    protected final void drawBitmaps(Canvas canvas) {
        mAnthtopoDelegate.drawAnthropomorphicBitmaps(canvas);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        getWorld().onEnemyActorAdded(this);

        if (getWorldPos().y > getWorld().mViewport.getWorldBoundaries().top) {
        	getWorld().getSoundManager().play(JumplingsGameWorld.SAMPLE_ENEMY_DROP);
        } else {
            getWorld().getSoundManager().play(JumplingsGameWorld.SAMPLE_ENEMY_BOING);
        }
    }

    @Override
    protected void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        getWorld().onEnemyActorRemoved(this);
    }
 
    @Override
    public void onBeginContact(Body thisBody, Box2DActor<JumplingsGameWorld> other, Body otherBody, Contact contact) {
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
        getWorld().onEnemyScaped(this);
        super.onScapedFromBounds();
    }

    @Override
    public void onHitted() {
        getWorld().onEnemyKilled(this);
        super.onHitted();
    }

    @Override
    protected ArrayList<JumplingActor<?>> getDebrisBodies() {
        ArrayList<JumplingActor<?>> debrisActors = new ArrayList<JumplingActor<?>>();

        // Main Body
        {
            Body body = mMainBody;
            DebrisActor debrisActor = getWorld().getFactory().getDebrisActor(body, mBmpDebrisBody);
            
            getWorld().addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left hand
        {
            Body body = mAnthtopoDelegate.leftHandBody;
            DebrisActor debrisActor = getWorld().getFactory().getDebrisActor(body, mBmpDebrisHandLeft);
            
            getWorld().addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right Hand
        {
            Body body = mAnthtopoDelegate.rightHandBody;
            DebrisActor debrisActor = getWorld().getFactory().getDebrisActor(body, mBmpDebrisHandRight);

            getWorld().addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left foot
        {
            Body body = mAnthtopoDelegate.leftFootBody;
            DebrisActor debrisActor = getWorld().getFactory().getDebrisActor(body, mBmpDebrisFootLeft);

            getWorld().addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right foot
        {
            Body body = mAnthtopoDelegate.rightFootBody;
            DebrisActor debrisActor = getWorld().getFactory().getDebrisActor(body, mBmpDebrisFootRight);
            
            getWorld().addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left Eye
        {
            Body body = mAnthtopoDelegate.leftEyeBody;
            DebrisActor debrisActor = getWorld().getFactory().getDebrisActor(body, mBmpDebrisEyeLeft);

            getWorld().addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right Eye
        {
            Body body = mAnthtopoDelegate.rightEyeBody;
            DebrisActor debrisActor = getWorld().getFactory().getDebrisActor(body, mBmpDebrisEyeRight);

            getWorld().addActor(debrisActor);
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
    public void onBumpChange(boolean bumped) {
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
