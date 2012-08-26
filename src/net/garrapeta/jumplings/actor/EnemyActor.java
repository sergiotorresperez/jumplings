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
    protected Bitmap bmpDebrisBody;

    protected Bitmap bmpDebrisFootRight;
    protected Bitmap bmpDebrisFootLeft;

    protected Bitmap bmpDebrisHandRight;
    protected Bitmap bmpDebrisHandLeft;

    protected Bitmap bmpDebrisEyeRight;
    protected Bitmap bmpDebrisEyeLeft;

    // ---------------------------------------------------------- M�todos
    // est�ticos

    static double getSimpleEnemyActorHitCount() {
        return 1;
    }

    // ----------------------------------------------------------------
    // Constructor

    /**
     * @param mGameWorld
     */
    public EnemyActor(JumplingsGameWorld jgWorld, PointF worldPos) {
        super(jgWorld, worldPos, Z_INDEX);
        ah = new AnthropomorphicHelper(this);
    }

    // ------------------------------------------- M�todos Heredados

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
        if (getWorldPos().y > jgWorld.viewport.getWorldBoundaries().top) {
            jgWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_ENEMY_THROW);
        } else {
            jgWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_ENEMY_BOING);
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
            DebrisActor debrisActor = new DebrisActor(jgWorld, body, bmpDebrisBody);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left hand
        {
            Body body = ah.leftHandBody;
            DebrisActor debrisActor = new DebrisActor(jgWorld, body, bmpDebrisHandLeft);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right Hand
        {
            Body body = ah.rightHandBody;
            DebrisActor debrisActor = new DebrisActor(jgWorld, body, bmpDebrisHandRight);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left foot
        {
            Body body = ah.leftFootBody;
            DebrisActor debrisActor = new DebrisActor(jgWorld, body, bmpDebrisFootLeft);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right foot
        {
            Body body = ah.rightFootBody;
            DebrisActor debrisActor = new DebrisActor(jgWorld, body, bmpDebrisFootRight);
            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Left Eye
        {
            Body body = ah.leftEyeBody;
            DebrisActor debrisActor = new DebrisActor(jgWorld, body, bmpDebrisEyeLeft);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Right Eye
        {
            Body body = ah.rightEyeBody;
            DebrisActor debrisActor = new DebrisActor(jgWorld, body, bmpDebrisEyeRight);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        return debrisActors;
    }

}
