package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

public class DoubleSonEnemyActor extends EnemyActor {

    // ---------------------------------------------------- Constantes

    public final static float DEFAULT_RADIUS = BASE_RADIUS * 1f;

    public final static short JUMPER_CODE_DOUBLE_SON = 2;

    // ------------------------------------------------------ Variables
    // estáticas

    // vivo
    protected final static int BMP_ORANGE_SIMPLE_BODY_ID = R.drawable.orange_simple_body;

    // debris
    protected final static int BMP_DEBRIS_ORANGE_SIMPLE_BODY_ID = R.drawable.orange_debris_simple_body;

    // ---------------------------------------------------- Variables

    // --------------------------------------------------- Inicializaci�n
    // est�tica

    // ----------------------------------------------------------------
    // Constructor

    public DoubleSonEnemyActor(JumplingsGameWorld mWorld) {
        super(mWorld);
        mRadius = DoubleSonEnemyActor.DEFAULT_RADIUS;
        mCode = DoubleSonEnemyActor.JUMPER_CODE_DOUBLE_SON;
    }

    // -------------------------------------------------------- M�todos Propios

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
        mAnthtopoDelegate.initAnthropomorphicBitmaps(BMP_ORANGE_SIMPLE_BODY_ID, DoubleEnemyActor.BMP_ORANGE_FOOT_RIGHT_ID,
                DoubleEnemyActor.BMP_ORANGE_FOOT_LEFT_ID, DoubleEnemyActor.BMP_ORANGE_HAND_RIGHT_ID, DoubleEnemyActor.BMP_ORANGE_HAND_LEFT_ID,
                BMP_EYE_0_RIGHT_OPENED_ID, BMP_EYE_0_LEFT_OPENED_ID, BMP_EYE_0_RIGHT_CLOSED_ID, BMP_EYE_0_LEFT_CLOSED_ID);

        // debris
        BitmapManager mb = getWorld().getBitmapManager();
        mBmpDebrisBody = mb.getBitmap(BMP_DEBRIS_ORANGE_SIMPLE_BODY_ID);

        mBmpDebrisFootRight = mb.getBitmap(DoubleEnemyActor.BMP_DEBRIS_ORANGE_FOOT_RIGHT_ID);
        mBmpDebrisFootLeft = mb.getBitmap(DoubleEnemyActor.BMP_DEBRIS_ORANGE_FOOT_LEFT_ID);

        mBmpDebrisHandRight = mb.getBitmap(DoubleEnemyActor.BMP_DEBRIS_ORANGE_HAND_RIGHT_ID);
        mBmpDebrisHandLeft = mb.getBitmap(DoubleEnemyActor.BMP_DEBRIS_ORANGE_HAND_LEFT_ID);

        mBmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_0_RIGHT_ID);
        mBmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_0_LEFT_ID);
    }
    
    @Override
    protected void free(JumplingsFactory factory) {
        getWorld().getFactory().free(this);
    }

}
