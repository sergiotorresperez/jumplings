package net.garrapeta.jumplings;

import net.garrapeta.jumplings.actor.SwipeActor;

public class Blade extends Weapon {
	
	
	// -------------------------------------------------------------------- Constantes
	
	public final static short WEAPON_CODE_BLADE = 2;
	
	private static int WEAPON_TIME_BLADE = 10000;
	
	// -------------------------------------------------------- Variables de instancia
	SwipeActor mSwipe;
	
		
	public Blade(JumplingsGameWorld demoWorld) {
		super(demoWorld);
		mSwipe = new SwipeActor(demoWorld);
		demoWorld.addActor(mSwipe);
	}

	@Override
	public void onTouchEvent(double[] info) {
		mSwipe.onTouchEvent(info);
	}
	
	public short getWeaponCode() {
		return WEAPON_CODE_BLADE;
	}

	public int getMaxTime() {
		return WEAPON_TIME_BLADE;
	}
	
    @Override
    public void onEnded() {
        mJGWorld.removeActor(mSwipe);
    }

}
