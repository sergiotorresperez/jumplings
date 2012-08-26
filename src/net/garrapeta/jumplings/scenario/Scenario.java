package net.garrapeta.jumplings.scenario;

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
    private final static int FADE_IN_TIME = 1000;

    // ------------------------------------ Variables de instancia

    JumplingsGameWorld dWorld;

    // cielo
    Layer layerBg0;
    // monta�as
    Layer layerBg1;
    // nubes
    Layer layerBg2;


    /** Tiempo que le queda al escenario para terminar de desaparecer */
    public float fadingInRemainigTime = FADE_IN_TIME;

    private Paint paint = new Paint();

    private Bitmap mSkyBitmap;
    private Bitmap mHillsBitmap;
    private Bitmap mCloudsBitmap;

    // ----------------------------------------------- Constructor

    /**
     * @param dWorld
     */
    public Scenario(JumplingsGameWorld dWorld) {
        this.dWorld = dWorld;
        
        loadBitmaps();

        // Inicializaci�n de las layers
        {
            int maxHeight = (int) (dWorld.mView.getHeight() * 1.5);
            layerBg0 = new Layer(this, mSkyBitmap, maxHeight, 0, 0, 2, 0, true, true);
        }
        {
            int maxHeight = (int) (dWorld.mView.getHeight() * 2);
            float initYPos = dWorld.mView.getHeight() - mHillsBitmap.getHeight();
            layerBg1 = new Layer(this, mHillsBitmap, maxHeight, 0, initYPos, 0, 0, false, false);
        }
        {
            int maxHeight = (int) (dWorld.mView.getHeight() * 2.7);
            float initYPos = -maxHeight + mCloudsBitmap.getHeight();
            layerBg2 = new Layer(this, mCloudsBitmap, maxHeight, 0, initYPos, 3, 0, true, false);
        }
    }

    /**
     * Loads the bitmaps
     */
    private void loadBitmaps() {
        mSkyBitmap    = dWorld.getBitmapManager().loadBitmap(R.drawable.bg_blue_sky);
        mHillsBitmap  = dWorld.getBitmapManager().loadBitmap(R.drawable.bg_green_hills);
        mCloudsBitmap = dWorld.getBitmapManager().loadBitmap(R.drawable.bg_clouds);
    }

    /**
     * Reseteo
     */
    public void reset() {
        layerBg0.reset();
        layerBg1.reset();
        layerBg2.reset();
    }

    public void setProgress(float progress) {
        layerBg0.setProgress(progress);
        layerBg1.setProgress(progress);
        layerBg2.setProgress(progress);
    }

    public void onGameOver() {
        layerBg0.onGameOver();
        layerBg1.onGameOver();
        layerBg2.onGameOver();
    }

    // --------------------------------------------- M�todos propios

    public void processFrame(float gameTimeStep) {
        if (fadingInRemainigTime > 0) {
            fadingInRemainigTime = Math.max(0, fadingInRemainigTime - gameTimeStep);
            int alpha = (int) (255 * (1 - (fadingInRemainigTime / FADE_IN_TIME)));
            paint.setAlpha(alpha);
        }
        layerBg0.processFrame(gameTimeStep);
        layerBg1.processFrame(gameTimeStep);
        layerBg2.processFrame(gameTimeStep);
    }

    public void draw(Canvas canvas) {
        if (JumplingsApplication.DRAW_SCENARIO) {
            layerBg0.draw(canvas, paint);
            layerBg1.draw(canvas, paint);
            layerBg2.draw(canvas, paint);
        }
    }

}
