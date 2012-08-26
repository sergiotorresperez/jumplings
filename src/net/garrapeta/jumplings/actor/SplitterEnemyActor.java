package net.garrapeta.jumplings.actor;

import net.garrapeta.MathUtils;
import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
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
    private int level;

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

    public SplitterEnemyActor(JumplingsGameWorld jgWorld, PointF worldPos, int level) {
        super(jgWorld, worldPos);

        this.level = level;
        this.radius = DEFAULT_BASE_RADIUS + (this.level * DEFAULT_BASE_RADIUS * RADIUS_FACTOR);

        BitmapManager mb = jWorld.getBitmapManager();

        switch (level) {
        case 2:
            this.code = SplitterEnemyActor.JUMPER_CODE_SPLITTER_TRIPLE;
            // vivo
            ah.initBitmaps(BMP_YELLOW_2_BODY_ID, BMP_YELLOW_2_FOOT_RIGHT_ID, BMP_YELLOW_2_FOOT_LEFT_ID, BMP_YELLOW_2_HAND_RIGHT_ID,
                    BMP_YELLOW_2_HAND_LEFT_ID, BMP_EYE_2_RIGHT_ID, BMP_EYE_2_LEFT_ID);

            // debris
            bmpDebrisBody = mb.getBitmap(BMP_DEBRIS_YELLOW_2_BODY_ID);

            bmpDebrisFootRight = mb.getBitmap(BMP_DEBRIS_YELLOW_2_FOOT_RIGHT_ID);
            bmpDebrisFootLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_2_FOOT_LEFT_ID);

            bmpDebrisHandRight = mb.getBitmap(BMP_DEBRIS_YELLOW_2_HAND_RIGHT_ID);
            bmpDebrisHandLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_2_HAND_LEFT_ID);

            bmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_2_RIGHT_ID);
            bmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_2_LEFT_ID);
            break;
        case 1:
            this.code = SplitterEnemyActor.JUMPER_CODE_SPLITTER_DOUBLE;
            // vivo
            ah.initBitmaps(BMP_YELLOW_1_BODY_ID, BMP_YELLOW_0_FOOT_RIGHT_ID, BMP_YELLOW_0_FOOT_LEFT_ID, BMP_YELLOW_0_HAND_RIGHT_ID,
                    BMP_YELLOW_0_HAND_LEFT_ID, BMP_EYE_0_RIGHT_ID, BMP_EYE_0_LEFT_ID);

            // debris
            bmpDebrisBody = mb.getBitmap(BMP_DEBRIS_YELLOW_1_BODY_ID);

            bmpDebrisFootRight = mb.getBitmap(BMP_DEBRIS_YELLOW_0_FOOT_RIGHT_ID);
            bmpDebrisFootLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_0_FOOT_LEFT_ID);

            bmpDebrisHandRight = mb.getBitmap(BMP_DEBRIS_YELLOW_0_HAND_RIGHT_ID);
            bmpDebrisHandLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_0_HAND_LEFT_ID);

            bmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_0_RIGHT_ID);
            bmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_0_LEFT_ID);
            break;
        case 0:
            this.code = SplitterEnemyActor.JUMPER_CODE_SPLITTER_SIMPLE;
            // vivo
            ah.initBitmaps(BMP_YELLOW_0_BODY_ID, BMP_YELLOW_0_FOOT_RIGHT_ID, BMP_YELLOW_0_FOOT_LEFT_ID, BMP_YELLOW_0_HAND_RIGHT_ID,
                    BMP_YELLOW_0_HAND_LEFT_ID, BMP_EYE_0_RIGHT_ID, BMP_EYE_0_LEFT_ID);

            // debris
            bmpDebrisBody = mb.getBitmap(BMP_DEBRIS_YELLOW_0_BODY_ID);

            bmpDebrisFootRight = mb.getBitmap(BMP_DEBRIS_YELLOW_0_FOOT_RIGHT_ID);
            bmpDebrisFootLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_0_FOOT_LEFT_ID);

            bmpDebrisHandRight = mb.getBitmap(BMP_DEBRIS_YELLOW_0_HAND_RIGHT_ID);
            bmpDebrisHandLeft = mb.getBitmap(BMP_DEBRIS_YELLOW_0_HAND_LEFT_ID);

            bmpDebrisEyeRight = mb.getBitmap(BMP_DEBRIS_EYE_0_RIGHT_ID);
            bmpDebrisEyeLeft = mb.getBitmap(BMP_DEBRIS_EYE_0_LEFT_ID);
            break;
        }

        initPhysics(worldPos);
    }

    // ----------------------------------------- M�todos de EnemyActor

    private final float getRestorationInitVy(float posY) {
        float maxHeight = posY + HEIGHT_RESTORATION_FACTOR
                * (jgWorld.viewport.getWorldBoundaries().top - jgWorld.viewport.getWorldBoundaries().bottom - posY);
        return (float) getInitialYVelocity(maxHeight);
    }

    static double getSplitterHitCount(int splitLevel) {
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

            mainBody = jgWorld.createBody(this, worldPos, true);
            mainBody.setBullet(true);

            // n�mero de segmentos que conforman la circunferencia
            // se hace el m�nimo con 8, por una limitaci�n que tiene box2d
            int sides = Math.min(8, 3 + (2 * level));

            // Create Shape with Properties
            PolygonShape polygonShape = new PolygonShape();

            // v�rtices que conforman la "circunferencia" (pol�gono)
            float[][] aux = MathUtils.getPolyconVertexes(0, 0, radius, sides);
            Vector2[] vertices = new Vector2[aux.length];

            int l = vertices.length;
            for (int i = 0; i < l; i++) {
                float[] point = aux[i];
                PointF pointf = new PointF(point[0], point[1]);
                vertices[i] = Viewport.pointFToVector2(pointf);
            }

            polygonShape.set(vertices);

            // Assign shape to Body
            Fixture f = mainBody.createFixture(polygonShape, 1.0f);
            f.setFilterData(CONTACT_FILTER);
            polygonShape.dispose();

        }

        ah.createLimbs(worldPos, radius);
    }

    @Override
    public void onHitted() {
        if (level > 0) {
            RectF b = jgWorld.viewport.getWorldBoundaries();
            EnemyActor actor1 = null;
            EnemyActor actor2 = null;

            Vector2 wc = mainBody.getWorldCenter();

            // Coordenadas de los nuevos enemigos.
            // Se aplica correcci�n para que salgan dentro de la pantalla.
            float posX1 = Math.max(b.left + radius, wc.x - radius);
            posX1 = Math.min(posX1, b.right - radius);
            float posY1 = Math.max(b.bottom + radius, wc.y - radius);
            posY1 = Math.min(posY1, b.top - radius);

            float posX2 = Math.max(b.left + radius, wc.x + radius);
            posX2 = Math.min(posX2, b.right - radius);
            float posY2 = Math.max(b.bottom + radius, wc.y - radius);
            posY2 = Math.min(posY2, b.top - radius);

            actor1 = new SplitterEnemyActor(jgWorld, new PointF(posX1, posY1), level - 1);

            actor2 = new SplitterEnemyActor(jgWorld, new PointF(posX2, posY2), level - 1);

            float xVel = radius * level * 2;
            float yVel = getRestorationInitVy(getWorldPos().y);

            jgWorld.addActor(actor1);
            actor1.setLinearVelocity(-xVel, yVel);

            jgWorld.addActor(actor2);
            actor2.setLinearVelocity(xVel, yVel);

        }

        super.onHitted();
    }

}
