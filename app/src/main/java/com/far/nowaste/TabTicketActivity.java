package com.far.nowaste;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.far.nowaste.ui.main.SectionsPagerAdapter;

public class TabTicketActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;

    // view
    FloatingActionButton newTicketBtn;
    FloatingActionButton checkBtn;
    FloatingActionButton errorBtn;

    // animazioni
    boolean isMenuOpen;
    OvershootInterpolator interpolator = new OvershootInterpolator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_ticket);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(viewPager);

        // toolbar
        mToolbar = findViewById(R.id.ticketsList_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // imposta l'animazione del floating button
        initFloatingMenu();

    }

    // ends this activity (back arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFloatingMenu() {
        // ClickListner del bottone
        newTicketBtn = findViewById(R.id.newTicket_addFloatingActionButton);
        checkBtn = findViewById(R.id.newTicket_checkFloatingActionButton);
        errorBtn = findViewById(R.id.newTicket_errorFloatingActionButton);

        checkBtn.setAlpha(0f);
        checkBtn.setTranslationY(100f);
        checkBtn.setScaleX(0.7f);
        checkBtn.setScaleY(0.7f);

        errorBtn.setAlpha(0f);
        errorBtn.setTranslationY(100f);
        errorBtn.setScaleX(0.7f);
        errorBtn.setScaleY(0.7f);

        Intent intentnewTick = new Intent(this, NewTicketActivity.class);
        Intent intentError = new Intent(this, ReportProblemActivity.class);

        newTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animeteFloatingMenu();
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentnewTick);
                animeteFloatingMenu();
            }
        });

        errorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentError);
                animeteFloatingMenu();
            }
        });
    }

    private void animeteFloatingMenu() {
        if(isMenuOpen){
            newTicketBtn.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
            checkBtn.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
            errorBtn.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

            Drawable defaulImage = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_create);
            newTicketBtn.setImageDrawable(defaulImage);

            isMenuOpen = false;
        }else {
            newTicketBtn.animate().setInterpolator(interpolator).rotation(90f).setDuration(300).start();
            checkBtn.animate().translationY(15f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
            errorBtn.animate().translationY(55f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();

            Drawable defaulImage = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_clear);
            newTicketBtn.setImageDrawable(defaulImage);

            isMenuOpen = true;
        }
    }
}