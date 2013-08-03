package com.garrapeta.jumplings.actor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;

import com.garrapeta.gameengine.Actor;
import com.garrapeta.jumplings.JumplingsApplication;
import com.garrapeta.jumplings.JumplingsGameWorld;

public abstract class TextActor extends Actor<JumplingsGameWorld> {

    // ----------------------------------------------------- Constantes

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 20;

    // ----------------------------------------- Variables de instancia

    private JumplingsGameWorld cWorld;

    protected String mText;
    protected Paint mPaint;

    PointF worldPos;

    float mLongevity;

    float mLifeTime = mLongevity;

    protected float mYVel;

    // -------------------------------------------------- Constructores

    public TextActor(JumplingsGameWorld cWorld, PointF worldPos) {
        super(cWorld, Z_INDEX);
        this.cWorld = cWorld;
        this.worldPos = worldPos;

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setTypeface(JumplingsApplication.game_font);
    }

    // -------------------------------------------------------- M�todos

    @Override
    public void processFrame(float gameTimeStep) {
        worldPos.y += mYVel * (gameTimeStep / 1000);

        mLifeTime = Math.max(0, mLifeTime - gameTimeStep);
        if (mLifeTime <= 0) {
            getWorld().removeActor(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int a = (int) ((mLifeTime / mLongevity) * 255);
        mPaint.setAlpha(a);
        PointF screenPos = cWorld.mViewport.worldToScreen(worldPos);
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
