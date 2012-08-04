package net.garrapeta.demo.wave;

import net.garrapeta.demo.JumplingsWorld;
import net.garrapeta.demo.actor.BladePowerUpActor;
import net.garrapeta.demo.actor.BombActor;
import net.garrapeta.demo.actor.EnemyActor;
import net.garrapeta.demo.actor.JumplingActor;
import net.garrapeta.demo.actor.LifePowerUpActor;
import net.garrapeta.gameengine.box2d.PhysicsUtils;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.widget.Button;

import com.badlogic.gdx.math.Vector2;



public abstract class Wave {
	
	// ------------------------------------------------- Constantes
	
	/** tag del log */
	public static final String LOG_SRC = "wave";
	
	public static float ENEMY_OFFSET = JumplingActor.BASE_RADIUS * 2.5f;

	
	// ------------------------------------ Variables de instancia
	
	/** mundo dueño de la wave  */
	protected JumplingsWorld jWorld;
	
	/** listener de la wave */
	protected IWaveEndListener listener;
	
	/** Si la wave está en ejecución  */
	protected boolean playing = false;
	
	/** nivel */
	protected int level;
	
	// ------------------------------------------------ Constructor

	public Wave (JumplingsWorld jworld, IWaveEndListener listener, int level) {
		this.jWorld    = jworld;
		this.listener = listener;
		this.level 	  = level;
		
		// Se resetean defaults
		jworld.setGravityX(0);
		jworld.setGravityY(- SensorManager.GRAVITY_EARTH);
	}
	
	// --------------------------------------------------- Métodos

	/**
	 *  Comienza la wave
	 */
	public void start() {
		play();
		if (listener != null) {
			listener.onWaveStarted();
		}
	}
	
	/**
	 *  Finaliza la wave
	 */
	public void end() {
		pause();
		listener.onWaveEnded();
	}
	
	/**
	 * Pone en ejecución la wave
	 */
	public void play() {
		this.playing = true;
	}
	
	/**
	 * @return si está en ejecución
	 */
	public boolean isPlaying() {
		return playing;
	}

	/**
	 * Para la wave
	 */
	public void pause() {
		this.playing = false;
	}
	
	/**
	 * @return nivel
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Método ejecutado cuando un enemigo escapa de pantalla
	 * @return si el evento es consumido por la wave
	 */
	public boolean onEnemyScaped(EnemyActor e) {
		return false;
	}
	
	/**
	 * Método ejecutado cuando una bomba estalla
	 * @return si el evento es consumido por la wave
	 */
	public boolean onBombExploded(BombActor bomb) {
		return false;
	}

	/**
	 * Método ejecutado cuando el usuario coge un power up de vida
	 * @return si el evento es consumido por la wave
	 */
	public boolean onLifePowerUp(LifePowerUpActor lifePowerUpActor) {
		return false;
	}
	
	/**
	 * Método ejecutado cuando el usuario coge un power up de blade
	 * @return si el evento es consumido por la wave
	 */
	public boolean onBladePowerUp(BladePowerUpActor bladePowerUpActor) {
		return false;
	}

	
	/**
	 * Método ejecutado cuando el jugador mata un enemigo
	 * @return si el evento es consumido por la wave
	 */
	public boolean onEnemyKilled(EnemyActor enemy) {
		return false;
	}

	/**
	 * Método ejecutado cuando el jugador pierde el juego
	 * @return si el evento es consumido por la wave
	 */
	public boolean onGameOver() {
		return false;
	}
	
	/**
	 * @return progreso, del 0 - 100
	 */
	public abstract float getProgress();
	
	// --------------------------------------------------- Métodos abstractos
	
	/** 
	 *  Lógica que se procesa en esta pantalla en cada frame 
	 *  @param physicsTimeStep
	 */
	public abstract void processFrame(float gameTimeStep, float physicsTimeStep);

	
	// ------------------------------ Métodos de utilidad para crear enemigos
	
	protected float getLeftPos() {
		return jWorld.worldBoundaries.left - ENEMY_OFFSET;
	}
	
	protected float getRightPos() {
		return jWorld.worldBoundaries.right + ENEMY_OFFSET;
	}
	
	protected float getTopPos() {
		return jWorld.worldBoundaries.top + ENEMY_OFFSET;
	}
	
	protected float getBottomPos() {
		return jWorld.worldBoundaries.bottom - ENEMY_OFFSET;
	}
	
	protected float getRandomPosX() {
		RectF bounds =  jWorld.worldBoundaries;
		
		float init =  bounds.left  + ENEMY_OFFSET;
		float fin  =  bounds.right - ENEMY_OFFSET;
		float w = fin - init;
		return init + (float) (Math.random() * w);
	}
	
	protected float getRandomPosY() {
		RectF bounds =  jWorld.worldBoundaries;
		float minY = bounds.bottom;
		float maxY = (bounds.top - bounds.bottom) / 2;
		return (float) (minY + (Math.random() * (maxY - minY)));
	}
	
	protected Vector2 getInitialVelocity(PointF initPos) {
		float g = jWorld.getGravityY();
		RectF bounds =  jWorld.worldBoundaries;
		
		// Factor de aletoriedad (0 - 1)
		float XFACTOR = 0.9f;
		float YFACTOR = 0.7f;
		
		// Distancia máxima que pueda viajar verticalmente
		float maxYDistance = bounds.top - initPos.y;
		
		// Distancia que va a viajer verticalmente. Se le hace un poco aleatoria.
		float yDistance = (float) (YFACTOR + ((1 - YFACTOR) * Math.random())) * maxYDistance;
		
		float vy = (float) PhysicsUtils.getInitialVelocity(yDistance, 0, g);
		
		// Tiempo que va a estar viajando (arriba + abajo)
		float t = 2 * (float) PhysicsUtils.getTime(vy, 0, g);
		
		float worldWidth = bounds.right - bounds.left;

		// Distancia máxima que pueda viajar horizontalmente
		float maxXDistance;
		// Dependiendo de la posición se le tira a la izquierda o derecha
		if (initPos.x > bounds.left + (worldWidth / 2)) {
			maxXDistance = bounds.left - initPos.x;
		} else {
			maxXDistance = bounds.right - initPos.x;
		}
		
		// Distancia que va a viajer horizontalmente. Se le hace un poco aleatoria.
		float xDistance = (float) (XFACTOR + ((1 - XFACTOR) * Math.random())) * maxXDistance;
		
		float vx = xDistance / t;
		
		return new Vector2(vx, vy);
	}
	
	// DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
	public void onTestButtonClicked(Button showAdBtn) {
	}
	// DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG



	

}
