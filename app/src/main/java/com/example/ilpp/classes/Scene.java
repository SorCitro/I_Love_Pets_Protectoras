package com.example.ilpp.classes;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import com.example.ilpp.activities.panel.PanelActivity;

public class Scene {

    public static final Class<?> MainUserActivityClass = PanelActivity.class;
    public static final Class<?> MainUserShelterActivityClass = PanelActivity.class;

    public static void go(Activity fromActivity, Class<?> toActivity) {
        Intent intent = new Intent(fromActivity, toActivity);
        fromActivity.startActivity(intent);
    }

    public static void goAndFinish(Activity fromActivity, Class<?> toActivity) {
        go(fromActivity, toActivity);
        fromActivity.finish();
    }

    public static Boolean goWithTransition(Activity fromActivity, Class<?> toActivity, View[] views) {

        Bundle bundle = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String>[] pairs = new Pair[views.length];

            for (int i = 0; i < views.length; i++) {
                pairs[i] = Pair.create(views[i], views[i].getTransitionName());
            }

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(fromActivity, pairs);
            bundle = options.toBundle();
        }

        Intent intent = new Intent(fromActivity, toActivity);

        if (bundle != null) {
            fromActivity.startActivity(intent, bundle);
            return true;
        } else {
            fromActivity.startActivity(intent);
            return false;
        }
    }

    public static void goWithTransitionAndFinish(Activity fromActivity, Class<?> toActivity, View[] views) {
        if (goWithTransition(fromActivity, toActivity, views))
            fromActivity.finishAfterTransition();
        else
            fromActivity.finish();
    }

}
