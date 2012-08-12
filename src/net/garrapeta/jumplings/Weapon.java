package net.garrapeta.jumplings;

public abstract class Weapon {

	// ---------------------------------------------------- Contantes
	
	// --------------------------------------- Variables de instancia
	protected JumplingsGameWorld mJGWorld;
	
	private long creationTimeStamp;
	
	// ------------------------------------------------- Constructores
	
	public Weapon(JumplingsGameWorld jgWorld) {
		this.mJGWorld = jgWorld;
		this.creationTimeStamp = jgWorld.currentGameMillis();
	}
	
	// ----------------------------------------------- M�todos propios
	
	/**
	 * @param info
	 */
	public abstract void onTouchEvent(double[] info);
	
	public abstract short getWeaponCode();
	
	public abstract int getMaxTime();
	
	public abstract void onEnded();
	
	public int getRemainingTime() {
		return getMaxTime() - (int) (mJGWorld.currentGameMillis() - creationTimeStamp);
	}
}
