package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.jumplings.JumplingsGameWorld;

public abstract class HarmerActor extends Actor {

    protected JumplingsGameWorld mJWorld;
    public long timestamp;

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 20;

    public HarmerActor(JumplingsGameWorld mJWorld) {
        super(mJWorld, 100);
        this.mJWorld = mJWorld;
    }

    // ----------------------------------------------- M�todos de Actor

    @Override
    protected void processFrame(float gameTimeStep) {
        // efectos sobre los mainActors
        Object[] as = mJWorld.mainActors.toArray();

        int l = as.length;

        for (int i = 0; i < l; i++) {
            MainActor j = (MainActor) as[i];
            if (j.timestamp <= timestamp) {
                effectOver(j);
            }
        }
    }

    // ----------------------------------------------- M�todos propios
    protected abstract void effectOver(MainActor j);

}
