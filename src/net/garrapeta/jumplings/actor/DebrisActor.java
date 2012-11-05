package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.jumplings.JumplingsWorld;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class DebrisActor extends JumplingActor {

    // ----------------------------------------------------------- Constantes

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = -10;

    public final static Filter DEBRIS_FILTER;

    public final static int DEBRIS_FILTER_BIT = 0x00004;

    /** Tiempo que permanece el actor en pantalla, en ms */
    private final static int DEFAULT_LONGEVITY = 1500;

    // ------------------------------------------------ Variables est�ticas

    protected Bitmap mBitmap;

    protected Paint mPaint;

    protected int mAlpha;

    // --------------------------------------------- Variables de instancia

    float longevity = DEFAULT_LONGEVITY;

    float lifeTime = longevity;

    // --------------------------------------------------- Inicializaci�n
    // est�tica

    static {

        DEBRIS_FILTER = new Filter();

        DEBRIS_FILTER.categoryBits = DebrisActor.DEBRIS_FILTER_BIT;

        DEBRIS_FILTER.maskBits = WallActor.WALL_FILTER_BIT | WallActor.FLOOR_FILTER_BIT | DebrisActor.DEBRIS_FILTER_BIT;

    }

    // ---------------------------------------------------------- Constructor

    public DebrisActor(JumplingsWorld jWorld, Body body, Bitmap bitmap) {
        // FIXME: avoid this void values
        super(jWorld, 0, Z_INDEX, body);
        mBitmap = bitmap;
        mPaint = new Paint();
        init(null);
    }

    // ------------------------------------------------------------- M�todos

    @Override
    protected void initBitmaps() {
        // FIXME: this is done in the constructor...
    }

    @Override
    protected void initBodies(PointF worldPos) {
        // se cambia el filtro
        ArrayList<Fixture> fs = mMainBody.getFixtureList();
        int l2 = fs.size();
        for (int i2 = 0; i2 < l2; i2++) {
            fs.get(i2).setFilterData(DEBRIS_FILTER);
        }
    }

    protected void drawBitmaps(Canvas canvas) {
        mPaint.setAlpha(mAlpha);
        mJWorld.drawBitmap(canvas, mMainBody, mBitmap, mPaint);
    }

    @Override
    public void processFrame(float gameTimeStep) {
        lifeTime = Math.max(0, lifeTime - gameTimeStep);
        if (lifeTime <= 0) {
            mGameWorld.removeActor(this);
        }
        mAlpha = (int) (255 * lifeTime / longevity);
    }

    @Override
    protected void dispose() {
        super.dispose();
        mBitmap = null;
        mPaint = null;
    }

}
