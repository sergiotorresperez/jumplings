package com.garrapeta.jumplings.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Utility methods
 */
public class Utils {

	/**
	 * @return if the device is connected to a network
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	/**
	 * Launches an Intent to share the message
	 * 
	 * @param context
	 * @param message
	 */
	public static void share(Context context, String message) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, message);
		sendIntent.setType("text/plain");
		context.startActivity(sendIntent);
	}

	/**
	 * Computes the MD5 hash of one string {@link http
	 * ://stackoverflow.com/questions/4846484/md5-or-other-hashing-in-android}
	 * 
	 * @param string
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static final String md5(final String string)
			throws NoSuchAlgorithmException {
		// Create MD5 Hash
		MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
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

	/**
	 * Return Pseudo Unique ID
	 * 
	 * {@link http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id}
	 * 
	 * @param context
	 *            Context
	 * @return ID
	 */
	public static String getUniquePseudoID(Context context) {
		// IF all else fails, if the user does is lower than API 9(lower
		// than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
		// returns 'null', then simply the ID returned will be soley based
		// off their Android device information. This is where the collisions
		// can happen.
		// Thanks http://www.pocketmagic.net/?p=1662!
		// Try not to use DISPLAY, HOST or ID - these items could change
		// If there are collisions, there will be overlapping data
		String m_szDevIDShort = "35" + (Build.BOARD.length() % 10)
				+ (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10)
				+ (Build.DEVICE.length() % 10)
				+ (Build.MANUFACTURER.length() % 10)
				+ (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

		// Thanks to @Roman SL!
		// http://stackoverflow.com/a/4789483/950427
		// Only devices with API >= 9 have android.os.Build.SERIAL
		// http://developer.android.com/reference/android/os/Build.html#SERIAL
		// If a user upgrades software or roots their phone, there will be a
		// duplicate entry
		String serial = null;
		try {
			serial = android.os.Build.class.getField("SERIAL").toString();

			// go ahead and return the serial for api => 9
			return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
					.toString();
		} catch (Exception ignored) {
			// String needs to be initialized
			serial = "serial"; // some value
		}

		// Thanks @Joe!
		// http://stackoverflow.com/a/2853253/950427
		// Finally, combine the values we have found by using the UUID class to
		// create a unique identifier
		return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
				.toString();
	}

}
