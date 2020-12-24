package com.far.nowaste;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.far.nowaste.ui.main.SectionsPagerAdapter;

public class TabTicketActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;

    CoordinatorLayout layout;
    Typeface nunito;

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

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
        layout = findViewById(R.id.ticketsList_layout);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(viewPager);

        // toolbar
        mToolbar = findViewById(R.id.ticketsList_toolbar);
        setSupportActionBar(mToolbar);

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

        if(MainActivity.CURRENTUSER.isOperatore()){
            newTicketBtn.setVisibility(View.GONE);
            checkBtn.setVisibility(View.GONE);
            errorBtn.setVisibility(View.GONE);
        } else {
            newTicketBtn.setVisibility(View.VISIBLE);
            checkBtn.setVisibility(View.VISIBLE);
            errorBtn.setVisibility(View.VISIBLE);
        }

        newTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFloatingMenu();
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), NewTicketActivity.class), 1);
                animateFloatingMenu();
            }
        });

        errorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), NewReportActivity.class), 2);
                animateFloatingMenu();
            }
        });
    }

    private void animateFloatingMenu() {
        if(isMenuOpen){
            newTicketBtn.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
            checkBtn.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
            errorBtn.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

            Drawable defaulImage = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_create);
            newTicketBtn.setImageDrawable(defaulImage);

            isMenuOpen = false;
        } else {
            newTicketBtn.animate().setInterpolator(interpolator).rotation(90f).setDuration(300).start();
            checkBtn.animate().translationY(15f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
            errorBtn.animate().translationY(55f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();

            Drawable defaulImage = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_clear);
            newTicketBtn.setImageDrawable(defaulImage);

            isMenuOpen = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            boolean ticketSent = data.getBooleanExtra("com.far.nowaste.NEW_TICKET_REQUEST", false);
            if (ticketSent) {
                showSnackbar("Ticket inviato!");
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            boolean reportSent = data.getBooleanExtra("com.far.nowaste.NEW_REPORT_REQUEST", false);
            if (reportSent) {
                showSnackbar("Report inviato!");
            }
        }
    }

    public void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }

    public void hideFAB(){
        if (!MainActivity.CURRENTUSER.isOperatore()) {
            newTicketBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void showFAB() {
        if (!MainActivity.CURRENTUSER.isOperatore()){
            newTicketBtn.setVisibility(View.VISIBLE);
        }
    }
}