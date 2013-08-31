package com.garrapeta.jumplings.ui;

import com.garrapeta.jumplings.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class JumplingsToast {

	public final static int LENGTH_SHORT = Toast.LENGTH_SHORT;
	public final static int LENGTH_LONG = Toast.LENGTH_LONG;
	
	public static void show(Activity activity, String text, int duration) {
		Toast toast = createToast(activity, duration);
		((TextView)toast.getView().findViewById(R.id.toast_text)).setText(text);
		toast.show();
	}
	
	public static void show(Activity activity, int textResId, int duration) {
		Toast toast = createToast(activity, duration);
		((TextView)toast.getView().findViewById(R.id.toast_text)).setText(textResId);
		toast.show();
	}
	
	private static Toast createToast(Activity activity, int duration) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) activity.findViewById(R.id.toast_layout_root));
		
		Toast toast = new Toast(activity);
		toast.setDuration(duration);
		toast.setView(layout);
		return toast;
	}
}
