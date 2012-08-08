package net.garrapeta.jumplings.wave;

import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.actor.BombActor;
import net.garrapeta.jumplings.actor.EnemyActor;
import net.garrapeta.jumplings.actor.LifePowerUpActor;
import net.garrapeta.jumplings.actor.MainActor;
import net.garrapeta.jumplings.actor.RoundEnemyActor;
import net.garrapeta.jumplings.actor.SplitterEnemyActor;
import android.graphics.PointF;
import android.view.View;
import android.widget.Button;

import com.badlogic.gdx.math.Vector2;

@SuppressWarnings("unused")
public class TestWave extends Wave {
	
	// ----------------------------------------------------- Constantes 
	
	// Clave para referirse a esta wave
	public final static String WAVE_KEY = TestWave.class.getCanonicalName();
	
	
	
	
	// --------------------------------------------------- Variables

	private int kills      = 0;
	private int totalKills = 20;
	
	JumplingsGameWorld jgWorld;
	
	// ------------------------------------------------------------- Constructor
	
	/** 
	 * @param jgWorld
	 * @param level
	 * @param listener
	 */
	public TestWave(JumplingsGameWorld jgWorld, IWaveEndListener listener) {
		super(jgWorld, listener, 0);
		this.jgWorld = jgWorld;
		if (JumplingsApplication.DEBUG_ENABLED) {
			this.jgWorld.jgActivity.testBtn.setVisibility(View.VISIBLE);
			this.jgWorld.jgActivity.weaponsRadioGroup.setVisibility(View.VISIBLE);
		}
	}

	// ------------------------------------------------------ M�todos de BaseWave 

	@Override
	public void processFrame(float gameTimeStep) {
	}
	

	@Override
	public void onTestButtonClicked(Button showAdBtn) {
//		this.world.cActivity.runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				world.cActivity.showDialog(DemoActivity.DIALOG_AD_ID);
//				
//			}
//		});
//		
		createEnemy();
//		createPowerUp();
		
	}
	
	@Override
	public float getProgress() {
		return kills * 100 / totalKills;
	}
	
	@Override
	public boolean onEnemyKilled(EnemyActor enemy) {
		kills ++;
		return false;
	}
	
	// ---------------------------------------------------- M�todos propios

	

	public void createEnemy() {
		
		boolean debug = false;
		
		float worldXPos;
		float worldYPos;
		
		if (debug) {
//			world.setGravityY(0);
			worldXPos = (jgWorld.viewport.getWorldBoundaries().right - jgWorld.viewport.getWorldBoundaries().left)   / 2; 
			//worldYPos = (world.worldBoundaries.top   - ENEMY_OFFSET);  
			worldYPos = 2;
		} else {
			worldXPos = getRandomPosX();
			worldYPos = getBottomPos();
		}

		
		PointF initPos = new PointF(worldXPos, worldYPos);
		Vector2 initVel = getInitialVelocity(initPos);
		
		RoundEnemyActor mainActor = new RoundEnemyActor(jgWorld, initPos);

//		ShapeActor sa = world.cActivity.CreateShapeEnemy(ShapelingsGameActivity.SHAPE_CIRCLE, initPos, bodyWorldRadius);
//		JumperActor mainActor =  new SplitterEnemyActor(world, initPos, color2, 2);

//		EnemyActor enemy = new SplitterEnemyActor(world, SplitterEnemyActor.DEFAULT_BASE_RADIUS, 
//                initPos, (short) 2, 2);
		
//				DoubleEnemyActor ba = new DoubleEnemyActor(world, bodyWorldRadius, worldPos, color1, color2);
//		SquareEnemyActor ba = new SquareEnemyActor(world, bodyWorldRadius, worldPos, color1);
//		JumperActor mainActor =  new BombActor(world, initPos);
		
		if (!debug) {
			mainActor.setLinearVelocity(initVel.x, initVel.y);
		} else {
			mainActor.setLinearVelocity(initVel.x, initVel.y);
			
		}
		jgWorld.addActor(mainActor);
		
		
	}
	
	public void createPowerUp() {
		
		boolean debug = false;
		
		float worldXPos;
		float worldYPos;
		
		if (debug) {
			jgWorld.setGravityY(0);
			worldXPos = (jgWorld.viewport.getWorldBoundaries().right - jgWorld.viewport.getWorldBoundaries().left)   / 2; 
			worldYPos = (jgWorld.viewport.getWorldBoundaries().top   - jgWorld.viewport.getWorldBoundaries().bottom) / 2;  
		} else {
			worldXPos = getRandomPosX();
			worldYPos = getBottomPos();
		}

		
		PointF initPos = new PointF(worldXPos, worldYPos);
		Vector2 initVel = getInitialVelocity(initPos);
			
//		RoundEnemyActor ba = new RoundEnemyActor(world, bodyWorldRadius, worldPos, color1);

//		ShapeActor sa = world.cActivity.CreateShapeEnemy(ShapelingsGameActivity.SHAPE_CIRCLE, initPos, bodyWorldRadius);
		MainActor mainActor =  new LifePowerUpActor(jgWorld, initPos);

//		EnemyActor enemy = new SplitterEnemyActor(world, SplitterEnemyActor.DEFAULT_BASE_RADIUS, 
//                initPos, (short) 2, 2);
		
		//		DoubleEnemyActor ba = new DoubleEnemyActor(world, bodyWorldRadius, worldPos, color1, color2);
//		SquareEnemyActor ba = new SquareEnemyActor(world, bodyWorldRadius, worldPos, color1);
		
		
		if (!debug) {
			mainActor.setLinearVelocity(initVel.x, initVel.y);
		}
		jgWorld.addActor(mainActor);
		
		
	}


		
}
