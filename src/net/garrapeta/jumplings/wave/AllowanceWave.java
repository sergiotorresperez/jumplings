package net.garrapeta.jumplings.wave;

import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.jumplings.R.id;
import net.garrapeta.jumplings.Wave;
import android.view.View;
import android.widget.ProgressBar;

public abstract class AllowanceWave extends Wave {

    // ----------------------------------------------------- Constantes

    private float FACTOR = 1.5f;
    // --------------------------------------------------- Variables

    /** Thread m�ximo de la wave */
    private double maxThreat;

    /** Thread que se permite crear en este ciclo */
    protected double allowedThreadGeneration;

    /** Thread creado desde la �ltima vez que fue 0 */
    double acumulated = 0;

    ProgressBar threadRatioBar;
    ProgressBar allowedThreadGenerationBar;
    ProgressBar accumulatedThreatBar;

    // ------------------------------------------------------------- Constructor

    /**
     * @param jWorld
     */
    public AllowanceWave(JumplingsWorld jWorld, IWaveEndListener listener, int level) {
        super(jWorld, listener, level);
    }

    // ------------------------------------------------------- M�todos heredados
    @Override
    public void start() {
        super.start();
    }

    @Override
    public void onProcessFrame(float stepTime) {
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

            this.jWorld.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    jWorld.getActivity().findViewById(id.game_threadBars).setVisibility(View.VISIBLE);
                }
            });

            threadRatioBar = (ProgressBar) jWorld.getActivity().findViewById(id.game_threadRatio);
            allowedThreadGenerationBar = (ProgressBar) jWorld.getActivity().findViewById(id.game_allowedThreadGeneration);
            accumulatedThreatBar = (ProgressBar) jWorld.getActivity().findViewById(id.game_acumulatedThreat);

            threadRatioBar.setMax(100);
            allowedThreadGenerationBar.setMax((int) (maxThreat * 100));
            accumulatedThreatBar.setMax((int) (maxThreat * 100));
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
        jWorld.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                threadRatioBar.setProgress((int) ((getCurrentThreat() / maxThreat) * 100));

            }
        });

    }

    private void updateAllowedThreadGenerationBar() {
        jWorld.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                allowedThreadGenerationBar.setProgress((int) (allowedThreadGeneration * 100));

            }
        });

    }

    private void updateAccumulatedThreatBar() {
        jWorld.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                accumulatedThreatBar.setProgress((int) (acumulated * 100));

            }
        });
    }

    // M�todos abstractos

    protected abstract float getCurrentThreat();

    protected abstract double generateThreat(double threatNeeded);
}
