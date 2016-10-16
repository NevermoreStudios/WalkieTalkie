package com.nevermore.walkietalkie;

import android.app.Activity;

/**
 * Every program needs an Util class
 * ~ Somebody, somewhere, probably
 *
 * Util more like Tranzit
 * ~ Pera Pisar
 */
public class Util {

    private static Activity activity;

    public static Activity getActivity() {
        return activity;
    }

    public static void setActivity(Activity activity) {
        Util.activity = activity;
    }

}
