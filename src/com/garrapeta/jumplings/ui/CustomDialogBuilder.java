package com.garrapeta.jumplings.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.garrapeta.jumplings.R;

/**
 * Helper class to create dialog with a customised appearance
 */
public class CustomDialogBuilder {

    private final Activity mActivity;

    private int mMessageBigResId = Integer.MIN_VALUE;

    private int mMessageSmallResId = Integer.MIN_VALUE;

    private View mBody = null;

    private boolean mLeftButtonSet = false;
    private String mLeftBtnText = null;
    private OnClickListener mLeftBtnOnClickListener = null;

    private boolean mRightButtonSet = false;
    private String mRightBtnText = null;
    private OnClickListener mRightBtnOnClickListener = null;

    private Button mLeftButton = null;
    private Button mRightButton = null;

    /**
     * Constructor
     * 
     * @param activity
     */
    public CustomDialogBuilder(Activity activity) {
        mActivity = activity;
    }

    /**
     * Sets the resource id of the big message in the dialog
     * 
     * @param resId
     * @return
     */
    public CustomDialogBuilder setMessageBig(int resId) {
        mMessageBigResId = resId;
        return this;
    }

    /**
     * Sets the resource id of the small message in the dialog
     * 
     * @param resId
     * @return
     */
    public CustomDialogBuilder setMessageSmall(int resId) {
        mMessageSmallResId = resId;
        return this;
    }

    /**
     * Sets the body of the dialog
     * 
     * @param resId
     * @return
     */
    public void setBody(View view) {
        mBody = view;
    }

    /**
     * Sets the text and listener of the left button of the dialog
     * 
     * @param text
     * @param onClickListener
     * @return
     */
    public CustomDialogBuilder setLeftButton(String text, OnClickListener onClickListener) {
        mLeftButtonSet = true;
        mLeftBtnText = text;
        mLeftBtnOnClickListener = onClickListener;
        return this;
    }

    /**
     * Sets the text and listener of the right button of the dialog
     * 
     * @param text
     * @param onClickListener
     * @return
     */
    public CustomDialogBuilder setRightButton(String text, OnClickListener onClickListener) {
        mRightButtonSet = true;
        mRightBtnText = text;
        mRightBtnOnClickListener = onClickListener;
        return this;
    }

    /**
     * @return creates the dialog
     */
    public Dialog create() {
        Dialog dialog = new Dialog(mActivity, R.style.CustomDialog);
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_custom, null);
        dialog.setContentView(view);

        if (mMessageBigResId != Integer.MIN_VALUE) {
            TextView textView = (TextView) view.findViewById(R.id.dialog_message_big);
            textView.setVisibility(View.VISIBLE);
            textView.setText(mMessageBigResId);
        }

        if (mMessageSmallResId != Integer.MIN_VALUE) {
            TextView textView = (TextView) view.findViewById(R.id.dialog_message_small);
            textView.setVisibility(View.VISIBLE);
            textView.setText(mMessageSmallResId);
        }

        if (mBody != null) {
            ViewGroup body = (ViewGroup) view.findViewById(R.id.dialog_body_frame);
            body.setVisibility(View.VISIBLE);
            body.addView(mBody);
        }

        mLeftButton = (Button) view.findViewById(R.id.dialog_left_btn);
        if (mLeftButtonSet) {
            mLeftButton.setVisibility(View.VISIBLE);
            mLeftButton.setText(mLeftBtnText);
            mLeftButton.setOnClickListener(mLeftBtnOnClickListener);
        }

        mRightButton = (Button) view.findViewById(R.id.dialog_right_btn);
        if (mRightButtonSet) {
            mRightButton.setVisibility(View.VISIBLE);
            mRightButton.setText(mRightBtnText);
            mRightButton.setOnClickListener(mRightBtnOnClickListener);
        }

        int width = mActivity.getResources()
                             .getDimensionPixelSize(R.dimen.dialog_width);
        dialog.getWindow()
              .setLayout(width, LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    /**
     * Gets the left button. This method has to be called after
     * {@link CustomDialogBuilder#create()}
     * 
     * @return the left button
     */
    public Button getLeftButton() {
        return mLeftButton;
    }

    /**
     * Gets the right button. This method has to be called after
     * {@link CustomDialogBuilder#create()}
     * 
     * @return the right button
     */
    public Button getRightButton() {
        return mRightButton;
    }

}
