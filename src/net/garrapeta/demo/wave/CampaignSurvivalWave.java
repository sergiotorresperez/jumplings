package net.garrapeta.demo.wave;

import net.garrapeta.demo.JumplingsApplication;
import net.garrapeta.demo.JumplingsGameActivity;
import net.garrapeta.demo.JumplingsGameWorld;
import net.garrapeta.demo.Player;
import net.garrapeta.demo.actor.EnemyActor;
import android.util.Log;
import android.widget.Toast;


public class CampaignSurvivalWave extends ActionBasedWave implements IWaveEndListener {

	// ----------------------------------------------------- Constantes
	
	// Clave para referirse a esta wave
	public final static String WAVE_KEY = CampaignSurvivalWave.class.getCanonicalName();
	
	/** Nivel inicial de la wave hija */
	private final static int INIT_LEVEL = 1;
	
	/** Ms que hay desde que termina la wave hasta que se realiza la
	 *  siguiente acci�n */
	public static final int AFTER_WAVE_END_REALTIME    = 500;
	
	/** Ms que hay desde que se cambia de wave hasta que empieza la siguiente */
	public static final int AFTER_WAVE_SWITCH_REALTIME = 500;
	
	/** Vidas que se ganan al pasar de nivel */ 
	public static final int  NEW_LEVEL_EXTRA_LIFES = 0;
	
	/** Tiempo m�nimo entre di�logos de anuncios. Se mostrar�n al acabar la wave. En ms.*/
	private int ADS_MIN_TIME_LAPSE = 60 * 2 * 1000 ;
	// ----------------------------------------- Variables de instancia

	JumplingsGameWorld jgWorld;
	
	/**
	 * Wave Actual
	 */
	private Wave currentWave;

	/** Acci�n que consiste en cambiar de wave */ 
	private RealTimeWaveAction waveSwitchAction;
	
	/** Acci�n que consiste en empezarla wave */ 
	private RealTimeWaveAction waveStartAction;
	
	/** Acci�n que consiste en mostrar Toast con el nivel actual */ 
	private RealTimeWaveAction showLevelAction;
	
	/** Timestamp de cuando se mostr� el �ltimo anuncio */
	private long lastAdTimeStamp = 0;
	
	// --------------------------------------------------- Constructor

	/**
	 * @param jgWorld
	 */
	public CampaignSurvivalWave(JumplingsGameWorld jgWorld, IWaveEndListener listener) {
		super(jgWorld, listener, INIT_LEVEL);
		this.jgWorld = jgWorld;
	
		waveSwitchAction = new RealTimeWaveAction(this) {
			@Override
			public void run() {
				switchWave();
			}
		};
		
		showLevelAction = new RealTimeWaveAction(this) {
			@Override
			public void run() {
				//s�lo se muestra toast al cambiar de nivel
				CampaignSurvivalWave.this.jgWorld.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showLevel();
					}
				});
				waveStartAction.schedule(AFTER_WAVE_SWITCH_REALTIME);
			}
		};
		
		waveStartAction = new RealTimeWaveAction(this) {
			@Override
			public void run() {
				startCurrentWave();
			}
		};
	}

	// ------------------------------------------- M�todos Heredados

	@Override
	public void start() {
		super.start();
		Log.i(LOG_SRC, "Starting Wave Campaign");
		waveSwitchAction.schedule(AFTER_WAVE_END_REALTIME);
	}

	@Override
	protected void processFrameSub(float realTimeStep) {
		if (currentWave != null) {
			currentWave.processFrame(realTimeStep);
		}
	}
	
	@Override
	public boolean onEnemyScaped(EnemyActor e) {
		if (currentWave != null) {
			return currentWave.onEnemyScaped(e);
		}
		return false;
	}
	
	@Override
	public boolean onEnemyKilled(EnemyActor enemy) {
		if (currentWave != null) {
			return currentWave.onEnemyKilled(enemy);
		}
		return false;
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
	
	@Override
	public float getProgress() {
		return currentWave.getProgress();
	}

	
	// ---------------------------------- M�todos de IWaveEventListener

	@Override
	public void onWaveStarted() {
		Log.i(LOG_SRC, "Wave started");
		jgWorld.onWaveStarted();
	}
	
	@Override
	public void onWaveEnded() {
		Log.i(LOG_SRC, "Wave ended");
		level++;
		waveSwitchAction.schedule(AFTER_WAVE_END_REALTIME);
		jgWorld.onWaveCompleted();
	}

	// ------------------------------------------------ M�todos propios

	private void switchWave() {
		Player player = jgWorld.getPlayer();
		player.addLifes(NEW_LEVEL_EXTRA_LIFES);

		currentWave = new AllowanceShooterWave(jgWorld, this, level);
		
		if (JumplingsApplication.MOBCLIX_ENABLED && jgWorld.currentGameMillis() - lastAdTimeStamp > ADS_MIN_TIME_LAPSE) {
			// Se muestra anuncio
			jgWorld.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					jgWorld.jgActivity.showDialog(JumplingsGameActivity.DIALOG_AD_ID);
				}});
			
			lastAdTimeStamp = jgWorld.currentGameMillis();
		}

		
		showLevelAction.schedule(500);
	}
	
	private void showLevel() {
		final String message = "Level " + level;
		Log.i(LOG_SRC, message);
		
		Toast toast = Toast.makeText(CampaignSurvivalWave.this.jgWorld.getActivity(), message, Toast.LENGTH_SHORT);
		toast.show();
		
		if (level != INIT_LEVEL) {
			jgWorld.nextScenario();
		}
	}
	
	private void startCurrentWave() {
		currentWave.start();
	}


}
