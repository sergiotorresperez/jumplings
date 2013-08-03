package com.garrapeta.jumplings;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.garrapeta.jumplings.actor.HarmerSlapActor;

public class WeaponSlap extends Weapon {

    // --------------------------------------------------------------------
    // Constantes

    public final static short WEAPON_CODE_GUN = 0;

    private static int WEAPON_TIME_GUN = Integer.MAX_VALUE;

    // -------------------------------------------------------- Variables de
    // instancia

    protected float bulletRadius;
    protected float bulletLongevity;

    protected long lastShootTimeStamp;

    protected int shootTimeGap;

    public WeaponSlap(JumplingsGameWorld jgWorld) {
        super(jgWorld);

        shootTimeGap = 100;
        bulletRadius = 0.5f;
        bulletLongevity = 150;
    }

    public void doLogic(float gameTimeStep) {

    }

    @Override
    public void onTouchEvent(double[] info) {
        if (info[0] == MotionEvent.ACTION_DOWN && (System.currentTimeMillis() - lastShootTimeStamp) >= shootTimeGap) {

        	mWorld.mFlashManager.flash(FlashManager.TAP_KEY);
            mWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_SLAP);

            lastShootTimeStamp = System.currentTimeMillis();

            PointF worldPos = mWorld.mViewport.screenToWorld((float) info[1], (float) info[2]);
            HarmerSlapActor bullet = new HarmerSlapActor(mWorld, worldPos, bulletRadius, bulletLongevity);
            bullet.setInitted();
 
            mWorld.addActor(bullet);
        }
    }

    public short getWeaponCode() {
        return WEAPON_CODE_GUN;
    }

    public int getMaxTime() {
        return WEAPON_TIME_GUN;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onEnd() {
    }

}
