package com.garrapeta.jumplings.ui.scores;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.game.Score;

/**
 * Adapter of the list
 */
class ScoreListAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Score> mScores;

    /**
     * @param list
     */
    public ScoreListAdapter(Context context, List<Score> list) {
        mContext = context;
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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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