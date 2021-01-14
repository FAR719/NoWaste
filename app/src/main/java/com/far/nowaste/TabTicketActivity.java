package com.far.nowaste;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.far.nowaste.ui.main.SectionsPagerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TabTicketActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    TabLayout tabLayout;

    ActionMode mActionMode;
    ConstraintLayout selectedLayout;
    String identificativo;

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
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

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

        if(MainActivity.CURRENT_USER.isOperatore()){
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

    public void updateCloseMenu(ConstraintLayout itemLayout, String identificativo){
        if (mActionMode == null) {
            selectedLayout = itemLayout;
            this.identificativo = identificativo;

            // cambia lo sfondo della statusbar dopo 190 millisecondi
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.material_grey_900));
                }
            }, 190);

            // attiva la contextual action bar
            mActionMode = TabTicketActivity.this.startActionMode(mActionModeCallback);
            selectedLayout.setBackgroundColor(ContextCompat.getColor(TabTicketActivity.this, R.color.ticket_item_background_selected));
        } else if (selectedLayout == itemLayout) {
            mActionMode.finish();
        } else {
            TypedValue outValue = new TypedValue();
            TabTicketActivity.this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            selectedLayout.setBackgroundResource(outValue.resourceId);
            selectedLayout = itemLayout;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedLayout.setBackgroundColor(ContextCompat.getColor(TabTicketActivity.this, R.color.ticket_item_background_selected));
                }
            }, 190);
            this.identificativo = identificativo;
        }
    }

    public boolean isMenuOpened(){
        return mActionMode != null;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate the menu
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_action_bar_tickets, menu);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setVisibility(View.GONE);
                }
            }, 190);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.close) {
                // apro il dialog per archiviare la chat
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(TabTicketActivity.this, R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Chiudi ticket");
                builder.setMessage("Vuoi chiudere questo ticket?");
                builder.setNegativeButton("Annulla", null);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // chiudi il ticket
                        Map<String, Object> statoTickets = new HashMap<>();
                        statoTickets.put("stato", false);
                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                        fStore.collection("tickets").document(identificativo).update(statoTickets).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showSnackbar("Ticket archiviato!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("LOG", "Error! " + e.getLocalizedMessage());
                                showSnackbar("Ticket non archiviato correttamente.");
                            }
                        });
                        mActionMode.finish();
                    }
                });
                builder.show();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
                    tabLayout.setVisibility(View.VISIBLE);
                    // set background
                    TypedValue outValue = new TypedValue();
                    TabTicketActivity.this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    selectedLayout.setBackgroundResource(outValue.resourceId);
                    selectedLayout = null;
                }
            }, 190);

            identificativo = null;
            mActionMode = null;
        }
    };

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
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT).setAnchorView(newTicketBtn)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.inverted_primary_text));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}