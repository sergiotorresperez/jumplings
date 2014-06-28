package com.garrapeta.jumplings.util;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.garrapeta.jumplings.PermData;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;

public class AdsHelper {

    public static boolean shoulShowAds(Context context) {
        return Build.VERSION.SDK_INT >= 9 && PermData.areAdsEnabled(context)
                && (!PermData.isPremiumPurchaseStateKnown(context) || !PermData.isPremiumPurchased(context));
    }

    public static void requestAd(AdView adView) {
        Bundle bundle = new Bundle();
        AdMobExtras extras = new AdMobExtras(bundle);
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtras(extras)
                                                     .build();

        adView.loadAd(adRequest);
    }

}
