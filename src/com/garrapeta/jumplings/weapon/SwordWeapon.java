package com.garrapeta.jumplings.weapon;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.view.MotionEvent;

import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.actor.MainActor;
import com.garrapeta.jumplings.module.FlashModule;

public class SwordWeapon extends Weapon {

    // --------------------------------------------------------------------
    // Constantes

    public final static short WEAPON_CODE_SWORD = 2;

    private static int LIFE_TIME = 10000;

    private final WeaponSwordListener mListener;

    private final SwordTipWeapon mSwordTipWeapon;

    // -------------------------------------------------------- Variables de
    // instancia
    SwordSwipeActor mSwipe;

    private float mRemainingLife;

    SwordWeapon(JumplingsGameWorld jgWorld, WeaponSwordListener listener) {
        super(jgWorld);
        mSwordTipWeapon = new SwordTipWeapon(jgWorld);
        mSwipe = new SwordSwipeActor(jgWorld);
        mSwipe.setInitted();
        jgWorld.addActor(mSwipe);
        mListener = listener;
    }

    @Override
    public void onTouchEvent(double[] info) {
        mSwordTipWeapon.onTouchEvent(info);
        mSwipe.onTouchEvent(info);
    }

    public short getWeaponCode() {
        return WEAPON_CODE_SWORD;
    }

    @Override
    public void onStart(float gameTimeStep) {
        mListener.onSwordStarted();
        mRemainingLife = LIFE_TIME;
        mWorld.getSoundManager()
              .play(JumplingsGameWorld.SAMPLE_SWORD_SHEATH);
        mSwordTipWeapon.onStart(gameTimeStep);
    }

    @Override
    public void processFrame(float gameTimeStep) {
        mSwordTipWeapon.processFrame(gameTimeStep);
        if (mListener == null) {
            return;
        }
        mRemainingLife -= gameTimeStep;
        if (mRemainingLife > 0) {
            mListener.onSwordRemainingTimeUpdated(mRemainingLife / LIFE_TIME);
        } else {
            mWorld.getSoundManager()
                  .play(JumplingsGameWorld.SAMPLE_SWORD_UNSHEATH);
            mWorld.removeActor(mSwipe);
            mListener.onSwordEnded();
        }
    }

    /**
     * Listener of the events produced by the sword
     */
    public static interface WeaponSwordListener {
        public void onSwordStarted();

        /**
         * @param remaining
         *            , between 0 and 1
         */
        public void onSwordRemainingTimeUpdated(float remaining);

        public void onSwordEnded();
    }

    /**
     * The swipe of the sword visible on the screen
     */
    private class SwordSwipeActor extends HarmerActor {

        private ArrayList<double[]> mSwipePoints;
        private Paint mPaint;
        private final int SWIPE_TIME = 150;

        private Path mPath = new Path();

        private final int MIN_START_DISTANCE = 15;
        private final int MIN_STOP_DISTANCE = 10;

        private double[] mPrev;

        private boolean swipping = false;

        private Region mCollisionDetectionMainActorRegion = new Region();
        private Region mCollisionDetectionAuxRegion = new Region();

        // ---------------------------------------------------- Constructor

        public SwordSwipeActor(JumplingsGameWorld mWorld) {
            super(mWorld);
            mSwipePoints = new ArrayList<double[]>();

            mPaint = new Paint();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            mPaint.setARGB(75, 0, 0, 255);
        }

        // ----------------------------------------------- M�todos heredados

        @Override
        public void processFrame(float gameTimeStep) {
            mPath.reset();

            long now = System.currentTimeMillis();
            while (mSwipePoints.size() > 0) {
                double[] info = mSwipePoints.get(0);
                if (now - info[3] > SWIPE_TIME) {
                    mSwipePoints.remove(info);
                } else {
                    break;
                }
            }

            if (mSwipePoints.size() > 0) {

                double[] info;
                float x;
                float y;

                info = mSwipePoints.get(0);
                x = (float) info[1];
                y = (float) info[2];
                mPath.moveTo(x, y);

                for (int i = 0; i < mSwipePoints.size(); i++) {
                    info = mSwipePoints.get(i);

                    x = (float) info[1];
                    y = (float) info[2];

                    mPath.lineTo(x, y);

                }
            }
            super.processFrame(gameTimeStep);
        }

        @Override
        protected void effectOver(MainActor mainActor) {
            if (hits(mainActor)) {
                mainActor.onHitted();
            }
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawPath(mPath, mPaint);
        }

        @Override
        protected void dispose() {
            super.dispose();
            mSwipePoints = null;
            mPaint = null;
            mPath = null;
            mPrev = null;
            mCollisionDetectionAuxRegion = null;
            mCollisionDetectionMainActorRegion = null;
        }

        // ---------------------------------------- M�todos propios

        public void onTouchEvent(double info[]) {
            if (mPrev != null) {
                int action = (int) info[0];

                if (action == MotionEvent.ACTION_MOVE) {
                    double dist = Math.sqrt(Math.pow(info[1] - mPrev[1], 2) + Math.pow(info[2] - mPrev[2], 2));

                    if (!swipping) {
                        if (dist >= MIN_START_DISTANCE) {
                            swipping = true;
                            mTimestamp = System.currentTimeMillis();

                            getWorld().getSoundManager()
                                      .play(JumplingsGameWorld.SAMPLE_SWORD_SWING);
                            getWorld().mFlashModule.flash(FlashModule.SWORD_SWING_KEY);
                        }
                    } else {
                        if (dist < MIN_STOP_DISTANCE) {
                            mSwipePoints.clear();
                            swipping = false;
                        }
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    mSwipePoints.clear();
                    swipping = false;
                }

                if (swipping) {
                    this.mSwipePoints.add(info);
                }

            }

            mPrev = info;
        }

        public boolean hits(MainActor mainActor) {
            PointF pos = mainActor.getWorldPos();

            float sr = getWorld().mViewport.worldUnitsToPixels(mainActor.mRadius);
            float screenPositionX = getWorld().mViewport.worldToScreenX(pos.x);
            float screenPositionY = getWorld().mViewport.worldToScreenY(pos.y);
            mCollisionDetectionMainActorRegion.set((int) (screenPositionX - sr), (int) (screenPositionY - sr), (int) (screenPositionX + sr),
                    (int) (screenPositionY + sr));

            return mCollisionDetectionAuxRegion.setPath(mPath, mCollisionDetectionMainActorRegion);
        }

    }

}
