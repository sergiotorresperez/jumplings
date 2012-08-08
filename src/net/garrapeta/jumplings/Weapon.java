package net.garrapeta.jumplings;

public abstract class Weapon {

	// ---------------------------------------------------- Contantes
	
	// --------------------------------------- Variables de instancia
	protected JumplingsGameWorld jgWorld;
	
	private long creationTimeStamp;
	
	// ------------------------------------------------- Constructores
	
	public Weapon(JumplingsGameWorld jgWorld) {
		this.jgWorld = jgWorld;
		this.creationTimeStamp = jgWorld.currentGameMillis();
	}
	
	// ----------------------------------------------- M�todos propios
	
	/**
	 * @param info
	 */
	public abstract void onTouchEvent(double[] info);
	
	public abstract short getWeaponCode();
	
	public abstract int getMaxTime();
	
	public int getRemainingTime() {
		return getMaxTime() - (int) (jgWorld.currentGameMillis() - creationTimeStamp);
	}
}
