package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.jumplings.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class SparksActor extends JumplingActor {

    // ----------------------------------------------------------- Constantes
    public final static float DEFAULT_RADIUS = BASE_RADIUS * 1.2f;

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 0;

    public final static int SPARKS_FILTER_BIT = 0x00020;

    private final static Filter SPARKS_FILTER;

    // ------------------------------------------------- Variables est�ticas

    // Vivo
    protected static final int[] bmpsSparkles = { R.drawable.sparks_big_0, R.drawable.sparks_big_1, R.drawable.sparks_big_2, R.drawable.sparks_big_3 };

    // ----------------------------------------------- Variables de instancia

    float mLongevity;

    float mLifeTime;

    protected int mAlpha;

    protected Bitmap mBmpSparkle;

    protected Paint paint;

    // ----------------------------------------------- Inicializaci�n est�tica

    static {
        SPARKS_FILTER = new Filter();

        SPARKS_FILTER.categoryBits = SPARKS_FILTER_BIT;

        SPARKS_FILTER.maskBits = SPARKS_FILTER_BIT | WallActor.WALL_FILTER_BIT;
    }

    // ---------------------------------------------------- M�todos est�ticos

    // --------------------------------------------------- Constructor

    public SparksActor(JumplingsWorld jWorld, PointF worldPos, int longevity) {
        super(jWorld, SparksActor.DEFAULT_RADIUS, Z_INDEX, worldPos);
        mLongevity = mLifeTime = longevity;
        paint = new Paint();
        init(worldPos);
    }

    // --------------------------------------------- M�todos heredados


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
            f.setFilterData(SPARKS_FILTER);
            circleShape.dispose();
        }

    }
    
    @Override
    protected void initBitmaps() {
        BitmapManager mb = mJWorld.getBitmapManager();
        mBmpSparkle = mb.getBitmap(bmpsSparkles[(int) (Math.random() * bmpsSparkles.length)]);
    }

    @Override
    public void processFrame(float gameTimeStep) {
        mLifeTime = Math.max(0, mLifeTime - gameTimeStep);
        if (mLifeTime <= 0) {
            mGameWorld.removeActor(this);
        }
        mAlpha = (int) (255 * mLifeTime / mLongevity);
    }

    @Override
    protected void drawBitmaps(Canvas canvas) {
        paint.setAlpha(mAlpha);
        mJWorld.drawBitmap(canvas, this.mainBody, mBmpSparkle, paint);
    }

}
