package com.garrapeta.jumplings.actor;

import com.garrapeta.MathUtils;
import com.garrapeta.gameengine.Viewport;
import com.garrapeta.gameengine.module.BitmapManager;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.R;
import android.graphics.PointF;
import android.graphics.RectF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class SplitterEnemyActor extends EnemyActor {

    // ---------------------------------------------------- Constantes

    public final static float DEFAULT_BASE_RADIUS = BASE_RADIUS * 1f;

    /**
     * Factor usado para determinar el tama�o del actor en conjunci�n con el
     * baseRadiusSize
     */
    private final static float RADIUS_FACTOR = 0.2f;

    public final static float HEIGHT_RESTORATION_FACTOR = 0.50f;

    public final static short JUMPER_CODE_SPLITTER_SIMPLE = 3;
    public final static short JUMPER_CODE_SPLITTER_DOUBLE = 4;
    public final static short JUMPER_CODE_SPLITTER_TRIPLE = 5;
    // ---------------------------------------------------- Variables

    /**
     * Nivel de anidamiendo. Si 1 se divide en dos at�micos.
     */
    private int mLevel;

    // ------------------------------------------------------ Variables de
    // instancia

    // vivo
    protected final static int BMP_YELLOW_2_BODY_ID = R.drawable.yellow_2_body;
    protected final static int BMP_YELLOW_1_BODY_ID = R.drawable.yellow_1_body;
    protected final static int BMP_YELLOW_0_BODY_ID = R.drawable.yellow_0_body;

    protected final static int BMP_YELLOW_2_FOOT_RIGHT_ID = R.drawable.yellow_2_foot_right;
    protected final static int BMP_YELLOW_2_FOOT_LEFT_ID = R.drawable.yellow_2_foot_left;

    protected final static int BMP_YELLOW_0_FOOT_RIGHT_ID = R.drawable.yellow_0_foot_right;
    protected final static int BMP_YELLOW_0_FOOT_LEFT_ID = R.drawable.yellow_0_foot_left;

    protected final static int BMP_YELLOW_2_HAND_RIGHT_ID = R.drawable.yellow_2_hand_right;
    protected final static int BMP_YELLOW_2_HAND_LEFT_ID = R.drawable.yellow_2_hand_left;

    protected final static int BMP_YELLOW_0_HAND_RIGHT_ID = R.drawable.yellow_0_hand_right;
    protected final static int BMP_YELLOW_0_HAND_LEFT_ID = R.drawable.yellow_0_hand_left;

    // debris
    protected final static int BMP_DEBRIS_YELLOW_2_BODY_ID = R.drawable.yellow_debris_2_body;
    protected final static int BMP_DEBRIS_YELLOW_1_BODY_ID = R.drawable.yellow_debris_1_body;
    protected final static int BMP_DEBRIS_YELLOW_0_BODY_ID = R.drawable.yellow_debris_0_body;

    protected final static int BMP_DEBRIS_YELLOW_2_FOOT_RIGHT_ID = R.drawable.yellow_debris_2_foot_right;
    protected final static int BMP_DEBRIS_YELLOW_2_FOOT_LEFT_ID = R.drawable.yellow_debris_2_foot_left;

    protected final static int BMP_DEBRIS_YELLOW_0_FOOT_RIGHT_ID = R.drawable.yellow_debris_0_foot_right;
    protected final static int BMP_DEBRIS_YELLOW_0_FOOT_LEFT_ID = R.drawable.yellow_debris_0_foot_left;

    protected final static int BMP_DEBRIS_YELLOW_2_HAND_RIGHT_ID = R.drawable.yellow_debris_2_hand_right;
    protected final static int BMP_DEBRIS_YELLOW_2_HAND_LEFT_ID = R.drawable.yellow_debris_2_hand_left;

    protected final static int BMP_DEBRIS_YELLOW_0_HAND_RIGHT_ID = R.drawable.yellow_debris_0_hand_right;
    protected final static int BMP_DEBRIS_YELLOW_0_HAND_LEFT_ID = R.drawable.yellow_debris_0_hand_left;

    // ----------------------------------------------------------------
    // Constructor

    public SplitterEnemyActor(JumplingsGameWorld mWorld) {
        super(mWorld);
    }

    public void init(PointF worldPos, int level) {
        if (level > 2) {
            throw new IllegalArgumentException("Maximun level for " + SplitterEnemyActor.class.getCanonicalName() + " is 2");
        }
        mLevel = level;
        mRadius = DEFAULT_BASE_RADIUS + level * DEFAULT_BASE_RADIUS * RADIUS_FACTOR;

        switch (mLevel) {
        case 2:
            mCode = SplitterEnemyActor.JUMPER_CODE_SPLITTER_TRIPLE;
            break;
        case 1:
            mCode = SplitterEnemyActor.JUMPER_CODE_SPLITTER_DOUBLE;
            break;
        case 0:
            mCode = SplitterEnemyActor.JUMPER_CODE_SPLITTER_SIMPLE;
            break;
        }
        super.init(worldPos);
    }

    // ----------------------------------------- M�todos de EnemyActor

    private final float getRestorationInitVy(float posY) {
        float maxHeight = posY + HEIGHT_RESTORATION_FACTOR
                * (getWorld().mViewport.getWorldBoundaries().top - getWorld().mViewport.getWorldBoundaries().bottom - posY);
        return (float) getInitialYVelocity(maxHeight);
    }

    static double getSplitterBaseThread(int splitLevel) {
        double count = 0;
        for (int i = splitLevel; i >= 0; i--) {
            count += Math.pow(2, i);
        }
        return count;
    }

    // ----------------------------------------- M�todos de SimpleActor

    @Override
    protected void initBodies(PointF worldPos) {
        // Cuerpo
        {

            mMainBody = getWorld().createBody(this, worldPos, true);
            mMainBody.setBullet(true);

            // n�mero de segmentos que conforman la circunferencia
            // se hace el m�nimo con 8, por una limitaci�n que tiene box2d
            int sides = Math.min(8, 3 + (2 * mLevel));

            // Create Shape with Properties
            PolygonShape polygonShape = new PolygonShape();

            // v�rtices que conforman la "circunferencia" (pol�gono)
            float[][] aux = MathUtils.getPolyconVertexes(0, 0, mRadius, sides);
            Vector2[] vertices = new Vector2[aux.length];

            int l = vertices.length;
            for (int i = 0; i < l; i++) {
                float[] point = aux[i];
                PointF pointf = new PointF(point[0], point[1]);
                vertices[i] = Viewport.pointFToVector2(pointf);
            }

            polygonShape.set(vertices);

            // Assign shape to Body
            Fixture f = mMainBody.createFixture(polygonShape, 1.0f);
            f.setFilterData(CONTACT_FILTER);
            polygonShape.dispose();

        }

        mAnthtopoDelegate.createAnthropomorphicLimbs(worldPos, mRadius);
    }

    @Override
    protected void initBitmaps() {
        BitmapManager mb = getWorld().getBitmapManager();
        switch (mLevel) {
        case 2:
            // vivo
            mAnthtopoDelegate.initAnthropomorphicBitmaps(BMP_YELLOW_2_BODY_ID, BMP_YELLOW_2_FOOT_RIGHT_ID, BMP_YELLOW_2_FOOT_LEFT_ID,
                    BMP_YELLOW_2_HAND_RIGHT_ID, BMP_YELLOW_2_HAND_LEFT_ID, BMP_EYE_2_RIGHT_OPENED_ID, BMP_EYE_2_LEFT_OPENED_ID,
                    BMP_EYE_2_RIGHT_CLOSED_ID, BMP_EYE_2_LEFT_CLOSED_ID);

            // debris
            mBmpDebrisBody = mb.getBitmap(BMP_DEBRIS_YELLOW_2_BODY_ID);

            mBmpDebrisFootRight = mb.getBitmap(BMP_DEBRIS_YELLOW_2_FOOT_RIGHT_ID);
            mBmpDebrisFootLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_2_FOOT_LEFT_ID);

            mBmpDebrisHandRight = mb.getBitmap(BMP_DEBRIS_YELLOW_2_HAND_RIGHT_ID);
            mBmpDebrisHandLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_2_HAND_LEFT_ID);

            mBmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_2_RIGHT_ID);
            mBmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_2_LEFT_ID);
            break;
        case 1:
            // vivo
            mAnthtopoDelegate.initAnthropomorphicBitmaps(BMP_YELLOW_1_BODY_ID, BMP_YELLOW_0_FOOT_RIGHT_ID, BMP_YELLOW_0_FOOT_LEFT_ID,
                    BMP_YELLOW_0_HAND_RIGHT_ID, BMP_YELLOW_0_HAND_LEFT_ID, BMP_EYE_0_RIGHT_OPENED_ID, BMP_EYE_0_LEFT_OPENED_ID,
                    BMP_EYE_0_RIGHT_CLOSED_ID, BMP_EYE_0_LEFT_CLOSED_ID);

            // debris
            mBmpDebrisBody = mb.getBitmap(BMP_DEBRIS_YELLOW_1_BODY_ID);

            mBmpDebrisFootRight = mb.getBitmap(BMP_DEBRIS_YELLOW_0_FOOT_RIGHT_ID);
            mBmpDebrisFootLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_0_FOOT_LEFT_ID);

            mBmpDebrisHandRight = mb.getBitmap(BMP_DEBRIS_YELLOW_0_HAND_RIGHT_ID);
            mBmpDebrisHandLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_0_HAND_LEFT_ID);

            mBmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_0_RIGHT_ID);
            mBmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_0_LEFT_ID);
            break;
        case 0:
            // vivo
            mAnthtopoDelegate.initAnthropomorphicBitmaps(BMP_YELLOW_0_BODY_ID, BMP_YELLOW_0_FOOT_RIGHT_ID, BMP_YELLOW_0_FOOT_LEFT_ID,
                    BMP_YELLOW_0_HAND_RIGHT_ID, BMP_YELLOW_0_HAND_LEFT_ID, BMP_EYE_0_RIGHT_OPENED_ID, BMP_EYE_0_LEFT_OPENED_ID,
                    BMP_EYE_0_RIGHT_CLOSED_ID, BMP_EYE_0_LEFT_CLOSED_ID);

            // debris
            mBmpDebrisBody = mb.getBitmap(BMP_DEBRIS_YELLOW_0_BODY_ID);

            mBmpDebrisFootRight = mb.getBitmap(BMP_DEBRIS_YELLOW_0_FOOT_RIGHT_ID);
            mBmpDebrisFootLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_0_FOOT_LEFT_ID);

            mBmpDebrisHandRight = mb.getBitmap(BMP_DEBRIS_YELLOW_0_HAND_RIGHT_ID);
            mBmpDebrisHandLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_0_HAND_LEFT_ID);

            mBmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_0_RIGHT_ID);
            mBmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_0_LEFT_ID);
            break;
        }
    }

    @Override
    public void onHitted() {
        if (mLevel > 0) {
            RectF b = getWorld().mViewport.getWorldBoundaries();
            SplitterEnemyActor actor1 = null;
            SplitterEnemyActor actor2 = null;

            Vector2 wc = mMainBody.getWorldCenter();

            // Coordenadas de los nuevos enemigos.
            // Se aplica correcci�n para que salgan dentro de la pantalla.
            float posX1 = Math.max(b.left + mRadius, wc.x - mRadius);
            posX1 = Math.min(posX1, b.right - mRadius);
            float posY1 = Math.max(b.bottom + mRadius, wc.y - mRadius);
            posY1 = Math.min(posY1, b.top - mRadius);

            float posX2 = Math.max(b.left + mRadius, wc.x + mRadius);
            posX2 = Math.min(posX2, b.right - mRadius);
            float posY2 = Math.max(b.bottom + mRadius, wc.y - mRadius);
            posY2 = Math.min(posY2, b.top - mRadius);

            actor1 = getWorld().getFactory().getSplitterEnemyActor(new PointF(posX1, posY1), mLevel - 1);

            actor2 = getWorld().getFactory().getSplitterEnemyActor(new PointF(posX2, posY2), mLevel - 1);

            float xVel = mRadius * mLevel * 2;
            float yVel = getRestorationInitVy(getWorldPos().y);

            getWorld().addActor(actor1);
            actor1.setLinearVelocity(-xVel, yVel);

            getWorld().addActor(actor2);
            actor2.setLinearVelocity(xVel, yVel);

        }

        super.onHitted();
    }

    @Override
    protected void free(JumplingsFactory factory) {
        getWorld().getFactory().free(this);
    }

}
