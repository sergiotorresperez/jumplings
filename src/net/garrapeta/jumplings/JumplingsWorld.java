package net.garrapeta.jumplings;
import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.gameengine.module.SoundManager;
import net.garrapeta.jumplings.actor.WallActor;
import net.garrapeta.jumplings.wave.Wave;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Mundo del juego
 * 
 * @author GaRRaPeTa
 */
public class JumplingsWorld extends Box2DWorld {

    // -------------------------------------------------------- Constantes

    public static final String LOG_SRC = JumplingsApplication.LOG_SRC_JUMPLINGS + ".world";
    
    public static final int WORLD_HEIGHT = 14;
    
    // ------------------------------------ Consantes de sonidos y vibraciones

    public static final int SAMPLE_ENEMY_PAIN = 0;
    
    // ------------------------------------------------------------ Variables


    /** Wave actual */
    Wave wave;
    


    // centro de la pantalla
    float centerX;
    float centerY;

    // ----------------------------------------------------------- Constructor

    public JumplingsWorld(Activity activity, GameView gameView) {
        super(activity, gameView);
    }

    // ----------------------------------------------------- M�todos de World

    @Override
    protected void onBeforeRunning() {
        Log.i(LOG_SRC, "onBeforeRunning " + this);
        // Paredes
        // -----------------------------------------------------------------

        RectF vb = viewport.getWorldBoundaries();
        float left = vb.left;
        float bottom = vb.bottom;
        float right = vb.right;
        float top = vb.top;

        // pared superior
        addActor(new WallActor(this, new PointF(0, 0), new PointF(left, top),
                new PointF(right, top), false, false));

        // pared inferior - FLOOR
        addActor(new WallActor(this, new PointF(0, 0), new PointF(left, bottom),
                new PointF(right, bottom), true, false));

        // pared izquierda
        addActor(new WallActor(this, new PointF(0, 0), new PointF(left, bottom),
                new PointF(left, top), false, false));

        // pared derecha
        addActor(new WallActor(this, new PointF(0, 0), new PointF(right, bottom),
                new PointF(right, top), false, false));

        // Paredes de seguridad
        // ---------------------------------------------------------------

        // tiene que ser negativo
        float securityMargin = -Wave.ENEMY_OFFSET * 3;

        float securityLeft = left + securityMargin;
        float securityBottom = bottom + securityMargin;
        float securityRight = right - securityMargin;
        float securityTop = top - securityMargin;

        // pared de seguridad superior
        addActor(new WallActor(this, new PointF(0, 0), new PointF(securityLeft, securityTop), new PointF(securityRight,
                securityTop), false, true));

        // pared de seguridad inferior
        addActor(new WallActor(this, new PointF(0, 0), new PointF(securityLeft, securityBottom), new PointF(
                securityRight, securityBottom), false, true));

        // pared de seguridad izquierda
        addActor(new WallActor(this, new PointF(0, 0), new PointF(securityLeft, securityBottom), new PointF(
                securityLeft, securityTop), false, true));

        // pared de seguridad derecha
        addActor(new WallActor(this, new PointF(0, 0), new PointF(securityRight, securityBottom), new PointF(
                securityRight, securityTop), false, true));
        
//        setGravityY(-SensorManager.GRAVITY_EARTH);
        setGravityY(-7);
    }

    @Override
    protected void loadResources() {
        loadCommonResources();

        // Preparaci�n samples bitmaps
        BitmapManager bm = getBitmapManager();
        bm.loadBitmap(R.drawable.intro_body);
        bm.loadBitmap(R.drawable.intro_foot_right);
        bm.loadBitmap(R.drawable.intro_foot_left);
        bm.loadBitmap(R.drawable.intro_hand_right);
        bm.loadBitmap(R.drawable.intro_hand_left);
        bm.loadBitmap(R.drawable.intro_eye_right_opened);
        bm.loadBitmap(R.drawable.intro_eye_left_opened);
        bm.loadBitmap(R.drawable.intro_eye_right_closed);
        bm.loadBitmap(R.drawable.intro_eye_left_closed);
    }
    
    protected void loadCommonResources() {
        // Preparación samples sonido
        SoundManager sm = getSoundManager();
        if (sm.isSoundEnabled()) {
            sm.add(R.raw.pain01, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain02, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain03, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain04, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain05, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain06, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain07, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain08, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain09, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain11, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
            sm.add(R.raw.pain12, JumplingsGameWorld.SAMPLE_ENEMY_PAIN, mActivity);
        }
    }

    @Override
    public boolean processFrame(float gameTimeStep) {
        // La generaci�n de enemigos, regeneraci�n de vida, comprobaci�n de
        // satisfacci�n
        // de condiciones de victoria derrota, etc, se delega en el wave-
        wave.processFrame(gameTimeStep);

        return false;
    }

    @Override
    protected void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
    }

    @Override
    public void onGameViewSizeChanged(int width, int height) {
        Log.i(LOG_SRC, "surfaceChanged " + this);
        this.viewport.setWorldHeight(WORLD_HEIGHT);
    }

    @Override
    public void onGameWorldSizeChanged() {
        if (!isStarted()) {
            Log.i(JumplingsApplication.LOG_SRC,"startNewGame " + this);


            // Se arranca el game loop
            start();
            // Se activa la wave
            wave.start();
        }
    }

    // -------------------------------------------------------- M�todos propios

    public void onWaveStarted() {
        Log.i(LOG_SRC, "Wave started");
    }

    public void onWaveCompleted() {
        Log.i(LOG_SRC, "Wave completed");
    }

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

        PointF screenPos = viewport.worldToScreen(worldPos.x, worldPos.y);
        canvas.save();

        canvas.translate(screenPos.x, screenPos.y);

        canvas.rotate(-(float) Math.toDegrees(body.getAngle()));

        // TODO: �No se puede especificar el punto de anclaje de otra manera?
        canvas.translate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);

        Matrix m = new Matrix();
        canvas.drawBitmap(bitmap, m, paint);
        canvas.restore();
    }

}
