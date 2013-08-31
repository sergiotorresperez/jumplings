package com.garrapeta.jumplings;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.garrapeta.jumplings.actor.HarmerSlapActor;
import com.garrapeta.jumplings.module.FlashModule;

public class WeaponSlap extends Weapon {

    // --------------------------------------------------------------------
    // Constantes

    public final static short WEAPON_CODE_GUN = 0;

    // -------------------------------------------------------- Variables de
    // instancia

    protected long lastShootTimeStamp;

    protected int shootTimeGap;

    public WeaponSlap(JumplingsGameWorld jgWorld) {
        super(jgWorld);
        shootTimeGap = 100;
    }

    public void doLogic(float gameTimeStep) {

    }

    @Override
    public void onTouchEvent(double[] info) {
        if (info[0] == MotionEvent.ACTION_DOWN && (System.currentTimeMillis() - lastShootTimeStamp) >= shootTimeGap) {

        	mWorld.mFlashModule.flash(FlashModule.TAP_KEY);
            mWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_SLAP);

            lastShootTimeStamp = System.currentTimeMillis();

            PointF worldPos = mWorld.mViewport.screenToWorld((float) info[1], (float) info[2]);
            HarmerSlapActor bullet = new HarmerSlapActor(mWorld, worldPos);
            bullet.setInitted();
 
            mWorld.addActor(bullet);
        }
    }

    public short getWeaponCode() {
        return WEAPON_CODE_GUN;
    }

    @Override
    public void onStart(float gameTimeStep) {
    	// nothing
    }

	@Override
	public void processFrame(float gameTimeStep) {
		// nothing
	}

}
