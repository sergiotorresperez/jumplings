package com.garrapeta.jumplings.weapon;

import com.garrapeta.gameengine.Actor;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.actor.MainActor;

public abstract class Weapon {

	// ---------------------------------------------------- Contantes
	
	// --------------------------------------- Variables de instancia
	protected JumplingsGameWorld mWorld;
	
	// ------------------------------------------------- Constructores
	
	public Weapon(JumplingsGameWorld jgWorld) {
		mWorld = jgWorld;
	}
	
	// ----------------------------------------------- M�todos propios
	
	/**
	 * @param info
	 */
	public abstract void onTouchEvent(double[] info);
	
	public abstract short getWeaponCode();

	public abstract void onStart(float gameTimeStep);

	public abstract void processFrame(float gameTimeStep);
	
	
	
	/**
	 * Actor associated to the weapon that does the real damage and has a visual representation
	 */
	protected abstract class HarmerActor extends Actor<JumplingsGameWorld> {

	    public long mTimestamp;

	    /**
	     * Z-Index del actor
	     */
	    public final static int Z_INDEX = 20;
	    
	    public HarmerActor(JumplingsGameWorld world) {
	        super(world, 100);
	    }

	    // ----------------------------------------------- M�todos de Actor

	    @Override
	    protected void processFrame(float gameTimeStep) {
	        // efectos sobre los mainActors
	        Object[] as = getWorld().mMainActors.toArray();

	        int l = as.length;

	        for (int i = 0; i < l; i++) {
	            MainActor j = (MainActor) as[i];
	            if (j.timestamp <= mTimestamp) {
	                effectOver(j);
	            }
	        }
	    }


	    // ----------------------------------------------- M�todos propios
	    protected abstract void effectOver(MainActor j);

	}

}
