package net.garrapeta.jumplings;

import java.util.HashMap;
import java.util.Map;

import net.garrapeta.jumplings.actor.BladePowerUpActor;
import net.garrapeta.jumplings.actor.BombActor;
import net.garrapeta.jumplings.actor.DoubleEnemyActor;
import net.garrapeta.jumplings.actor.EnemyActor;
import net.garrapeta.jumplings.actor.LifePowerUpActor;
import net.garrapeta.jumplings.actor.RoundEnemyActor;
import net.garrapeta.jumplings.actor.SplitterEnemyActor;
import net.garrapeta.jumplings.ui.CustomDialogBuilder;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Tutorial that shows tips to help the player learn the different features of the game *
 */
public class Tutorial implements GameEventsListener {
   
    private static final String MESSAGE_ID_KEY = "messageKey" ;

    /**
     * Enum with each different feature / event to
     */
    public enum TipId {
        TIP_ON_ENEMY_SCAPED, 
        TIP_ON_COMBO, 
        TIP_ON_ROUND_ENEMY_KILLED, 
        TIP_ON_DOUBLE_ENEMY_KILLED, 
        TIP_ON_SPLITTER_ENEMY_KILLED, 
        TIP_ON_BOMB_EXPLODED, 
        TIP_ON_LIFE_POWER_UP, 
        TIP_ON_BLADE_POWER_UP_START, 
        TIP_ON_BLADE_POWER_UP_END
    }

    private final FragmentActivity mActivity;
    private final String mFragmentTag;
    private final Handler mTipHandler;
    private Map<TipId, TipData> mTipData;

    /**
     * @param activity
     * @param fragmentTag
     */
    public Tutorial(FragmentActivity activity, String fragmentTag) {
        mActivity = activity;
        mFragmentTag = fragmentTag;
        
        mTipData = new HashMap<TipId, TipData>();
    
        mTipHandler = new Handler(new Handler.Callback() {            
            @Override
            public boolean handleMessage(Message msg) {
                TipData tipData = (TipData) msg.obj;
                DialogFragment tipDialogFragment = new TipDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(MESSAGE_ID_KEY, tipData.mMessageResId);
                tipDialogFragment.setArguments(bundle);
                tipDialogFragment.show(mActivity.getSupportFragmentManager(),  mFragmentTag);
                return true;
            }
        });
    }

    /**
     * Initialises the state of the tips.
     * Should be run in background.
     */
    public void init() {
        PermData permData = PermData.getInstance();
        mTipData.put(TipId.TIP_ON_ENEMY_SCAPED, new TipData(R.string.tip_enemy_scaped, permData.isTipShown(TipId.TIP_ON_ENEMY_SCAPED)));
        mTipData.put(TipId.TIP_ON_COMBO, new TipData(R.string.tip_combo, permData.isTipShown(TipId.TIP_ON_COMBO)));
        mTipData.put(TipId.TIP_ON_ROUND_ENEMY_KILLED, new TipData(R.string.tip_round_enemy_killed, permData.isTipShown(TipId.TIP_ON_ROUND_ENEMY_KILLED)));
        mTipData.put(TipId.TIP_ON_DOUBLE_ENEMY_KILLED, new TipData(R.string.tip_double_enemy_killed, permData.isTipShown(TipId.TIP_ON_DOUBLE_ENEMY_KILLED)));
        mTipData.put(TipId.TIP_ON_SPLITTER_ENEMY_KILLED, new TipData(R.string.tip_splitter_enemy_killed, permData.isTipShown(TipId.TIP_ON_SPLITTER_ENEMY_KILLED)));
        mTipData.put(TipId.TIP_ON_BOMB_EXPLODED, new TipData(R.string.tip_bomb_exploded, permData.isTipShown(TipId.TIP_ON_BOMB_EXPLODED)));
        mTipData.put(TipId.TIP_ON_LIFE_POWER_UP, new TipData(R.string.tip_life_power_up, permData.isTipShown(TipId.TIP_ON_LIFE_POWER_UP)));
        mTipData.put(TipId.TIP_ON_BLADE_POWER_UP_START, new TipData(R.string.tip_blade_power_up_start, permData.isTipShown(TipId.TIP_ON_BLADE_POWER_UP_START)));
        mTipData.put(TipId.TIP_ON_BLADE_POWER_UP_END, new TipData(R.string.tip_blade_power_up_end, permData.isTipShown(TipId.TIP_ON_BLADE_POWER_UP_END)));
    }

    @Override
    public boolean onEnemyScaped(EnemyActor enemy) {
        return showTip(TipId.TIP_ON_ENEMY_SCAPED);
    }

    @Override
    public boolean onGameOver() {
        return false;
    }

    @Override
    public boolean onCombo() {
        showTip(TipId.TIP_ON_COMBO);
        return false;
    }

    @Override
    public boolean onBombExploded(BombActor bomb) {
        return showTip(TipId.TIP_ON_BOMB_EXPLODED);
    }

    @Override
    public boolean onLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        showTip(TipId.TIP_ON_LIFE_POWER_UP);
        return false;
    }

    @Override
    public boolean onBladePowerUpStart(BladePowerUpActor bladePowerUpActor) {
        showTip(TipId.TIP_ON_BLADE_POWER_UP_START);
        return false;
    }

    @Override
    public boolean onBladePowerUpEnd() {
        showTip(TipId.TIP_ON_BLADE_POWER_UP_END);
        return false;
    }

    @Override
    public boolean onEnemyKilled(EnemyActor enemy) {
        switch (enemy.getCode()) {
        case RoundEnemyActor.JUMPER_CODE_SIMPLE:
            showTip(TipId.TIP_ON_ROUND_ENEMY_KILLED);
            break;
        case DoubleEnemyActor.JUMPER_CODE_DOUBLE:
            showTip(TipId.TIP_ON_DOUBLE_ENEMY_KILLED);
            break;
        case SplitterEnemyActor.JUMPER_CODE_SPLITTER_DOUBLE:
        case SplitterEnemyActor.JUMPER_CODE_SPLITTER_TRIPLE:
            showTip(TipId.TIP_ON_SPLITTER_ENEMY_KILLED);
            break;
        }
        return false;
    }

    /**
     * @param tipId
     * @return if the message will be shown
     */
    private boolean showTip(TipId tipId) {
        final TipData tipData = mTipData.get(tipId);
        if (!tipData.mShown) {
            PermData.getInstance().setTipShown(tipId);
            tipData.mShown = true;
            Message msg = mTipHandler.obtainMessage();
            msg.obj = tipData;
            mTipHandler.sendMessage(msg);
            return true;
        }
        return false;
    }

    /**
     * Data of a tip
     */
    private static class TipData {
        final int mMessageResId;
        boolean mShown;

        TipData(int messageResId, boolean shown) {
            mMessageResId = messageResId;
            mShown = shown;
        }
    }

    /**
     * Tips dialog
     */
    public static class TipDialogFragment extends DialogFragment {
        
        private TipDialogListener mListener;
        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            
            CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());

            builder.setMessageSmall(getArguments().getInt(MESSAGE_ID_KEY)).setLeftButton("OK", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTipDialogClosed();
                    dismiss();
                }
            });

            
            
            return builder.create();
        }
        
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (TipDialogListener) activity;
                mListener.onTipDialogShown();
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()  + " must implement " + TipDialogListener.class.getSimpleName());
            }
        }
        
        @Override
        public void onDetach() {
            super.onDetach();
            mListener.onTipDialogClosed();
        }

        /**
         * Interface to listen dialog events.
         */
        public static interface TipDialogListener {
            public void onTipDialogShown();
            public void onTipDialogClosed();
        }
    }
}
