package com.garrapeta.jumplings.game.weapon;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.gameengine.Viewport;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.game.JumplingsGameWorld;
import com.garrapeta.jumplings.game.actor.MainActor;
import com.garrapeta.jumplings.game.module.FlashModule;

public class FingerprintWeapon extends TapWeapon {

    // --------------------------------------------------------------------
    // Constantes

    public final static short WEAPON_CODE_FINGERPRINT = 0;

    private final static Random sRandom = new Random();

    FingerprintWeapon(JumplingsGameWorld jgWorld) {
        super(jgWorld, 100);
    }

    @Override
    public short getWeaponCode() {
        return WEAPON_CODE_FINGERPRINT;
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

        private final static float KILL_RADIUS = 1.2f;
        private final static float BLAST_RADIUS = KILL_RADIUS * 1.7f;
        private final static float BLAST_FORCE = 35;
        private final static float LONGEVITY = 150;

        protected final static int BMP_FINGERPRINT_ID = R.drawable.fingerprint;

        private final static int MAX_ANGLE = 30;
        // ----------------------------------------- Variables de instancia

        private Bitmap mBmpFingerprint;

        private final float mAngle;

        // -------------------------------------------------- Constructores

        public FingerprintActor(JumplingsGameWorld cWorld, PointF worldPos) {
            super(cWorld, worldPos, LONGEVITY, KILL_RADIUS);
            mWorld = cWorld;
            mWorldPos = worldPos;

            BitmapManager mb = getWorld().getBitmapManager();
            mBmpFingerprint = mb.getBitmap(BMP_FINGERPRINT_ID);

            mAngle = -MAX_ANGLE + sRandom.nextInt(MAX_ANGLE * 2);
        }

        // ----------------------------------------------- M�todos heredados

        @Override
        protected void onTapEffect() {
            mWorld.mFlashModule.flash(FlashModule.FINGERPRINT_KEY);
            mWorld.getSoundManager()
                  .play(JumplingsGameWorld.SAMPLE_FINGERPRINT);
        }

        @Override
        protected void drawBitmap(Canvas canvas, float screenPosX, float screenPosY) {
            mWorld.drawBitmap(canvas, screenPosX, screenPosY, mAngle, mBmpFingerprint, mPaint);
        }

        @Override
        protected void onMissed(MainActor actor) {
            if (intersects(BLAST_RADIUS, actor.mRadius, actor.getWorldPos())) {
                mWorld.applyBlast(Viewport.pointFToVector2(mWorldPos), actor.mMainBody, BLAST_RADIUS, BLAST_FORCE);
                actor.onBumpChange(true);
            }
        }

        @Override
        protected void dispose() {
            super.dispose();
            mBmpFingerprint = null;
        }
    }

}
