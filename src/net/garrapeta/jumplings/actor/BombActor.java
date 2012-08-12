package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.jumplings.R;
import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.sound.SoundManager;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameActivity;
import net.garrapeta.jumplings.JumplingsGameWorld;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.media.MediaPlayer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public class BombActor extends MainActor {

	// ----------------------------------------------------------- Constantes
	
	public final static short JUMPER_CODE_BOMB = 6;
	
	public  final static float  DEFAULT_RADIUS = BASE_RADIUS * 1.25f;
	
	private final static int SPARKS_LAPSE = 250;
	
	private final static int SPARKS_PER_LAPSE = 2;
	
	/** Tiempo que permanecen las chispas en pantalla, en ms. Estela de la mecha*/
	private final static int SPARKLE_LONGEVITY_FUSE			= 1500;
	
	/** Tiempo que permanecen las chispas, en ms. Al explotar las bombas */
	private final static int SPARKLE_LONGEVITY_EXPLOSION	= 4000;
	
	/** N�mero de chispas que salen al explotar la bomba */
	private final static int SPARKS_AT_EXPLOSION = 12;
	
	/** Fuerza de las chispas al explotar la bomba */
	private final static int EXPLOSION_SPARKLE_FORCE = 1000;
	
	/** Fuerza de las onda expansiva */
	private float BLAST_FORCE    = 80;
	
	/** Radio de las onda expansiva */
	private float BLAST_RADIUS    = 6;
	
	// ------------------------------------------------- Variables est�ticas
	
	// Vivo
	protected final static Bitmap BMP_BOMB_BODY;
	protected final static Bitmap BMP_BOMB_FUSE;
	
	//Debris
	protected final static Bitmap BMP_DEBRIS_BOMB_BODY;
	protected final static Bitmap BMP_DEBRIS_BOMB_FUSE;
	
	// ----------------------------------------------- Variables de instancia
	public Body fuseBody;
	
	private long lastSparkle;
	
	private MediaPlayer fusePlayer;
	
	// ---------------------------------------------------- M�todos est�ticos
	
	static double getBombHitCount() {
		return 0f;
	}
	
	// --------------------------------------------------- Constructor
	
	public BombActor(JumplingsGameWorld jgWorld, PointF worldPos) {
		super(jgWorld, worldPos, Z_INDEX);
		this.code = BombActor.JUMPER_CODE_BOMB;
		radius = BombActor.DEFAULT_RADIUS;
		initPhysics(worldPos);
	}

	// ----------------------------------------------- Inicializaci�n est�tica
	
	static {
		
		Resources r = JumplingsApplication.getInstance().getResources();

		// vivo
		BMP_BOMB_BODY			= BitmapFactory.decodeResource(r, R.drawable.bomb_body);
		BMP_BOMB_FUSE			= BitmapFactory.decodeResource(r, R.drawable.bomb_fuse);
		
		// debris
		BMP_DEBRIS_BOMB_BODY	= BitmapFactory.decodeResource(r, R.drawable.bomb_debris_body);
		BMP_DEBRIS_BOMB_FUSE	= BitmapFactory.decodeResource(r, R.drawable.bomb_debris_fuse);
	}
	// --------------------------------------------- M�todos heredados
	
	@Override
	protected void initBodies(PointF worldPos) {
		
		// Cuerpo
		{
			// Create Shape with Properties
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(radius);
			mainBody = jgWorld.createBody(this, worldPos, true);
			mainBody.setBullet(true);
			
			// Assign shape to Body
			Fixture f = mainBody.createFixture(circleShape, 1.0f);
			f.setFilterData(CONTACT_FILTER);
			circleShape.dispose();
			
		}
		
		// Mecha
		{
			float w = radius / 3;
			float h = radius / 2;
			// Create Shape with Properties
			PolygonShape polygonShape = new PolygonShape();
			polygonShape.setAsBox(w, h);
			PointF pos = new PointF(worldPos.x, worldPos.y + radius + h);
			fuseBody = jgWorld.createBody(this, pos, true);
			fuseBody.setBullet(false);
			
			// Assign shape to Body
			Fixture f = fuseBody.createFixture(polygonShape, 1.0f);
			f.setRestitution(AUX_BODIES_RESTITUTION);
			f.setFilterData(NO_CONTACT_FILTER);
			polygonShape.dispose();
			
			// Uni�n
			WeldJointDef jointDef = new WeldJointDef();

			
			jointDef.initialize(mainBody, 
								fuseBody,					            
								Viewport.pointFToVector2(pos));
			
			jgWorld.createJoint(this, jointDef);
		}
	}
	
	
	@Override
	protected ArrayList<JumplingActor> getDebrisBodies() {
		ArrayList<JumplingActor> debrisActors =  new ArrayList<JumplingActor>();
		
		// Main Body
		{
			Body body = mainBody;
			DebrisActor debrisActor = new DebrisActor(jgWorld,  body, BMP_DEBRIS_BOMB_BODY); 
						
			mGameWorld.addActor(debrisActor);
			debrisActors.add(debrisActor);
		}
		
		// Fuse
		{
			Body body = fuseBody;
			DebrisActor debrisActor = new DebrisActor(jgWorld,  body, BMP_DEBRIS_BOMB_FUSE); 
			
			mGameWorld.addActor(debrisActor);
			debrisActors.add(debrisActor);
		}
		
		return debrisActors;
	}

	@Override
	public void processFrame(float gameTimeStep) {
		long now = System.currentTimeMillis();
		if (now - lastSparkle >= SPARKS_LAPSE) {
			int sparkles = (int) (Math.random() * SPARKS_PER_LAPSE);
			
			for (int i = 0; i < sparkles; i++) {
				PointF aux = Viewport.vector2ToPointF(fuseBody.getWorldCenter());
				PointF pos = new PointF(aux.x, aux.y);
				SparksActor sparkle = new SparksActor(jgWorld, pos, SPARKLE_LONGEVITY_FUSE);
				jgWorld.addActor(sparkle);
				lastSparkle = now;
			}
		}
	}
	
	
	@Override
	protected void drawBitmaps(Canvas canvas) {
		jgWorld.drawBitmap(canvas, this.mainBody, 		BMP_BOMB_BODY);
		jgWorld.drawBitmap(canvas, this.fuseBody, 		BMP_BOMB_FUSE);
	}
	
	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
		jgWorld.bombCount++;
		if (jgWorld.jgActivity.soundOn) {
			SoundManager.getInstance().play(JumplingsGameActivity.SAMPLE_BOMB_LAUNCH);
			fusePlayer = SoundManager.getInstance().play(JumplingsGameActivity.SAMPLE_FUSE, true, false);
		}
	}
	
	@Override
	public void onRemovedFromWorld() {
		super.onRemovedFromWorld();
		jgWorld.bombCount--;
		if (jgWorld.bombCount == 0 && jgWorld.jgActivity.soundOn) {
			SoundManager.getInstance().stop(fusePlayer);
		}
	}
	
	// ------------------------------------------------ M�todos propios

	@Override
	public void onHitted() {
		jgWorld.onBombExploded(this);
		
		// sonido
		if (jgWorld.jgActivity.soundOn) {
			SoundManager.getInstance().play(JumplingsGameActivity.SAMPLE_BOMB_BOOM);
		}

		// Se genera una onda expansiva sobre los enemigos
		Object[] as = jgWorld.jumplingActors.toArray();
		
		int l = as.length;
		
		for (int i = 0; i < l; i++) {
			JumplingActor a = (JumplingActor)as[i];
			jgWorld.applyBlast(mainBody.getWorldCenter(), a.mainBody, BLAST_RADIUS, BLAST_FORCE);		
		}

		// Se crean chispas de la explosi�n 		
		Vector2 aux = mainBody.getWorldCenter();
		ArrayList<JumplingActor> sparkles = new ArrayList<JumplingActor>();
		for (int i = 0; i < SPARKS_AT_EXPLOSION; i++) {
			SparksActor sparkle = new SparksActor(jgWorld, new PointF(aux.x, aux.y), SPARKLE_LONGEVITY_EXPLOSION);
			jgWorld.addActor(sparkle);
			sparkles.add(sparkle);
		}

		// se aceleran para que salgan disparadas
		applyBlast(sparkles, EXPLOSION_SPARKLE_FORCE);
		
		
		super.onHitted();
	}

}
