package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;

public class LifePowerUpActor extends PowerUpActor {

    // ----------------------------------------------------------- Constantes

    public final static short JUMPER_CODE_POWER_UP_LIFE = 7;

    public final static float DEFAULT_RADIUS = BASE_RADIUS * 1.05f;

    // ------------------------------------------------- Variables est�ticas

    // vivo
    protected final static int BMP_HEART_ID = R.drawable.powerup_heart;

    // debris
    protected final static int BMP_DEBRIS_HEART_ID = R.drawable.powerup_debris_heart;

    // --------------------------------------------------- Constructor

    public LifePowerUpActor(JumplingsGameWorld mJWorld) {
        super(mJWorld);
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
        BitmapManager mb = mJWorld.getBitmapManager();
        mBmpIcon = mb.getBitmap(BMP_HEART_ID);

        // debris
        mBmpDebrisIcon = mb.getBitmap(BMP_DEBRIS_HEART_ID);
    }
 
    @Override
    public void onHitted() {
        mJgWorld.onLifePowerUp(this);
        super.onHitted();
    }


}
