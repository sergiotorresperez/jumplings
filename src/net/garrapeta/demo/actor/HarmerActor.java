package net.garrapeta.demo.actor;

import net.garrapeta.demo.JumplingsGameWorld;
import net.garrapeta.gameengine.Actor;

public abstract class HarmerActor extends Actor {

	protected JumplingsGameWorld jgWorld;
	public long timestamp;
	
	/**
	 *  Z-Index del actor
	 */
	public final static int Z_INDEX = 20;
	
	public HarmerActor(JumplingsGameWorld jgWorld) {
		super(jgWorld, 100);
		this.jgWorld = jgWorld;		
	}
	
	// ----------------------------------------------- Métodos de Actor
	
	public void doLogic(float gameTimeStep) {
		// efectos sobre los mainActors
		Object[] as = jgWorld.mainActors.toArray();
		
		int l = as.length;
		
		for (int i = 0; i < l; i++) {
			MainActor j = (MainActor)as[i];
			if (j.timestamp <= timestamp) {
				effectOver(j);
			}		
		}
	}
	
	@Override
	public boolean isPointInActor(float worldX, float worldY) {
		return false;
	}
	
	// ----------------------------------------------- Métodos propios
	protected abstract void effectOver(MainActor j);
	
	
}
