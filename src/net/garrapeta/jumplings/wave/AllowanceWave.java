package net.garrapeta.jumplings.wave;

import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.jumplings.R.id;
import net.garrapeta.jumplings.Wave;
import android.view.View;
import android.widget.ProgressBar;

public abstract class AllowanceWave<T extends JumplingsWorld> extends Wave<T> {

    // ----------------------------------------------------- Constantes

    private float FACTOR = 1.5f;
    // --------------------------------------------------- Variables

    /** Thread m�ximo de la wave */
    private double maxThreat;

    /** Thread que se permite crear en este ciclo */
    protected double allowedThreadGeneration;

    /** Thread creado desde la �ltima vez que fue 0 */
    double acumulated = 0;

    ProgressBar mThreadRatioBar;
    ProgressBar mAllowedThreadGenerationBar;
    ProgressBar mAccumulatedThreatBar;

    // ------------------------------------------------------------- Constructor

    /**
     * @param jWorld
     */
    public AllowanceWave(T world, IWaveEndListener listener, int level) {
        super(world, listener, level);
    }

    // ------------------------------------------------------- M�todos heredados
    @Override
    public void start() {
        super.start();
    }

    @Override
    public void onProcessFrame(float stepTime) {
        if (!mIsGameOver) {
            if (JumplingsApplication.DEBUG_THREAD_BARS_ENABLED) {
                updateThreadRatioBar();
                updateAllowedThreadGenerationBar();
                updateAccumulatedThreatBar();
            }
    
            float existant = getCurrentThreat();
    
            double lackRatio = (maxThreat - existant) / maxThreat;
            allowedThreadGeneration += (stepTime / 200) * lackRatio;
            allowedThreadGeneration = Math.min(allowedThreadGeneration, maxThreat);
    
            double generated = 0;
            // Log.i(LOG_SRC, "maxThreat: " + maxThreat + ", existant: " +
            // existant + ", allowedThreadGeneration: " +
            // allowedThreadGeneration + ", acumulated: " + acumulated);
            if (acumulated < (maxThreat * FACTOR)) {
                generated = generateThreat(allowedThreadGeneration);
    
                if (generated > 0) {
                    acumulated += generated;
                    allowedThreadGeneration = 0;
                }
            } else if (existant == 0) {
                acumulated = 0;
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
        this.maxThreat = this.allowedThreadGeneration = threat;

        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        if (JumplingsApplication.DEBUG_THREAD_BARS_ENABLED) {

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
            mAllowedThreadGenerationBar.setMax((int) (maxThreat * 100));
            mAccumulatedThreatBar.setMax((int) (maxThreat * 100));
            updateAllowedThreadGenerationBar();
        }
        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    }

    /**
     * Devuelve maxThreat
     * 
     * @param maxThreat
     */
    protected double getMaxThreat() {
        return maxThreat;
    }

    // M�todos de debug

    private void updateThreadRatioBar() {
        getWorld().mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mThreadRatioBar.setProgress((int) ((getCurrentThreat() / maxThreat) * 100));

            }
        });

    }

    private void updateAllowedThreadGenerationBar() {
        getWorld().mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAllowedThreadGenerationBar.setProgress((int) (allowedThreadGeneration * 100));

            }
        });

    }

    private void updateAccumulatedThreatBar() {
        getWorld().mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAccumulatedThreatBar.setProgress((int) (acumulated * 100));

            }
        });
    }

    // M�todos abstractos

    protected abstract float getCurrentThreat();

    protected abstract double generateThreat(double threatNeeded);
}
