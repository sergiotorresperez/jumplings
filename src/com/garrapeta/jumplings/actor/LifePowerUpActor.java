package com.garrapeta.jumplings.actor;

import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.R;

public class LifePowerUpActor extends PowerUpActor {

    // ----------------------------------------------------------- Constantes

    public final static short JUMPER_CODE_POWER_UP_LIFE = 7;


    // ------------------------------------------------- Variables est�ticas

    // vivo
    protected final static int BMP_HEART_ID = R.drawable.powerup_heart;

    // debris
    protected final static int BMP_DEBRIS_HEART_ID = R.drawable.powerup_debris_heart;

    // --------------------------------------------------- Constructor

    public LifePowerUpActor(JumplingsGameWorld mWorld) {
        super(mWorld);
        mCode = LifePowerUpActor.JUMPER_CODE_POWER_UP_LIFE;
    }

    // ------------------------------------------------- M�todos est�ticos

    static double getLifePowerUpBaseThread() {
        return 0;
    }

    // --------------------------------------------- M�todos heredados

    @Override
    protected void initBitmaps() {
        // vivo
        BitmapManager mb = getWorld().getBitmapManager();
        mBmpIcon = mb.getBitmap(BMP_HEART_ID);

        // debris
        mBmpDebrisIcon = mb.getBitmap(BMP_DEBRIS_HEART_ID);
    }
 
    @Override
    public void onHitted() {
        getWorld().onLifePowerUp(this);
        super.onHitted();
    }

    @Override
    protected void free(JumplingsFactory factory) {
        getWorld().getFactory().free(this);
    }

}
