package com.garrapeta.jumplings;

import android.graphics.PointF;
import android.graphics.RectF;
import android.widget.Button;

import com.badlogic.gdx.math.Vector2;
import com.garrapeta.gameengine.utils.PhysicsUtils;
import com.garrapeta.jumplings.actor.BladePowerUpActor;
import com.garrapeta.jumplings.actor.BombActor;
import com.garrapeta.jumplings.actor.EnemyActor;
import com.garrapeta.jumplings.actor.JumplingActor;
import com.garrapeta.jumplings.actor.LifePowerUpActor;

public abstract class Wave<T extends JumplingsWorld> implements GameEventsListener {

    // ------------------------------------------------- Constantes

    /** tag del log */
    public static final String LOG_SRC = JumplingsApplication.LOG_SRC_JUMPLINGS + ".wave";

    public static float ENEMY_OFFSET = JumplingActor.BASE_RADIUS * 2.5f;

    // ------------------------------------ Variables de instancia

    /** mundo dueño de la wave */
    private final T mWorld;

    /** Si la wave est� en ejecuci�n */
    private boolean mPlaying = false;

    /** nivel */
    protected int mLevel;
    
    /** If game is over */
    protected boolean mIsGameOver = false;


    // ------------------------------------------------ Constructor

    public Wave(T world, int level) {
        this.mWorld = world;
        this.mLevel = level;

        // Se resetean defaults
        mWorld.setGravityX(0);
    }

    // --------------------------------------------------- M�todos

    /**
     * Comienza la wave
     */
    public void start() {
        play();
    }

    /**
     * Pone en ejecuci�n la wave
     */
    public void play() {
        this.mPlaying = true;
    }

    /**
     * @return si est� en ejecuci�n
     */
    public boolean isPlaying() {
        return mPlaying;
    }

    /**
     * Para la wave
     */
    public final void pause() {
        mPlaying = false;
    }

    /**
     * @return nivel
     */
    public int getLevel() {
        return mLevel;
    }

    @Override
    public boolean onEnemyScaped(EnemyActor e) {
        return false;
    }
    
    @Override
    public boolean onGameOver() {
        mIsGameOver = true;
        return false;
    }
    
    @Override
    public boolean onCombo() {
        return false;
    }

    @Override
    public boolean onBombExploded(BombActor bomb) {
        return false;
    }

    @Override
    public boolean onEnemyKilled(EnemyActor enemy) {
        return false;
    }

    @Override
    public boolean onLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        return false;
    }

    @Override
    public boolean onBladePowerUpStart(BladePowerUpActor bladePowerUpActor) {
        return false;
    }
    
    @Override
    public boolean onBladePowerUpEnd() {
        return false;
    }

    public boolean onFail() {
        return false;
    }



    // --------------------------------------------------- M�todos abstractos

    /**
     * L�gica que se procesa en esta pantalla en cada frame
     */
    public final void processFrame(float gameTimeStep) {
        if (mPlaying) {
            onProcessFrame(gameTimeStep);
        }
    }

    /**
     * L�gica que se procesa en esta pantalla en cada frame
     */
    protected abstract void onProcessFrame(float gameTimeStep);

    // ------------------------------ M�todos de utilidad para crear enemigos

    protected float getLeftPos() {
        return mWorld.mViewport.getWorldBoundaries().left - ENEMY_OFFSET;
    }

    protected float getRightPos() {
        return mWorld.mViewport.getWorldBoundaries().right + ENEMY_OFFSET;
    }

    protected float getTopPos() {
        return mWorld.mViewport.getWorldBoundaries().top + ENEMY_OFFSET;
    }

    protected float getBottomPos() {
        return mWorld.mViewport.getWorldBoundaries().bottom - ENEMY_OFFSET;
    }

    protected float getRandomPosX() {
        RectF bounds = mWorld.mViewport.getWorldBoundaries();

        float init = bounds.left + ENEMY_OFFSET;
        float fin = bounds.right - ENEMY_OFFSET;
        float w = fin - init;
        return init + (float) (Math.random() * w);
    }

    protected float getRandomPosY() {
        RectF bounds = mWorld.mViewport.getWorldBoundaries();
        float minY = bounds.bottom;
        float maxY = (bounds.top - bounds.bottom) / 2;
        return (float) (minY + (Math.random() * (maxY - minY)));
    }

    protected Vector2 getInitialVelocity(PointF initPos) {
        float g = mWorld.getGravityY();
        RectF bounds = mWorld.mViewport.getWorldBoundaries();

        // Factor de aletoriedad (0 - 1)
        float XFACTOR = 0.9f;
        float YFACTOR = 0.7f;

        // Distancia m�xima que pueda viajar verticalmente
        float maxYDistance = bounds.top - initPos.y;

        // Distancia que va a viajer verticalmente. Se le hace un poco
        // aleatoria.
        float yDistance = (float) (YFACTOR + ((1 - YFACTOR) * Math.random())) * maxYDistance;

        float vy = (float) PhysicsUtils.getInitialVelocity(yDistance, 0, g);

        // Tiempo que va a estar viajando (arriba + abajo)
        float t = 2 * (float) PhysicsUtils.getTime(vy, 0, g);

        float worldWidth = bounds.right - bounds.left;

        // Distancia m�xima que pueda viajar horizontalmente
        float maxXDistance;
        // Dependiendo de la posici�n se le tira a la izquierda o derecha
        if (initPos.x > bounds.left + (worldWidth / 2)) {
            maxXDistance = bounds.left - initPos.x;
        } else {
            maxXDistance = bounds.right - initPos.x;
        }

        // Distancia que va a viajer horizontalmente. Se le hace un poco
        // aleatoria.
        float xDistance = (float) (XFACTOR + ((1 - XFACTOR) * Math.random())) * maxXDistance;

        float vx = xDistance / t;

        return new Vector2(vx, vy);
    }

    /**
     * Frees resources
     */
    public void dispose() {
    }

    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    public void onTestButtonClicked(Button showAdBtn) {
    }
    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG

    protected T getWorld() {
        return mWorld;
    }



}
