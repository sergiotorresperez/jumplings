package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.jumplings.JumplingsGameWorld;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;

public abstract class MainActor extends JumplingActor {

	// ---------------------------------------------------- Constantes
	
	/** C�digo del actor */
	protected short mCode = -1;
	
	/**
	 *  Z-Index del actor
	 */
	public final static int Z_INDEX = 0;
	
	public long timestamp;
	
	/** Fuerza con la que se desintegran los actores en basurilla  */
	public final static float DESINTEGRATION_FORCE = 100;
	
	// ------------------------------------------ Variables de instancia
	
    JumplingsGameWorld mJgWorld;
	

	
	// ---------------------------------------------------------------- Constructor
	
	/**
	 * @param mJWorld
	 * @param worldPos
	 * @param radius
	 * @param zIndex
	 */
	public MainActor(JumplingsGameWorld mJWorld, PointF worldPos, float radius, int zIndex) {
		super(mJWorld, radius, zIndex, worldPos);
		this.mJgWorld = mJWorld;
		this.timestamp = System.currentTimeMillis();
	}
	
	// ------------------------------------------- M�todos est�ticos
	
	public static double getHitCount(short code) {
		switch (code) {
		case RoundEnemyActor.JUMPER_CODE_SIMPLE:
			return RoundEnemyActor.getSimpleEnemyActorHitCount();
		case DoubleEnemyActor.JUMPER_CODE_DOUBLE:
			return DoubleEnemyActor.getDoubleEnemyActorHitCount();
		case DoubleSonEnemyActor.JUMPER_CODE_DOUBLE_SON:
			return DoubleSonEnemyActor.getSimpleEnemyActorHitCount();
		case SplitterEnemyActor.JUMPER_CODE_SPLITTER_SIMPLE:
			return SplitterEnemyActor.getSplitterHitCount(0);
		case SplitterEnemyActor.JUMPER_CODE_SPLITTER_DOUBLE:
			return SplitterEnemyActor.getSplitterHitCount(1);
		case SplitterEnemyActor.JUMPER_CODE_SPLITTER_TRIPLE:
			return SplitterEnemyActor.getSplitterHitCount(2);
		case BombActor.JUMPER_CODE_BOMB:
			return BombActor.getBombHitCount();
		case LifePowerUpActor.JUMPER_CODE_POWER_UP_LIFE:
			return LifePowerUpActor.getLifePowerUpHitCount();
		case BladePowerUpActor.JUMPER_CODE_POWER_UP_BLADE:
			return BladePowerUpActor.getBladePowerUpHitCount();
		}
		
		throw new IllegalArgumentException("Unknown mainActor code: " + code);
	}
	


	// ------------------------------------------------------- M�todos Propios
	
	/**
	 * @return c�digo del actor
	 */
	public final short getCode() {
		return mCode;
	}
	
	/**
	 * Explosi�n del actor
	 */
	public void desintegrateInDebris() { 
		// se rompen las joints
		if (mJoints != null) {
			Object[] aux = mJoints.toArray();
			for (int i = 0; i < aux.length; i++) {
				mJWorld.destroyJoint(this, (Joint) aux[i]);
			}
		}
		
		// se aceleran para que salgan disparados
		applyBlast(getDebrisBodies(), DESINTEGRATION_FORCE);
	}
	
	
	/**
	 * Acerela los actores como si fuera efecto de una onda expansiva
	 * @param as
	 */
	protected void applyBlast(ArrayList<JumplingActor> as, float force) {
		double twoPi = 2 * Math.PI;
		double offset = Math.random() * twoPi;
		int l = as.size();
		for (int i = 0; i < l; i++) {
			int remaining = l - i;
			int index =  (int) Math.floor(Math.random() * remaining);
			JumplingActor a = as.get(index);
			
			double angle = (offset + ( twoPi / l) * i) % twoPi;
			
//			b.setLinearVelocity(new Vector2(b.getLinearVelocity().x * 2, 
//					                        b.getLinearVelocity().y * 2));
			float mass = a.getMass();
			float fX = (float) Math.sin(angle) * mass * force;
			float fY = (float) Math.cos(angle) * mass * force;
			
			Body b = a.mMainBody;
			Vector2 p = b.getWorldCenter(); 
			b.applyForce(fX, fY, p.x, p.y);
			as.remove(a);
		}
	}
	
	/**
	 * Desintegraci�n en basurilla
	 * @return  array con los actores de basurilla
	 */
	protected abstract ArrayList<JumplingActor> getDebrisBodies();
	

	
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	public void onHitted() {
		desintegrateInDebris();
		mJWorld.removeActor(this);
	}

    @Override
    protected void dispose() {
        super.dispose();
        mJgWorld = null;
    }
}
