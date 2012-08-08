package net.garrapeta.demo.wave;

import net.garrapeta.demo.JumplingsWorld;
import net.garrapeta.demo.actor.IntroActor;
import android.graphics.PointF;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;

public class IntroWave extends ActionBasedWave {
	
	// ----------------------------------------------------- Constantes 
	
	/** Ms que hay entre las creaciones de jumplings */
	public static final int JUMPLING_CREATION_REALTIME    = 2000;
	
	// --------------------------------------------------- Variables
	
	/** Acci�n que consiste en sacar al mu�eco saltar�n */ 
	private GameWaveAction jumplingCreationAction;
	
	// ------------------------------------------------------------- Constructor
	
	/** 
	 * @param jWorld
	 */
	public IntroWave(JumplingsWorld jWorld, IWaveEndListener listener) {
		super(jWorld, listener, 0);
		
		jWorld.setGravityY(-SensorManager.GRAVITY_EARTH);
		
		jumplingCreationAction = new GameWaveAction(this) {
			@Override
			public void run() {
				createJumplingWave();
				jumplingCreationAction.schedule(JUMPLING_CREATION_REALTIME);
			}


		};
	}
	
	
	// ------------------------------------------------------- M�todos heredados	
	@Override
	public  void start() {
		super.start();
		Log.i(LOG_SRC, "Starting Intro Wave");
		jumplingCreationAction.schedule(JUMPLING_CREATION_REALTIME);
	}


	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void processFrameSub(float realTimeStep) {
	}

	@Override
	public float getProgress() {
		return 0;
	}
	
	// ---------------------------------------------------- M�todos propios
	
	private void createJumplingWave() {
		Log.i(LOG_SRC, "Creating intro jumpling");
		
		float worldXPos;
		float worldYPos;
		
		worldXPos = getRandomPosX();
		worldYPos = getBottomPos();
		
		PointF initPos = new PointF(worldXPos, worldYPos);
		Vector2 initVel = getInitialVelocity(initPos);
		
		IntroActor actor = new IntroActor(jWorld, initPos);
		actor.setLinearVelocity(initVel.x, initVel.y);
		jWorld.addActor(actor);
	}

}
