package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

public class RoundEnemyActor extends EnemyActor {

    // ---------------------------------------------------- Constantes

    public final static float DEFAULT_RADIUS = BASE_RADIUS * 1f;

    public final static short JUMPER_CODE_SIMPLE = 0;

    // ------------------------------------------------------ Variables
    // est�ticas

    // vivo
    protected final static int BMP_RED_BODY_ID = R.drawable.red_body;

    protected final static int BMP_RED_FOOT_RIGHT_ID = R.drawable.red_foot_right;
    protected final static int BMP_RED_FOOT_LEFT_ID = R.drawable.red_foot_left;

    protected final static int BMP_RED_HAND_RIGHT_ID = R.drawable.red_hand_right;
    protected final static int BMP_RED_HAND_LEFT_ID = R.drawable.red_hand_left;

    // debris
    protected final static int BMP_DEBRIS_RED_BODY_ID = R.drawable.red_debris_body;

    protected final static int BMP_DEBRIS_RED_FOOT_RIGHT_ID = R.drawable.red_debris_foot_right;
    protected final static int BMP_DEBRIS_RED_FOOT_LEFT_ID = R.drawable.red_debris_foot_left;

    protected final static int BMP_DEBRIS_RED_HAND_RIGHT_ID = R.drawable.red_debris_hand_right;
    protected final static int BMP_DEBRIS_RED_HAND_LEFT_ID = R.drawable.red_debris_hand_left;

    // ---------------------------------------------------- Variables

    // ----------------------------------------------------------------
    // Constructor

    public RoundEnemyActor(JumplingsGameWorld mJWorld, PointF worldPos) {
        super(mJWorld, RoundEnemyActor.DEFAULT_RADIUS, worldPos);

        this.mCode = RoundEnemyActor.JUMPER_CODE_SIMPLE;
    }

    // ------------------------------------------------------ M�todos heredados

    // -------------------------------------------------------- M�todos Propios

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

        ah.createLimbs(worldPos, mRadius);
    }
    
    @Override
    protected void initBitmaps() {
        // vivo
        ah.initBitmaps(BMP_RED_BODY_ID, BMP_RED_FOOT_RIGHT_ID, BMP_RED_FOOT_LEFT_ID, BMP_RED_HAND_RIGHT_ID, BMP_RED_HAND_LEFT_ID, BMP_EYE_0_RIGHT_ID, BMP_EYE_0_LEFT_ID);

        // debris
        BitmapManager mb = mJWorld.getBitmapManager();
        mBmpDebrisBody = mb.getBitmap(BMP_DEBRIS_RED_BODY_ID);

        mBmpDebrisFootRight = mb.getBitmap(BMP_DEBRIS_RED_FOOT_RIGHT_ID);
        mBmpDebrisFootLeft = mb.getBitmap(BMP_DEBRIS_RED_FOOT_LEFT_ID);

        mBmpDebrisHandRight = mb.getBitmap(BMP_DEBRIS_RED_HAND_RIGHT_ID);
        mBmpDebrisHandLeft = mb.getBitmap(BMP_DEBRIS_RED_HAND_LEFT_ID);

        mBmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_0_RIGHT_ID);
        mBmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_0_LEFT_ID);
    }

}
