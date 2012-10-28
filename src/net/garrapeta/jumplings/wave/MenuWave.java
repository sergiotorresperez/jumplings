package net.garrapeta.jumplings.wave;

import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.gameengine.SyncGameMessage;
import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.jumplings.Wave;
import net.garrapeta.jumplings.actor.IntroActor;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;

/**
 * Wave used in the menu activity
 */
public class MenuWave extends Wave {

    // ----------------------------------------------------- Constantes

    /** Ms que hay entre las creaciones de jumplings */
    public static final int JUMPLING_CREATION_REALTIME = 1700;

    // --------------------------------------------------- Variables
    // ------------------------------------------------------------- Constructor

    /**
     * @param jWorld
     */
    public MenuWave(JumplingsWorld jWorld, IWaveEndListener listener) {
        super(jWorld, listener, 0);
    }

    // ------------------------------------------------------- M�todos heredados
    @Override
    public void start() {
        super.start();
        Log.i(LOG_SRC, "Starting Intro Wave");
        scheduleIntroActorCreation();
    }

    @Override
    public void onProcessFrame(float gameTimeStep) {
    }

    // ---------------------------------------------------- M�todos propios

    private void scheduleIntroActorCreation() {
        jWorld.post(new SyncGameMessage() {
            @Override
            public void doInGameLoop(GameWorld world) {
                createIntroActor();
                scheduleIntroActorCreation();
            }
        }, JUMPLING_CREATION_REALTIME);
    }

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
