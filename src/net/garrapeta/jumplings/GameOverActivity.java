package net.garrapeta.jumplings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import net.garrapeta.jumplings.facebook.FacebookAuthenticator;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.openfeint.api.OpenFeint;
import com.openfeint.api.resource.Leaderboard;
import com.openfeint.api.resource.Score;

/**
 * Actividad para introducir un nuevo High Score
 * 
 * @author GaRRaPeTa
 */
public class GameOverActivity extends Activity {

    // -----------------------------------------------------------------
    // Constantes

    /** Clave para pasar highScore entre actividades */
    public static final String NEW_HIGHSCORE_KEY = HighScore.class.getCanonicalName();

    public static final int SCORE_SUBMISSION_ERROR_DIALOG = 0;
    public static final int SERVER_COMUNICATION_PROGRESS_DIALOG = 1;
    public static final int FACEBOOK_COMUNICATION_PROGRESS_DIALOG = 2;
    public static final int FACEBOOK_ERROR_DIALOG = 3;
    public static final int FEINT_COMUNICATION_PROGRESS_DIALOG = 4;
    public static final int FEINT_ERROR_DIALOG = 5;
    public static final int TWITTER_COMUNICATION_PROGRESS_DIALOG = 6;
    public static final int TWITTER_ERROR_DIALOG = 7;

    public static final String feintLeaderboardId = "926197";

    /** Minimum length of the username */
    private static final int MINIMUM_NAME_LENGTH = 4;

    // ------------------------------------------------------------------
    // Variables

    private HighScore playerScore;

    private Button mSaveScoreButton;

    private EditText mPlayerNameEditText;

    private View scoreIntroductionView;
    private View nextActionView;

    private Button postToFeintButton;

    private Button postToFacebookButton;

    private Button postToTwitterButton;

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
            playerScore = (HighScore) b.getParcelable(NEW_HIGHSCORE_KEY);
        }

        // DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG
        // - DEBUG
        if (playerScore == null) {
            playerScore = new HighScore(this);
            playerScore.score = 999999;
            playerScore.level = 99;
        }
        // DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG - DEBUG
        // - DEBUG

        initGui();

        // Facebook initialisation
        FacebookAuthenticator.getInstance().init(this, JumplingsApplication.facebokAppID);
    }

    private void initGui() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.gameover);

        newHighScore = playerScore.score > 0 && HighScore.getLocalHighScoresPosition(playerScore.score) < HighScore.MAX_LOCAL_HIGHSCORE_COUNT;

        TextView scoreTextView = (TextView) findViewById(R.id.gameover_scoreTextView);
        scoreTextView.setText("Your score: " + playerScore.score);

        HighScore highest = PermData.getInstance().getLocalGetHighScore();
        if (highest != null) {
            TextView messageTextView = (TextView) findViewById(R.id.gameover_messageTextView);
            messageTextView.setVisibility(View.VISIBLE);
            long prevHighScore = PermData.getInstance().getLocalGetHighScore().score;
            if (playerScore.score > prevHighScore) {
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
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = s.toString();
                    mSaveScoreButton.setEnabled(text != null && text.trim().length() >= MINIMUM_NAME_LENGTH);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            mPlayerNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    // TODO: Event is sometimes null:
                    // http://stackoverflow.com/questions/11301061/null-keyevent-and-actionid-0-in-oneditoraction-jelly-bean-nexus-7
                    if (event != null && (event.getAction() == KeyEvent.ACTION_DOWN) && (actionId == EditorInfo.IME_NULL)) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(GameOverActivity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
                        return true;
                    }
                    return false;

                }
            });
        } else {
            scoreIntroductionView.setVisibility(View.INVISIBLE);
            nextActionView.setVisibility(View.VISIBLE);
        }

        mPlayerNameEditText.setText(PermData.getInstance().getLastPlayerName());

        if (waveKey != null) {
            Button replayButton = (Button) findViewById(R.id.gameover_replayBtn);
            replayButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent i = new Intent(GameOverActivity.this, GameActivity.class);
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
                Intent i = new Intent(GameOverActivity.this, HighScoreListingActivity.class);
                startActivity(i);
            }
        });

        if (JumplingsApplication.FEINT_ENABLED && OpenFeint.isUserLoggedIn()) {
            postToFeintButton = (Button) findViewById(R.id.gameover_postToFeintBtn);
            postToFeintButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitHighScoreToFeint();
                }
            });
            postToFeintButton.setVisibility(View.VISIBLE);
        }

        if (JumplingsApplication.FACEBOOK_ENABLED) {
            postToFacebookButton = (Button) findViewById(R.id.gameover_postToFacebookBtn);
            postToFacebookButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitHighScoreToFacebook();
                }
            });
            postToFacebookButton.setVisibility(View.VISIBLE);
        }

        if (JumplingsApplication.TWITTER_ENABLED) {
            postToTwitterButton = (Button) findViewById(R.id.gameover_postToTwitterBtn);
            postToTwitterButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitHighScoreToTwitter();
                }
            });
            postToTwitterButton.setVisibility(View.VISIBLE);
        }

        // Ads
        if (JumplingsApplication.MOBCLIX_ENABLED) {
            findViewById(R.id.gameover_advertising_banner_view).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.i(JumplingsApplication.LOG_SRC, "onNewIntent " + this);

        /*
         * Este m�todo funciona como callback de la autenticaci�n hecha por
         * Twitter.
         * 
         * Cuando Twitter ha concedido la autentiaci�n invoca el callback
         * "twitter4j://authenticated". El SSOO abre esta actividad (por el
         * intent-filter), y al haberse declarado singleInstance se ejecuta este
         * m�todo.
         */
        onBackFromTwitterLogin(intent);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog d = null;
        AlertDialog.Builder builder = null;

        switch (id) {

        case SERVER_COMUNICATION_PROGRESS_DIALOG:
            d = ProgressDialog.show(this, "", "Contacting server...", true);
            break;

        case FACEBOOK_COMUNICATION_PROGRESS_DIALOG:
            d = ProgressDialog.show(this, "", "Contacting Facebook...", true);
            break;

        case SCORE_SUBMISSION_ERROR_DIALOG:
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Error submitting score to server").setCancelable(true).setPositiveButton("OK", null);

            d = builder.create();
            break;

        case FACEBOOK_ERROR_DIALOG:
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Error submitting score to Facebook.").setCancelable(true).setPositiveButton("OK", null);

            d = builder.create();
            break;

        case FEINT_ERROR_DIALOG:
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Error submitting score to Feint.").setCancelable(true).setPositiveButton("OK", null);

            d = builder.create();
            break;

        case FEINT_COMUNICATION_PROGRESS_DIALOG:
            d = ProgressDialog.show(this, "", "Contacting Feint...", true);
            break;

        case TWITTER_COMUNICATION_PROGRESS_DIALOG:
            d = ProgressDialog.show(this, "", "Contacting Twitter...", true);
            break;

        case TWITTER_ERROR_DIALOG:
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Error submitting score to Twitter.").setCancelable(true).setPositiveButton("OK", null);

            d = builder.create();
            break;
        }

        return d;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (JumplingsApplication.FACEBOOK_ENABLED) {
            FacebookAuthenticator.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }

    // -------------------------------------------------------- M�todos propios

    /**
     * Salva el score
     */
    private void saveHighScore() {
        playerScore.playerName = mPlayerNameEditText.getText().toString();
        PermData.getInstance().saveLastPlayerName(playerScore.playerName);
        PermData.getInstance().addNewLocalScore(playerScore);
        PermData.getInstance().setLocalScoresSubmissionPending(true);

        scoreIntroductionView.setVisibility(View.INVISIBLE);
        nextActionView.setVisibility(View.VISIBLE);

        if (JumplingsApplication.FEINT_ENABLED && OpenFeint.isUserLoggedIn()) {
            postToFeintButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Manda el score a Feint
     */
    private void submitHighScoreToFeint() {
        Score s = new Score(playerScore.score, null); // Second parameter is
                                                      // null to indicate that
                                                      // custom display text is
                                                      // not used.
        Leaderboard l = new Leaderboard(feintLeaderboardId);

        showDialog(FEINT_COMUNICATION_PROGRESS_DIALOG);

        s.submitTo(l, new Score.SubmitToCB() {
            @Override
            public void onSuccess(boolean newHighScore) {
                // TODO: No daba Feint feedback del success??

                Toast.makeText(GameOverActivity.this, "Your score has been posted to Feint!", Toast.LENGTH_SHORT).show();

                dismissDialog(FEINT_COMUNICATION_PROGRESS_DIALOG);

                postToFeintButton.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                Log.e(JumplingsApplication.LOG_SRC, "Error (" + exceptionMessage + ") posting score to Feint.");

                Toast.makeText(GameOverActivity.this, "Error (" + exceptionMessage + ") posting score to Feint.", Toast.LENGTH_SHORT).show();

                dismissDialog(FEINT_COMUNICATION_PROGRESS_DIALOG);
                showDialog(FEINT_ERROR_DIALOG);
            }
        });

    }

    /**
     * Manda el score a Twitter
     */
    private void submitHighScoreToTwitter() {
        // Se comprueba que est� logeado
        try {
            AccessToken acessToken = JumplingsApplication.twitter.getOAuthAccessToken();
            if (acessToken == null) {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            logToTwitter();
            return;
        }

        Log.i(JumplingsApplication.LOG_SRC, "Submitting score to Twitter");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog(TWITTER_COMUNICATION_PROGRESS_DIALOG);
            }
        });

        // TODO: usar llamada as�ncrona, de la versi�n nueva de Twitter4j
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message = getScoreMessage();
                    // Esta llamada es bloqueante
                    JumplingsApplication.twitter.updateStatus(message);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog(TWITTER_COMUNICATION_PROGRESS_DIALOG);

                            Toast.makeText(GameOverActivity.this, "Your score has been posted to Twitter!", Toast.LENGTH_SHORT).show();

                            postToTwitterButton.setVisibility(View.GONE);
                        }
                    });

                } catch (Throwable t) {
                    // FIXME: si se manda dos veces el mismo score, twitter da
                    // error. Evitarlo
                    Log.e(JumplingsApplication.LOG_SRC, "Error when submitting score to Twitter: " + t.toString());
                    t.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog(TWITTER_COMUNICATION_PROGRESS_DIALOG);
                            showDialog(TWITTER_ERROR_DIALOG);
                        }
                    });
                }

            }
        }).start();
    }

    /**
     * Se loguea en Twitter
     */
    private void logToTwitter() {
        try {
            // se intenta recuperar el token de sistema persistente
            Log.i(JumplingsApplication.LOG_SRC, "Trying to recover Twitter access token from perm data");

            String token = PermData.getInstance().getTwitterToken();
            String tokenSecret = PermData.getInstance().getTwitterTokenSecret();

            if (token != null && tokenSecret != null) {
                logToTwitter(token, tokenSecret);
                return;
            }

            Log.i(JumplingsApplication.LOG_SRC, "Remotely logging to Twitter");

            // TODO: refinar permisos que se piden a twitter

            JumplingsApplication.twitterHttpOauthConsumer = new CommonsHttpOAuthConsumer(JumplingsApplication.twitterConsumerKey,
                    JumplingsApplication.twitterConsumerSecret);

            JumplingsApplication.twitterHttpOauthprovider = new DefaultOAuthProvider("http://twitter.com/oauth/request_token",
                    "http://twitter.com/oauth/access_token", "http://twitter.com/oauth/authorize");

            // FIXME: el bot�n se cuelga al abrir actividad
            final String authUrl = JumplingsApplication.twitterHttpOauthprovider.retrieveRequestToken(JumplingsApplication.twitterHttpOauthConsumer,
                    JumplingsApplication.twitterCallbackUrl);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));

        } catch (final Throwable t) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(JumplingsApplication.LOG_SRC, "Error when submitting score to Twitter: " + t.toString());
                    t.printStackTrace();
                    showDialog(TWITTER_ERROR_DIALOG);
                }
            });
        }
    }

    private void logToTwitter(String token, String tokenSecret) {
        JumplingsApplication.twitter.setOAuthConsumer(JumplingsApplication.twitterConsumerKey, JumplingsApplication.twitterConsumerSecret);
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        JumplingsApplication.twitter.setOAuthAccessToken(accessToken);

        submitHighScoreToTwitter();
    }

    private void onBackFromTwitterLogin(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(JumplingsApplication.twitterCallbackUrl)) {
            String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
            try {
                // this will populate token and token_secret in consumer

                JumplingsApplication.twitterHttpOauthprovider.retrieveAccessToken(JumplingsApplication.twitterHttpOauthConsumer, verifier);

                Log.i(JumplingsApplication.LOG_SRC, "Remotely logged to Twitter");

                String token = JumplingsApplication.twitterHttpOauthConsumer.getToken();
                String tokenSecret = JumplingsApplication.twitterHttpOauthConsumer.getTokenSecret();

                PermData.getInstance().saveTwitterToken(token);
                PermData.getInstance().saveTwitterTokenSecret(tokenSecret);

                logToTwitter(token, tokenSecret);

            } catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(JumplingsApplication.LOG_SRC, "Error retrieveing access token from Twitter: " + e.toString());
                        e.printStackTrace();
                        showDialog(TWITTER_ERROR_DIALOG);
                    }
                });
            }
        }
    }

    /**
     * Manda el score a Facebook
     */
    private void submitHighScoreToFacebook() {
        if (!FacebookAuthenticator.getInstance().isSessionValid()) {
            logToFacebook();
        } else {
            Log.i(JumplingsApplication.LOG_SRC, "Submitting score to facebook");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(FACEBOOK_COMUNICATION_PROGRESS_DIALOG);
                }
            });

            String message = getScoreMessage();

            Bundle parameters = new Bundle();

            parameters.putString("message", message);

            RequestListener rl = new RequestListener() {
                @Override
                public void onComplete(final String response, Object state) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog(FACEBOOK_COMUNICATION_PROGRESS_DIALOG);
                            Log.i(JumplingsApplication.LOG_SRC, "Facebook response: " + response);

                            try {
                                JSONObject responseObj = new JSONObject(response);

                                if (!responseObj.has("error")) {
                                    Toast.makeText(GameOverActivity.this, "Your score has been posted to Facebook!", Toast.LENGTH_SHORT).show();

                                    postToFacebookButton.setVisibility(View.GONE);
                                } else {
                                    // FIXME: si se manda dos veces el mismo
                                    // score, facebook da error. Evitarlo
                                    JSONObject error = responseObj.getJSONObject("error");
                                    Log.e(JumplingsApplication.LOG_SRC, "Error in Facebook response: " + error.getString("message"));
                                    showDialog(FACEBOOK_ERROR_DIALOG);
                                }

                            } catch (JSONException jse) {
                                Log.i(JumplingsApplication.LOG_SRC, "Problems reading facebook response: " + response);
                                jse.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onIOException(final IOException ioe, Object state) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog(FACEBOOK_COMUNICATION_PROGRESS_DIALOG);
                            Log.e(JumplingsApplication.LOG_SRC, "Error when submitting score to Facebook: " + ioe.toString());
                            ioe.printStackTrace();
                            showDialog(FACEBOOK_ERROR_DIALOG);
                        }
                    });
                }

                @Override
                public void onFileNotFoundException(final FileNotFoundException fnfe, Object state) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog(FACEBOOK_COMUNICATION_PROGRESS_DIALOG);
                            Log.e(JumplingsApplication.LOG_SRC, "Error when submitting score to Facebook: " + fnfe.toString());
                            fnfe.printStackTrace();
                            showDialog(FACEBOOK_ERROR_DIALOG);
                        }
                    });
                }

                @Override
                public void onMalformedURLException(final MalformedURLException mue, Object state) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog(FACEBOOK_COMUNICATION_PROGRESS_DIALOG);
                            Log.e(JumplingsApplication.LOG_SRC, "Error when submitting score to Facebook: " + mue.toString());
                            mue.printStackTrace();
                            showDialog(FACEBOOK_ERROR_DIALOG);
                        }
                    });
                }

                @Override
                public void onFacebookError(final FacebookError fe, Object state) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog(FACEBOOK_COMUNICATION_PROGRESS_DIALOG);
                            Log.e(JumplingsApplication.LOG_SRC, "Error when submitting score to Facebook: " + fe.toString());
                            fe.printStackTrace();
                            showDialog(FACEBOOK_ERROR_DIALOG);
                        }
                    });
                }
            };

            FacebookAuthenticator.getInstance().request("me/feed", parameters, "POST", rl, null);
        }

    }

    /**
     * Se loguea en Facebook
     */
    private void logToFacebook() {
        FacebookAuthenticator.getInstance().login(new DialogListener() {
            @Override
            public void onComplete(Bundle values) {
                submitHighScoreToFacebook();
            }

            @Override
            public void onFacebookError(FacebookError fe) {
                showDialog(FACEBOOK_ERROR_DIALOG);
            }

            @Override
            public void onError(DialogError de) {
                showDialog(FACEBOOK_ERROR_DIALOG);
            }

            @Override
            public void onCancel() {
                Toast.makeText(GameOverActivity.this, "Facebook operation cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getScoreMessage() {
        return "I've achieved " + playerScore.score + " points in Jumplings!";
    }

}
