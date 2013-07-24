package com.garrapeta.jumplings.ui;

import com.garrapeta.jumplings.JumplingsApplication;
import com.garrapeta.jumplings.flurry.FlurryHelper;

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
import android.widget.Toast;

import com.mobclix.android.sdk.MobclixAdView;
import com.mobclix.android.sdk.MobclixAdViewListener;
import com.mobclix.android.sdk.MobclixIABRectangleMAdView;

/**
 * Helper class to manage the ad dialog
 */
public class AdDialogHelper implements MobclixAdViewListener {

    private FragmentActivity mActivity;
    
    private final String mFragmentTag;

    private boolean mAvailable = false;

    /**
     * Ad view. This has to be created before it is displayed, as we need to instantiate it
     * to request an ad. When the dialog is show this view it attached to it.
     */
    private final MobclixAdView mAdView;

    /**
     * Constructor
     * 
     * @param activity
     * @param fragmentTag
     */
    public AdDialogHelper(FragmentActivity activity, String fragmentTag) {
        mActivity = activity;
        mFragmentTag = fragmentTag;

        mAdView = new MobclixIABRectangleMAdView(mActivity);
        mAdView.addMobclixAdViewListener(this);
        mAdView.setAllowAutoplay(false);
        mAdView.pause();
    }

    /**
     * Shows the dialog if there is an ad available
     * @return if the dialog has been shown.
     */
    public boolean showIfAvailable() {
        if (mAvailable) {
            AdDialogFragment adDialogFragment = new AdDialogFragment();
            adDialogFragment.setCancelable(false);
            adDialogFragment.show(mActivity.getSupportFragmentManager(), mFragmentTag);
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
    }

    /**
     * Ad dialog
     */
    public static class AdDialogFragment extends DialogFragment {

        private int NEGATIVE_BUTTON_DISABLED_TIME = 5;
        private static final String CONTINUE_BUTTON_STR = "Play";
        
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
                            mContinueBtn.setText(CONTINUE_BUTTON_STR);
                            mContinueBtn.setEnabled(true);
                        } else {
                            mContinueBtn.setText(CONTINUE_BUTTON_STR + " (" + n + ")");
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
            
            builder.setRightButton(CONTINUE_BUTTON_STR, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                    mClient.getAdDialogFactory().mAdView.getAd();
                }
            });
            
            if (JumplingsApplication.ADS_BUY_DIALOG_BUTTON_ENABLED ) {
                builder.setLeftButton("Get rid of ads", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "TODO", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            
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
            View adView = mClient.getAdDialogFactory().mAdView;
            ViewGroup adParent = (ViewGroup) adView.getParent();
            if (adParent != null) {
                adParent.removeView(adView);
            }

            mClient.onAdDialogClosed();
        }

    }

    @Override
    public void onSuccessfulLoad(MobclixAdView ad) {
        mAvailable = true;
    }

    @Override
    public void onFailedLoad(MobclixAdView ad, int error) {
        mAvailable = false;
    }

    @Override
    public String keywords() {
        return null;
    }

    @Override
    public void onAdClick(MobclixAdView arg0) {
    }

    @Override
    public void onCustomAdTouchThrough(MobclixAdView arg0, String arg1) {
    }

    @Override
    public boolean onOpenAllocationLoad(MobclixAdView arg0, int arg1) {
        return false;
    }

    @Override
    public String query() {
        return null;
    }

}
