package net.garrapeta.jumplings.scenario;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Capa del escerario
 * 
 * @author GaRRaPeTa
 */
class Layer {

    // ------------------------------------------------ Constantes

    // TODO: hacer esto en funci�n del FPS
    /** Ciclos que tarda en reflejarse un update de progreso */
    private float PROGRESS_UPDATE_CICLES = 5;

    /** Ciclos que tarda la ca�da cuando el jugador muere */
    private float GAMEOVER_FALL_CICLES = 50;

    // ------------------------------------ Variables de instancia

    private float mInitXPos;
    private float mInitYPos;

    private float mInitXVel;
    private float mInitYVel;

    private int mPatternWidth;
    private int mPatternHeight;

    private float mXPos = 0;
    private float mYPos = 0;

    /** Posci�n y a la que se tiene que llegar por un update */
    private float mDesiredYPos = 0;

    // velocidades, en p�xeles por ciclo
    // TODO: expresar las velocidades en p�xeles por segundo
    private float mXVel = 0;
    private float mYVel = 0;

    /** Componente de la velocidad aportada por los updates de progreso */
    private float mUpdateYVel = 0;

    // TODO: get rid of this
    private LayerScenario mScenario;


    private boolean mTilesX;
    private boolean mTilesY;
    private Bitmap mBitmap;

    // Altura m�xima del layer
    private float mMaxHeight;
    
    private int mCopiesX;
    private int mCopiesY;

    
    private Matrix mPatrix = new Matrix();
    // ----------------------------------------------- Constructor

    /**
     * @param bmp
     * @param maxHeight
     */
    Layer(LayerScenario scenario, Bitmap bmp, int maxHeight, float initXPos, float initYPos, float initXVel, float initYVel, boolean tilesX,
            boolean tilesY, int desiredWidth, int desiredHeight) {

        this.mScenario = scenario;

        int bitmapWidth = bmp.getWidth();
        int bitmapHeight = bmp.getHeight();

        this.mMaxHeight = maxHeight;

        this.mInitXPos = initXPos;
        this.mInitYPos = initYPos;

        this.mInitXVel = initXVel;
        this.mInitYVel = initYVel;
        mTilesX = tilesX;
        mTilesY = tilesY;

        mBitmap = bmp;

        mPatternWidth = bitmapWidth;
        mPatternHeight = bitmapHeight;

        mCopiesX = getCopies(desiredWidth, mPatternWidth, tilesX);
        mCopiesY = getCopies(desiredHeight, mPatternHeight, tilesY);

    }

    private int getCopies(int desired, int pattern, boolean tiles) {
        if (!tiles) {
            return 1;
        }
        int copies = (int) android.util.FloatMath.ceil((float) desired / pattern) + 1;
        return copies;
    }

    private float getOffset(float offset, float patternLength, boolean tiles) {
        if (tiles) {
            int laps = (int) (offset / patternLength) + 1;
            return offset - (patternLength * laps);
        } else {
            return offset;
        }

    }


    // -------------------------------------------- M�todos propios

    /**
     * Reseteo
     */
    public void reset() {
        mXPos = mInitXPos;
        mYPos = mInitYPos;

        mDesiredYPos = mYPos;

        mXVel = mInitXVel;
        mYVel = mInitYVel;

        mUpdateYVel = 0;
    }

    /**
     * @param gameTimeStep
     * @param physicsTimeStep
     */
    void processFrame(float gameTimeStep) {
        mXPos += mXVel; // xVel * (gameTimeStep / 1000);
        mYPos += mYVel; // yVel * (gameTimeStep / 1000);

        // aportaci�n de velocidad por el update
        if (mUpdateYVel != 0) {
            if (mUpdateYVel > 0) {
                mYPos = Math.min(mDesiredYPos, mYPos + mUpdateYVel);
            } else if (mUpdateYVel < 0) {
                mYPos = Math.max(mInitYPos, mYPos + mUpdateYVel);
            }

            // si ya ha llegado al punto deseado se para
            if (mYPos == mDesiredYPos) {
                mUpdateYVel = 0;
            }
        }

    }

    synchronized void setProgress(float progress) {
        if (progress <= 100) {
            float aux = mMaxHeight - mScenario.mWorld.mView.getHeight();
            mDesiredYPos = mInitYPos + (progress * aux) / 100;
            float diff = mDesiredYPos - mYPos;
            mUpdateYVel = diff / PROGRESS_UPDATE_CICLES;
        }
    }

    synchronized void onGameOver() {
        mDesiredYPos = mInitYPos;
        float diff = mDesiredYPos - mYPos;
        mUpdateYVel = diff / GAMEOVER_FALL_CICLES;
    }

    public void draw(Canvas canvas, Paint paint) {

        float tx = getOffset(mXPos, mPatternWidth, mTilesX);
        float ty = getOffset(mYPos, mPatternHeight, mTilesY);

        for (int i = 0; i < mCopiesX; i++) {
            for (int j = 0; j < mCopiesY; j++) {
                mPatrix.postTranslate(tx + mBitmap.getWidth() * i, ty + mBitmap.getHeight() * j);
                canvas.drawBitmap(mBitmap, mPatrix, paint);
                mPatrix.reset();
            }
        }

    }

}