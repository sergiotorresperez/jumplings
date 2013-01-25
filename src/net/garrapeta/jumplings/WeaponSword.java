package net.garrapeta.jumplings;

import net.garrapeta.jumplings.actor.HarmerSwipeActor;

public class WeaponSword extends Weapon {
	
	
	// -------------------------------------------------------------------- Constantes
	
	public final static short WEAPON_CODE_BLADE = 2;
	
	private static int WEAPON_TIME_BLADE = 10000;
	
	// -------------------------------------------------------- Variables de instancia
	HarmerSwipeActor mSwipe;
	
		
	public WeaponSword(JumplingsGameWorld demoWorld) {
		super(demoWorld);
		mSwipe = new HarmerSwipeActor(demoWorld);
		mSwipe.setInitted();
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
    public void onStart() {
        mWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_SWORD_SHEATH);
    }

    @Override
    public void onEnd() {
        mWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_SWORD_UNSHEATH);
        mWorld.removeActor(mSwipe);
    }

}
