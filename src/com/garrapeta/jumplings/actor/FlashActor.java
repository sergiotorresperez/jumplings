package com.garrapeta.jumplings.actor;

import com.garrapeta.gameengine.Actor;
import com.garrapeta.jumplings.JumplingsGameWorld;
import android.graphics.Canvas;
import android.graphics.Color;

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

    public final static int FLASH_SHOT_DURATION = 100;
    public final static int FLASH_SHOT_ALPHA = 200;
    public final static int FLASH_SHOT_COLOR = Color.WHITE;
    public final static int FLASH_SHOT_PRIORITY = -1;

    public final static int FLASH_FAIL_DURATION = 1000;
    public final static int FLASH_FAIL_ALPHA = 230;
    public final static int FLASH_FAIL_COLOR = Color.BLACK;
    public final static int FLASH_FAIL_PRIORITY = 1;

//    public final static int FLASH_BOMB_DURATION = 2000;
//    public final static int FLASH_BOMB_ALPHA = 255;
//    public final static int FLASH_BOMB_COLOR = Color.WHITE;
//
//    public final static int FLASH_BOMB2_DURATION = 100;
//    public final static int FLASH_BOMB2_ALPHA = 255;
//    public final static int FLASH_BOMB2_COLOR = Color.BLACK;

    public final static int FLASH_LIFEUP_DURATION = 750;
    public final static int FLASH_LIFEUP_ALPHA = 180;
    public final static int FLASH_LIFEUP_COLOR = Color.rgb(255, 105, 180);
    public final static int FLASH_LIFEUP_PRIORITY = 2;

    public final static int FLASH_BLADE_DRAWN_DURATION = 750;
    public final static int FLASH_BLADE_DRAWN_ALPHA = 180;
    public final static int FLASH_BLADE_DRAWN_COLOR = Color.BLUE;
    public final static int FLASH_BLADE_DRAWN_PRIORITY = 0;

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

    public void init(int color, int alpha, int longevity, int priority) {
        if (mActive && priority < mPriority) {
            return;
        }
        this.mBaseAlpha = Color.alpha(color) * (alpha / 255f);
        this.mBaseRed = Color.red(color);
        this.mBaseGreen = Color.green(color);
        this.mBaseBlue = Color.blue(color);
        this.mLongevity = longevity;
        this.lmLfeTime = longevity;
        this.mPriority = priority;
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

}
