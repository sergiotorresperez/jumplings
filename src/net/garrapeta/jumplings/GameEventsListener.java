package net.garrapeta.jumplings;

import net.garrapeta.jumplings.actor.BladePowerUpActor;
import net.garrapeta.jumplings.actor.BombActor;
import net.garrapeta.jumplings.actor.EnemyActor;
import net.garrapeta.jumplings.actor.LifePowerUpActor;

public interface GameEventsListener {

    /**
     * Método ejecutado cuando un enemigo escapa de pantalla
     * 
     * @return if the event is consumed
     */
    public boolean onEnemyScaped(EnemyActor eenemy) ;
    
    /**
     * Invocado al morir el jugador
     * 
     * @return if the event is consumed
     */
    public boolean onGameOver();
    
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
    
    /**
     * Called when the player gets a blade power up
     * 
     * @param bladePowerUpActor
     * @return if the event is consumed
     */
    public boolean onBladePowerUpStart(BladePowerUpActor bladePowerUpActor);
    
    /**
     * Called when the blade finishes
     * 
     * @return if the event is consumed
     */
    public boolean onBladePowerUpEnd();
}
