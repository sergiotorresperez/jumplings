package com.garrapeta.jumplings.actor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.RectF;

import com.garrapeta.gameengine.Viewport;
import com.garrapeta.jumplings.JumplingsGameWorld;

public class HarmerSlapActor extends HarmerActor {

    // ----------------------------------------------------- Constantes

    private final static float KILL_RADIUS = 0.7f;
    private final static float BLAST_RADIUS = 3;
    private final static float BLAST_FORCE = 20; // 35;

    // ----------------------------------------- Variables de instancia

    protected Paint mPaint;

    PointF mWorldPos;

    public float mLongevity;

    public float mLifeTime = mLongevity;

    private float mMaxExplosionRadius;

    protected JumplingsGameWorld mWorld;

    private boolean mFirstFrame = true;

    private boolean mAlreadyKilled = false;
    
    // Rectangles to compute the intersection with enemies
    private final RectF mIntersectionRectThis;
    private final RectF mIntersectionRectOther;

    // -------------------------------------------------- Constructores

    public HarmerSlapActor(JumplingsGameWorld cWorld, PointF worldPos, float maxRadius, float longevity) {
        super(cWorld);
        mWorld = cWorld;
        mWorldPos = worldPos;
        mMaxExplosionRadius = maxRadius;
        mLongevity = longevity;
        mLifeTime = longevity;

        mPaint = new Paint();
        mPaint.setColor(Color.YELLOW);
        mPaint.setTextAlign(Align.CENTER);
        
        mIntersectionRectThis = new RectF();
        mIntersectionRectOther = new RectF();

        mTimestamp = System.currentTimeMillis();

    }

    // ----------------------------------------------- M�todos heredados

    @Override
    public void processFrame(float gameTimeStep) {

        // vida de la bala
        mLifeTime = Math.max(0, mLifeTime - gameTimeStep);
        if (mLifeTime <= 0) {
            mWorld.removeActor(this);
        }

        super.processFrame(gameTimeStep);

        mFirstFrame = false;
    }

    @Override
    protected void dispose() {
        super.dispose();
        mWorld = null;
        mPaint = null;
        mWorldPos = null;
    }

    @Override
    protected void effectOver(MainActor j) {
        if (mFirstFrame) {
            if (this.kills(j)) {
                j.onHitted();
            } else {
                // se aplica onda expansiva
                mWorld.applyBlast(Viewport.pointFToVector2(mWorldPos), j.mMainBody, BLAST_RADIUS, BLAST_FORCE);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int a = (int) ((mLifeTime / mLongevity) * 255);
        mPaint.setAlpha(a);
        PointF screenPos = mWorld.mViewport.worldToScreen(mWorldPos);
        float currentRadius = ((mLongevity - mLifeTime) / mLongevity) * mMaxExplosionRadius;
        canvas.drawCircle(screenPos.x, screenPos.y, mWorld.mViewport.worldUnitsToPixels(currentRadius), mPaint);
    }

    // --------------------------------------- M�todos propios

    private boolean hits(MainActor mainActor) {
        final PointF pos = mainActor.getWorldPos();
        final float radius = mainActor.mRadius;
        mIntersectionRectOther.set(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius);
        mIntersectionRectThis.set(mWorldPos.x - KILL_RADIUS, mWorldPos.y - KILL_RADIUS, mWorldPos.x + KILL_RADIUS, mWorldPos.y + KILL_RADIUS);
        return RectF.intersects(mIntersectionRectOther, mIntersectionRectThis);
    }

    private boolean kills(MainActor mainActor) {
        if (!mAlreadyKilled) {
            if (hits(mainActor)) {
                mAlreadyKilled = true;
                return true;
            }
        }
        return false;
    }

}
