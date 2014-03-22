package com.garrapeta.jumplings.scenario;

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

    final private float mScenarioXOffset;
    final private float mScenarioYOffset;

    final private float mInitXVel;
    final private float mInitYVel;

    final private int mBitmapWidth;
    final private int mBitmapHeight;

    /** Posci�n y a la que se tiene que llegar por un update */
    // private float mDesiredYPos = 0;
    private float mAnimLeft = 0;

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

    // offsets produced by the init position
    private float mInitXOffset = 0;
    private float mInitYOffset = 0;

    // offset produced by the progress
    private float mProgressYOffset = 0;

    // offsets produced by the animation
    private float mAnimlXOffset = 0;
    private float mAnimlYOffset = 0;

    private Matrix mMatrix = new Matrix();

    // ----------------------------------------------- Constructor

    /**
     * @param bmp
     * @param maxHeight
     */
    Layer(LayerScenario scenario, Bitmap bmp, int maxHeight, float scenarioXOffset, float mcenarioXOffset, float initXVel, float initYVel, boolean tilesX,
            boolean tilesY, int desiredWidth, int desiredHeight) {

        this.mScenario = scenario;

        int bitmapWidth = bmp.getWidth();
        int bitmapHeight = bmp.getHeight();

        this.mMaxHeight = maxHeight;

        this.mScenarioXOffset = scenarioXOffset;
        this.mScenarioYOffset = mcenarioXOffset;

        this.mInitXVel = initXVel;
        this.mInitYVel = initYVel;
        mTilesX = tilesX;
        mTilesY = tilesY;

        mBitmap = bmp;

        mBitmapWidth = bitmapWidth;
        mBitmapHeight = bitmapHeight;

        mCopiesX = getCopies(desiredWidth, mBitmapWidth, tilesX);
        mCopiesY = getCopies(desiredHeight, mBitmapHeight, tilesY);

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
        if (!mTilesX) {
            mInitXOffset = mScenarioXOffset;
        } else {
            mInitXOffset = 0;
        }
        if (!mTilesY) {
            mInitYOffset = -mMaxHeight + mScenario.mWorld.mGameView.getHeight() + mScenarioYOffset;
        } else {
            mInitYOffset = 0;
        }

        mProgressYOffset = 0;

        mAnimLeft = 0;

        mXVel = mInitXVel;
        mYVel = mInitYVel;

        mAnimlYOffset = 0;
        mAnimlXOffset = 0;

        mUpdateYVel = 0;
    }

    /**
     * @param gameTimeStep
     * @param physicsTimeStep
     */
    void processFrame(float gameTimeStep) {
        mAnimlXOffset += mXVel;
        mAnimlYOffset += mYVel;

        // aportaci�n de velocidad por el update
        if (mAnimLeft != 0) {
            float delta;
            if (mUpdateYVel > 0) {
                delta = Math.min(mAnimLeft, mUpdateYVel);
            } else {
                delta = Math.max(mAnimLeft, mUpdateYVel);
            }
            mProgressYOffset += delta;
            mAnimLeft -= delta;
        }

    }

    synchronized void setProgress(float progress) {
        if (progress <= 100) {

            float newProgressPos = (mMaxHeight - mScenario.mWorld.mGameView.getHeight()) * progress / 100;
            startProgressAnim(newProgressPos - mProgressYOffset, PROGRESS_UPDATE_CICLES);
        }
    }

    private void startProgressAnim(float deltaProgress, float time) {
        mAnimLeft = deltaProgress;
        mUpdateYVel = mAnimLeft / time;
    }

    synchronized void onGameOver() {
        startProgressAnim(-mProgressYOffset, GAMEOVER_FALL_CICLES);
    }

    public void draw(Canvas canvas, Paint paint) {

        float xPos = mInitXOffset + mAnimlXOffset;
        float yPos = mInitYOffset + mAnimlYOffset + mProgressYOffset;

        float tx = getOffset(xPos, mBitmapWidth, mTilesX);
        float ty = getOffset(yPos, mBitmapHeight, mTilesY);

        for (int i = 0; i < mCopiesX; i++) {
            for (int j = 0; j < mCopiesY; j++) {
                float pty = ty + mBitmap.getHeight() * j;
                mMatrix.postTranslate(tx + mBitmap.getWidth() * i, pty);
                canvas.drawBitmap(mBitmap, mMatrix, paint);
                mMatrix.reset();
            }
        }
    }

}