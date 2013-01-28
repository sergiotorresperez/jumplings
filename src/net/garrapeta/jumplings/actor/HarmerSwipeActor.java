package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.PermData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

public class HarmerSwipeActor extends HarmerActor {

    private ArrayList<double[]> mSwipePoints;
    private Paint mPaint;
    private final int TIME = 100;
    private Path mPath = new Path();
    private RectF mKillingArea = new RectF();
    private boolean mKillingAreaUpdated = false;

    private final int MIN_START_DISTANCE = 30;
    private final int MIN_STOP_DISTANCE = 15;

    private double[] mPrev;

    private boolean swipping = false;

    // ---------------------------------------------------- Constructor

    public HarmerSwipeActor(JumplingsGameWorld mWorld) {
        super(mWorld);
        mSwipePoints = new ArrayList<double[]>();

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setARGB(75, 0, 0, 255);
    }

    // ----------------------------------------------- M�todos heredados

    @Override
    public void processFrame(float gameTimeStep) {

        synchronized (mSwipePoints) {

            mPath.reset();

            long now = System.currentTimeMillis();
            while (mSwipePoints.size() > 0) {
                double[] info = mSwipePoints.get(0);
                if (now - info[3] > TIME) {
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

                mKillingAreaUpdated = false;
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
        mKillingArea = null;
        mPrev = null;
    }

    // ---------------------------------------- M�todos propios

    public void onTouchEvent(double info[]) {
        synchronized (mSwipePoints) {
            if (mPrev != null) {
                int action = (int) info[0];

                if (action == MotionEvent.ACTION_MOVE) {
                    double dist = Math.sqrt(Math.pow(info[1] - mPrev[1], 2) + Math.pow(info[2] - mPrev[2], 2));

                    if (!swipping) {
                        if (dist >= MIN_START_DISTANCE) {
                            swipping = true;
                            mTimestamp = System.currentTimeMillis();

                            getWorld().getSoundManager().play(JumplingsGameWorld.SAMPLE_SWORD_SWING);
                            if (getWorld().mFlashCfgLevel == PermData.CFG_LEVEL_ALL) {
                                getWorld().mFlashActor.init(Color.WHITE, 50, 250, -1);
                            }
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

    }

    public boolean hits(MainActor mainActor) {
        PointF pos = mainActor.getWorldPos();

        if (!mKillingAreaUpdated) {
            mPath.computeBounds(mKillingArea, true);
            mKillingAreaUpdated = true;
        }

        float sr = getWorld().mViewport.worldUnitsToPixels(mainActor.mRadius);
        PointF sc = getWorld().mViewport.worldToScreen(pos.x, pos.y);
        RectF aux = new RectF(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr);

        return RectF.intersects(aux, mKillingArea);
    }

}
