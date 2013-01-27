package net.garrapeta.jumplings;

import net.garrapeta.gameengine.utils.PhysicsUtils;
import net.garrapeta.jumplings.actor.BladePowerUpActor;
import net.garrapeta.jumplings.actor.BombActor;
import net.garrapeta.jumplings.actor.EnemyActor;
import net.garrapeta.jumplings.actor.JumplingActor;
import net.garrapeta.jumplings.actor.LifePowerUpActor;
import net.garrapeta.jumplings.wave.IWaveEndListener;
import android.graphics.PointF;
import android.graphics.RectF;
import android.widget.Button;

import com.badlogic.gdx.math.Vector2;

public abstract class Wave<T extends JumplingsWorld> {

    // ------------------------------------------------- Constantes

    /** tag del log */
    public static final String LOG_SRC = JumplingsApplication.LOG_SRC_JUMPLINGS + ".wave";

    public static float ENEMY_OFFSET = JumplingActor.BASE_RADIUS * 2.5f;

    // ------------------------------------ Variables de instancia

    /** mundo dueño de la wave */
    private T mWorld;

    /** listener de la wave */
    protected IWaveEndListener mListener;

    /** Si la wave est� en ejecuci�n */
    private boolean mPlaying = false;

    /** nivel */
    protected int level;
    
    /** If game is over */
    protected boolean mIsGameOver = false;


    // ------------------------------------------------ Constructor

    public Wave(T world, IWaveEndListener listener, int level) {
        this.mWorld = world;
        this.mListener = listener;
        this.level = level;

        // Se resetean defaults
        mWorld.setGravityX(0);
    }

    // --------------------------------------------------- M�todos

    /**
     * Comienza la wave
     */
    public void start() {
        play();
        if (mListener != null) {
            mListener.onWaveStarted();
        }
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
        return level;
    }

    /**
     * M�todo ejecutado cuando un enemigo escapa de pantalla
     * 
     * @return si el evento es consumido por la wave
     */
    public boolean onEnemyScaped(EnemyActor e) {
        return false;
    }

    /**
     * M�todo ejecutado cuando una bomba estalla
     * 
     * @return si el evento es consumido por la wave
     */
    public boolean onBombExploded(BombActor bomb) {
        return false;
    }

    /**
     * M�todo ejecutado cuando el usuario coge un power up de vida
     * 
     * @return si el evento es consumido por la wave
     */
    public boolean onLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        return false;
    }

    /**
     * M�todo ejecutado cuando el usuario coge un power up de blade
     * 
     * @return si el evento es consumido por la wave
     */
    public boolean onBladePowerUp(BladePowerUpActor bladePowerUpActor) {
        return false;
    }

    /**
     * M�todo ejecutado cuando el jugador mata un enemigo
     * 
     * @return si el evento es consumido por la wave
     */
    public boolean onEnemyKilled(EnemyActor enemy) {
        return false;
    }

    public boolean onFail() {
        return false;
    }

    /**
     * M�todo ejecutado cuando el jugador pierde el juego
     * 
     * @return si el evento es consumido por la wave
     */
    public boolean onGameOver() {
        mIsGameOver = true;
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
        mWorld = null;
        mListener = null;
    }

    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    public void onTestButtonClicked(Button showAdBtn) {
    }
    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG

    protected T getWorld() {
        return mWorld;
    }



}
