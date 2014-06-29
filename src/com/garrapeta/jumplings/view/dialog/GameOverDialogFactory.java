package com.garrapeta.jumplings.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.garrapeta.jumplings.R;

/**
 * Helper class to create the GameOver DialogFragment
 */
public class GameOverDialogFactory {

    /**
     * @return the GameOver DialogFragment
     */
    public static DialogFragment create() {
        GameOverDialogFragment dialog = new GameOverDialogFragment();
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * GameOver dialog
     */
    public static class GameOverDialogFragment extends DialogFragment {

        private GameOverDialogListener mListener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());

            builder.setMessageBig(R.string.game_over)
                   .setLeftButton(getActivity().getString(R.string.nav_proceed), new OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           mListener.onGameOverDialogClosed();
                           dismiss();
                       }
                   });

            return builder.create();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (GameOverDialogListener) activity;
                mListener.onGameOverDialogShown();
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement " + GameOverDialogListener.class.getSimpleName());
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener.onGameOverDialogClosed();
        }

        /**
         * Interface to listen dialog events.
         */
        public static interface GameOverDialogListener {
            public void onGameOverDialogShown();

            public void onGameOverDialogClosed();
        }
    }
}
