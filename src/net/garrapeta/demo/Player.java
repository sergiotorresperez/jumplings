package net.garrapeta.demo;


import net.garrapeta.demo.actor.ComboTextActor;
import net.garrapeta.demo.actor.EnemyActor;
import net.garrapeta.demo.actor.ScoreTextActor;
import android.graphics.PointF;

public class Player {

	// --------------------------------------------------------- Constantes
	
	public static final int DEFAUL_MAX_LIFES   = 5;
	
	public static final int DEFAUL_INIT_LIFES  = 3;
	
	public static final int DEFAUL_INIT_SCORE = 0;
	
	public static final int COMBO_MAX_SPACING_TIME = 350;
	
	public static final int BASE_POINTS = 5;
	
	// --------------------------------------------- Variables de instancia
	
	private JumplingsGameWorld world;
	
	private int maxLifes = DEFAUL_MAX_LIFES;
	
	private int life  = DEFAUL_INIT_LIFES;
	
	private int score = DEFAUL_INIT_SCORE;
	
	private boolean isVulnerable = true;
	
	private long prevEnemyKillTimestamp;
	
	private int currentComboLevel;

	// ------------------------------------------------------------- Métodos
	
	public Player(JumplingsGameWorld world) {
		this.world = world;
	}
	
	/**
	 * @return the life
	 */
	public int getLifes() {
		return life;
	}
	

	public void addLifes(int add) {
		setLife(life + add);
	}
	
	public void subLifes(int sub) {
		setLife(life - sub);
	}
	
	public void topUpLife() {
		setLife(maxLifes);
	}

	/**
	 * @param life the life to set
	 */
	private void setLife(int newLifes) {
		newLifes = Math.min(newLifes, maxLifes);
		newLifes = Math.max(0, newLifes);
		this.life = newLifes;
		world.jgActivity.updateLifeCounterView();
	}
	
	public int getMaxLifes() {
		return maxLifes;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}
	
	public void onEnemyKilled(EnemyActor enemy) {
		int points = BASE_POINTS;
		long timeStamp = world.currentGameMillis();
		
		if (timeStamp - prevEnemyKillTimestamp < COMBO_MAX_SPACING_TIME) {
			currentComboLevel ++;
		} else {
			currentComboLevel = 0;
		}
		
		prevEnemyKillTimestamp = timeStamp;
		
		points += (currentComboLevel * BASE_POINTS);
		setScore(score + points);
		
		PointF worldPos = enemy.getWorldPos();
		ScoreTextActor scoreActor = new ScoreTextActor(world, worldPos, points);
		world.addActor(scoreActor);
		if (currentComboLevel > 0) {
			ComboTextActor comboActor = new ComboTextActor(world, new PointF(worldPos.x, worldPos.y), currentComboLevel);
			world.addActor(comboActor);
		}
		
	}

	/**
	 * @param score the score to set
	 */
	private void setScore(int score) {
		this.score = score;
		world.jgActivity.updateScoreTextView();
	}
	
	public boolean isVulnerable() {
		return isVulnerable;
	}

	public void makeVulnerable() {
		world.jgActivity.stopBlinkingLifeBar();
		this.isVulnerable = true;
	}
	
	public void makeInvulnerable(final float time) {
		this.isVulnerable = false;
		world.jgActivity.startBlinkingLifeBar();
		
		if (time > 0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep((int)time);
					} catch (InterruptedException e) {
					}
					makeVulnerable();
				}

			}).start();
		}
	}
	
}
