package com.adamnickle.lockaway;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Adam on 8/8/2015.
 */
public abstract class FragmentHelper
{
    private FragmentHelper() { }

    public static void transition( FragmentManager fragmentManager, @IdRes int container, Fragment newFragment )
    {
        fragmentManager
                .beginTransaction()
                .addToBackStack( newFragment.getClass().getName() )
                .setCustomAnimations(
                        R.anim.fragment_slide_enter,
                        R.anim.fragment_slide_exit,
                        R.anim.fragment_slide_enter,
                        R.anim.fragment_slide_exit )
                .replace( container, newFragment )
                .commit();
    }
}
