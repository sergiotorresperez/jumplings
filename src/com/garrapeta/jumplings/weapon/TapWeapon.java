package com.garrapeta.jumplings.weapon;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.actor.MainActor;

public abstract class TapWeapon extends Weapon {

    protected long mTapTimeStamp;

    protected int mTimeGap;

    TapWeapon(JumplingsGameWorld jgWorld, int timeGap) {
        super(jgWorld);
        mTimeGap = timeGap;
    }

    @Override
    public final void onTouchEvent(double[] info) {
        if (info[0] == MotionEvent.ACTION_DOWN && (System.currentTimeMillis() - mTapTimeStamp) >= mTimeGap) {
            mTapTimeStamp = System.currentTimeMillis();

            float worldPositionX = mWorld.mViewport.screenToWorldX((float) info[1]);
            float worldPositionY = mWorld.mViewport.screenToWorldY((float) info[2]);

            TapActor tap = getTapActor(mWorld, new PointF(worldPositionX, worldPositionY));
            tap.setInitted();
            tap.onTapEffect();
            mWorld.addActor(tap);
        }
    }

    protected abstract TapActor getTapActor(JumplingsGameWorld jgWorld, PointF worldPos);

    /**
     * The actor that harms
     */
    protected abstract class TapActor extends HarmerActor {

        protected Paint mPaint;

        PointF mWorldPos;

        private float mTimeLeft;
        private final float mLongevity;
        private final float mKillRadius;

        private boolean mFirstFrame = true;

        private boolean mAlreadyKilled = false;

        // Rectangles to compute the intersection with enemies
        private final RectF mIntersectionRectThis;
        private final RectF mIntersectionRectOther;

        // -------------------------------------------------- Constructores

        public TapActor(JumplingsGameWorld cWorld, PointF worldPos, float longevity, float killRadius) {
            super(cWorld);
            mWorld = cWorld;
            mWorldPos = worldPos;
            mTimeLeft = longevity;
            mLongevity = longevity;
            mKillRadius = killRadius;

            mPaint = new Paint();
            mPaint.setColor(Color.YELLOW);

            mIntersectionRectThis = new RectF();
            mIntersectionRectOther = new RectF();
        }

        @Override
        public final void processFrame(float gameTimeStep) {

            // vida del harmer
            mTimeLeft = Math.max(0, mTimeLeft - gameTimeStep);
            if (mTimeLeft <= 0) {
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
        protected final void effectOver(MainActor actor) {
            if (mFirstFrame) {
                if (this.kills(actor)) {
                    actor.onHitted();
                } else {
                    onMissed(actor);
                }
            }
        }

        protected abstract void onTapEffect();

        @Override
        public final void draw(Canvas canvas) {
            int alpha = (int) ((mTimeLeft / mLongevity) * 255);
            mPaint.setAlpha(alpha);

            float screenPositionX = mWorld.mViewport.worldToScreenX(mWorldPos.x);
            float screenPositionY = mWorld.mViewport.worldToScreenY(mWorldPos.y);

            if (getWorld().mDrawActorBitmaps) {
                drawBitmap(canvas, screenPositionX, screenPositionY);
            }
            if (getWorld().mWireframeMode || !getWorld().mDrawActorBitmaps) {
                canvas.drawCircle(screenPositionX, screenPositionY, mWorld.mViewport.worldUnitsToPixels(mKillRadius), mPaint);
            }
        }

        private final boolean hits(MainActor mainActor) {
            return intersects(mKillRadius, mainActor.mRadius, mainActor.getWorldPos());
        }

        private final boolean kills(MainActor mainActor) {
            if (!mAlreadyKilled) {
                if (hits(mainActor)) {
                    mAlreadyKilled = true;
                    return true;
                }
            }
            return false;
        }

        protected final boolean intersects(float thisRadius, float otherRadius, PointF otherPos) {
            mIntersectionRectOther.set(otherPos.x - otherRadius, otherPos.y - otherRadius, otherPos.x + otherRadius, otherPos.y + otherRadius);
            mIntersectionRectThis.set(mWorldPos.x - thisRadius, mWorldPos.y - thisRadius, mWorldPos.x + thisRadius, mWorldPos.y + thisRadius);
            return RectF.intersects(mIntersectionRectOther, mIntersectionRectThis);
        }

        protected abstract void drawBitmap(Canvas canvas, float screenPosX, float screenPosY);

        protected abstract void onMissed(MainActor actor);
    }

}
