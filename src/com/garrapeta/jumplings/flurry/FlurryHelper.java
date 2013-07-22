package com.garrapeta.jumplings.flurry;

import android.content.Context;
import android.text.TextUtils;

import com.flurry.android.FlurryAgent;

/**
 * Helper to integrate Flurry
 */
public class FlurryHelper {
	
    private static boolean sInitialized;
    private static String sApiKey;
    private static boolean sIsEnabled;
    private final static Object sLock = new Object();
    
    /**
     * Initializes the module. Cano be called only once.
     * @param enabled
     * @param apiKey
     */
    public static void initialize(final boolean enabled, final String apiKey, final boolean flurryLogEnabled) {
    	synchronized (sLock) {
	    	if (sInitialized) {
	    		throw new IllegalStateException("Was already initialized");
	    	}
	    	sInitialized = true;
	    	sIsEnabled = enabled;
	    	sApiKey = apiKey;
	    	FlurryAgent.setLogEnabled(flurryLogEnabled);
    	}
    }
    
    private static void checkInitialized() throws IllegalStateException {
    	synchronized (sLock) {
	    	if (!sInitialized) {
	    		throw new IllegalStateException("Flurry helper is not initialized");
	    	}
	    	if (TextUtils.isEmpty(sApiKey)) {
	    		throw new IllegalStateException("API key is not specified");
	    	}
    	}
    }
    
    /**
     * Should be called in onStart of every Activity
     * @param context
     */
    public static void onStartSession(Context context) {
    	checkInitialized();
    	if (sIsEnabled) {
    		FlurryAgent.onStartSession(context, sApiKey);
    	}	
    }
    
    /**
     * Should be called in onStop of every Activity
     * @param context
     */
    public static void onEndSession(Context context) {
    	checkInitialized();
    	if (sIsEnabled) {
    		FlurryAgent.onEndSession(context);
    	}	
    }

}
