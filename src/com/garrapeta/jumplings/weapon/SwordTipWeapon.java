package com.garrapeta.jumplings.weapon;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.actor.MainActor;
import com.garrapeta.jumplings.module.FlashModule;

public class SwordTipWeapon extends TapWeapon {

    // --------------------------------------------------------------------
    // Constantes

    public final static short WEAPON_CODE_SWORDTIP = 2;

    private final static Random sRandom = new Random();

    SwordTipWeapon(JumplingsGameWorld jgWorld) {
        super(jgWorld, 100);
    }

    public short getWeaponCode() {
        return WEAPON_CODE_SWORDTIP;
    }

    @Override
    public void onStart(float gameTimeStep) {
        // nothing
    }

    @Override
    public void processFrame(float gameTimeStep) {
        // nothing
    }

    @Override
    protected TapActor getTapActor(JumplingsGameWorld jgWorld, PointF worldPos) {
        return new FingerprintActor(mWorld, worldPos);
    }

    /**
     * The actor that harms
     */
    private class FingerprintActor extends TapActor {

        // ----------------------------------------------------- Constantes

        private final static float KILL_RADIUS = 0.8f;
        private final static float LONGEVITY = 150;

        protected final static int BMP_SWORD_POINT_ID = R.drawable.sword_point;

        private final static int MAX_ANGLE = 360;
        // ----------------------------------------- Variables de instancia

        private Bitmap mBmpFingerprint;

        private final float mAngle;

        // -------------------------------------------------- Constructores

        public FingerprintActor(JumplingsGameWorld cWorld, PointF worldPos) {
            super(cWorld, worldPos, LONGEVITY, KILL_RADIUS);
            mWorld = cWorld;
            mWorldPos = worldPos;

            BitmapManager mb = getWorld().getBitmapManager();
            mBmpFingerprint = mb.getBitmap(BMP_SWORD_POINT_ID);

            mAngle = sRandom.nextInt(MAX_ANGLE);
        }

        // ----------------------------------------------- M�todos heredados

        @Override
        protected void onTapEffect() {
            mWorld.mFlashModule.flash(FlashModule.SWORD_STAB_KEY);
            mWorld.getSoundManager()
                  .play(JumplingsGameWorld.SAMPLE_SWORD_STAB);
        }

        @Override
        protected void drawBitmap(Canvas canvas, float screenPosX, float screenPosY) {
            mWorld.drawBitmap(canvas, screenPosX, screenPosY, mAngle, mBmpFingerprint, mPaint);
        }

        @Override
        protected void onMissed(MainActor actor) {
            // nothing
        }

        @Override
        protected void dispose() {
            super.dispose();
            mBmpFingerprint = null;
        }
    }

}
