package net.garrapeta.jumplings.actor;

import net.garrapeta.MathUtils;
import net.garrapeta.gameengine.Box2DActor;
import net.garrapeta.gameengine.GameMessage;
import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.utils.PhysicsUtils;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Clase base de los actores f�sicos 
 * @author GaRRaPeTa
 */
public abstract class JumplingActor extends Box2DActor {

	// ----------------------------------------- Constantes

	/**
	 *  Radio base de los actores, en unidades del mundo.
	 */
	public final static float  BASE_RADIUS		= 0.6f;
	
	private final static float OVERALL_ACTOR_RESTITUTION   = 0.7f;
	
	protected final static float AUX_BODIES_RESTITUTION   = OVERALL_ACTOR_RESTITUTION;

	private final static float BOUNCING_VEL = -20;
	
	public final static int    CONTACT_FILTER_BIT    = 0x00001;
	
	public final static Filter NO_CONTACT_FILTER;
	
	public final static Filter CONTACT_FILTER;
	
	// ----------------------------------------- Variables de instancia
	

	JumplingsWorld jWorld;
	
	public Body mainBody;

	/**
	 *  Radio de la circunferencia circunscrita en el cuerpo del enemigo, en unidades del mundo.
	 */
	protected float radius;

	
	/**
	 *  Si el enemigo ha llegado a entrar dentro de los bounds del juego. 
	 *  Si el enemigo nace fuera del mundo visible se considera que no
	 *  se ha introducido del todo.
	 */
	protected boolean entered;


	// --------------------------------------------------- Inicializaci�n est�tica
	
	static  {
		
		NO_CONTACT_FILTER = new Filter();
		NO_CONTACT_FILTER.categoryBits 	= 0;
		NO_CONTACT_FILTER.maskBits 		= 0;
		
		CONTACT_FILTER = new Filter();
		
		CONTACT_FILTER.categoryBits = CONTACT_FILTER_BIT;
		
		CONTACT_FILTER.maskBits     = WallActor.WALL_FILTER_BIT   | 
                					  WallActor.FLOOR_FILTER_BIT  |
                					  CONTACT_FILTER_BIT;

	}
	
	// --------------------------------------------------- Constructor
	
	/**
	 * @param cWorld
	 * @param zIndex
	 */
	public JumplingActor(JumplingsWorld jWorld, int zIndex) {
		super(jWorld, zIndex);
		this.jWorld = jWorld;
	}
	
	
	// ----------------------------------------- M�todos de Box2DActor
	
	@Override
	public void onAddedToWorld() {
		this.entered = isInsideWorld();
	}
	
	
	
	@Override
	public final void onPreSolveContact(Body bodyA, Box2DActor actorB, Body bodyB, Contact contact, Manifold oldManifold) {

		if (actorB instanceof WallActor) {
			WallActor wall = (WallActor) actorB;
			if (!entered) {
				// est� entrando se le deja pasar
				contact.setEnabled(false);
			} else if (wall.floor) {
				// se va a escapar!
				float v = bodyA.getLinearVelocity().y;
				// si va muy r�pido hacia abajo se perdona al jugador y se deja que rebote
				if (v > BOUNCING_VEL) {
					// se deja que se escape
					contact.setEnabled(false);
				}
			}
			
		}
		
	}

	@Override
	public final void onBeginContact(Body thisBody, Box2DActor other, Body otherBody, Contact contact) {
		if (other instanceof WallActor) {
			WallActor wall = (WallActor) other;
			if (wall.security) {
				jWorld.removeActor(this);
			}
		}
	}
	
	@Override
	public final void onEndContact(Body bodyA, Box2DActor actorB, Body bodyB,	Contact contact) {
		
		if (actorB instanceof WallActor) {
			
			WallActor wall = (WallActor) actorB;
			if (!entered) {
				// si ha acabado de entrar en pantalla se pone vulnerable
				entered = true;
				
			} else if (entered && wall.floor) {
				if (!isInsideWorld()) {
					// finalmente escapa
					onScapedFromBounds();
				}
			}
		}
		
	}
	
	
	@Override
	public final void draw(Canvas canvas) {
		if (JumplingsApplication.DRAW_ACTOR_SHAPES) {
			super.drawBodiesShapes(canvas);
		}
		if (JumplingsApplication.DRAW_ACTOR_BITMAPS) {
			drawBitmaps(canvas);
		}
	}
	

	// ------------------------------------------------ M�todos propios
	
	protected abstract void drawBitmaps(Canvas canvas);


	/**
	 *  Inicializa todo lo relativo a la f�sica
	 */
	protected final void initPhysics(PointF worldPos) {
		initBodies(worldPos);
		
		// Para que el actor se comporte con una restituci�n global, se pone 
		// al cuerpo principal una restituci�n apropiada
		//TODO: ¿que era esto??
		double ratio = getMainMassRatio();
		float mainBodyRestitution = (float) (OVERALL_ACTOR_RESTITUTION * (1 / ratio));
		mainBody.getFixtureList().get(0).setRestitution(mainBodyRestitution);
	}
	
	/**
	 * Crea los cuerpos y los elementos f�sicos
	 * @param worldPos
	 */
	protected abstract void initBodies(PointF worldPos);
	
	/**
	 * @return
	 */
	protected boolean isInsideWorld() {
		PointF worldPos = this.getWorldPos();
		RectF wr = jWorld.viewport.getWorldBoundaries();
		
		return MathUtils.isCircunferenceInRectangle(worldPos.x, worldPos.y, radius,
				                                    wr.left,  wr.bottom,
				                                    wr.right, wr.top);

	}
	
	protected void onScapedFromBounds() {
		jWorld.removeActor(this);
	}
	
	/**
	 * @return
	 */
	protected Body[] getMainBodies() {
		return new Body[] {mainBody};
	}	
	
	/**
	 * @return
	 */
	public double getMainMassRatio() {
		double mainBodiesMass = 0;
		
		Body[] mainBodies = getMainBodies();
		int l = mainBodies.length;
		for (int i = 0; i < l; i++) {
			mainBodiesMass += mainBodies[i].getMass();
		}
		
		return mainBodiesMass / this.getMass();
	}
	
	
	/**
	 * @return
	 */
	public final PointF getWorldPos() {
		float x = 0;
		float y = 0;
		
		Body[] mainBodies = getMainBodies();
		int l = mainBodies.length;
		for (int i = 0; i < l; i++) {
			Vector2 wc = mainBodies[i].getWorldCenter();
			x += wc.x;
			y += wc.y;
		}
		
		return new PointF(x / l, y / l);
	}

	/**
	 * @param vx
	 * @param vy
	 */
	public final void setLinearVelocity(float vx, float vy) {
		double ratio = getMainMassRatio();
		
		Vector2 aux = new Vector2((float) (vx * (1 / ratio)), 
				                  (float) (vy * (1 / ratio)));
		
		Body[] mainBodies = getMainBodies();
		int l = mainBodies.length;
		for (int i = 0; i < l; i++) {
			mainBodies[i].setLinearVelocity(aux);
		}
	}
	

	/**
	 * @return
	 */
	public final PointF getLinearVelocity() {
		float vx = 0;
		float vy = 0;
		
		Body[] mainBodies = getMainBodies();
		int l = mainBodies.length;
		for (int i = 0; i < l; i++) {
			Vector2 v = mainBodies[i].getLinearVelocity();
			vx += v.x;
			vy += v.y;
		}
		
		return new PointF(vx / l, vy / l);
	}
	
	/**
	 * @param maxHeight
	 * @return
	 */
	public final double getInitialYVelocity(double maxHeight) {
		double distance = maxHeight - getWorldPos().y;
		return PhysicsUtils.getInitialVelocity(distance, 0, jWorld.getGravityY() );
	}
}
