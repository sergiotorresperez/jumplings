package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.graphics.PointF;

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

    public LifePowerUpActor(JumplingsGameWorld mJWorld, PointF worldPos) {
        super(mJWorld, worldPos);

        this.code = LifePowerUpActor.JUMPER_CODE_POWER_UP_LIFE;

        // vivo
        BitmapManager mb = mJWorld.getBitmapManager();
        bmpIcon = mb.getBitmap(BMP_HEART_ID);

        // debris
        bmpDebrisIcon = mb.getBitmap(BMP_DEBRIS_HEART_ID);

    }

    // ------------------------------------------------- M�todos est�ticos

    static double getLifePowerUpHitCount() {
        // Se le pone un valor positivo, para incentivar que el jugador la coja
        return 1.5f;
    }

    // --------------------------------------------- M�todos heredados

    @Override
    public void onHitted() {
        jgWorld.onLifePowerUp(this);
        super.onHitted();
    }

}
