package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class EnemyActor extends MainActor {

    // ---------------------------------------------------- Constantes

    // ------------------------------------------ Variables est�ticas

    // vivo
    protected final static int BMP_EYE_2_RIGHT_ID = R.drawable.eye_2_right;
    protected final static int BMP_EYE_2_LEFT_ID = R.drawable.eye_2_left;

    protected final static int BMP_EYE_0_RIGHT_ID = R.drawable.eye_0_right;
    protected final static int BMP_EYE_0_LEFT_ID = R.drawable.eye_0_left;

    // debris
    protected final static int BMP_DEBRIS_EYE_2_RIGHT_ID = BMP_EYE_2_RIGHT_ID;
    protected final static int BMP_DEBRIS_EYE_2_LEFT_ID = BMP_EYE_2_LEFT_ID;

    protected final static int BMP_DEBRIS_EYE_0_RIGHT_ID = BMP_EYE_0_RIGHT_ID;
    protected final static int BMP_DEBRIS_EYE_0_LEFT_ID = BMP_EYE_0_LEFT_ID;

    // ------------------------------------------ Variables de instancia

    AnthropomorphicHelper ah;

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

    static double getSimpleEnemyActorHitCount() {
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
    }
    
    // ------------------------------------------- M�todos Heredados

    @Override
    protected void initFields() {
        ah = new AnthropomorphicHelper(this);
    }

    @Override
    public final void drawBodiesShapes(Canvas canvas) {
        ah.drawShapes(canvas);
    }

    @Override
    protected final void drawBitmaps(Canvas canvas) {
        ah.drawBitmaps(canvas);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (getWorldPos().y > mJWorld.viewport.getWorldBoundaries().top) {
            // TODO: sample when enemy falls
//            mJWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_ENEMY_THROW);
        } else {
            mJWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_ENEMY_BOING);
        }
    }

    // ------------------------------------------------ M�todos propios

    @Override
    protected Body[] getMainBodies() {
        return new Body[] { mainBody };
    }

    @Override
    protected void onScapedFromBounds() {
        jgWorld.onEnemyScaped(this);
        super.onScapedFromBounds();
    }

    @Override
    public void onHitted() {
        jgWorld.onEnemyKilled(this);
        super.onHitted();
    }

    @Override
    protected ArrayList<JumplingActor> getDebrisBodies() {
        ArrayList<JumplingActor> debrisActors = new ArrayList<JumplingActor>();

        // Main Body
        {
            Body body = mainBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisBody);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left hand
        {
            Body body = ah.leftHandBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisHandLeft);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right Hand
        {
            Body body = ah.rightHandBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisHandRight);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left foot
        {
            Body body = ah.leftFootBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisFootLeft);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right foot
        {
            Body body = ah.rightFootBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisFootRight);
            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left Eye
        {
            Body body = ah.leftEyeBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisEyeLeft);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right Eye
        {
            Body body = ah.rightEyeBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, mBmpDebrisEyeRight);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        return debrisActors;
    }

}
