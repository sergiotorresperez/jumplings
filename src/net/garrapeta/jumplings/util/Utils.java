package net.garrapeta.jumplings.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility methods
 */
public class Utils {
	
	
	/**
	 * @return if the device is connected to a network
	 */
	public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager  = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

	/**
	 * Computes the MD5 hash of one string
	 * {@link http://stackoverflow.com/questions/4846484/md5-or-other-hashing-in-android}
	 * 
	 * @param string
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static final String md5(final String string) throws NoSuchAlgorithmException {
        // Create MD5 Hash
        MessageDigest digest = java.security.MessageDigest .getInstance("MD5");
        digest.update(string.getBytes());
        byte messageDigest[] = digest.digest();

        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            String h = Integer.toHexString(0xFF & messageDigest[i]);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }
        return hexString.toString();
	}

}
