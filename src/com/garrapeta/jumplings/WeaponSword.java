package com.garrapeta.jumplings;

import com.garrapeta.jumplings.actor.HarmerSwipeActor;

public class WeaponSword extends Weapon {
	
	
	// -------------------------------------------------------------------- Constantes
	
	public final static short WEAPON_CODE_SWORD = 2;
	
	private static int LIFE_TIME = 10000;
	
	private final WeaponSwordListener mListener;
	
	// -------------------------------------------------------- Variables de instancia
	HarmerSwipeActor mSwipe;
	
	private float mRemainingLife;
		
	public WeaponSword(JumplingsGameWorld jgWorld, WeaponSwordListener listener) {
		super(jgWorld);
		mSwipe = new HarmerSwipeActor(jgWorld);
		mSwipe.setInitted();
		jgWorld.addActor(mSwipe);
		mListener = listener;
	}

	@Override
	public void onTouchEvent(double[] info) {
		mSwipe.onTouchEvent(info);
	}
	
	public short getWeaponCode() {
		return WEAPON_CODE_SWORD;
	}

    @Override
    public void onStart(float gameTimeStep ) {
    	mListener.onSwordStarted();
    	mRemainingLife = LIFE_TIME;
        mWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_SWORD_SHEATH);
    }

	@Override
	public void processFrame(float gameTimeStep) {
        if (mListener == null) {
        	return;
        }
        mRemainingLife -= gameTimeStep;
		if (mRemainingLife > 0) {
			mListener.onSwordRemainingTimeUpdated(mRemainingLife / LIFE_TIME);
        } else {
        	mWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_SWORD_UNSHEATH);
        	mWorld.removeActor(mSwipe);
        	mListener.onSwordEnded();
        }
	}

	/**
	 * Listener of the events produced by the sword
	 */
	public static interface WeaponSwordListener {
		public void onSwordStarted();
		/**
		 * @param remaining, between 0 and 1
		 */
		public void onSwordRemainingTimeUpdated(float remaining);
		public void onSwordEnded();
	}
}
