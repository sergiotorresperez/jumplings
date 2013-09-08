package com.garrapeta.jumplings;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.gameengine.Box2DWorld;
import com.garrapeta.gameengine.GameView;
import com.garrapeta.gameengine.module.SoundModule;
import com.garrapeta.gameengine.utils.L;
import com.garrapeta.jumplings.actor.JumplingActor;
import com.garrapeta.jumplings.actor.JumplingsFactory;
import com.garrapeta.jumplings.actor.WallActor;
import com.garrapeta.jumplings.flurry.FlurryHelper;
import com.garrapeta.jumplings.ui.ErrorDialogFactory;

/**
 * Mundo del juego
 * 
 * @author GaRRaPeTa
 */
public class JumplingsWorld extends Box2DWorld {

    // -------------------------------------------------------- Constantes

    public static final String TAG = JumplingsApplication.TAG_JUMPLINGS + ".world";

    /**
     * World units of the world across the width of the screen. The height is calculated 
     * automatically depending on this value an the specs of the display. 
     */
    public static final int WORLD_WIDTH = 7;

    // ------------------------------------ Consantes de sonidos y vibraciones

    public static final short SAMPLE_ENEMY_PAIN = 0;

    /**
     * Tag used to refer to the error fragment
     */
	private static final String ERROR_FRAGMENT_TAG = "error_fragment_tag";

    // ------------------------------------------------------------ Variables

    public FragmentActivity mActivity;

    /** Wave actual */
    Wave<?> mWave;

    // centro de la pantalla
    float centerX;
    float centerY;

    private JumplingsFactory mFactory;

    public ArrayList<JumplingActor<?>> mJumplingActors = new ArrayList<JumplingActor<?>>();
    
    public final boolean mWireframeMode;
    public final boolean mDrawActorBitmaps;

    // ----------------------------------------------------------- Constructor

    public JumplingsWorld(FragmentActivity activity, GameView gameView, Context context) {
        super(gameView, context, (short) (PermData.isSoundEnabled(context) ? PermData.CFG_LEVEL_ALL : PermData.CFG_LEVEL_NONE) , PermData.getVibratorLevel(context));
        mActivity = activity;
        mFactory = new JumplingsFactory(this);
        mDrawActorBitmaps = PermData.paintActorBitmaps(mActivity);
        mWireframeMode = PermData.isWireframeMode(mActivity);
    }

    // ----------------------------------------------------- M�todos de World

    @Override
    protected void onBeforeRunning() {
        if (L.sEnabled) Log.i(TAG, "onBeforeRunning " + this);

        // Paredes
        // -----------------------------------------------------------------

        RectF vb = mViewport.getWorldBoundaries();
        float left = vb.left;
        float bottom = vb.bottom;
        float right = vb.right;
        float top = vb.top;

        // pared superior
        addActor(new WallActor(this, new PointF(0, 0), new PointF(left, top), new PointF(right, top), false, false).setInitted());

        // pared inferior - FLOOR
        addActor(new WallActor(this, new PointF(0, 0), new PointF(left, bottom), new PointF(right, bottom), true, false).setInitted());

        // pared izquierda
        addActor(new WallActor(this, new PointF(0, 0), new PointF(left, bottom), new PointF(left, top), false, false).setInitted());

        // pared derecha
        addActor(new WallActor(this, new PointF(0, 0), new PointF(right, bottom), new PointF(right, top), false, false).setInitted());

        // Paredes de seguridad
        // ---------------------------------------------------------------

        // tiene que ser negativo
        float securityMargin = -Wave.ENEMY_OFFSET * 3;

        float securityLeft = left + securityMargin;
        float securityBottom = bottom + securityMargin;
        float securityRight = right - securityMargin;
        float securityTop = top - securityMargin;

        // pared de seguridad superior
        addActor(new WallActor(this, new PointF(0, 0), new PointF(securityLeft, securityTop), new PointF(securityRight, securityTop), false, true).setInitted());

        // pared de seguridad inferior
        addActor(new WallActor(this, new PointF(0, 0), new PointF(securityLeft, securityBottom), new PointF(securityRight, securityBottom), false, true).setInitted());

        // pared de seguridad izquierda
        addActor(new WallActor(this, new PointF(0, 0), new PointF(securityLeft, securityBottom), new PointF(securityLeft, securityTop), false, true).setInitted());

        // pared de seguridad derecha
        addActor(new WallActor(this, new PointF(0, 0), new PointF(securityRight, securityBottom), new PointF(securityRight, securityTop), false, true).setInitted());

        // setGravityY(-SensorManager.GRAVITY_EARTH);
        setGravityY(-7);
    }

    @Override
    protected void loadResources() {
        loadCommonResources();
        Resources resources = mActivity.getResources();
        // Preparaci�n samples bitmaps
        BitmapManager bm = getBitmapManager();
        bm.loadBitmap(resources, R.drawable.intro_body);
        bm.loadBitmap(resources, R.drawable.intro_foot_right);
        bm.loadBitmap(resources, R.drawable.intro_foot_left);
        bm.loadBitmap(resources, R.drawable.intro_hand_right);
        bm.loadBitmap(resources, R.drawable.intro_hand_left);
        bm.loadBitmap(resources, R.drawable.intro_eye_right_opened);
        bm.loadBitmap(resources, R.drawable.intro_eye_left_opened);
        bm.loadBitmap(resources, R.drawable.intro_eye_right_closed);
        bm.loadBitmap(resources, R.drawable.intro_eye_left_closed);
    }

    protected void loadCommonResources() {
        // Preparación samples sonido
		SoundModule sm = getSoundManager();
		sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_ENEMY_PAIN)
				.add(R.raw.pain01)
				.add(R.raw.pain02)
				.add(R.raw.pain03)
				.add(R.raw.pain04)
				.add(R.raw.pain05)
				.add(R.raw.pain06)
				.add(R.raw.pain07)
				.add(R.raw.pain08)
				.add(R.raw.pain09)
				.add(R.raw.pain10)
				.add(R.raw.pain11)
				.add(R.raw.pain12);

        
    }

    @Override
    public boolean processFrame(float gameTimeStep) {
        // La generaci�n de enemigos, regeneraci�n de vida, comprobaci�n de
        // satisfacci�n
        // de condiciones de victoria derrota, etc, se delega en el wave-
        mWave.processFrame(gameTimeStep);

        return false;
    }

    @Override
    protected void drawBackground(Canvas canvas) {
    	if (!mWireframeMode) {
    		drawScenario(canvas);
    	} else {
    		super.drawBackground(canvas);
    	}
    }

    protected void drawScenario(Canvas canvas) {
    	canvas.drawColor(Color.WHITE);
    }
   
    @Override
    public void onGameViewSizeChanged(int width, int height) {
        if (L.sEnabled) Log.i(TAG, "surfaceChanged " + this);
        mViewport.setWorldSizeGivenWorldUnitsPerInchX(WORLD_WIDTH);
    }

    @Override
    public void onGameWorldSizeChanged(RectF worldBoundaries) {
    	// FIXME: if this comes from a screen orientation change to portrait the size of the world will be wrong
        if (!isRunning()) {
            if (L.sEnabled) Log.i(JumplingsApplication.TAG, "onGameWorldSizeChanged: " + worldBoundaries.width() + " x " + worldBoundaries.height() + ". Staring Game" + this);

            // Se arranca el game loop
            start();
            // Se activa la wave
            mWave.start();
        }
    }

    public void onJumplingActorAdded(JumplingActor<?> actor) {
        mJumplingActors.add(actor);
    }

    public void onJumplingActorRemoved(JumplingActor<?> actor) {
        mJumplingActors.remove(actor);
    }
    

    @Override
    protected void dispose() {
        super.dispose();
        mActivity = null;
        mWave.dispose();
        mWave = null;
        mFactory.clear();
        mFactory = null;
        mJumplingActors.clear();
        mJumplingActors = null;
    }
    
    @Override
    public void onError(Throwable error) {
    	if (L.sEnabled) Log.e(TAG, "Game error!", error);
 		FlurryHelper.onGameEngineError(error);

		// show error dialog
    	mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
	            DialogFragment dialog = ErrorDialogFactory.create();
	            dialog.show(mActivity.getSupportFragmentManager(), ERROR_FRAGMENT_TAG);
			}
		});
    }

    // -------------------------------------------------------- M�todos propios

    public void drawBitmap(Canvas canvas, Body body, Bitmap bitmap) {
        drawBitmap(canvas, body, bitmap, null);
    }

    /**
     * Dibuja el bitmap centrado en el body
     * 
     * @param canvas
     * @param body
     * @param bitmap
     */
    public final void drawBitmap(Canvas canvas, Body body, Bitmap bitmap, Paint paint) {
        Vector2 worldPos = body.getWorldCenter();
        float screenPositionX = mViewport.worldToScreenX(worldPos.x);
        float screenPositionY = mViewport.worldToScreenY(worldPos.y);
        float angle = -(float) Math.toDegrees(body.getAngle());
    	drawBitmap(canvas, screenPositionX, screenPositionY, angle, bitmap, paint);        
    }
    
    /**
     * Dibuja el bitmap en las coordenadas proporcionadas
     * 
     * @param canvas
     * @param x, in pixels, in screen units
     * @param y, in pixels, in screen units
     * @param angle in degrees, in screen units
     * @param bitmap
     * @param paint
     */
    public final void drawBitmap(Canvas canvas, float x, float y , float angle, Bitmap bitmap, Paint paint) {
        canvas.save();

        canvas.translate(x, y);

        canvas.rotate(angle);

        // TODO: �No se puede especificar el punto de anclaje de otra manera?
        canvas.translate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);

        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();
    }

    /**
     * @return the factory to create Jumplings
     */
    public JumplingsFactory getFactory() {
        return mFactory;
    }
}
