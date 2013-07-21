package com.garrapeta.jumplings.actor;

import java.util.ArrayList;

import com.garrapeta.jumplings.JumplingsWorld;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class DebrisActor extends JumplingActor<JumplingsWorld> {

    // ----------------------------------------------------------- Constantes

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = -10;

    public final static Filter DEBRIS_FILTER;

    public final static int DEBRIS_FILTER_BIT = 0x00004;

    /** Tiempo que permanece el actor en pantalla, en ms */
    private final static int DEFAULT_LONGEVITY = 1250;

    // ------------------------------------------------ Variables est�ticas

    protected Bitmap mBitmap;

    protected Paint mPaint;

    protected int mAlpha;

    // --------------------------------------------- Variables de instancia

    float mLongevity = DEFAULT_LONGEVITY;

    float mLifeTime;

    // --------------------------------------------------- Inicialización  estática

    static {

        DEBRIS_FILTER = new Filter();

        DEBRIS_FILTER.categoryBits = DebrisActor.DEBRIS_FILTER_BIT;

        DEBRIS_FILTER.maskBits = WallActor.WALL_FILTER_BIT | WallActor.FLOOR_FILTER_BIT ;

    }

    // ---------------------------------------------------------- Constructor

    public DebrisActor(JumplingsWorld world) {
        super(world, Z_INDEX);
        mPaint = new Paint();
    }

    public void init(Body body, Bitmap bitmap) {
        mBitmap = bitmap;
        mMainBody = body;
        addBody(mMainBody);
        // FIXME: avoid this void values
        init(null);
    }

    public void init(PointF worldPos) {
        super.init(worldPos);
        mLifeTime = mLongevity;
    }

    // ------------------------------------------------------------- M�todos

    @Override
    protected void initBitmaps() {
        // FIXME: this is done in the initialiser...
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
        getWorld().drawBitmap(canvas, mMainBody, mBitmap, mPaint);
    }

    @Override
    public void processFrame(float gameTimeStep) {
        mLifeTime = Math.max(0, mLifeTime - gameTimeStep);
        if (mLifeTime <= 0) {
            getWorld().removeActor(this);
        }
        mAlpha = (int) (255 * mLifeTime / mLongevity);
    }

    @Override
    protected void free(JumplingsFactory factory) {
        getWorld().getFactory().free(this);
    }

    @Override
    protected void dispose() {
        super.dispose();
        mBitmap = null;
        mPaint = null;
    }
}
