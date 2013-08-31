package com.garrapeta.jumplings.actor;

import java.util.ArrayList;

import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.garrapeta.jumplings.JumplingsGameWorld;

public abstract class MainActor extends JumplingActor<JumplingsGameWorld> {

	// ---------------------------------------------------- Constantes
	
	/** C�digo del actor */
	protected short mCode = -1;
	
	/**
	 *  Z-Index del actor
	 */
	public final static int Z_INDEX = 0;
	
	public long timestamp;
	
	/** Fuerza con la que se desintegran los actores en basurilla  */
	public final static float DESINTEGRATION_FORCE = 60;
	
	// ------------------------------------------ Variables de instancia

	
	// ---------------------------------------------------------------- Constructor
	
	public MainActor(JumplingsGameWorld world, int zIndex) {
        super(world, zIndex);
        this.timestamp = System.currentTimeMillis();
    }

	// ------------------------------------------- M�todos est�ticos
	
	public static double getBaseThread(short code) {
		switch (code) {
		case RoundEnemyActor.JUMPER_CODE_SIMPLE:
			return RoundEnemyActor.getSimpleEnemyBaseThread();
		case DoubleEnemyActor.JUMPER_CODE_DOUBLE:
			return DoubleEnemyActor.getDoubleEnemyBaseThread();
		case DoubleSonEnemyActor.JUMPER_CODE_DOUBLE_SON:
			return DoubleSonEnemyActor.getSimpleEnemyBaseThread();
		case SplitterEnemyActor.JUMPER_CODE_SPLITTER_SIMPLE:
			return SplitterEnemyActor.getSplitterBaseThread(0);
		case SplitterEnemyActor.JUMPER_CODE_SPLITTER_DOUBLE:
			return SplitterEnemyActor.getSplitterBaseThread(1);
		case SplitterEnemyActor.JUMPER_CODE_SPLITTER_TRIPLE:
			return SplitterEnemyActor.getSplitterBaseThread(2);
		case BombActor.JUMPER_CODE_BOMB:
			return BombActor.getBombBaseThread();
		case LifePowerUpActor.JUMPER_CODE_POWER_UP_LIFE:
			return LifePowerUpActor.getLifePowerUpBaseThread();
		case SwordPowerUpActor.JUMPER_CODE_POWER_UP_SWORD:
			return SwordPowerUpActor.getSwordPowerUpBaseThread();
		}
		
		throw new IllegalArgumentException("Unknown mainActor code: " + code);
	}
	

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        getWorld().onMainActorAdded(this);
    }

    @Override
    protected void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        getWorld().onMainActorRemoved(this);
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
			    getWorld().destroyJoint(this, (Joint) aux[i]);
			}
		}
		
		// se aceleran para que salgan disparados
		applyBlast(getDebrisBodies(), DESINTEGRATION_FORCE);
	}
	
	
	/**
	 * Acerela los actores como si fuera efecto de una onda expansiva
	 * @param as
	 */
	protected void applyBlast(ArrayList<JumplingActor<?>> as, float force) {
		double twoPi = 2 * Math.PI;
		double offset = Math.random() * twoPi;
		int l = as.size();
		for (int i = 0; i < l; i++) {
			int remaining = l - i;
			int index =  (int) Math.floor(Math.random() * remaining);
			JumplingActor<?> a = as.get(index);
			
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
	protected abstract ArrayList<JumplingActor<?>> getDebrisBodies();
	

	
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	public void onHitted() {
		desintegrateInDebris();
		getWorld().removeActor(this);
	}

}
