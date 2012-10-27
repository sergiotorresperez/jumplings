package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class DoubleEnemyActor extends EnemyActor {

    // ---------------------------------------------------------------
    // Constantes

    public final static float DEFAULT_RADIUS = BASE_RADIUS * 1.1f;

    public final static float HEIGHT_RESTORATION_FACTOR = 1f / 3f;

    public final static short JUMPER_CODE_DOUBLE = 1;

    // ------------------------------------------------------ Variables
    // est�ticas

    // vivo
    protected final static int BMP_ORANGE_BODY_ID = R.drawable.orange_double_body;

    protected final static int BMP_ORANGE_FOOT_RIGHT_ID = R.drawable.orange_foot_right;
    protected final static int BMP_ORANGE_FOOT_LEFT_ID = R.drawable.orange_foot_left;

    protected final static int BMP_ORANGE_HAND_RIGHT_ID = R.drawable.orange_hand_right;
    protected final static int BMP_ORANGE_HAND_LEFT_ID = R.drawable.orange_hand_left;

    // debris
    protected final static int BMP_DEBRIS_ORANGE_BODY_ID = R.drawable.orange_debris_double_body;

    protected final static int BMP_DEBRIS_ORANGE_FOOT_RIGHT_ID = R.drawable.orange_debris_foot_right;
    protected final static int BMP_DEBRIS_ORANGE_FOOT_LEFT_ID = R.drawable.orange_debris_foot_left;

    protected final static int BMP_DEBRIS_ORANGE_HAND_RIGHT_ID = R.drawable.orange_debris_hand_right;
    protected final static int BMP_DEBRIS_ORANGE_HAND_LEFT_ID = R.drawable.orange_debris_hand_left;

    // ----------------------------------------------------------------
    // Variables

    // ------------------------------------------------------ M�todos est�ticos

    static double getDoubleEnemyActorHitCount() {
        return 2;
    }

    // ---------------------------------------------------------------
    // Constructor

    /**
     * @param gameWorld
     */
    public DoubleEnemyActor(JumplingsGameWorld mJWorld, PointF worldPos) {
        super(mJWorld, DoubleEnemyActor.DEFAULT_RADIUS, worldPos);

        this.mCode = DoubleEnemyActor.JUMPER_CODE_DOUBLE;
        init(worldPos);
    }

    @Override
    protected void initBodies(PointF worldPos) {
        // Cuerpo
        {
            // Create Shape with Properties
            PolygonShape polygonShape = new PolygonShape();
            Vector2[] vertices = new Vector2[] { new Vector2(0, mRadius), new Vector2(-mRadius, 0), new Vector2(0, -mRadius), new Vector2(mRadius, 0) };
            polygonShape.set(vertices);

            mainBody = mJWorld.createBody(this, worldPos, true);
            mainBody.setBullet(true);

            // Assign shape to Body
            Fixture f = mainBody.createFixture(polygonShape, 1.0f);
            f.setFilterData(CONTACT_FILTER);
            polygonShape.dispose();

        }

        mAnthtopoDelegate.createAnthropomorphicLimbs(worldPos, mRadius);
    }

    @Override
    protected void initBitmaps() {
        // vivo
        mAnthtopoDelegate.initAnthropomorphicBitmaps(BMP_ORANGE_BODY_ID, BMP_ORANGE_FOOT_RIGHT_ID, BMP_ORANGE_FOOT_LEFT_ID, BMP_ORANGE_HAND_RIGHT_ID,
                BMP_ORANGE_HAND_LEFT_ID, BMP_EYE_0_RIGHT_OPENED_ID, BMP_EYE_0_LEFT_OPENED_ID, BMP_EYE_0_RIGHT_CLOSED_ID, BMP_EYE_0_LEFT_CLOSED_ID);

        // debris
        BitmapManager mb = mJWorld.getBitmapManager();
        mBmpDebrisBody = mb.getBitmap(BMP_DEBRIS_ORANGE_BODY_ID);

        mBmpDebrisFootRight = mb.getBitmap(BMP_DEBRIS_ORANGE_FOOT_RIGHT_ID);
        mBmpDebrisFootLeft = mb.getBitmap(BMP_DEBRIS_ORANGE_FOOT_LEFT_ID);

        mBmpDebrisHandRight = mb.getBitmap(BMP_DEBRIS_ORANGE_HAND_RIGHT_ID);
        mBmpDebrisHandLeft = mb.getBitmap(BMP_DEBRIS_ORANGE_HAND_LEFT_ID);

        mBmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_0_RIGHT_ID);
        mBmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_0_LEFT_ID);
    }

    // -------------------------------------------------------- M�todos propios

    private final float getRestorationInitVy(float posY) {
        float maxHeight = posY + HEIGHT_RESTORATION_FACTOR * (mJWorld.viewport.getWorldBoundaries().top - posY);
        return (float) getInitialYVelocity(maxHeight);
    }

    @Override
    public void onHitted() {

        EnemyActor son = null;
        float xVel = 0;
        Vector2 pos = null;

        pos = mainBody.getWorldCenter();
        son = new DoubleSonEnemyActor(jgWorld, Viewport.vector2ToPointF(pos));

        xVel = mainBody.getLinearVelocity().x;

        mJWorld.addActor(son);

        float yVel = getRestorationInitVy(pos.y);
        son.setLinearVelocity(xVel / 2, yVel);

        super.onHitted();
    }

}
