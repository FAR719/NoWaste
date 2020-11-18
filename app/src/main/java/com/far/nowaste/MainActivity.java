package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.SearchView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // toolbar
    Toolbar mToolbar;
    MenuItem mSearchItem;

    // navigationView
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //definizione variabili
    SearchView wasteSearchView;
    MaterialCardView seccoCardView;
    MaterialCardView plasticaCardView;
    MaterialCardView cartaCardView;
    MaterialCardView organicoCardView;
    MaterialCardView vetroCardView;
    MaterialCardView metalliCardView;
    MaterialCardView elettriciCardView;
    MaterialCardView specialiCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // navigationView
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.nav_view);

        // collega le view
        seccoCardView = (MaterialCardView) findViewById(R.id.seccoCardView);
        plasticaCardView = (MaterialCardView)findViewById(R.id.plasticaCardView);
        cartaCardView = (MaterialCardView)findViewById(R.id.cartaCardView);
        organicoCardView = (MaterialCardView)findViewById(R.id.organicoCardView);
        vetroCardView = (MaterialCardView)findViewById(R.id.vetroCardView);
        metalliCardView = (MaterialCardView)findViewById(R.id.metalliCardView);
        elettriciCardView = (MaterialCardView)findViewById(R.id.elettriciCardView);
        specialiCardView = (MaterialCardView)findViewById(R.id.specialiCardView);

        // navigationView
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.openNavDrawer, R.string.closeNavDrawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // definizione onClick secco
        seccoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "SECCO");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick plastica
        plasticaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "PLASTICA");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick carta
        cartaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "CARTA");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick organico
        organicoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "ORGANICO");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick vetro
        vetroCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "VETRO");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick metalli
        metalliCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "METALLI");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick elettrici
        elettriciCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "ELETTRICI");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick speciali
        specialiCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "SPECIALI");
                startActivity(cardDetailActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        mSearchItem = menu.findItem(R.id.m_search);

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

        //set querylistener
        MenuItem menuItem = menu.findItem(R.id.m_search);
        androidx.appcompat.widget.SearchView wasteSearchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(menuItem);

        wasteSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", query);
                startActivity(cardDetailActivity);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.quantum_grey_600));

        if (show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, 0.0f, (float) width);
                createCircularReveal.setDuration(250);
                createCircularReveal.start();
            } else {
                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) (-mToolbar.getHeight()), 0.0f);
                translateAnimation.setDuration(220);
                mToolbar.clearAnimation();
                mToolbar.startAnimation(translateAnimation);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, (float) width, 0.0f);
                createCircularReveal.setDuration(250);
                createCircularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mToolbar.setBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimary));
                        drawerLayout.setStatusBarBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimaryDark));
                    }
                });
                createCircularReveal.start();
            } else {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-mToolbar.getHeight()));
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(220);
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mToolbar.setBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimary));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mToolbar.startAnimation(animationSet);
            }
            drawerLayout.setStatusBarBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimaryDark));
        }
    }

    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}