package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.jumplings.JumplingsGameWorld;

public abstract class HarmerActor extends Actor {

    protected JumplingsGameWorld jgWorld;
    public long timestamp;

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 20;

    public HarmerActor(JumplingsGameWorld jgWorld) {
        super(jgWorld, 100);
        this.jgWorld = jgWorld;
    }

    // ----------------------------------------------- M�todos de Actor

    public void doLogic(float gameTimeStep) {
        // efectos sobre los mainActors
        Object[] as = jgWorld.mainActors.toArray();

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
