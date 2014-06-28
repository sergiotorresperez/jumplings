package com.garrapeta.jumplings;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.garrapeta.gameengine.utils.L;
import com.garrapeta.jumplings.flurry.FlurryHelper;
import com.garrapeta.jumplings.util.AdsHelper;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

public class HighScoreListingActivity extends BaseGameActivity {

    private static final String TAG = HighScoreListingActivity.class.getSimpleName();

    private List<Score> mLocalScoreList;

    private ListView mLocalHighScoresView;
    private Button mSubmitScoresBtn;
    private Button mClearScoresBtn;

    // -------------------------------------------------------- Variables
    // est�ticas

    // -------------------------------------------------------- M�todos de
    // Activity

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_highscores);

        if (L.sEnabled)
            Log.i(JumplingsApplication.TAG, "onCreate " + this);

        mLocalScoreList = PermData.getLocalScoresList(this);
        mLocalHighScoresView = (ListView) findViewById(R.id.highscoresListing_localHighScoresListView);

        feedLocalHighScoresView();

        if (PermData.areDebugFeaturesEnabled(this)) {
            mClearScoresBtn = (Button) findViewById(R.id.highscoresListing_clearLocalScoresBtn);
            mClearScoresBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermData.clearAll(HighScoreListingActivity.this);
                    mLocalScoreList = PermData.getLocalScoresList(HighScoreListingActivity.this);
                    feedLocalHighScoresView();
                }
            });
            mClearScoresBtn.setVisibility(View.VISIBLE);
        }

        mSubmitScoresBtn = (Button) findViewById(R.id.highscoresListing_submitBtn);
        mSubmitScoresBtn.setVisibility(View.GONE);
        mSubmitScoresBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.config_google_play_games_leaderboard_id)), 0);
            }
        });

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

    private void feedLocalHighScoresView() {
        ScoreListAdapter adapter = new ScoreListAdapter(mLocalScoreList);
        mLocalHighScoresView.setAdapter(adapter);
        mLocalHighScoresView.setCacheColorHint(0xFFFFFFFF);
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

    /**
     * Adapter of the list
     */
    private class ScoreListAdapter extends BaseAdapter {
        private List<Score> mScores;

        /**
         * @param list
         */
        public ScoreListAdapter(List<Score> list) {
            mScores = list;
        }

        @Override
        public int getCount() {
            return mScores.size();
        }

        @Override
        public Object getItem(int position) {
            return mScores.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) HighScoreListingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item_score, parent, false);
            }

            Score score = mScores.get(position);

            {
                // index
                TextView view = (TextView) convertView.findViewById(R.id.scoreItem_index);
                view.setText(String.valueOf(position + 1 + "."));
            }

            {
                // player name
                TextView view = (TextView) convertView.findViewById(R.id.scoreItem_playerName);
                view.setText(String.valueOf(score.mPlayerName));
            }

            {
                // level
                TextView view = (TextView) convertView.findViewById(R.id.scoreItem_level);
                view.setText(String.valueOf(score.mLevel));
            }

            {
                // score
                TextView view = (TextView) convertView.findViewById(R.id.scoreItem_score);
                view.setText(String.valueOf(score.mScore));
            }
            return convertView;
        }

    }

}
