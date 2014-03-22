package com.garrapeta.jumplings.wave;

public interface ICampaignWave {

    public void onChildWaveStarted();

    public void onChildWaveEnded();

    public boolean isInBetweenWaves();
}
