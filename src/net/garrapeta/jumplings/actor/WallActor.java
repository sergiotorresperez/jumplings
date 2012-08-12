package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.actor.Box2DEdgeActor;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;


public class WallActor extends Box2DEdgeActor {

	// ---------------------------------------------------- Constantes
	
	public  final static int    WALL_FILTER_BIT      = 0x00008;
	public  final static int    FLOOR_FILTER_BIT     = 0x00010;
	
	private final static Filter WALL_FILTER;
	
	private final static Filter FLOOR_FILTER;
	
	public final static float   WALL_RESTITUTION   = 1;
	
	// ----------------------------------------------------- Variables
	
	boolean floor    = false;
	boolean security = false;
	
	// --------------------------------------------------- Inicializaci�n est�tica
	
	static  {
		WALL_FILTER = new Filter();
		
		WALL_FILTER.categoryBits = WallActor.WALL_FILTER_BIT;
		
		FLOOR_FILTER  = new Filter();
		
		FLOOR_FILTER.categoryBits = WallActor.FLOOR_FILTER_BIT;
		
		
	}
	
	// ---------------------------------------------------------------- Constructor

	public WallActor(Box2DWorld world, PointF worldPos, PointF p0, PointF p1, boolean floor, boolean security) {
		
		super(world, worldPos, p0, p1, false);
		
		
		Filter filter = (floor) ? FLOOR_FILTER : WALL_FILTER; 
		for (int i = 0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
//			b.setBullet(true);
			
			ArrayList<Fixture> fs = b.getFixtureList();
			
			int l2 = fs.size();
			for (int i2 = 0; i2 < l2; i2++) {
				Fixture f = fs.get(i2);
				f.setRestitution(WALL_RESTITUTION);
				f.setFilterData(filter);
			}
		}
		
		this.floor 		= floor;
		this.security 	= security;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}
	
	// -------------------------------------------------------- M�todos de Box2dActor
	
	
	
}
