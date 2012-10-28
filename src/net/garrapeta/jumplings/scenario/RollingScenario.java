package net.garrapeta.jumplings.scenario;

import net.garrapeta.gameengine.AsyncGameMessage;
import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.jumplings.scenario.ScenarioFactory.ScenariosIds;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Scenario which implements a succession of sub-scenarios. This class handles
 * the fading-out of the previous scenario
 * 
 * @author garrapeta
 */
public class RollingScenario implements IScenario {

    // ------------------------------------------------ Constantes

    /** Tiempo que tarda el escenario en aparecer, en ms */
    public final static int FADE_IN_TIME = 800;

    // ------------------------------------ Variables de instancia

    private JumplingsWorld mWorld;

    private int mScenarioAlpha;

    private IScenario mCurrentScenario;

    private IScenario mPreviousScenario;

    private ScenariosIds[] mScenariosIds;

    private int mCurrentScenarioIdIndex = 0;

    /** Tiempo que le queda al escenario para terminar de desaparecer */
    public float mFadingInRemainigTime;

    /**
     * Constructor
     */
    RollingScenario(JumplingsWorld world, ScenariosIds[] scenariosId) {
        mWorld = world;
        mScenariosIds = scenariosId;
    }

    @Override
    public void setProgress(float progress) {
        if (mCurrentScenario != null) {
            mCurrentScenario.setProgress(progress);
        }
    }

    @Override
    public void onGameOver() {
        if (mCurrentScenario != null) {
            mCurrentScenario.onGameOver();
        }
    }

    @Override
    public void processFrame(float gameTimeStep) {
        if (mFadingInRemainigTime > 0) {
            mFadingInRemainigTime = Math.max(0, mFadingInRemainigTime - gameTimeStep);
            float factor = 1 - (mFadingInRemainigTime / FADE_IN_TIME);
            mScenarioAlpha = (int) (255 * factor);

            if (mPreviousScenario != null) {
                mPreviousScenario.processFrame(gameTimeStep);
                if (mFadingInRemainigTime == 0) {
                    mPreviousScenario = null;
                }
            }
        }
        if (mCurrentScenario != null) {
            mCurrentScenario.processFrame(gameTimeStep);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mPreviousScenario != null) {
            mPreviousScenario.draw(canvas, paint);
        }
        if (mCurrentScenario != null) {
            int savedAlpha = paint.getAlpha();
            paint.setAlpha(mScenarioAlpha);
            mCurrentScenario.draw(canvas, paint);
            paint.setAlpha(savedAlpha);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void end() {
        setNextScenario();
    }

    private void setNextScenario() {
        final IScenario prevScenario = mCurrentScenario;
        final IScenario newScenario = ScenarioFactory.getScenario(mWorld, mScenariosIds[mCurrentScenarioIdIndex]);
        mCurrentScenarioIdIndex = (mCurrentScenarioIdIndex + 1) % mScenariosIds.length;

        mWorld.post(new AsyncGameMessage() {
            @Override
            public void doInBackground() {
                newScenario.init();
            }

            @Override
            public void doInGameLoop(GameWorld world) {
                mPreviousScenario = prevScenario;
                mCurrentScenario = newScenario;
                mFadingInRemainigTime = FADE_IN_TIME;
            }
        });
    }

}
