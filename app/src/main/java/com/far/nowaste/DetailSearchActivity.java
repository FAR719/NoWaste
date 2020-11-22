package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class DetailSearchActivity extends AppCompatActivity {

    Toolbar mToolbar;

    private FirebaseFirestore firebaseFirestore;

    TextView titoloTextView;
    TextView materialeTextView;
    TextView smaltimentoTextView;
    TextView descrizioneTextView;
    TextView punteggioTextView;

    String stringName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_search);

        // toolbar
        mToolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        // background DayNight
        mToolbar.setBackgroundColor(getThemeColor(DetailSearchActivity.this, R.attr.colorPrimary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // to launch the activity
        Intent in = getIntent();

        // variabile passata
        stringName = in.getStringExtra("com.far.nowaste.NAME1");

        // associazione view
        titoloTextView = findViewById(R.id.titoloTextView);
        materialeTextView = findViewById(R.id.materialeTextView);
        smaltimentoTextView = findViewById(R.id.smaltimentoTextView);
        descrizioneTextView = findViewById(R.id.descrizioneTextView);
        punteggioTextView = findViewById(R.id.punteggioTextView);

        // associazione firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // query
        firebaseFirestore.collection("rifiuti").document("Arancin").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String name = value.getString("nome");
                titoloTextView.setText(name);
            }
        });
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

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }
}