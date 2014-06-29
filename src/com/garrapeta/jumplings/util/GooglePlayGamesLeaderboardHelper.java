package com.garrapeta.jumplings.util;

import android.content.Context;

import com.garrapeta.gameengine.utils.LogX;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.game.Score;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult;

/**
 * Class to help submitting a score to the Google Play games services
 * leaderboard
 */
public class GooglePlayGamesLeaderboardHelper {

    private static final String TAG = GooglePlayGamesLeaderboardHelper.class.getSimpleName();

    public static void submitHighestScoreIfNeeded(final Context context, final GoogleApiClient apiClient) {
        Score highestScore = PermData.getLocalHighestScore(context);
        if (highestScore == null) {
            return;
        }
        final long highestScoreValue = highestScore.mScore;
        if (PermData.getHighestScoreSentToLeaderboard(context) > highestScoreValue) {
            LogX.d(TAG, "A higher score has been already submitted. No need to submit.");
            return;
        }

        submitHightScore(context, apiClient, highestScoreValue);
    }

    private static void submitHightScore(final Context context, final GoogleApiClient apiClient, final long highestScoreValue) {
        LogX.i(TAG, "Submitting score to Google leaderboard");

        final PendingResult<SubmitScoreResult> result = Games.Leaderboards.submitScoreImmediate(apiClient, getLeaderboardId(context), highestScoreValue);
        result.setResultCallback(new ResultCallback<Leaderboards.SubmitScoreResult>() {

            @Override
            public void onResult(SubmitScoreResult finalResult) {
                Status status = finalResult.getStatus();
                LogX.i(TAG, "Result of the score submission: " + status);
                if (status.getStatusCode() == GamesStatusCodes.STATUS_OK) {
                    PermData.saveHighestScoreSentToLeaderboard(context, highestScoreValue);
                }
            }
        });
    }

    private static String getLeaderboardId(Context context) {
        return context.getString(R.string.config_google_play_games_leaderboard_id);
    }
}
