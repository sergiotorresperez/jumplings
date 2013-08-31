package com.garrapeta.jumplings;

import com.garrapeta.jumplings.actor.BombActor;
import com.garrapeta.jumplings.actor.EnemyActor;
import com.garrapeta.jumplings.actor.LifePowerUpActor;

public interface GameEventsListener {

    /**
     * Método ejecutado cuando un enemigo escapa de pantalla
     * 
     * @return if the event is consumed
     */
    public boolean onEnemyScaped(EnemyActor eenemy) ;
    
    /**
     * Called when the player makes a Combo
     * 
     * @return if the event is consumed
     */
    public boolean onCombo();
    
    /**
     * Called when the player has killed an enemy.
     * 
     * @param enemy
     * @return if the event is consumed
     */
    public boolean onEnemyKilled(EnemyActor enemy);
    
    /**
     * Called when the player explodes a bomb
     * 
     * @param bomb
     * @return if the event is consumed
     */
    public boolean onBombExploded(BombActor bomb);
    
    /**
     * Called when the player gets a life up power up
     * 
     * @param lifePowerUpActor
     * @return if the event is consumed
     */
    public boolean onLifePowerUp(LifePowerUpActor lifePowerUpActor);

}
