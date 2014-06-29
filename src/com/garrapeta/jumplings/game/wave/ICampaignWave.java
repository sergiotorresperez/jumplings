package com.garrapeta.jumplings.game.wave;

public interface ICampaignWave {

    public void onChildWaveStarted();

    public void onChildWaveEnded();

    public boolean isInBetweenWaves();
}
