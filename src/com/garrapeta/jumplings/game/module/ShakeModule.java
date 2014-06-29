package com.garrapeta.jumplings.game.module;

import android.graphics.Canvas;

import com.garrapeta.gameengine.Viewport;
import com.garrapeta.gameengine.module.LevelActionsModule;
import com.garrapeta.jumplings.game.JumplingsGameWorld;
import com.garrapeta.jumplings.util.PermData;

public class ShakeModule {

    public final static short ENEMY_KILLED_SHAKE = 0;
    public final static short PLAYER_FAIL_SHAKE = 1;

    private final ShakeModuleDelegate mDelegate;

    /** Duranci�n en ms del shake actual */
    private float mShakeDuration = 0;
    /** Tiempo que le queda al shake actual */
    private float mShakeRemaining = 0;
    /** Intensidad, en unidades del mundo, del shake actual */
    private float mShakeIntensity = 0;

    private boolean mCanvasRestorePending;

    public ShakeModule(short minimumLevel, JumplingsGameWorld jumplingsGameWorld) {
        mDelegate = new ShakeModuleDelegate(minimumLevel);
        mDelegate.create(PermData.CFG_LEVEL_ALL, ENEMY_KILLED_SHAKE)
                 .add(new ShakeData(100f, 0.20f));
        mDelegate.create(PermData.CFG_LEVEL_SOME, PLAYER_FAIL_SHAKE)
                 .add(new ShakeData(425f, 0.75f));
    }

    public boolean shake(short key) {
        return mDelegate.executeOverOneResourceForKey(key);
    }

    public void processFrame(float gameTimeStep) {
        if (mShakeRemaining > 0) {
            mShakeRemaining -= gameTimeStep;
        }
    }

    public void preDraw(Canvas canvas, Viewport viewport) {
        if (mShakeRemaining > 0) {
            float intensity = (mShakeRemaining / mShakeDuration) * mShakeIntensity;

            float pixels = (int) viewport.worldUnitsToPixels(intensity);

            float pixelsX = pixels;
            if (Math.random() > 0.5) {
                pixelsX *= -1;
            }

            float pixelsY = pixels;
            if (Math.random() > 0.5) {
                pixelsY *= -1;
            }

            canvas.save();
            canvas.translate(pixelsX, pixelsY);
            mCanvasRestorePending = true;
        }
    }

    public void postDraw(Canvas canvas) {
        if (mCanvasRestorePending) {
            canvas.restore();
            mCanvasRestorePending = false;
        }
    }

    private void createShake(ShakeData shakeData) {
        mShakeDuration = shakeData.mShakeDuration;
        mShakeRemaining = mShakeDuration;
        mShakeIntensity = shakeData.mShakeIntensity;
    }

    private static class ShakeData {
        private final float mShakeDuration;
        private final float mShakeIntensity;

        private ShakeData(float shakeDuration, float shakeIntensity) {
            mShakeDuration = shakeDuration;
            mShakeIntensity = shakeIntensity;
        }
    }

    /**
     * Delegate used by the module
     * 
     * @author garrapeta
     */
    private class ShakeModuleDelegate extends LevelActionsModule<ShakeData, Void> {

        private ShakeModuleDelegate(short minimumLevel) {
            super(minimumLevel);
        }

        @Override
        protected void onExecute(ShakeData shakeData, Void... params) {
            ShakeModule.this.createShake(shakeData);
        }
    }

}
