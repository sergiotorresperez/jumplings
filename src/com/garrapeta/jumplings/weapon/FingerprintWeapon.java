package com.garrapeta.jumplings.weapon;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.gameengine.Viewport;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.actor.MainActor;
import com.garrapeta.jumplings.module.FlashModule;

public class FingerprintWeapon extends Weapon {

    // --------------------------------------------------------------------
    // Constantes

    public final static short WEAPON_CODE_FINGERPRINT = 0;

    private final static Random sRandom = new Random();
    
    // -------------------------------------------------------- Variables de
    // instancia

    protected long lastShootTimeStamp;

    protected int shootTimeGap;

    FingerprintWeapon(JumplingsGameWorld jgWorld) {
        super(jgWorld);
        shootTimeGap = 100;
    }

    @Override
    public void onTouchEvent(double[] info) {
        if (info[0] == MotionEvent.ACTION_DOWN && (System.currentTimeMillis() - lastShootTimeStamp) >= shootTimeGap) {

        	mWorld.mFlashModule.flash(FlashModule.TAP_KEY);
            mWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_FINGERPRINT);

            lastShootTimeStamp = System.currentTimeMillis();

            PointF worldPos = mWorld.mViewport.screenToWorld((float) info[1], (float) info[2]);
            FingerprintActor bullet = new FingerprintActor(mWorld, worldPos);
            bullet.setInitted();
 
            mWorld.addActor(bullet);
        }
    }

    public short getWeaponCode() {
        return WEAPON_CODE_FINGERPRINT;
    }

    @Override
    public void onStart(float gameTimeStep) {
    	// nothing
    }

	@Override
	public void processFrame(float gameTimeStep) {
		// nothing
	}

	/**
	 * The actor that harms
	 */
	private class FingerprintActor extends HarmerActor {

	    // ----------------------------------------------------- Constantes

	    private final static float KILL_RADIUS = 1.2f;
	    private final static float BLAST_RADIUS = 2;
	    private final static float BLAST_FORCE = 20;
	    private final static float LONGEVITY = 150;

	    protected final static int BMP_FINGERPRINT_ID = R.drawable.fingerprint;
	    
	    private final static int MAX_ANGLE = 30; 
	    // ----------------------------------------- Variables de instancia

	    protected Paint mPaint;

	    PointF mWorldPos;

	    public float mLifeTime;

	    protected JumplingsGameWorld mWorld;

	    private boolean mFirstFrame = true;

	    private boolean mAlreadyKilled = false;
	    
	    // Rectangles to compute the intersection with enemies
	    private final RectF mIntersectionRectThis;
	    private final RectF mIntersectionRectOther;

	    private Bitmap mBmpFingerprint;
	    
	    private final float mAngle;
	    // -------------------------------------------------- Constructores

	    public FingerprintActor(JumplingsGameWorld cWorld, PointF worldPos) {
	        super(cWorld);
	        mWorld = cWorld;
	        mWorldPos = worldPos;
	        mLifeTime = LONGEVITY;

	        mPaint = new Paint();
	        mPaint.setColor(Color.YELLOW);
	        mPaint.setTextAlign(Align.CENTER);
	        
	        BitmapManager mb = getWorld().getBitmapManager();
	        mBmpFingerprint = mb.getBitmap(BMP_FINGERPRINT_ID);
	        
	        mIntersectionRectThis = new RectF();
	        mIntersectionRectOther = new RectF();

	        mTimestamp = System.currentTimeMillis();

	        mAngle = -MAX_ANGLE + sRandom.nextInt(MAX_ANGLE * 2);
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
	        int alpha = (int) ((mLifeTime / LONGEVITY) * 255);
	        mPaint.setAlpha(alpha);
	        PointF screenPos = mWorld.mViewport.worldToScreen(mWorldPos);
	        
	        if (getWorld().mDrawActorBitmaps) {
	        	mWorld.drawBitmap(canvas, screenPos.x, screenPos.y, mAngle, mBmpFingerprint, mPaint);
	        }
	        if (getWorld().mWireframeMode || !getWorld().mDrawActorBitmaps) {
	        	canvas.drawCircle(screenPos.x, screenPos.y, mWorld.mViewport.worldUnitsToPixels(KILL_RADIUS), mPaint);
	        }
	    }

	    // ------------------------------------------------- Self methods

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
	                mLifeTime = Math.min(mLifeTime, LONGEVITY / 3);
	                return true;
	            }
	        }
	        return false;
	    }

	}

}
