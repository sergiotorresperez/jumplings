package net.garrapeta.jumplings.scenario;

import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.jumplings.R;
import android.graphics.Bitmap;

/**
 * 
 * Nature scenario
 * 
 * @author GaRRaPeTa
 */
public class NatureScenario extends LayerScenario {

    private final static int LAYER1_ID = R.drawable.scenario_nature_1;
    private final static int LAYER2_ID = R.drawable.scenario_nature_2;
    private final static int LAYER3_ID = R.drawable.scenario_nature_3;
    private final static int LAYER4_ID = R.drawable.scenario_nature_4;

    // ----------------------------------------------- Constructor

    /**
     * @param dWorld
     */
    NatureScenario(JumplingsWorld world) {
        super(world);
    }

    @Override
    public void initLayers(BitmapManager bm) {
        int viewWidth = mWorld.mView.getWidth();
        int viewHeight = mWorld.mView.getHeight();

        // Initialisation of Layers
        {
            // TODO: avoid blocking game thread with this load
            Bitmap bmp = bm.loadBitmap(mWorld.mActivity.getResources(), LAYER1_ID);
            int maxHeight = (int) (viewHeight * 1.5);
            addLayer(new Layer(this, bmp, maxHeight, 0, 0, 2, 0, true, true, viewWidth, viewHeight));
        }
        {
            int maxHeight = (int) (viewHeight * 1.4);
            Bitmap bmp = bm.loadBitmap(mWorld.mActivity.getResources(), LAYER2_ID);
            float initYPos = maxHeight - bmp.getHeight();
            addLayer(new Layer(this, bmp, maxHeight, 0, initYPos, 0, 0, true, false, viewWidth, viewHeight));
        }
        {
            Bitmap bmp = bm.loadBitmap(mWorld.mActivity.getResources(), LAYER3_ID);
            int maxHeight = (int) (viewHeight * 2.7);
            addLayer(new Layer(this, bmp, maxHeight, 0, 0, 3, 0, true, false, viewWidth, viewHeight));
        }
        {
            int maxHeight = (int) (viewHeight * 2);
            Bitmap bmp = bm.loadBitmap(mWorld.mActivity.getResources(), LAYER4_ID);
            float initYPos = maxHeight - bmp.getHeight();
            addLayer(new Layer(this, bmp, maxHeight, 0, initYPos, 0, 0, true, false, viewWidth, viewHeight));
        }
    }

    @Override
    public void disposeLayers(BitmapManager bm) {
        // TODO: delegate this into the layer
        bm.releaseBitmap(LAYER1_ID);
        bm.releaseBitmap(LAYER2_ID);
        bm.releaseBitmap(LAYER3_ID);
        bm.releaseBitmap(LAYER4_ID);
    }

}
