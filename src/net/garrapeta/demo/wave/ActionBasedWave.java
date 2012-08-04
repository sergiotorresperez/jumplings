package net.garrapeta.demo.wave;

import java.util.ArrayList;

import net.garrapeta.demo.JumplingsWorld;
import android.util.Log;

public abstract class ActionBasedWave extends Wave {

	// ----------------------------------------------------------------- Constantes
	
	// ----------------------------------------------------- Variables de instancia
	
	/** lista de acciones por procesar	 */
	private ArrayList<WaveAction> actions;
	
	
	/** ms reales en el momento de inicialización de la wave */
	private long initizationRealMillis;
	
	/** ms en el mundo físco en el momento de inicialización de la wave */
	private double initializationPhysicsMillis;
	

	
	// ---------------------------------------------------------------- Constructor
	
	/**
	 * Constructor 
	 * 
	 * @param world
	 * @param level
	 * @param listener
	 */
	public ActionBasedWave(JumplingsWorld world, IWaveEndListener listener, int level) {
		super(world, listener, level);
		Log.i(LOG_SRC, "Initting " + this);
		
		// Se inicializan variables
		this.actions 		 = new ArrayList<WaveAction>();
		
		this.initizationRealMillis        = System.currentTimeMillis();
		this.initializationPhysicsMillis  = world.currentPhysicsMillis();
		this.actions.clear();
	}
	
	//------------------------------------------------------------ Métodos de IWave
	
	@Override
	public final void processFrame(float gameTimeStep, float physicsTimeStep) {

		if (playing) {
			Log.v(LOG_SRC, "ProcessFrame (" + gameTimeStep + ")" + this);
			
			// se comprueba que la wave no ha terminado por tiempo
			if (isFinished()) {
				// no se comunica el fin de la wave hasta que todos los enemigos
				// están muertos
				if (listener != null) {
					end();	
				}
				// no se procesa más, porque ya ha terminado la wave
				return;
			}			
				
			synchronized (actions) {
				// se procesa la cola de acciones
				for (int i = 0; i < actions.size(); ) {
					WaveAction action = actions.get(i);
					if (action.testExecution()) {
						// se saca de la lista
						action.execute();
					} else {
						i++;
					}
				}
			}
			
			// procesamiento real
			processFrameSub(gameTimeStep, physicsTimeStep);
		}
	}

	// ------------------------------------------------------------- Métodos propios

	
	protected abstract boolean isFinished();
	
	
	
	/**
	 * @return ms reales transcurridos desde la inicialización de la wave
	 */
	protected long currentRealMillis() {
		return System.currentTimeMillis() - initizationRealMillis;
	}
	
	/**
	 * @return ms en el mundo físico transcurridos desde la inicialización de la wave
	 */
	protected double currentPhysicsMillis() {
		return jWorld.currentPhysicsMillis() - initializationPhysicsMillis;
	}
	
	// ---------------------------------------------------- Métodos abstractos
	
	/**
	 * Método de sub-proceso de frame
	 * A ser implementado por subclases
	 * 
	 * @param realTimeStep
	 * @param physicsTimeStep
	 */
	protected abstract void processFrameSub(float realTimeStep, float physicsTimeStep);
	
	// -------------------------------------------------------- Clases internas
	

	/**
	 * 
	 * Acciones que se van programando y procesando según avanza la wave
	 * 
	 * @author GaRRaPeTa
	 */
	protected abstract class WaveAction implements Runnable {
		
		ActionBasedWave wave;
		
		boolean pending;
		
		public WaveAction (ActionBasedWave wave) {
			this.wave = wave;
		}

		abstract void schedule(float delay);
		
		abstract boolean testExecution();

		final void execute() {
			synchronized (actions) {
				pending = false;
				actions.remove(this);
				run();
			}
		}
		
		/**
		 * @return si la acción está pendiente de ser ejecutada
		 */
		public boolean isPending() {
			synchronized (actions) {
				return pending;
			}
		}
		
	}
	
	/**
	 * Accione progamables por tiempo real
	 * @author GaRRaPeTa
	 */
	public abstract class RealTimeWaveAction extends WaveAction {

		private float realTimeDelay;
		
		private long addedRealTimeStamp;

		
		public RealTimeWaveAction (ActionBasedWave wave) {
			super(wave);
		}
		
		final void schedule(float delay) {
			Log.i(LOG_SRC, "Scheduled real time action, delay: " + delay);
			synchronized (actions) {
				actions.add(this);
				pending = true;
			}
			realTimeDelay = delay;
			addedRealTimeStamp = System.currentTimeMillis();
			
		}

		final boolean testExecution() {
			return (System.currentTimeMillis() - addedRealTimeStamp >= realTimeDelay);
		}
	}
	
	
	/**
	 * Accione progamables por tiempo físico
	 * @author GaRRaPeTa
	 */
	public abstract class PhysicsTimeWaveAction extends WaveAction {

		private float   physicalTimeDelay;
		
		private double  addedPhysicalTimeStamp;

		
		public PhysicsTimeWaveAction (ActionBasedWave wave) {
			super(wave);
		}
		
		public final void schedule(float delay) {
			Log.i(LOG_SRC, "Scheduled real time action, delay: " + delay);
			synchronized (actions) {
				actions.add(this);
				pending = true;
			}
			physicalTimeDelay      = delay;
			addedPhysicalTimeStamp = jWorld.currentPhysicsMillis();
			
		}

		final boolean testExecution() {
			return  (jWorld.currentPhysicsMillis() - addedPhysicalTimeStamp >= physicalTimeDelay);
		}
	}
}
