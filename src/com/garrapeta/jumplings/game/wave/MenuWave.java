package com.garrapeta.jumplings.game.wave;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.garrapeta.gameengine.Actor;
import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.gameengine.utils.L;
import com.garrapeta.jumplings.game.JumplingsWorld;
import com.garrapeta.jumplings.game.Wave;
import com.garrapeta.jumplings.game.actor.IntroActor;

/**
 * Wave used in the menu activity
 */
public class MenuWave extends Wave<JumplingsWorld> {

    // ----------------------------------------------------- Constantes

    /** Ms que hay entre las creaciones de jumplings */
    private static final int JUMPLING_CREATION_REALTIME = 1700;

    /** Maximun number of jumplings that can be in a square world unit */
    private static final float WORLD_WIDTH_JUMPLINGS_RATIO = 0.005f;

    // --------------------------------------------------- Variables
    // ------------------------------------------------------------- Constructor

    /**
     * @param jWorld
     */
    public MenuWave(JumplingsWorld jWorld) {
        super(jWorld, 0);
    }

    // ------------------------------------------------------- M�todos heredados
    @Override
    public void start() {
        super.start();
        if (L.sEnabled)
            Log.i(TAG, "Starting Intro Wave");
        scheduleIntroActorCreation();
    }

    @Override
    public void onProcessFrame(float gameTimeStep) {
    }

    // ---------------------------------------------------- M�todos propios

    private void scheduleIntroActorCreation() {
        getWorld().post(new SyncGameMessage() {
            @Override
            public void doInGameLoop(GameWorld world) {
                if (countIntroActors() < getMaxIntroActors()) {
                    createIntroActor();
                }
                scheduleIntroActorCreation();
            }

        }, JUMPLING_CREATION_REALTIME);
    }

    private int countIntroActors() {
        int introActorsCount = 0;
        for (Actor<?> actor : getWorld().mActors) {
            if (actor instanceof IntroActor) {
                introActorsCount++;
            }
        }
        return introActorsCount;
    }

    private int getMaxIntroActors() {
        RectF worldBoundaries = getWorld().mViewport.getWorldBoundaries();
        // FIXME: the - should not be needed. Maybe RectF is not a good class
        // for holding the worldBoundaries
        float worldSquareUnits = worldBoundaries.width() * -worldBoundaries.height();
        return (int) (worldSquareUnits * WORLD_WIDTH_JUMPLINGS_RATIO);
    }

    private void createIntroActor() {
        if (L.sEnabled)
            Log.i(TAG, "Creating intro jumpling");

        float worldXPos;
        float worldYPos;

        worldXPos = getRandomPosX();
        worldYPos = getBottomPos();

        PointF initPos = new PointF(worldXPos, worldYPos);
        Vector2 initVel = getInitialVelocity(initPos);

        IntroActor actor = getWorld().getFactory()
                                     .getIntroActor(initPos);
        actor.setLinearVelocity(initVel.x, initVel.y);
        getWorld().addActor(actor);
    }

}
