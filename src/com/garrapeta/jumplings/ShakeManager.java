package com.garrapeta.jumplings;

import android.graphics.Canvas;

import com.garrapeta.gameengine.LevelBasedResourcesManager;
import com.garrapeta.gameengine.Viewport;

public class ShakeManager {

	private final CustomLevelBasedResourcesManager mCustomLevelBasedResourcesManager;
	
    /** Duranci�n en ms del shake actual */
    private float mShakeDuration = 0;
    /** Tiempo que le queda al shake actual */
    private float mShakeRemaining = 0;
    /** Intensidad, en unidades del mundo, del shake actual */
    private float mShakeIntensity = 0;
    
    private boolean mCanvasRestorePending;
    
    final static short ENEMY_KILLED_SHAKE = 0;
    final static short PLAYER_FAIL_SHAKE = 1;
   
	public ShakeManager(short minimumLevel, JumplingsGameWorld jumplingsGameWorld) {
		mCustomLevelBasedResourcesManager = new CustomLevelBasedResourcesManager(minimumLevel);
		mCustomLevelBasedResourcesManager.create(PermData.CFG_LEVEL_ALL, ENEMY_KILLED_SHAKE).add(new ShakeData(100f, 0.20f));
		mCustomLevelBasedResourcesManager.create(PermData.CFG_LEVEL_SOME, PLAYER_FAIL_SHAKE).add(new ShakeData(425f, 0.75f));
	}
	
	public boolean shake(short key) {
		return mCustomLevelBasedResourcesManager.executeOverOneResourceForKey(key);
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
	
	private class CustomLevelBasedResourcesManager extends LevelBasedResourcesManager<ShakeData, ShakeData, Void>  {
		
		private CustomLevelBasedResourcesManager(short minimumLevel) {
			super(minimumLevel);
		}

		@Override
		protected ShakeData load(ShakeData shakeData) {
			return shakeData;
		}

		@Override
		protected void onExecute(ShakeData shakeData, Void... params) {
			ShakeManager.this.createShake(shakeData);
		}

		@Override
		protected void onRelease(ShakeData shakeData) {
			// nothing
		}
	}

}
