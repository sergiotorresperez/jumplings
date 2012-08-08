package net.garrapeta.jumplings;

import net.garrapeta.jumplings.actor.SwipeActor;

public class Blade extends Weapon {
	
	
	// -------------------------------------------------------------------- Constantes
	
	public final static short WEAPON_CODE_BLADE = 2;
	
	private static int WEAPON_TIME_BLADE = 10000;
	
	// -------------------------------------------------------- Variables de instancia
	SwipeActor swipe;
	
		
	public Blade(JumplingsGameWorld demoWorld) {
		super(demoWorld);
		swipe = new SwipeActor(demoWorld);
		demoWorld.addActor(swipe);
		
	}

	@Override
	public void onTouchEvent(double[] info) {
		swipe.onTouchEvent(info);
	}
	
	public short getWeaponCode() {
		return WEAPON_CODE_BLADE;
	}

	public int getMaxTime() {
		return WEAPON_TIME_BLADE;
	}
}
