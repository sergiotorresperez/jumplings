package net.garrapeta.jumplings.facebook;

import net.garrapeta.jumplings.JumplingsApplication;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

/**
 * Class for handling the authentication process with Facebook
 * 
 * TODO: code delegates methods for other features, such as logout, etc
 * TODO: allow different permissions
 */
public class FacebookAuthenticator {

    private static String TAG = FacebookAuthenticator.class.getSimpleName();
    private static FacebookAuthenticator sInstance;

    private boolean mIsInitted = false;
    private Facebook mFacebook;
    private Activity mActivity;
    private AsyncFacebookRunner mRunner;
    private String mAppId;

    /**
     * Protected constructor
     */
    private FacebookAuthenticator() {

    }

    public static FacebookAuthenticator getInstance() {
        if (sInstance == null) {
            sInstance = new FacebookAuthenticator();
        }
        return sInstance;
    }

    /**
     * Performs the initialisation of the authenticator
     * 
     * @param activity
     * @param facebokAppID
     */
    public void init(Activity activity, String facebokAppID) {
        mAppId = facebokAppID;
        Log.i(TAG, "Initting with app id:" + mAppId);
        mActivity = activity;
        mFacebook = new Facebook(facebokAppID);
        mRunner =  new AsyncFacebookRunner(mFacebook);
        mIsInitted = true;
    }

    /**
     * To be called from the {@link Activity#onActivityResult()} of the host Activity
     * 
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       checkInitted();
       mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    /**
     * @return if the session in valed (user is authenticated)
     */
    public boolean isSessionValid() {
        checkInitted();
        return mFacebook.isSessionValid();
    }

    /**
     * Tries to log in in Facebook
     * @param listener
     */
    public void login(DialogListener listener) {
        Log.i(TAG, "Trying to recover Facebook access token from perm data");
        checkInitted();

        // Get existing access_token if any
        String access_token = getFacebookAccessToken();
        long expires = getFacebookAccessExpires();

        if (access_token != null) {
            mFacebook.setAccessToken(access_token);
        }
        if (expires != 0) {
            mFacebook.setAccessExpires(expires);
        }

        // Only call authorize if the access_token has expired.
        if (!mFacebook.isSessionValid()) {

            Log.i(JumplingsApplication.LOG_SRC, "Remotely logging to Facebook");

            mFacebook.authorize(mActivity, new String[] { "publish_stream" }, wrap(listener));
        } else {
            Log.i(JumplingsApplication.LOG_SRC, "Facebook access token found in perm data");
            listener.onComplete(null);
        }
    }


    /**
     * Perform a request
     * 
     * @param graphPath
     * @param parameters
     * @param httpMethod
     * @param listener
     * @param state
     */
    public void request(String graphPath, Bundle parameters, String httpMethod, RequestListener listener, Object state) {
        mRunner.request(graphPath, parameters, httpMethod, listener, state);
    }

    private void checkInitted() {
        if (!mIsInitted) {
            throw new IllegalStateException(FacebookAuthenticator.class.getCanonicalName() + " is not initted");
        }
    }

    private DialogListener wrap(final DialogListener listener) {
        return new DialogListener() {
            @Override
            public void onComplete(Bundle values) {
                Log.i(JumplingsApplication.LOG_SRC, "Remotely logged to facebook");
                saveFacebookAccessToken(mFacebook.getAccessToken());
                saveFacebookAccessExpires(mFacebook.getAccessExpires());
                listener.onComplete(values);
            }

            @Override
            public void onFacebookError(FacebookError fe) {
                Log.e(JumplingsApplication.LOG_SRC, "Error when login to Facebook: " + fe.toString());
                fe.printStackTrace();
                listener.onFacebookError(fe);
            }

            @Override
            public void onError(DialogError de) {
                Log.e(JumplingsApplication.LOG_SRC, "Error when login to Facebook: " + de.toString());
                de.printStackTrace();
                listener.onError(de);
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }
        };
    }

    private String getFacebookAccessToken() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        return sharedPref.getString(getAccessTokenKey(), null);
    }

    private void saveFacebookAccessToken(String accesToken) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        Editor editor = sharedPref.edit();
        editor.putString(getAccessTokenKey(), accesToken);
        editor.commit();
    }

    private void saveFacebookAccessExpires(long expires) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);

        Editor editor = sharedPref.edit();
        editor.putLong(getAccessTokenExpiresKey(), expires);
        editor.commit();
    }

    private long getFacebookAccessExpires() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        return sharedPref.getLong(getAccessTokenExpiresKey(), 0);
    }
    
    private String getAccessTokenExpiresKey() {
        checkInitted();
        return "FB_TOKEN_" + mAppId;
    }

    private String getAccessTokenKey() {
        checkInitted();
        return "FB_EXPIRES_" + mAppId;
    }
}
