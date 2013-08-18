package com.garrapeta.jumplings.wave;

import android.util.Log;

import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.jumplings.GameActivity;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.Player;
import com.garrapeta.jumplings.Wave;
import com.garrapeta.jumplings.actor.EnemyActor;
import com.garrapeta.jumplings.scenario.IScenario;
import com.garrapeta.jumplings.scenario.RollingScenario;
import com.garrapeta.jumplings.scenario.ScenarioFactory;

public class CampaignWave extends Wave<JumplingsGameWorld> implements ICampaignWave {

    // ----------------------------------------------------- Constantes

    // Clave para referirse a esta wave
    public final static String WAVE_KEY = CampaignWave.class.getCanonicalName();

    /** Nivel inicial de la wave hija */
    private final static int INIT_LEVEL = 1;

    /**
     * Ms que tarda en aparecer la primerta wave
     */
    public static final int FIRST_WAVE_DELAY = 0;

    /**
     * Ms que hay desde que termina la wave hasta que se realiza la siguiente
     * acci�n
     */
    public static final int INTER_WAVE_DELAY = 300;

    private static final int NEXT_SCENARIO_DELAY = 700;

    private static final int RESUME_DELAY = NEXT_SCENARIO_DELAY + RollingScenario.FADE_IN_TIME;

    /**
     * Tiempo m�nimo entre di�logos de anuncios. Se mostrar�n al acabar la wave.
     * En ms.
     */
    private int ADS_MIN_TIME_LAPSE = (int) (60 * 2.4 * 1000);

    /** Vidas que se ganan al pasar de nivel */
    public static final int NEW_LEVEL_EXTRA_LIFES = 0;

    // ----------------------------------------- Variables de instancia

    JumplingsGameWorld mWorld;
    IScenario mScenario;

    /**
     * Wave Actual
     */
    private GameWave mCurrentWave;

    /** Timestamp de cuando se mostr� el �ltimo anuncio */
    private long lastAdTimeStamp = 0;

    // --------------------------------------------------- Constructor

    /**
     * @param jgWorld
     */
    public CampaignWave(JumplingsGameWorld jgWorld) {
        super(jgWorld, INIT_LEVEL);
        mWorld = jgWorld;
        mScenario = ScenarioFactory.getScenario(jgWorld, ScenarioFactory.ScenariosIds.ROLLING);
        jgWorld.setScenario(mScenario);
    }

    // ------------------------------------------- M�todos Heredados

    @Override
    public void start() {
        super.start();
        Log.i(LOG_SRC, "Starting Wave Campaign");
        mScenario.init();
        scheduleNextWave(FIRST_WAVE_DELAY);
    }

    @Override
    public void onProcessFrame(float realTimeStep) {
        if (mCurrentWave != null) {
            mCurrentWave.processFrame(realTimeStep);
        }
    }

    @Override
    public boolean onEnemyScaped(EnemyActor e) {
        if (mCurrentWave != null) {
            return mCurrentWave.onEnemyScaped(e);
        }
        return false;
    }

    @Override
    public boolean onEnemyKilled(EnemyActor enemy) {
        mScenario.setProgress(mCurrentWave.getProgress());
        if (mCurrentWave != null) {
            return mCurrentWave.onEnemyKilled(enemy);
        }
        return false;
    }

    @Override
    public void dispose() {
        mWorld = null;
        mScenario.dispose();
        mScenario = null;
        mCurrentWave.dispose();
        mCurrentWave = null;
    }

    @Override
    public boolean onGameOver() {
        return mCurrentWave.onGameOver();
    }

    // ---------------------------------- M�todos de IWaveEventListener

    @Override
    public void onChildWaveStarted() {
        Log.i(LOG_SRC, "Wave started");
    }

    @Override
    public void onChildWaveEnded() {
        Log.i(LOG_SRC, "Wave ended");
        mCurrentWave.pause();
        mLevel++;
        scheduleNextWave(INTER_WAVE_DELAY);
    }
    
	@Override
	public boolean isInBetweenWaves() {
		return mCurrentWave.isCompleted() || !mCurrentWave.isPlaying();
	}
	
	@Override
	public boolean isGameOver() {
		return mIsGameOver;
	}

    // ------------------------------------------------ M�todos propios

    private void switchWave() {
    	if (mWorld.currentGameMillis() - lastAdTimeStamp > ADS_MIN_TIME_LAPSE) {
            // Se muestra anuncio
            mWorld.mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // FIXME: avoid this cast
                    ((GameActivity)mWorld.mActivity).showAdDialogIfAvailable();
                }
            });

            lastAdTimeStamp = mWorld.currentGameMillis();
        }

        Player player = mWorld.getPlayer();
        player.addLifes(NEW_LEVEL_EXTRA_LIFES);
        mCurrentWave = new GameWave(mWorld, this, mLevel, mLevel == INIT_LEVEL);
    }

    private void showLevel() {
        // FIXME: avoid this cast
        ((GameActivity)mWorld.mActivity).onLevelChanged(mLevel);
    }

    private void scheduleNextWave(float delay) {
        mWorld.post(new SyncGameMessage() {
            @Override
            public void doInGameLoop(GameWorld world) {
                switchWave();
                showLevel();
                scheduleNextScenario(NEXT_SCENARIO_DELAY);
                scheduleResume(RESUME_DELAY);
            }
        }, delay);

    }

    private void scheduleResume(float delay) {
        mWorld.post(new SyncGameMessage() {
            @Override
            public void doInGameLoop(GameWorld world) {
                mCurrentWave.play();
            }
        }, delay);
    }

    private void scheduleNextScenario(float delay) {
        mWorld.post(new SyncGameMessage() {
            @Override
            public void doInGameLoop(GameWorld world) {
                mScenario.end();
            }
        }, delay);

    }

}
