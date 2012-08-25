package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.PermData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

public class SwipeActor extends HarmerActor {

	private ArrayList<double[]> swipePoints;
	private Paint paint;
	private final int  TIME = 100;
	private Path path = new Path();
	private RectF killingArea = new RectF();
	private boolean killingAreaUpdated = false;
	
	private final int MIN_START_DISTANCE = 30;
	private final int MIN_STOP_DISTANCE  = 15;
	
	private double[] prev;
	
	private boolean swipping = false;
	
	// ---------------------------------------------------- Constructor
	
	public SwipeActor(JumplingsGameWorld jgWorld) {
		super(jgWorld);
		swipePoints = new ArrayList<double[]>();
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
	}

	// ----------------------------------------------- M�todos heredados
	

	@Override
	public void processFrame(float gameTimeStep) {
		
		synchronized (swipePoints) {
		
			path.reset();
			
			long now = System.currentTimeMillis();
			while (swipePoints.size() > 0) {
				double[] info = swipePoints.get(0);
				if (now - info[3] > TIME) {
					swipePoints.remove(info);
				} else {
					break;
				}
			}
			
			if (swipePoints.size() > 0) {
				
				double[] info;
				float x;
				float y;
				
				info = swipePoints.get(0);
				x = (float) info[1];
				y = (float) info[2];
				path.moveTo(x, y);
				
				for (int i = 0; i < swipePoints.size(); i++) {
					info = swipePoints.get(i);
			
					x = (float) info[1];
					y = (float) info[2];
					
					path.lineTo(x, y);	
					
				}
		
				killingAreaUpdated = false;
			}
		}
		
		super.processFrame(gameTimeStep);
	}

	@Override
	protected void effectOver(MainActor mainActor) {
		if (hits(mainActor)) {
			mainActor.onHitted();
		}
	}



	@Override
	public void draw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	
	// ---------------------------------------- M�todos propios

	public void onTouchEvent(double info[]) {		
		synchronized (swipePoints) {
			if (prev != null) {
				int action = (int) info[0];
				
				if (action == MotionEvent.ACTION_MOVE) {
					double dist = Math.sqrt(Math.pow(info[1] - prev[1], 2) + Math.pow(info[2] - prev[2], 2));
					
					if (!swipping) {
						if (dist >= MIN_START_DISTANCE) {
							swipping = true;
							timestamp = System.currentTimeMillis();
							
						    jgWorld.getSoundManager().play(JumplingsGameWorld.SAMPLE_BLADE_WHIP);
							if (jgWorld.mFlashCfgLevel == PermData.CFG_LEVEL_ALL) {
								FlashActor flash = new FlashActor(jgWorld ,Color.WHITE, 50, 250);
								jgWorld.addActor(flash);
							}
						}
					} else {
						if (dist < MIN_STOP_DISTANCE) {
							swipePoints.clear();
							swipping = false;
						}
					}
				} else if (action == MotionEvent.ACTION_UP) {
					swipePoints.clear();
					swipping = false;
				}
		
				
				if (swipping) {
					this.swipePoints.add(info);
				}
			
			}
			
			prev = info;
		}
		
	}

	public boolean hits(MainActor mainActor) {
		PointF pos = mainActor.getWorldPos();
		
		if (!killingAreaUpdated) {
			path.computeBounds(killingArea, true);
			killingAreaUpdated = true;
		}
		
		float  sr  = jgWorld.viewport.worldUnitsToPixels(mainActor.radius);
		PointF sc  = jgWorld.viewport.worldToScreen(pos.x, pos.y);
		RectF aux = new RectF(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr);
		
		return RectF.intersects(aux, killingArea);
	}



}
