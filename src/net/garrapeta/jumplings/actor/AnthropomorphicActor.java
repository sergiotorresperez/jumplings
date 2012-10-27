package net.garrapeta.jumplings.actor;

import android.graphics.Canvas;

/**
 * Interface of those actors that are anthropomorphic
 * @author garrapeta
 */
public interface AnthropomorphicActor {

    
    /**
     * Inititialises the bitmap of an {@link AnthropomorphicActor}
     * @param bmpBodyId
     * @param bmpFootRightId
     * @param bmpFootLeftId
     * @param bmpHandRightId
     * @param bmpHandLeftId
     * @param bmpEyeRightId
     * @param bmpEyeLeftId
     */
    public abstract void initAnthropomorphicBitmaps(int bmpBodyId, int bmpFootRightId, int bmpFootLeftId, int bmpHandRightId, int bmpHandLeftId, int bmpEyeRightId,
            int bmpEyeLeftId);

    /**
     * Used to draw the shapes of an {@link AnthropomorphicActor} actor
     * @param canvas
     */
    public void drawAnthropomorphicShapes(Canvas canvas);
    
    /**
     * Used to draw the bitmaps of an {@link AnthropomorphicActor} actor
     * @param canvas
     */
    public abstract void drawAnthropomorphicBitmaps(Canvas canvas);

    /**
     *  Called when the parent of this {@link AnthropomorphicDelegate} has been bumped
     */
    public abstract void onBumped();

}