package com.garrapeta.jumplings.flurry;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.flurry.android.FlurryAgent;

/**
 * Helper to integrate Flurry
 */
public class FlurryHelper {

	private static boolean sInitialized;
	private static String sApiKey;
	private static boolean sIsEnabled;
	private final static Object sLock = new Object();

	private final static String EVENT_GAME_OVER = "game_over";
	private final static String EVENT_SCORES_SUBMITTED = "scores_submitted";
	private final static String EVENT_SHARE_BTN_CLICKED = "share_btn_clicked";
	private final static String EVENT_AD_DIALOG_SHOWN = "ad_dialog_shown";
	private final static String EVENT_BUY_BTN_CLICKED = "buy_btn_clicked";

	private final static String ERROR_GAME_ENGINE = "game_engine";
	private final static String ERROR_ID_SCORE_SUBMISSION = "score_submission";
	private final static String ERROR_ID_SCORE_DOWNLOAD = "score_download";
	private static final String EVENT_GAME_OVER_PARAM_GAME_DURATION = "duration_in_game_millis";
	private static final String EVENT_GAME_OVER_PARAM_LEVEL = "level";
	private static final String EVENT_GAME_OVER_PARAM_SCORE = "score";

	/**
	 * Initializes the module. Cano be called only once.
	 * 
	 * @param enabled
	 * @param apiKey
	 */
	public static void initialize(final boolean enabled, final String apiKey,
			final boolean flurryLogEnabled) {
		synchronized (sLock) {
			if (sInitialized) {
				throw new IllegalStateException("Was already initialized");
			}
			sInitialized = true;
			sIsEnabled = enabled;
			sApiKey = apiKey;
			FlurryAgent.setLogEnabled(flurryLogEnabled);
		}
	}

	private static void checkInitialized() throws IllegalStateException {
		synchronized (sLock) {
			if (!sInitialized) {
				throw new IllegalStateException(
						"Flurry helper is not initialized");
			}
			if (TextUtils.isEmpty(sApiKey)) {
				throw new IllegalStateException("API key is not specified");
			}
		}
	}

	/**
	 * Should be called in onStart of every Activity
	 * 
	 * @param context
	 */
	public static void onStartSession(Context context) {
		checkInitialized();
		if (sIsEnabled) {
			FlurryAgent.onStartSession(context, sApiKey);
		}
	}

	public static void logGameOver(long durationInGameMillis, int level, long score) {
		checkInitialized();
		if (sIsEnabled) {
			final Map<String, String> arguments = new HashMap<String, String>(3);
			arguments.put(EVENT_GAME_OVER_PARAM_GAME_DURATION, Long.toString(durationInGameMillis));
			arguments.put(EVENT_GAME_OVER_PARAM_LEVEL, Integer.toString(level));
			arguments.put(EVENT_GAME_OVER_PARAM_SCORE, Long.toString(score));
			FlurryAgent.onEvent(EVENT_GAME_OVER, arguments);
		}
	}
	
	public static void logScoresSubmitted() {
		checkInitialized();
		if (sIsEnabled) {
			FlurryAgent.onEvent(EVENT_SCORES_SUBMITTED);
		}
	}
	
	public static void logShareButtonClicked() {
		checkInitialized();
		if (sIsEnabled) {
			FlurryAgent.onEvent(EVENT_SHARE_BTN_CLICKED);
		}
	}

	public static void logAdDialogShown() {
		checkInitialized();
		if (sIsEnabled) {
			FlurryAgent.onEvent(EVENT_AD_DIALOG_SHOWN);
		}
	}
	
	public static void logBuyBtnClicked() {
		checkInitialized();
		if (sIsEnabled) {
			FlurryAgent.onEvent(EVENT_BUY_BTN_CLICKED);
		}
	}
	/**
	 * Should be called in onStop of every Activity
	 * 
	 * @param context
	 */
	public static void onEndSession(Context context) {
		checkInitialized();
		if (sIsEnabled) {
			FlurryAgent.onEndSession(context);
		}
	}

	public static void onGameEngineError(Throwable t) {
		checkInitialized();
		if (sIsEnabled) {
			FlurryAgent.onError(ERROR_GAME_ENGINE, "Error in game engine", t);
		}
	}

	public static void onErrorScoreDownloadError(Throwable t) {
		checkInitialized();
		if (sIsEnabled) {
			FlurryAgent.onError(ERROR_ID_SCORE_DOWNLOAD,
					"Error downloading scores", t);
		}
	}

	public static void onErrorScoreSubmissionError(Throwable t) {
		checkInitialized();
		if (sIsEnabled) {
			FlurryAgent.onError(ERROR_ID_SCORE_SUBMISSION,
					"Error submitting scores", t);
		}
	}

}
