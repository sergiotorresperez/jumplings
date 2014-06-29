package com.garrapeta.jumplings.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.garrapeta.gameengine.utils.L;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.billing.util.IabHelper;
import com.garrapeta.jumplings.billing.util.IabHelper.OnIabPurchaseFinishedListener;
import com.garrapeta.jumplings.billing.util.IabHelper.QueryInventoryFinishedListener;
import com.garrapeta.jumplings.billing.util.IabResult;
import com.garrapeta.jumplings.billing.util.Inventory;
import com.garrapeta.jumplings.billing.util.Purchase;

/**
 * Helper to make in app billing purchases. </p> Adapted from the TrivialDrive
 * example by Google
 * 
 */
public class InAppPurchaseHelper {

    // Debug tag, for logging
    private static final String TAG = InAppPurchaseHelper.class.getSimpleName();

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 20002;

    // The helper object
    private IabHelper mHelper;

    private boolean mIsSetUp = false;

    private final String mNoAdsSKU;

    /**
     * Constructor
     * 
     * @param context
     */
    public InAppPurchaseHelper(Context context) {
        // Create the helper, passing it our context and the public key to
        // verify signatures with
        if (L.sEnabled)
            Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(context, getAppIdKey(context));
        mNoAdsSKU = context.getString(R.string.inapp_sku_noads);
    }

    /**
     * Queries if the user purchased the in-app billing item
     * 
     * @param context
     * @param listener
     */
    public void queryIsPremiumPurchasedAsync(final Context context, final PurchaseStateQueryCallback listener) {
        if (!mIsSetUp) {
            if (L.sEnabled)
                Log.d(TAG, "Starting setup.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (L.sEnabled)
                        Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        if (listener != null) {
                            listener.onPurchaseStateQueryError("Error setting up the billing helper");
                        }
                        return;
                    }
                    mIsSetUp = true;
                    queryIsPremiumPurchasedAsync(context, listener);
                }
            });
            return;
        }

        if (L.sEnabled)
            Log.i(TAG, "Querying inventory.");
        mHelper.queryInventoryAsync(new QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (!result.isSuccess()) {
                    if (L.sEnabled)
                        Log.e(TAG, "Querying inventory resulted in error: " + result.getMessage());
                    if (listener != null) {
                        listener.onPurchaseStateQueryError("Error querying user inventory: " + result.getMessage());
                    }
                } else {
                    boolean isPurchased = false;
                    Purchase purchase = inv.getPurchase(mNoAdsSKU);
                    if (purchase != null) {
                        // 0 means purchased
                        isPurchased = (purchase.getPurchaseState() == 0);
                    }
                    if (L.sEnabled)
                        Log.i(TAG, "Querying inventory finished. Is purchased: " + isPurchased);
                    PermData.setPremiumPurchased(context, isPurchased);
                    if (listener != null) {
                        listener.onPurchaseStateQueryFinished(isPurchased);
                    }
                }
            }
        });
    }

    /**
     * Starts the process of purchasing the in-app billing item
     * 
     * @param activity
     * @param listener
     */
    public void purchasePremiumAsync(final Activity activity, final PurchaseCallback listener) {
        if (!mIsSetUp) {
            if (L.sEnabled)
                Log.d(TAG, "Starting setup.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (L.sEnabled)
                        Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        if (listener != null) {
                            listener.onPurchaseError("Error setting up the billing helper");
                        }
                        return;
                    }
                    mIsSetUp = true;
                    purchasePremiumAsync(activity, listener);
                }
            });
            return;
        }

        if (L.sEnabled)
            Log.i(TAG, "Launching purchase process");
        mHelper.launchPurchaseFlow(activity, mNoAdsSKU, RC_REQUEST, new OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (!result.isSuccess()) {
                    if (L.sEnabled)
                        Log.e(TAG, "Purchase process finished with error: " + result.getMessage());
                    if (listener != null) {
                        listener.onPurchaseError("Error purchasing item: " + result.getMessage());
                    }
                } else {
                    boolean isPurchased = false;
                    if (info != null) {
                        // 0 means purchased
                        isPurchased = (info.getPurchaseState() == 0);
                    }
                    if (L.sEnabled)
                        Log.i(TAG, "Purchase process finished. Is purchased: " + isPurchased);
                    PermData.setPremiumPurchased(activity, isPurchased);
                    if (listener != null) {
                        listener.onPurchaseFinished(isPurchased);
                    }
                }
            }
        });
    }

    /**
     * To be called from the Activity that uses the helper
     * 
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (L.sEnabled)
            Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            return false;
        } else {
            if (L.sEnabled)
                Log.d(TAG, "onActivityResult handled by IABUtil.");
            return true;
        }
    }

    /**
     * Frees resources
     */
    public void dispose() {
        // very important:
        if (L.sEnabled)
            Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    private String getAppIdKey(Context context) {
        /*
         * base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY (that
         * you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         * 
         * Instead of just storing the entire literal string here embedded in
         * the program, construct the key at runtime from pieces or use bit
         * manipulation (for example, XOR with some other string) to hide the
         * actual key. The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with
         * one of their own and then fake messages from the server.
         */
        // TODO: obfuscate
        return context.getString(R.string.google_play_base64_rsa_app_id);
    }

    /**
     * Callback to listen to the outcome of a purchase state query
     */
    public interface PurchaseStateQueryCallback {
        public void onPurchaseStateQueryFinished(boolean purchased);

        public void onPurchaseStateQueryError(String message);
    }

    /**
     * Callback to listen to the outcome of a purchase
     */
    public interface PurchaseCallback {
        public void onPurchaseFinished(boolean purchased);

        public void onPurchaseError(String message);
    }

}
