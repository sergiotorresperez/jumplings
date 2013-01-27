package net.garrapeta.jumplings.actor;

import com.badlogic.gdx.physics.box2d.Body;
import net.garrapeta.gameengine.utils.Pool;


import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.JumplingsWorld;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

public class JumplingsFactory {

    public final static String LOG_SRC = JumplingsApplication.LOG_SRC_JUMPLINGS + ".pool";

    private final JumplingsWorld mJumplingsWorld;

    private final Pool<DebrisActor> mDebrisActorPool;
    private final Pool<SparksActor> mSparksActor;

    // TODO: make the pools final
    private Pool<RoundEnemyActor> mRoundEnemyActorPool;
    private Pool<DoubleEnemyActor> mDoubleEnemyActorPool;
    private Pool<DoubleSonEnemyActor> mDoubleSonEnemyActorPool;
    private Pool<SplitterEnemyActor> mSplitterEnemyActorPool;

    public JumplingsFactory(JumplingsWorld jumplingsWorld) {
        mJumplingsWorld = jumplingsWorld;

        mDebrisActorPool = new  Pool<DebrisActor>() {
            @Override
            protected DebrisActor newObject() {
                return new DebrisActor(mJumplingsWorld);
            }
        };

        mSparksActor = new Pool<SparksActor>() {
            @Override
            protected SparksActor newObject() {
                return new SparksActor(mJumplingsWorld);
            }
        };

        // FIXME: fix this dynamic cast
        if (mJumplingsWorld instanceof JumplingsGameWorld) {
            final JumplingsGameWorld jumplingsGameWorld = (JumplingsGameWorld) mJumplingsWorld;
            mRoundEnemyActorPool = new Pool<RoundEnemyActor>() {
                @Override
                protected RoundEnemyActor newObject() {
                    return new RoundEnemyActor(jumplingsGameWorld);
                }
            };
            mDoubleEnemyActorPool = new Pool<DoubleEnemyActor>(){
                @Override
                protected DoubleEnemyActor newObject() {
                    return new DoubleEnemyActor(jumplingsGameWorld);
                }
            };
            mDoubleSonEnemyActorPool = new Pool<DoubleSonEnemyActor>(){
                @Override
                protected DoubleSonEnemyActor newObject() {
                    return new DoubleSonEnemyActor(jumplingsGameWorld);
                }
            };
            mSplitterEnemyActorPool = new Pool<SplitterEnemyActor>(){
                @Override
                protected SplitterEnemyActor newObject() {
                    return new SplitterEnemyActor(jumplingsGameWorld);
                }
            };
        }
    }

    // Get

    public DebrisActor getDebrisActor(Body body, Bitmap bitmap) {
        DebrisActor actor = mDebrisActorPool.obtain();
        Log.i(LOG_SRC, "DebrisActor: " +   mDebrisActorPool.getDebugString());
        actor.init(body, bitmap);
        return actor;
    }

    public SparksActor getSparksActor(PointF worldPos, int longevity) {
        SparksActor actor = mSparksActor.obtain();
        Log.i(LOG_SRC, "SparksActor: " + mSparksActor.getDebugString());
        actor.init(worldPos, longevity);
        return actor;
    }

    public RoundEnemyActor getRoundEnemyActor(PointF worldPos) {
        RoundEnemyActor actor = mRoundEnemyActorPool.obtain();
        Log.i(LOG_SRC, "RoundEnemyActor: " +  mRoundEnemyActorPool.getDebugString());
        actor.init(worldPos);
        return actor;
    }

    public DoubleEnemyActor getDoubleEnemyActor(PointF worldPos) {
        DoubleEnemyActor actor = mDoubleEnemyActorPool.obtain();
        Log.i(LOG_SRC, "DoubleEnemyActor: " + mDoubleEnemyActorPool.getDebugString());
        actor.init(worldPos);
        return actor;
    }
    
    public DoubleSonEnemyActor getDoubleSonEnemyActor(PointF worldPos) {
        DoubleSonEnemyActor actor = mDoubleSonEnemyActorPool.obtain();
        Log.i(LOG_SRC, "DoubleSonEnemyActor: " + mDoubleSonEnemyActorPool.getDebugString());
        actor.init(worldPos);
        return actor;
    }
    
    public SplitterEnemyActor getSplitterEnemyActor(PointF worldPos, int level) {
        SplitterEnemyActor actor = mSplitterEnemyActorPool.obtain();
        Log.i(LOG_SRC, "SplitterEnemyActor: " + mSplitterEnemyActorPool.getDebugString());
        actor.init(worldPos, level);
        return actor;
    }

    // Free

    public void free(DebrisActor actor) {
        mDebrisActorPool.free(actor);
    }

    public void free(SparksActor actor) {
        mSparksActor.free(actor);
    }

    public void free(RoundEnemyActor actor) {
        mRoundEnemyActorPool.free(actor);
    }

    public void free(DoubleEnemyActor actor) {
        mDoubleEnemyActorPool.free(actor);
    }

    public void free(DoubleSonEnemyActor actor) {
        mDoubleSonEnemyActorPool.free(actor);
    }

    public void free(SplitterEnemyActor actor) {
        mSplitterEnemyActorPool.free(actor);
    }
}
