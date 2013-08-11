package com.garrapeta.jumplings.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.garrapeta.jumplings.R;

/**
 * Helper class to create the Pause DialogFragment 
 */
public class PauseDialogFactory {

    /**
     * @return the pause DialogFragment
     */
    public static DialogFragment create() {
        PauseDialogFragment dialog = new PauseDialogFragment();
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * Pause dialog
     */
    public static class PauseDialogFragment extends DialogFragment {
        
        private PauseDialogListener mListener;
        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            
            CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());

            builder.setMessageBig(R.string.game_paused);

            builder.setRightButton(getString(R.string.game_menu), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMainMenuButtonClicked();
                    dismiss();
                }
            });

            builder.setLeftButton(getString(R.string.game_resume), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onResumeButtonClicked();
                    dismiss();
                }
            });
            
            return builder.create();
        }
        
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (PauseDialogListener) activity;
                mListener.onPauseDialogShown();
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()  + " must implement " + PauseDialogListener.class.getSimpleName());
            }
        }
        
        @Override
        public void onDetach() {
            super.onDetach();
            mListener.onPauseDialogClosed();
        }

        /**
         * Interface to listen dialog events.
         */
        public static interface PauseDialogListener {
            public void onPauseDialogShown();
            public void onPauseDialogClosed();
            public void onResumeButtonClicked();
            public void onMainMenuButtonClicked();
        }
    }
}
