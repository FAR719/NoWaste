package com.far.nowaste;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class SearchToolbarAnimation {

    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    MenuItem mSearchItem;
    Context context;
    Resources resources;

    public SearchToolbarAnimation(DrawerLayout mDrawerLayout, Toolbar mToolbar, MenuItem mSearchItem, Context context, Resources resources){
        this.mDrawerLayout = mDrawerLayout;
        this.mToolbar = mToolbar;
        this.mSearchItem = mSearchItem;
        this.context = context;
        this.resources = resources;
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

        mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.search_background));
        mToolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.search_background));
        // mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(context, R.color.search_background));
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
                    mToolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                    mToolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.white));
                    // mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                }
            });
        }
        createCircularReveal.start();
    }

    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
}
