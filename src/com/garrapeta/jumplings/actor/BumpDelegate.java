package com.garrapeta.jumplings.actor;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.garrapeta.gameengine.Box2DActor;
import com.garrapeta.jumplings.JumplingsGameWorld;

/**
 * Common implementation for all {@link IBumpable}, to let objects implementing
 * that interface delegate in this.
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

    public void reset(AnthropomorphicDelegate<?> anthrophoDelegate) {
        mIsBumped = false;
        anthrophoDelegate.setEyesOpened(true);
    }

    public void processFrame(float gameTimeStep) {
        if (mIsBumped) {
            mRemainingTime -= gameTimeStep;
            if (mRemainingTime <= 0) {
                mBumpable.onBumpChange(false);
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
        mBumpable.onBumpChange(true);
    }

    public void onBumped(boolean bumped, JumplingActor<?> actor, AnthropomorphicDelegate<?> anthrophoDelegate) {
        mIsBumped = bumped;
        if (bumped) {
            mRemainingTime = BUMP_TIME;
            actor.getWorld()
                 .getSoundManager()
                 .play(JumplingsGameWorld.SAMPLE_ENEMY_PAIN);
        }
        anthrophoDelegate.setEyesOpened(!bumped);
    }

}
