package com.garrapeta.jumplings.util;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;

public class AdMobHelper {

    public static void requestAd(AdView adView) {
        Bundle bundle = new Bundle();
        AdMobExtras extras = new AdMobExtras(bundle);
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtras(extras)
                                                     .build();

        adView.loadAd(adRequest);
    }

}
