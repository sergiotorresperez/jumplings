package com.garrapeta.jumplings;

public abstract class Weapon {

	// ---------------------------------------------------- Contantes
	
	// --------------------------------------- Variables de instancia
	protected JumplingsGameWorld mWorld;
	
	private long creationTimeStamp;
	
	// ------------------------------------------------- Constructores
	
	public Weapon(JumplingsGameWorld jgWorld) {
		this.mWorld = jgWorld;
		this.creationTimeStamp = jgWorld.currentGameMillis();
	}
	
	// ----------------------------------------------- M�todos propios
	
	/**
	 * @param info
	 */
	public abstract void onTouchEvent(double[] info);
	
	public abstract short getWeaponCode();
	
	public abstract int getMaxTime();
	
	public abstract void onStart();
	public abstract void onEnd();
	
	public int getRemainingTime() {
		return getMaxTime() - (int) (mWorld.currentGameMillis() - creationTimeStamp);
	}
}
