package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.jumplings.JumplingsGameWorld;

public abstract class HarmerActor extends Actor<JumplingsGameWorld> {

    public long mTimestamp;

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 20;

    public HarmerActor(JumplingsGameWorld world) {
        super(world, 100);
    }

    // ----------------------------------------------- M�todos de Actor

    @Override
    protected void processFrame(float gameTimeStep) {
        // efectos sobre los mainActors
        Object[] as = getWorld().mainActors.toArray();

        int l = as.length;

        for (int i = 0; i < l; i++) {
            JumplingsGameActor j = (JumplingsGameActor) as[i];
            if (j.timestamp <= mTimestamp) {
                effectOver(j);
            }
        }
    }


    // ----------------------------------------------- M�todos propios
    protected abstract void effectOver(JumplingsGameActor j);

}
