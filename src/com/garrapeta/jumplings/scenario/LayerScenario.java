package com.garrapeta.jumplings.scenario;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.jumplings.JumplingsWorld;

/**
 * Layer-based scenario
 * 
 * @author garrapeta
 * 
 */
public abstract class LayerScenario implements IScenario {

    private List<Layer> mLayers;

    JumplingsWorld mWorld;

    LayerScenario(JumplingsWorld world) {
        mWorld = world;
        mLayers = new ArrayList<Layer>();
    }

    @Override
    public void init() {
        initLayers(mWorld.getBitmapManager());
        reset();
    }

    @Override
    public void end() {
        dispose();
    }

    @Override
    public final void setProgress(float progress) {
        for (Layer layer : mLayers) {
            layer.setProgress(progress);
        }
    }

    @Override
    public final void onGameOver() {
        for (Layer layer : mLayers) {
            layer.onGameOver();
        }
    }

    @Override
    public final void processFrame(float gameTimeStep) {
        final int size = mLayers.size();
        for (int i = 0; i < size; i++) {
            mLayers.get(i)
                   .processFrame(gameTimeStep);
        }
    }

    @Override
    public final void draw(Canvas canvas, Paint paint) {
        final int size = mLayers.size();
        for (int i = 0; i < size; i++) {
            mLayers.get(i)
                   .draw(canvas, paint);
        }
    }

    abstract void initLayers(BitmapManager bm);

    void addLayer(Layer layer) {
        mLayers.add(layer);
    }

    final void reset() {
        for (Layer layer : mLayers) {
            layer.reset();
        }
    }

}