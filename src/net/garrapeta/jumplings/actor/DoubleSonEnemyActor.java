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

    // ------------------------------------------------------ Variables estáticas

    // vivo
    protected final static int BMP_ORANGE_SIMPLE_BODY_ID = R.drawable.orange_simple_body;

    // debris
    protected final static int BMP_DEBRIS_ORANGE_SIMPLE_BODY_ID = R.drawable.orange_debris_simple_body;

    // ---------------------------------------------------- Variables

    // --------------------------------------------------- Inicializaci�n
    // est�tica

    // ----------------------------------------------------------------
    // Constructor

    public DoubleSonEnemyActor(JumplingsGameWorld mJWorld, PointF worldPos) {
        super(mJWorld, DoubleSonEnemyActor.DEFAULT_RADIUS, worldPos);
        this.code = DoubleSonEnemyActor.JUMPER_CODE_DOUBLE_SON;

        // vivo
        ah.initBitmaps(BMP_ORANGE_SIMPLE_BODY_ID, DoubleEnemyActor.BMP_ORANGE_FOOT_RIGHT_ID, DoubleEnemyActor.BMP_ORANGE_FOOT_LEFT_ID,
                DoubleEnemyActor.BMP_ORANGE_HAND_RIGHT_ID, DoubleEnemyActor.BMP_ORANGE_HAND_LEFT_ID, BMP_EYE_0_RIGHT_ID, BMP_EYE_0_LEFT_ID);

        // debris
        BitmapManager mb = mJWorld.getBitmapManager();
        bmpDebrisBody = mb.getBitmap(BMP_DEBRIS_ORANGE_SIMPLE_BODY_ID);

        bmpDebrisFootRight = mb.getBitmap(DoubleEnemyActor.BMP_DEBRIS_ORANGE_FOOT_RIGHT_ID);
        bmpDebrisFootLeft = mb.getBitmap(DoubleEnemyActor.BMP_DEBRIS_ORANGE_FOOT_LEFT_ID);

        bmpDebrisHandRight = mb.getBitmap(DoubleEnemyActor.BMP_DEBRIS_ORANGE_HAND_RIGHT_ID);
        bmpDebrisHandLeft = mb.getBitmap(DoubleEnemyActor.BMP_DEBRIS_ORANGE_HAND_LEFT_ID);

        bmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_0_RIGHT_ID);
        bmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_0_LEFT_ID);
    }

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

}
