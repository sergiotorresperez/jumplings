package com.garrapeta.jumplings.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.garrapeta.jumplings.R;

/**
 * Helper class to create the Purchase dialog DialogFragment
 */
public class PurchaseDialogFactory {

    /**
     * @return the Purchase Dialog Fragment
     */
    public static DialogFragment create() {
        PurchaseDialogFragment dialog = new PurchaseDialogFragment();
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * Purchase dialog
     */
    public static class PurchaseDialogFragment extends DialogFragment {

        private PurchaseDialogListener mListener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());

            builder.setMessageBig(R.string.purchaseDlg_message_big)
                   .setMessageSmall(R.string.purchaseDlg_message_small)
                   .setLeftButton(getActivity().getString(R.string.purchaseDlg_no), new OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dismiss();
                       }
                   })
                   .setRightButton(getActivity().getString(R.string.purchaseDlg_yes), new OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           mListener.onPurchaseBtnClicked();
                           dismiss();
                       }
                   });

            return builder.create();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (PurchaseDialogListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement " + PurchaseDialogListener.class.getSimpleName());
            }
        }

        /**
         * Interface to listen dialog events.
         */
        public static interface PurchaseDialogListener {
            public void onPurchaseBtnClicked();
        }
    }
}
