package net.garrapeta.jumplings.wave;

import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.jumplings.actor.IntroActor;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;

/**
 * Wave used in the menu activity
 */
public class MenuWave extends ActionBasedWave {

    // ----------------------------------------------------- Constantes

    /** Ms que hay entre las creaciones de jumplings */
    public static final int JUMPLING_CREATION_REALTIME = 1700;

    // --------------------------------------------------- Variables

    /** Acci�n que consiste en sacar al mu�eco saltar�n */
    private GameWaveAction jumplingCreationAction;

    // ------------------------------------------------------------- Constructor

    /**
     * @param jWorld
     */
    public MenuWave(JumplingsWorld jWorld, IWaveEndListener listener) {
        super(jWorld, listener, 0);

        jumplingCreationAction = new GameWaveAction(this) {
            @Override
            public void run() {
                createIntroActor();
                jumplingCreationAction.schedule(JUMPLING_CREATION_REALTIME);
            }

        };
    }

    // ------------------------------------------------------- M�todos heredados
    @Override
    public void start() {
        super.start();
        Log.i(LOG_SRC, "Starting Intro Wave");
        jumplingCreationAction.schedule(JUMPLING_CREATION_REALTIME);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void processFrameSub(float realTimeStep) {
    }

    @Override
    public float getProgress() {
        return 0;
    }

    // ---------------------------------------------------- M�todos propios

    private void createIntroActor() {
        Log.i(LOG_SRC, "Creating intro jumpling");

        float worldXPos;
        float worldYPos;

        worldXPos = getRandomPosX();
        worldYPos = getBottomPos();

        PointF initPos = new PointF(worldXPos, worldYPos);
        Vector2 initVel = getInitialVelocity(initPos);

        IntroActor actor = new IntroActor(jWorld, initPos);
        actor.setLinearVelocity(initVel.x, initVel.y);
        jWorld.addActor(actor);
    }

}
