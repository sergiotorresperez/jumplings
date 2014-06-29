package com.garrapeta.jumplings.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.garrapeta.jumplings.R;

/**
 * Helper class to create the Error DialogFragment
 */
public class ErrorDialogFactory {

    /**
     * @return the Error DialogFragment
     */
    public static DialogFragment create() {
        ErrorDialogFragment dialog = new ErrorDialogFragment();
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * Error dialog
     */
    public static class ErrorDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());

            builder.setMessageBig(R.string.game_error_title)
                   .setMessageSmall(R.string.game_error)
                   .setLeftButton(getActivity().getString(R.string.nav_proceed), new OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dismiss();
                           getActivity().finish();
                       }
                   });

            return builder.create();
        }
    }
}
