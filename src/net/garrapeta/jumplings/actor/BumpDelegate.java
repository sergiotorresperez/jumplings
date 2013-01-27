package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.Box2DActor;
import net.garrapeta.jumplings.JumplingsGameWorld;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

/**
 * Common implementation for all {@link IBumpable}, to let objects
 * implementing that interface delegate in this.
 * 
 * @author garrapeta
 * 
 */
public class BumpDelegate {

    private static final float BUMP_TIME = 250;
    
    private IBumpable mBumpable;
    
    private float mRemainingTime;
    private boolean mIsBumped = false;

    public BumpDelegate(IBumpable bumpable) {
        mBumpable = bumpable;
    }

    public void processFrame(float gameTimeStep) {
        if (mIsBumped) {
            mRemainingTime -= gameTimeStep;
            if (mRemainingTime <= 0) {
                mIsBumped = false;
                mBumpable.onBumpedChanged(false);
            }
        }
    }

    public void onBeginContact(boolean entered, Body thisBody, Box2DActor<?> other, Body otherBody, Contact contact) {
        if (mIsBumped || !entered) {
            return;
        }
        if (other instanceof WallActor) {
            WallActor wall = (WallActor) other;
            if (wall.floor || wall.security) {
                return;
            }
        }
        mIsBumped = true;
        mRemainingTime = BUMP_TIME;
        mBumpable.onBumpedChanged(true);
    }

    public void onBumped(boolean bumped, JumplingActor<?> actor, AnthropomorphicDelegate<?> anthrophoDelegate) {
        if (bumped) {
            actor.getWorld().getSoundManager().play(JumplingsGameWorld.SAMPLE_ENEMY_PAIN);
        }
        anthrophoDelegate.setEyesOpened(!bumped);
    }

}
