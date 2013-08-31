package com.garrapeta.jumplings;

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
}
