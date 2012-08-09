package net.garrapeta.jumplings;

import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.GameView;
import net.garrapeta.jumplings.actor.WallActor;
import net.garrapeta.jumplings.wave.Wave;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.SensorManager;
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
    
    // ------------------------------------------------------------ Variables

    public JumplingsActivity jActivity;

    /** Wave actual */
    Wave wave;

    // centro de la pantalla
    float centerX;
    float centerY;

    // ----------------------------------------------------------- Constructor

    public JumplingsWorld(JumplingsActivity jActivity, GameView gameView) {
        super(jActivity, gameView);
        this.jActivity = jActivity;
    }

    // ----------------------------------------------------- M�todos de World

    public void create() {

        Log.i(LOG_SRC, "create " + this);

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
        
        setGravityY(-SensorManager.GRAVITY_EARTH);

    }

    @Override
    public synchronized boolean processFrame(float gameTimeStep) {
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
        if (!jActivity.isWorldStarted()) {
            jActivity.startWorld();
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
