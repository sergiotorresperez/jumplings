package com.garrapeta.jumplings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.garrapeta.jumplings.flurry.FlurryHelper;
import com.garrapeta.jumplings.util.Utils;

/**
 * Actividad para introducir un nuevo High Score
 * 
 * @author GaRRaPeTa
 */
public class GameOverActivity extends Activity {

	// -----------------------------------------------------------------
	// Constantes

	/** Clave para pasar highScore entre actividades */
	public static final String NEW_HIGHSCORE_KEY = Score.class
			.getCanonicalName();

	/** Minimum length of the username */
	private static final int MINIMUM_NAME_LENGTH = 4;

	// ------------------------------------------------------------------
	// Variables

	private Score mPlayerScore;

	private Button mSaveScoreButton;

	private EditText mPlayerNameEditText;

	private View scoreIntroductionView;
	private View nextActionView;

	/** Si el score es lo suficientemente alto para ser grabado */
	private boolean newHighScore = false;

	/** Wave de la partida jugada */
	private String waveKey;

	// -------------------------------------------------------- Variables
	// est�ticas

	// -------------------------------------------------------- M�todos de
	// Activity

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(JumplingsApplication.LOG_SRC, "onCreate " + this);

		// Wave y botones
		waveKey = null;

		Bundle b = getIntent().getExtras();
		if (b != null) {
			waveKey = b.getString(GameActivity.WAVE_BUNDLE_KEY);
			mPlayerScore = (Score) b.getParcelable(NEW_HIGHSCORE_KEY);
		}

		// DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG
		// - DEBUG
		if (mPlayerScore == null) {
			mPlayerScore = new Score(this);
			mPlayerScore.score = 999999;
			mPlayerScore.level = 99;
		}
		// DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG
		// - DEBUG

		initGui();

	}

    @Override
    protected void onStart() {
        super.onStart();
        FlurryHelper.onStartSession(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        FlurryHelper.onEndSession(this);
    }
    
	private void initGui() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_gameover);

		Button shareButton = (Button) findViewById(R.id.gameover_shareBtn);
		shareButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryHelper.logShareButtonClicked();
				Utils.share(GameOverActivity.this, getShareScoreMessage());
			}
		});

		newHighScore = mPlayerScore.score > 0
				&& Score.getLocalHighScoresPosition(mPlayerScore.score) < Score.MAX_LOCAL_HIGHSCORE_COUNT;

		TextView scoreTextView = (TextView) findViewById(R.id.gameover_scoreTextView);
		scoreTextView.setText("Your score: " + mPlayerScore.score);

		Score highest = PermData.getInstance().getLocalGetHighScore();
		if (highest != null) {
			TextView messageTextView = (TextView) findViewById(R.id.gameover_messageTextView);
			messageTextView.setVisibility(View.VISIBLE);
			long prevHighScore = PermData.getInstance().getLocalGetHighScore().score;
			if (mPlayerScore.score > prevHighScore) {
				messageTextView.setText("You've beaten the local highscore!");
			} else {
				messageTextView.setText("HighScore is " + prevHighScore);
			}
		}

		scoreIntroductionView = findViewById(R.id.gameover_nameIntroductionLayout);
		nextActionView = findViewById(R.id.gameover_nextActionView);

		if (newHighScore) {
			scoreIntroductionView.setVisibility(View.VISIBLE);
			nextActionView.setVisibility(View.INVISIBLE);

			mSaveScoreButton = (Button) findViewById(R.id.gameover_saveScoreBtn);
			mSaveScoreButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					saveHighScore();
				}
			});

			mPlayerNameEditText = (EditText) findViewById(R.id.gameover_playerNameEditText);

			mPlayerNameEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					String text = s.toString();
					mSaveScoreButton.setEnabled(text != null
							&& text.trim().length() >= MINIMUM_NAME_LENGTH);
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			mPlayerNameEditText
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView tv,
								int actionId, KeyEvent event) {
							// If the event is a key-down event on the "enter"
							// button
							// TODO: Event is sometimes null:
							// http://stackoverflow.com/questions/11301061/null-keyevent-and-actionid-0-in-oneditoraction-jelly-bean-nexus-7
							if (event != null
									&& (event.getAction() == KeyEvent.ACTION_DOWN)
									&& (actionId == EditorInfo.IME_NULL)) {
								InputMethodManager imm = (InputMethodManager) getSystemService(GameOverActivity.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										tv.getWindowToken(), 0);
								return true;
							}
							return false;

						}
					});

			mPlayerNameEditText.setText(PermData.getInstance()
					.getLastPlayerName());
		} else {
			scoreIntroductionView.setVisibility(View.INVISIBLE);
			nextActionView.setVisibility(View.VISIBLE);
		}

		if (waveKey != null) {
			Button replayButton = (Button) findViewById(R.id.gameover_replayBtn);
			replayButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					Intent i = new Intent(GameOverActivity.this,
							GameActivity.class);
					i.putExtra(GameActivity.WAVE_BUNDLE_KEY, waveKey);

					startActivity(i);
				}
			});
		}

		Button menuButton = (Button) findViewById(R.id.gameover_menuBtn);
		menuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				Intent i = new Intent(GameOverActivity.this, MenuActivity.class);
				startActivity(i);
			}
		});

		Button viewHighScoresButton = (Button) findViewById(R.id.gameover_viewHighScoresBtn);
		viewHighScoresButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(GameOverActivity.this,
						HighScoreListingActivity.class);
				startActivity(i);
			}
		});

		// Ads
		if (JumplingsApplication.ADS_ENABLED) {
			findViewById(R.id.gameover_advertising_banner_view).setVisibility(
					View.VISIBLE);
		}
	}

	// -------------------------------------------------------- M�todos
	// propios

	/**
	 * Salva el score
	 */
	private void saveHighScore() {
		mPlayerScore.playerName = mPlayerNameEditText.getText().toString();
		PermData.getInstance().saveLastPlayerName(mPlayerScore.playerName);
		PermData.getInstance().addNewLocalScore(mPlayerScore);
		PermData.getInstance().setLocalScoresSubmissionPending(true);

		scoreIntroductionView.setVisibility(View.INVISIBLE);
		nextActionView.setVisibility(View.VISIBLE);
	}

	private String getShareScoreMessage() {
		return "I've achieved " + mPlayerScore.score + " points in Jumplings!";
	}

}
