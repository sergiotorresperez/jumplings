package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.graphics.PointF;

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

    public BladePowerUpActor(JumplingsGameWorld jgWorld, PointF worldPos) {
        super(jgWorld, worldPos);
        code = BladePowerUpActor.JUMPER_CODE_POWER_UP_BLADE;
        BitmapManager mb = jWorld.getBitmapManager();
        // vivo
        bmpIcon = mb.getBitmap(BMP_SWORD_ID);
        // debris
        bmpDebrisIcon = mb.getBitmap(BMP_DEBRIS_SWORD_ID);
    }

    // ------------------------------------------------- M�todos est�ticos

    static double getBladePowerUpHitCount() {
        // Se le pone un valor negativo, para incentivar la aparici�n de
        // enemigos
        return -1f;
    }

    // --------------------------------------------- M�todos heredados
    @Override
    public void onHitted() {
        jgWorld.onBladePowerUp(this);
        super.onHitted();
    }

}
