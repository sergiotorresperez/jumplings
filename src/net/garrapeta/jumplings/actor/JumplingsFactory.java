package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.actor.DefaultActorPool;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.JumplingsWorld;
import android.util.Log;

public class JumplingsFactory {

    public final static String LOG_SRC = JumplingsApplication.LOG_SRC_JUMPLINGS + ".pool";

    private final  JumplingsWorld mJumplingsWorld;
    

    private DefaultActorPool<DebrisActor, JumplingsWorld> mDebrisActorPool;
    private DefaultActorPool<RoundEnemyActor, JumplingsGameWorld> mRoundEnemyActorPool ;

    public JumplingsFactory(JumplingsWorld jumplingsWorld) {
        mJumplingsWorld = jumplingsWorld;

        mDebrisActorPool = new DefaultActorPool<DebrisActor, JumplingsWorld>(DebrisActor.class, JumplingsWorld.class, mJumplingsWorld);

        // FIXME: fix this dynamic cast
        if (mJumplingsWorld instanceof JumplingsGameWorld) {
            JumplingsGameWorld jumplingsGameWorld = (JumplingsGameWorld) mJumplingsWorld;
            mRoundEnemyActorPool = new DefaultActorPool<RoundEnemyActor, JumplingsGameWorld>(RoundEnemyActor.class, JumplingsGameWorld.class, jumplingsGameWorld);
        }
    }

    public DebrisActor getDebrisActor() {
        Log.i(LOG_SRC, "getDebrisActor:  " + DebrisActor.sCount + " allocations.");
        return mDebrisActorPool.obtain();
    }

    public RoundEnemyActor getRoundEnemyActor() {
        Log.i(LOG_SRC, "getRoundEnemyActor:  " + RoundEnemyActor.sCount + " allocations.");
        return mRoundEnemyActorPool.obtain();
    }

    public void free(JumplingActor jumplingActor) {
        // FIXME: try to do this without instanceof
        if (jumplingActor instanceof RoundEnemyActor) {
            Log.d(LOG_SRC, "freeing:  " + jumplingActor);
            mRoundEnemyActorPool.free((RoundEnemyActor)jumplingActor);
        } else  if (jumplingActor instanceof DebrisActor) {
            Log.d(LOG_SRC, "freeing:  " + jumplingActor);
            mDebrisActorPool.free((DebrisActor)jumplingActor);
        }
    }
}
