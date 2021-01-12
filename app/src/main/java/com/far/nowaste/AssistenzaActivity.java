package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AssistenzaActivity extends AppCompatActivity {

    // variabili
    Toolbar mToolbar;
    FloatingActionButton mNewBugBtn;
    RecyclerView mAssistenzaList;

    RelativeLayout layout;
    Typeface nunito;

    // firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistenza);

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
        layout = findViewById(R.id.assistenza_layout);

        // toolbar
        mToolbar = findViewById(R.id.assistenza_toolbar);
        setSupportActionBar(mToolbar);

        // recyclerView
        mAssistenzaList = findViewById(R.id.assistenza_recyclerView);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNewBugBtn = findViewById(R.id.assistenza_floatingActionButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Query query = fStore.collection("assistenza").orderBy("nome", Query.Direction.DESCENDING);

        // visibilità pulsante
        if (fAuth.getCurrentUser() != null) {
            mNewBugBtn.setVisibility(View.VISIBLE);
            mNewBugBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getApplicationContext(), NewBugActivity.class), 1);
                }
            });
        } else {
            mNewBugBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            boolean bugSent = data.getBooleanExtra("com.far.nowaste.NEW_BUG_REQUEST", false);
            if (bugSent) {
                showSnackbar("Segnalazione inviata!");
            }
        }
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

    public void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT).setAnchorView(mNewBugBtn)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}