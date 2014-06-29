package com.garrapeta.jumplings.ui.scores;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.util.AdsHelper;
import com.garrapeta.jumplings.util.FlurryHelper;
import com.garrapeta.jumplings.util.PermData;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

/**
 * High scores listing activity
 *
 */
public class ScoresActivity extends BaseGameActivity implements OnClickListener {

    private ListView mLocalHighScoresView;
    private View mSubmitScoresBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_highscores);

        mLocalHighScoresView = (ListView) findViewById(R.id.highscoresListing_localHighScoresListView);

        ScoreListAdapter adapter = new ScoreListAdapter(this, PermData.getLocalScoresList(this));
        mLocalHighScoresView.setAdapter(adapter);
        mLocalHighScoresView.setCacheColorHint(0xFFFFFFFF);

        mSubmitScoresBtn = findViewById(R.id.highscoresListing_submitBtn);
        mSubmitScoresBtn.setVisibility(View.GONE);
        mSubmitScoresBtn.setOnClickListener(this);

        final AdView adView = (AdView) findViewById(R.id.highscoresListing_advertising_banner_view);
        if (AdsHelper.shoulShowAds(this)) {
            AdsHelper.requestAd(adView);
            adView.setVisibility(View.VISIBLE);
        } else {
            adView.setVisibility(View.GONE);
        }
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

    @Override
    public void onSignInFailed() {
        Toast.makeText(this, "failed", Toast.LENGTH_SHORT)
             .show();

    }

    @Override
    public void onSignInSucceeded() {
        Toast.makeText(this, "oK!!", Toast.LENGTH_SHORT)
             .show();

        mSubmitScoresBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.highscoresListing_submitBtn:
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.config_google_play_games_leaderboard_id)), 0);
            return;
        }
    }
}
