package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;

public class BladePowerUpActor extends PowerUpActor {

    // ----------------------------------------------------------- Constantes

    public final static short JUMPER_CODE_POWER_UP_BLADE = 8;

    public final static float DEFAULT_RADIUS = BASE_RADIUS * 1.05f;

    // ------------------------------------------------- Variables est�ticas

    // vivo
    protected final static int BMP_SWORD_ID = R.drawable.powerup_sword;

    // debris
    protected final static int BMP_DEBRIS_SWORD_ID = R.drawable.powerup_debris_sword;

    // --------------------------------------------------- M�todos est�ticos

    // --------------------------------------------------- Constructor

    public BladePowerUpActor(JumplingsGameWorld mWorld) {
        super(mWorld);
        mCode = BladePowerUpActor.JUMPER_CODE_POWER_UP_BLADE;
    }

    // ------------------------------------------------- M�todos est�ticos

    static double getBladePowerUpBaseThread() {
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
        getWorld().onBladePowerUp(this);
        super.onHitted();
    }

}
