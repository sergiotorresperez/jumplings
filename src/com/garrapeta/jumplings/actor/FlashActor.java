package com.garrapeta.jumplings.actor;

import android.graphics.Canvas;
import android.graphics.Color;

import com.garrapeta.gameengine.Actor;
import com.garrapeta.jumplings.JumplingsGameWorld;

/**
 * 
 * TODO: avoid haviing only one flash actor active or blend colours
 * 
 */
public class FlashActor extends Actor<JumplingsGameWorld> {

    // ----------------------------------------------------------- Constantes

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = Integer.MAX_VALUE;

    // ------------------------------------------------ Variables de instancia

    float mLongevity;

    float lmLfeTime;

    private float mBaseAlpha;
    private float mBaseRed;
    private float mBaseGreen;
    private float mBaseBlue;
    private boolean mActive;
    private int mPriority = Integer.MIN_VALUE;

    // ---------------------------------------------------------- Constructor

    public FlashActor(JumplingsGameWorld world) {
        super(world, Integer.MAX_VALUE);

    }

    // ------------------------------------------------------------- M�todos

    public void init(FlashData data) {
        if (mActive && data.mPriority < mPriority) {
            return;
        }
        this.mBaseAlpha = Color.alpha(data.mColor) * (data.mAlpha / 255f);
        this.mBaseRed = Color.red(data.mColor);
        this.mBaseGreen = Color.green(data.mColor);
        this.mBaseBlue = Color.blue(data.mColor);
        this.mLongevity = data.mLongevity;
        this.lmLfeTime = data.mLongevity;
        this.mPriority = data.mPriority;
        mActive = lmLfeTime > 0;
    }

    @Override
    public void processFrame(float gameTimeStep) {
        if (!mActive) {
            return;
        }
        lmLfeTime = Math.max(0, lmLfeTime - gameTimeStep);
        if (lmLfeTime <= 0) {
            mActive = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mActive) {
            return;
        }
        float alphaFactor = lmLfeTime / mLongevity;
        float finalAlpha = alphaFactor * mBaseAlpha;
        canvas.drawARGB((int) finalAlpha, (int) mBaseRed, (int) mBaseGreen, (int) mBaseBlue);
    }

    public static class FlashData {
        int mColor;
        int mAlpha;
        int mLongevity;
        int mPriority;

        public FlashData(final int color, final int alpha, final int longevity, final int priority) {
            mColor = color;
            mAlpha = alpha;
            mLongevity = longevity;
            mPriority = priority;
        }
    }

}
