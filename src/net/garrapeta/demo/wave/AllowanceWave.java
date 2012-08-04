package net.garrapeta.demo.wave;

import net.garrapeta.demo.JumplingsApplication;
import net.garrapeta.demo.JumplingsWorld;
import net.garrapeta.gameengine.R.id;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.ProgressBar;

public abstract class AllowanceWave extends ActionBasedWave {
	
	// ----------------------------------------------------- Constantes 
	
	private float FACTOR = 1.5f;
	// --------------------------------------------------- Variables

	
	/** Thread máximo de la wave  */
	private double maxThreat;

	/** Thread que se permite crear en este ciclo */
	protected double allowedThreadGeneration;
	
	/** Thread creado desde la última vez que fue 0 */
	double acumulated = 0;
	
	ProgressBar threadRatioBar ;
	ProgressBar allowedThreadGenerationBar ;
	ProgressBar accumulatedThreatBar;
	
	// ------------------------------------------------------------- Constructor
	
	/** 
	 * @param jWorld
	 */
	public AllowanceWave(JumplingsWorld jWorld, IWaveEndListener listener, int level) {
		super(jWorld, listener, level);
		
		jWorld.setGravityY(-SensorManager.GRAVITY_EARTH);
	}
	
	
	// ------------------------------------------------------- Métodos heredados	
	@Override
	public  void start() {
		super.start();
	}
	
	@Override
	protected void processFrameSub(float stepTime, float physicsStepTime) {
		if (JumplingsApplication.DEBUG_THREAD_BARS_ENABLED) {
			updateThreadRatioBar();
			updateAllowedThreadGenerationBar();
			updateAccumulatedThreatBar();
		}
		
		if (getProgress() < 100) {
			
			float  existant   = getCurrentThreat();
			 
			double lackRatio = (maxThreat - existant) / maxThreat;
			allowedThreadGeneration += (stepTime / 200) * lackRatio ;
			allowedThreadGeneration  = Math.min(allowedThreadGeneration, maxThreat);
				
			double generated  = 0;
//			Log.i(LOG_SRC, "maxThreat: " + maxThreat + ", existant: " + existant + ", allowedThreadGeneration: " + allowedThreadGeneration + ", acumulated: " + acumulated);
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
	


	// ---------------------------------------------------- Métodos propios

	/**
	 * Establece maxTreat
	 * @param threat
	 */
	protected void setMaxThreat(double threat) {
		this.maxThreat = this.allowedThreadGeneration = threat; 
	
		
		// DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG 
		if (JumplingsApplication.DEBUG_THREAD_BARS_ENABLED) {

			this.jWorld.jActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					jWorld.jActivity.findViewById(id.game_threadBars).setVisibility(View.VISIBLE);
				}}
			);
			
			threadRatioBar = (ProgressBar)jWorld.jActivity.findViewById(id.game_threadRatio);
			allowedThreadGenerationBar = (ProgressBar)jWorld.jActivity.findViewById(id.game_allowedThreadGeneration);
			accumulatedThreatBar = (ProgressBar)jWorld.jActivity.findViewById(id.game_acumulatedThreat);
				
			threadRatioBar.setMax(100);
			allowedThreadGenerationBar.setMax((int)(maxThreat * 100));
			accumulatedThreatBar.setMax((int)(maxThreat * 100));
			updateAllowedThreadGenerationBar();
		}
		// DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG 
	}
	
	/**
	 * Devuelve maxThreat
	 * @param maxThreat
	 */
	protected double getMaxThreat() {
		return maxThreat;
	}
	

	
	// Métodos de debug
	
	private void updateThreadRatioBar() {
		jWorld.jActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				threadRatioBar.setProgress((int) ((getCurrentThreat() / maxThreat) * 100));
				
			}});
		
	}
	
	private void updateAllowedThreadGenerationBar() {
		jWorld.jActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				allowedThreadGenerationBar.setProgress((int) (allowedThreadGeneration * 100));
				
			}});
		
	}
	
	private void updateAccumulatedThreatBar() {
		jWorld.jActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				accumulatedThreatBar.setProgress((int) (acumulated * 100));
				
			}});
	}
	
	
	// Métodos abstractos
	
	protected abstract float getCurrentThreat();
		
	protected abstract double generateThreat(double threatNeeded);
}
