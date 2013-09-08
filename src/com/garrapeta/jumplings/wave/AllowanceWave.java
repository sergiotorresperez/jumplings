package com.garrapeta.jumplings.wave;

import android.view.View;
import android.widget.ProgressBar;

import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.PermData;
import com.garrapeta.jumplings.R.id;
import com.garrapeta.jumplings.Wave;

public abstract class AllowanceWave<T extends JumplingsGameWorld> extends Wave<T> {

    // ----------------------------------------------------- Constantes

    private float FACTOR = 1.5f;
    // --------------------------------------------------- Variables

    /** Thread m�ximo de la wave */
    private double mMaxThreat;

    /** Thread que se permite crear en este ciclo */
    protected double mAllowedThreadGeneration;

    /** Thread creado desde la �ltima vez que fue 0 */
    double mAccumulated = 0;
    
    private final boolean mThreadBarsEnabled;

    private ProgressBar mThreadRatioBar;
    private ProgressBar mAllowedThreadGenerationBar;
    private ProgressBar mAccumulatedThreatBar;

    // ------------------------------------------------------------- Constructor

    /**
     * @param jWorld
     */
    public AllowanceWave(T world, int level) {
        super(world, level);
        mThreadBarsEnabled = PermData.showThreadBars(world.mActivity);
    }

    // ------------------------------------------------------- M�todos heredados

    @Override
    public void onProcessFrame(float stepTime) {
        if (!getWorld().isGameOver()) {
            if (mThreadBarsEnabled) {
                updateThreadRatioBar();
                updateAllowedThreadGenerationBar();
                updateAccumulatedThreatBar();
            }
    
            float existant = getCurrentThreat();
    
            double lackRatio = (mMaxThreat - existant) / mMaxThreat;
            mAllowedThreadGeneration += (stepTime / 200) * lackRatio;
            mAllowedThreadGeneration = Math.min(mAllowedThreadGeneration, mMaxThreat);
    
            double generated = 0;
            // if (L.sEnabled) Log.i(TAG, "maxThreat: " + maxThreat + ", existant: " +
            // existant + ", allowedThreadGeneration: " +
            // allowedThreadGeneration + ", acumulated: " + acumulated);
            if (mAccumulated < (mMaxThreat * FACTOR)) {
                generated = generateThreat(mAllowedThreadGeneration);
    
                if (generated > 0) {
                    mAccumulated += generated;
                    mAllowedThreadGeneration = 0;
                }
            } else if (existant == 0) {
                mAccumulated = 0;
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        mAccumulatedThreatBar = null;
        mAllowedThreadGenerationBar = null;
        mThreadRatioBar = null;
    }
    // ---------------------------------------------------- M�todos propios

    /**
     * Establece maxTreat
     * 
     * @param threat
     */
    protected void setMaxThreat(double threat) {
        this.mMaxThreat = this.mAllowedThreadGeneration = threat;

        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        if (mThreadBarsEnabled) {

            this.getWorld().mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWorld().mActivity.findViewById(id.game_threadBars).setVisibility(View.VISIBLE);
                }
            });

            mThreadRatioBar = (ProgressBar) getWorld().mActivity.findViewById(id.game_threadRatio);
            mAllowedThreadGenerationBar = (ProgressBar) getWorld().mActivity.findViewById(id.game_allowedThreadGeneration);
            mAccumulatedThreatBar = (ProgressBar) getWorld().mActivity.findViewById(id.game_acumulatedThreat);

            mThreadRatioBar.setMax(100);
            mAllowedThreadGenerationBar.setMax((int) (mMaxThreat * 100));
            mAccumulatedThreatBar.setMax((int) (mMaxThreat * 100));
            updateAllowedThreadGenerationBar();
        }
        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    }

    /**
     * Devuelve maxThreat
     * 
     * @param mMaxThreat
     */
    protected double getMaxThreat() {
        return mMaxThreat;
    }

    // M�todos de debug

    private void updateThreadRatioBar() {
        getWorld().mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mThreadRatioBar.setProgress((int) ((getCurrentThreat() / mMaxThreat) * 100));

            }
        });

    }

    private void updateAllowedThreadGenerationBar() {
        getWorld().mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAllowedThreadGenerationBar.setProgress((int) (mAllowedThreadGeneration * 100));

            }
        });

    }

    private void updateAccumulatedThreatBar() {
        getWorld().mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAccumulatedThreatBar.setProgress((int) (mAccumulated * 100));

            }
        });
    }

    // M�todos abstractos

    protected abstract float getCurrentThreat();

    protected abstract double generateThreat(double threatNeeded);
}
