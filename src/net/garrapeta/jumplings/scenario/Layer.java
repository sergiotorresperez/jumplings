package net.garrapeta.jumplings.scenario;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Capa del escerario
 * @author GaRRaPeTa
 */
class Layer {
	
	// ------------------------------------------------ Constantes
	
	// TODO: hacer esto en funci�n del FPS
	/** Ciclos que tarda en reflejarse un update de progreso */
	private float PROGRESS_UPDATE_CICLES = 5;
	
	/** Ciclos que tarda la ca�da cuando el jugador muere */
	private float GAMEOVER_FALL_CICLES   = 50;
	
	// ------------------------------------ Variables de instancia
	
	private float initXPos;
	private float initYPos;
	
	private float initXVel;
	private float initYVel;
	
	private float xPos = 0;
	private float yPos = 0;
	
	/** Posci�n y a la que se tiene que llegar por un update */
	private float desiredYPos = 0;
	
	// velocidades, en p�xeles por ciclo
	// TODO: expresar las velocidades en p�xeles por segundo
	float xVel = 0;
	float yVel = 0;
	
	/** Componente de la velocidad aportada por los updates de progreso */
	float updateYVel = 0;
	
	Scenario scenario;
	
	// Bitmaps
	private Bitmap bitmap;
	
	private int bitmapWidth;
	private int bitmapHeight;
	
	private Rect  rSrc = new Rect();
	private RectF rDst = new RectF();
	
	private boolean tileHorizontally = false;
	private boolean tileVertically   = false;
	
	// Altura m�xima del layer
	float maxHeight;
	
	// ----------------------------------------------- Constructor
	
	/**
	 * @param bmp
	 * @param maxHeight
	 */
	Layer (Scenario scenario, Bitmap bmp, int maxHeight, 
		   float initXPos, float initYPos, 
		   float initXVel, float initYVel,
		   boolean tileHorizontally, boolean tileVertically) {
		
		this.scenario = scenario;
		this.bitmap = bmp;
		
		bitmapWidth  = bitmap.getWidth();
		bitmapHeight = bitmap.getHeight();
		
		this.maxHeight = maxHeight;
		
		this.initXPos = initXPos;
		this.initYPos = initYPos;
		
		this.initXVel = initXVel;
		this.initYVel = initYVel;
		
		this.tileHorizontally = tileHorizontally;
		this.tileVertically   = tileVertically;
	}
	
	// -------------------------------------------- M�todos propios
	
	/**
	 *  Reseteo
	 */
	public void reset() {
		xPos = initXPos;
		yPos = initYPos;
		
		desiredYPos = yPos;
		
		xVel = initXVel;
		yVel = initYVel;
		
		updateYVel = 0;
	}
	
	/**
	 * @param gameTimeStep
	 * @param physicsTimeStep
	 */
	void processFrame(float gameTimeStep) {
		xPos += xVel; //xVel * (gameTimeStep / 1000);
		yPos += yVel; //yVel * (gameTimeStep / 1000);
		
		
		
		// aportaci�n de velocidad por el update
		if (updateYVel != 0) {
			if (updateYVel > 0) {
				yPos = Math.min(desiredYPos, yPos + updateYVel);
			} else if (updateYVel < 0) {
				yPos = Math.max(initYPos,    yPos + updateYVel);
			}
			
			// si ya ha llegado al punto deseado se para
			if (yPos == desiredYPos) {
				updateYVel = 0;
			}
		}


	}
	
	synchronized void setProgress(float progress) {
		if (progress <= 100) {
			float aux = maxHeight - scenario.dWorld.mView.getHeight();
			desiredYPos = initYPos + (progress * aux) / 100;
			float diff = desiredYPos - yPos;
			updateYVel = diff / PROGRESS_UPDATE_CICLES;
		}
	}
	
	synchronized void onGameOver() {
		desiredYPos = initYPos;
		float diff = desiredYPos - yPos;
		updateYVel = diff / GAMEOVER_FALL_CICLES;
	}
	
	/**
	 * @param canvas
	 * @param paint
	 */
	public void draw(Canvas canvas, Paint paint) {
		int neededWitdh  = scenario.dWorld.mView.getWidth();
		int neededHeight = scenario.dWorld.mView.getHeight();
		
		int filledWidth  = 0;
		
		int xPosAux = (int) xPos;
		int yPosAux = (int) yPos;
		
		if (tileHorizontally) {
			xPosAux = xPosAux % scenario.dWorld.mView.getWidth();
		}
		if (tileVertically) {
			yPosAux = yPosAux % scenario.dWorld.mView.getHeight();
		}		

		
		int auxLeft;
		if (tileHorizontally) {
			auxLeft  = (bitmapWidth  - (xPosAux % bitmapWidth))  % bitmapWidth;
		} else {
			auxLeft = 0;
		}
		
		while (filledWidth < neededWitdh) {
			rSrc.left   = auxLeft;
			
			if (tileHorizontally) {
				rSrc.right  = Math.min(bitmapWidth, neededWitdh - filledWidth);
				rDst.left   = filledWidth;
			} else {
				rSrc.right  = bitmapWidth;
				rDst.left   = neededWitdh - bitmapWidth + xPosAux;
			}
		
			int width = (rSrc.right - rSrc.left);
			rDst.right  = rDst.left + width;		 
			
			int filledHeight = 0;
			int auxTop = (bitmapHeight - (yPosAux % bitmapHeight)) % bitmapHeight;
			
			if (tileVertically) {
				auxTop  = (bitmapHeight - (yPosAux % bitmapHeight)) % bitmapHeight;
			} else {
				auxTop = 0;
			}
			
			while (filledHeight < neededHeight) {
				rSrc.top     = auxTop;
				
				if (tileVertically) {
					rSrc.bottom = Math.min(bitmapHeight, neededHeight - filledHeight);
					rDst.top    = filledHeight;
				} else {
					rSrc.bottom = bitmapHeight;
					rDst.top    = /*neededHeight - bitmapHeight +*/ yPosAux;
				}
				
				int height = (rSrc.bottom - rSrc.top);
				rDst.bottom = rDst.top + height;		 

				canvas.drawBitmap(bitmap, rSrc, rDst, paint);
				
				if (!tileVertically) {
					break;
				}
				auxTop = 0;
				filledHeight += height;
			}
			
			if (!tileHorizontally) {
				break;
			}
			auxLeft = 0;
			filledWidth += width;
		}
		
	}



}