package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.jumplings.JumplingsGameWorld;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * 
 * TODO: avoid haviing only one flash actor active or blend colours
 *
 */
public class FlashActor extends Actor {

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

    float longevity;

    float lifeTime;

    private float baseAlpha;
    private float baseRed;
    private float baseGreen;
    private float baseBlue;
    private boolean mActive;
    private int mPriority = Integer.MIN_VALUE;

    // ---------------------------------------------------------- Constructor

    public FlashActor(JumplingsGameWorld cWorld) {
        super(cWorld, Integer.MAX_VALUE);
        
    }

    // ------------------------------------------------------------- M�todos

    public void init(int color, int alpha, int longevity, int priority) {
        if (mActive && priority < mPriority) {
            return;
        }
        this.baseAlpha = Color.alpha(color) * (alpha / 255f);
        this.baseRed = Color.red(color);
        this.baseGreen = Color.green(color);
        this.baseBlue = Color.blue(color);
        this.longevity = longevity;
        this.lifeTime = longevity;
        this.mPriority = priority;
        mActive = lifeTime > 0;
    }

    @Override
    public void processFrame(float gameTimeStep) {
        if (!mActive) {
            return;
        }
        lifeTime = Math.max(0, lifeTime - gameTimeStep);
        if (lifeTime <= 0) {
            mActive = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mActive) {
            return;
        }
        float alphaFactor = lifeTime / longevity;
        float finalAlpha = alphaFactor * baseAlpha;
        canvas.drawARGB((int) finalAlpha, (int) baseRed, (int) baseGreen, (int) baseBlue);
    }

}
