package net.garrapeta.jumplings;

import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.jumplings.ActorPool.PoolableActor;

import com.badlogic.gdx.utils.Pool;


public abstract class ActorPool<T extends PoolableActor, S extends GameWorld> extends Pool<T> {

    private final S mGameWorld;
 
    /**
     * {@link Pool#Pool()}
     */
    public ActorPool(S gameworld) {
        super();
        mGameWorld = gameworld;
    }

    /**
     * {@link Pool#Pool()
     * 
     * @param initialCapacity
     * @param max
     */
    public ActorPool(S gameworld, int initialCapacity, int max) {
        super(initialCapacity, max);
        mGameWorld = gameworld;
    }

    /**
     * {@link Pool#Pool()
     * 
     * @param initialCapacity
     */
    public ActorPool(S gameworld, int initialCapacity) {
        super(initialCapacity);
        mGameWorld = gameworld;
    }

    @Override
    protected final T newObject() {
        return newObject(mGameWorld);
    }
 
 
    /**
     * {@link Pool#newObject()}
     * 
     * @param gameWorld
     * @return
     */
    protected abstract T newObject(GameWorld gameWorld);

    /**
     * {@link  com.badlogic.gdx.utils.Pool.Poolable}
     */
    static public interface PoolableActor extends com.badlogic.gdx.utils.Pool.Poolable {
     }

}
