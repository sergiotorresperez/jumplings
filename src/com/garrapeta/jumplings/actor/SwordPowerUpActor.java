package com.garrapeta.jumplings.actor;

import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.weapon.SwordWeapon;

public class SwordPowerUpActor extends PowerUpActor {

    // ----------------------------------------------------------- Constantes

    public final static short JUMPER_CODE_POWER_UP_SWORD = 8;

    // ------------------------------------------------- Variables est�ticas

    // vivo
    protected final static int BMP_SWORD_ID = R.drawable.powerup_sword;

    // debris
    protected final static int BMP_DEBRIS_SWORD_ID = R.drawable.powerup_debris_sword;

    // --------------------------------------------------- M�todos est�ticos

    // --------------------------------------------------- Constructor

    public SwordPowerUpActor(JumplingsGameWorld mWorld) {
        super(mWorld);
        mCode = SwordPowerUpActor.JUMPER_CODE_POWER_UP_SWORD;
    }

    // ------------------------------------------------- M�todos est�ticos

    static double getSwordPowerUpBaseThread() {
        return 0;
    }

    // --------------------------------------------- M�todos heredados

    @Override
    protected void initBitmaps() {
        BitmapManager mb = getWorld().getBitmapManager();
        // vivo
        mBmpIcon = mb.getBitmap(BMP_SWORD_ID);
        // debris
        mBmpDebrisIcon = mb.getBitmap(BMP_DEBRIS_SWORD_ID);
    }

    @Override
    public void onHitted() {
        getWorld().setWeapon(SwordWeapon.WEAPON_CODE_SWORD);
        super.onHitted();
    }

    @Override
    protected void free(JumplingsFactory factory) {
        getWorld().getFactory()
                  .free(this);
    }

}
