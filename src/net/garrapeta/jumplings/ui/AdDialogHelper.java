package net.garrapeta.jumplings.ui;

import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobclix.android.sdk.MobclixAdView;
import com.mobclix.android.sdk.MobclixAdViewListener;
import com.mobclix.android.sdk.MobclixIABRectangleMAdView;

/**
 * Helper class to manage the ad dialog
 */
public class AdDialogHelper implements MobclixAdViewListener {

    private FragmentActivity mActivity;

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
     */
    public AdDialogHelper(FragmentActivity activity) {
        mActivity = activity;

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
            adDialogFragment.show(mActivity.getSupportFragmentManager(), "ad");
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
        private static final String NEGATIVE_BUTTON_STR = "Play";
        
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
                            mContinueBtn.setText(NEGATIVE_BUTTON_STR);
                            mContinueBtn.setEnabled(true);
                        } else {
                            mContinueBtn.setText(NEGATIVE_BUTTON_STR + " (" + n + ")");
                            continueButtonStep(1000, --n);
                        }
                    }
                    
                    return true;
                }
            });
        }
 
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.dialog_ad, null);

            ViewGroup adFrame = (ViewGroup) view.findViewById(R.id.adddialog_advertising_rectangle_frame);
            adFrame.addView(mClient.getAdDialogFactory().mAdView);

            setCancelable(false);
            getDialog().getWindow().requestFeature(STYLE_NO_TITLE);

            mContinueBtn = (Button) view.findViewById(R.id.addialog_continue);
            mContinueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                    mClient.getAdDialogFactory().mAdView.getAd();
                }
            });

            Button buyButton = (Button) view.findViewById(R.id.addialog_buy);
            buyButton.setVisibility(JumplingsApplication.MOBCLIX_BUY_DIALOG_BUTTON_ENABLED ? View.VISIBLE : View.GONE);
 
            return view;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mClient = (AdDialogListener) activity;
                mClient.onAdDialogShown();
                continueButtonStep(0, NEGATIVE_BUTTON_DISABLED_TIME);
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
