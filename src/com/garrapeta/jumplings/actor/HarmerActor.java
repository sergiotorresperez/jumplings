package com.garrapeta.jumplings.actor;

import android.graphics.Bitmap;

import com.garrapeta.gameengine.Actor;
import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.jumplings.JumplingsGameWorld;

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
        Object[] as = getWorld().mMainActors.toArray();

        int l = as.length;

        for (int i = 0; i < l; i++) {
            MainActor j = (MainActor) as[i];
            if (j.timestamp <= mTimestamp) {
                effectOver(j);
            }
        }
    }


    // ----------------------------------------------- M�todos propios
    protected abstract void effectOver(MainActor j);

}
