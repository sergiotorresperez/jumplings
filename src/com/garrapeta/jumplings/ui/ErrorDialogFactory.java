package com.garrapeta.jumplings.ui;

import com.garrapeta.jumplings.R;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;

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

            builder.setMessageBig(R.string.error).setLeftButton("Quit", new OnClickListener() {
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
