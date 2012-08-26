package net.garrapeta.jumplings.scenario;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 
 * Clase para pintar el escenario
 * 
 * @author GaRRaPeTa
 */
public class Scenario {

    // ------------------------------------------------ Constantes

    /** Tiempo que tarda el escenario en aparecer, en ms */
    private final static int FADE_IN_TIME = 750;

    // ------------------------------------ Variables de instancia

    JumplingsGameWorld mWorld;

    // cielo
    Layer mLayerBg0;
    // monta�as
    Layer mLayerBg1;
    // nubes
    Layer mLayerBg2;

    /** Tiempo que le queda al escenario para terminar de desaparecer */
    public float mFadingInRemainigTime = FADE_IN_TIME;

    private Scenario mPreviousScenario;

    private Paint mPaint = new Paint();

    private static final int BMP_SKY_ID = R.drawable.bg_blue_sky;
    private static final int BMP_HILLS_ID = R.drawable.bg_green_hills;
    private static final int BMP_CLOUDS_ID = R.drawable.bg_clouds;

    // ----------------------------------------------- Constructor

    /**
     * @param dWorld
     */
    public Scenario(JumplingsGameWorld dWorld, Scenario previousScenario) {
        mWorld = dWorld;
        mPreviousScenario = previousScenario;

        BitmapManager bm = dWorld.getBitmapManager();

        int viewWidth = dWorld.mView.getWidth();
        int viewHeight = dWorld.mView.getHeight();
        // Inicializaci�n de las layers
        {
            Bitmap bmp = bm.getBitmap(BMP_SKY_ID);
            int maxHeight = (int) (viewHeight * 1.5);
            mLayerBg0 = new Layer(this, bmp, maxHeight, 0, 0, 2, 0, true, true, viewWidth, viewHeight);
        }
        {
            int maxHeight = (int) (viewHeight * 2);
            Bitmap bmp = bm.getBitmap(BMP_HILLS_ID);
            float initYPos = viewHeight - bmp.getHeight();
            mLayerBg1 = new Layer(this, bmp, maxHeight, 0, initYPos, 0, 0, true, false, viewWidth, viewHeight);
        }
        {
            Bitmap bmp = bm.getBitmap(BMP_CLOUDS_ID);
            int maxHeight = (int) (viewHeight * 2.7);
            float initYPos = -maxHeight + bmp.getHeight();
            mLayerBg2 = new Layer(this, bmp, maxHeight, 0, initYPos, 3, 0, true, false, viewWidth, viewHeight);
        }
    }

    /**
     * Reseteo
     */
    public void reset() {
        mLayerBg0.reset();
        mLayerBg1.reset();
        mLayerBg2.reset();
    }

    public void setProgress(float progress) {
        mLayerBg0.setProgress(progress);
        mLayerBg1.setProgress(progress);
        mLayerBg2.setProgress(progress);
    }

    public void onGameOver() {
        mLayerBg0.onGameOver();
        mLayerBg1.onGameOver();
        mLayerBg2.onGameOver();
    }

    // --------------------------------------------- M�todos propios

    public void processFrame(float gameTimeStep) {
        if (mFadingInRemainigTime > 0) {
            mFadingInRemainigTime = Math.max(0, mFadingInRemainigTime - gameTimeStep);
            float factor = 1 - (mFadingInRemainigTime / FADE_IN_TIME);
            int alpha = (int) (255 * factor);
            mPaint.setAlpha(alpha);

            if (mPreviousScenario != null) {
                mPreviousScenario.processFrame(gameTimeStep);
                if (mFadingInRemainigTime == 0) {
                    mPreviousScenario = null;
                }
            }
        }

        mLayerBg0.processFrame(gameTimeStep);
        mLayerBg1.processFrame(gameTimeStep);
        mLayerBg2.processFrame(gameTimeStep);
    }

    public void draw(Canvas canvas) {
        if (JumplingsApplication.DRAW_SCENARIO) {
            if (mPreviousScenario != null) {
                mPreviousScenario.draw(canvas);
            }
            mLayerBg0.draw(canvas, mPaint);
            mLayerBg1.draw(canvas, mPaint);
            mLayerBg2.draw(canvas, mPaint);

        }
    }

}
