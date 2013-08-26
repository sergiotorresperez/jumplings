package com.garrapeta.jumplings.actor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.PointF;

import com.garrapeta.gameengine.Actor;
import com.garrapeta.jumplings.JumplingsGameWorld;

public abstract class TextActor extends Actor<JumplingsGameWorld> {

    // ----------------------------------------------------- Constantes

    private static final String GAME_FONT_PATH = "fonts/AnuDaw.ttf";
    
    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 20;
    public final Typeface mGameFont;

    // ----------------------------------------- Variables de instancia

    private JumplingsGameWorld mJgWorld;

    protected String mText;
    protected Paint mPaint;

    PointF mWorldPos;

    float mLongevity;

    float mLifeTime = mLongevity;

    protected float mYVel;

    // -------------------------------------------------- Constructores

    public TextActor(JumplingsGameWorld jgWorld, PointF worldPos) {
        super(jgWorld, Z_INDEX);
        mJgWorld = jgWorld;
        mWorldPos = worldPos;
        mGameFont = Typeface.createFromAsset(mJgWorld.mActivity.getAssets(), GAME_FONT_PATH);
        
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setTypeface(mGameFont);
    }

    // -------------------------------------------------------- M�todos

    @Override
    public void processFrame(float gameTimeStep) {
        mWorldPos.y += mYVel * (gameTimeStep / 1000);

        mLifeTime = Math.max(0, mLifeTime - gameTimeStep);
        if (mLifeTime <= 0) {
            getWorld().removeActor(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int a = (int) ((mLifeTime / mLongevity) * 255);
        mPaint.setAlpha(a);
        PointF screenPos = mJgWorld.mViewport.worldToScreen(mWorldPos);
        canvas.drawText(mText, screenPos.x, screenPos.y, mPaint);
    }

    @Override
    protected void dispose() {
        mText = null;
        mPaint = null;
    }
 
    public void forceDisappear() {
        mLifeTime =mLifeTime / 4;
    }
}
