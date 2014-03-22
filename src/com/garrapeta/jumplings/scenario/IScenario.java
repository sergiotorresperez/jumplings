package com.garrapeta.jumplings.scenario;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Scenario of the game
 * 
 * @author garrapeta
 */
public interface IScenario {

    /**
     * Inits the scenario
     */
    public void init();

    /**
     * Executed when the scenario ends
     */
    public void end();

    /**
     * Sets the progress
     * 
     * @param progress
     */
    public void setProgress(float progress);

    /**
     * Executed when game is over
     */
    public void onGameOver();

    /**
     * Executed each frame
     * 
     * @param gameTimeStep
     */
    public void processFrame(float gameTimeStep);

    /**
     * Draws the scenario
     * 
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint);

    /**
     * Frees resources
     */
    public void dispose();

}