package com.far.nowaste.Other;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import com.far.nowaste.MainActivity;
import com.far.nowaste.R;

public class SearchToolbarAnimation {

    Window window;
    Toolbar mToolbar;
    MenuItem mSearchItem;
    Context context;
    Resources resources;


    // variabili per la night mode
    int nightMode;
    SharedPreferences sharedPreferences;

    public SearchToolbarAnimation(Window window, Toolbar mToolbar, MenuItem mSearchItem, Context context, Resources resources, SharedPreferences sharedPreferences){
        this.window = window;
        this.mToolbar = mToolbar;
        this.mSearchItem = mSearchItem;
        this.context = context;
        this.resources = resources;
        this.sharedPreferences = sharedPreferences;
        nightMode = sharedPreferences.getInt("NightModeInt", 1);
    }

    public void setAnimation(){
        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Called when SearchView is collapsing
                if (mSearchItem.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Called when SearchView is expanding
                animateSearchToolbar(1, true, true);
                return true;
            }
        });
    }

    // animazioni searchView
    private void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        nightMode = sharedPreferences.getInt("NightModeInt", 1);
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES){
            mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.toolbar_dark));
            mToolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.toolbar_dark));
        } else if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            mToolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.search_background));
            mToolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.search_background));
        }
        Animator createCircularReveal;
        int width = mToolbar.getWidth() - (containsOverflow ? resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) - ((resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
        if (show) {
            createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar, isRtl(resources) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, 0.0f, (float) width);
            createCircularReveal.setDuration(250);
        } else {
            createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar, isRtl(resources) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, (float) width, 0.0f);
            createCircularReveal.setDuration(250);
            createCircularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (nightMode == AppCompatDelegate.MODE_NIGHT_YES){
                        mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.toolbar_dark));
                    } else if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
                        mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.basil));
                    } else {
                        mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                    }
                    mToolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.white));
                }
            });
        }
        createCircularReveal.start();
    }

    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
}
