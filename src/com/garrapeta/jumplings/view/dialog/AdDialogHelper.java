package com.garrapeta.jumplings.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.util.AdsHelper;
import com.garrapeta.jumplings.util.FlurryHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * Helper class to manage the ad dialog
 */
public class AdDialogHelper extends AdListener {

    private FragmentActivity mActivity;

    private final String mFragmentTag;

    private boolean mAdLoaded = false;

    private AdDialogFragment mAdDialogFragment;
    /**
     * Ad view. This has to be created before it is displayed, as we need to
     * instantiate it to request an ad. When the dialog is show this view it
     * attached to it.
     */
    private AdView mAdView;

    /**
     * Constructor
     * 
     * @param activity
     * @param fragmentTag
     */
    public AdDialogHelper(FragmentActivity activity, String fragmentTag) {
        mActivity = activity;
        mFragmentTag = fragmentTag;

        mAdDialogFragment = new AdDialogFragment();
        mAdDialogFragment.setCancelable(false);

        // Create the adView
        mAdView = new AdView(activity);
        mAdView.setAdUnitId(activity.getString(R.string.admob_in_game_ad_unit));
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdListener(this);

        if (AdsHelper.shoulShowAds(activity)) {
            AdsHelper.requestAd(mAdView);
        }
    }

    /**
     * Shows the dialog if there is an ad available
     * 
     * @return if the dialog has been shown.
     */
    public boolean showIfAvailable() {
        if (mAdLoaded) {
            mAdDialogFragment.show(mActivity.getSupportFragmentManager(), mFragmentTag);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Interface that users of the helper must implement
     */
    public static interface AdDialogListener {
        public void onAdDialogShown();

        public void onAdDialogClosed();

        public AdDialogHelper getAdDialogFactory();

        public void onPurchaseBtnClicked();
    }

    /**
     * Ad dialog
     */
    public class AdDialogFragment extends DialogFragment {

        private int NEGATIVE_BUTTON_DISABLED_TIME = 5;

        private final static int MSG_CONTINUE_BTN_STEP = 0;

        private AdDialogListener mClient;

        private Button mContinueBtn;

        private final Handler mAdDialogHandler;

        /**
         * Constructor
         */
        public AdDialogFragment() {
            mAdDialogHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == MSG_CONTINUE_BTN_STEP) {
                        int n = msg.arg1;

                        if (n == 0) {
                            mContinueBtn.setText(getActivity().getString(R.string.game_ad_dlg_continue));
                            mContinueBtn.setEnabled(true);
                        } else {
                            mContinueBtn.setText(getActivity().getString(R.string.game_ad_dlg_continue_in_secs, n));
                            continueButtonStep(1000, --n);
                        }
                    }

                    return true;
                }
            });
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());

            builder.setBody(mClient.getAdDialogFactory().mAdView);

            builder.setRightButton(getActivity().getString(R.string.game_ad_dlg_continue), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            })
                   .setLeftButton(getActivity().getString(R.string.game_ad_dlg_buy), new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           getDialog().dismiss();
                           mClient.onPurchaseBtnClicked();
                       }
                   });

            Dialog dialog = builder.create();

            mContinueBtn = builder.getRightButton();
            mContinueBtn.setEnabled(false);
            continueButtonStep(0, NEGATIVE_BUTTON_DISABLED_TIME);

            return dialog;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            FlurryHelper.logAdDialogShown();
            try {
                mClient = (AdDialogListener) activity;
                mClient.onAdDialogShown();
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement " + AdDialogListener.class.getSimpleName());
            }
        }

        private void continueButtonStep(long delay, int n) {
            Message msg = mAdDialogHandler.obtainMessage(MSG_CONTINUE_BTN_STEP);
            msg.arg1 = n;
            mAdDialogHandler.sendMessageDelayed(msg, delay);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mAdLoaded = false;
            AdsHelper.requestAd(mAdView);
            ((ViewGroup) mAdView.getParent()).removeView(mAdView);
            mClient.onAdDialogClosed();
        }
    }

    @Override
    public void onAdLoaded() {
        mAdLoaded = true;
    };

    @Override
    public void onAdFailedToLoad(int errorcode) {
        mAdLoaded = false;
    };

}
