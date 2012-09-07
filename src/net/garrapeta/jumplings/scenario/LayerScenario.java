package net.garrapeta.jumplings.scenario;

import java.util.ArrayList;
import java.util.List;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsWorld;
import android.graphics.Canvas;
import android.graphics.Paint;

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
        disposeLayers(mWorld.getBitmapManager());
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
        for (Layer layer : mLayers) {
            layer.processFrame(gameTimeStep);
        }
    }

    @Override
    public final void draw(Canvas canvas, Paint paint) {

        for (Layer layer : mLayers) {
            layer.draw(canvas, paint);
        }
    }

    abstract void initLayers(BitmapManager bm);

    abstract void disposeLayers(BitmapManager bm);

    void addLayer(Layer layer) {
        mLayers.add(layer);
    }

    final void reset() {
        for (Layer layer : mLayers) {
            layer.reset();
        }
    }

}