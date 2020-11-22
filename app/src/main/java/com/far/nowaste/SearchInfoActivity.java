package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

public class SearchInfoActivity extends AppCompatActivity {

    Toolbar mToolbar;

    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;

    TextView titoloTextView;
    TextView materialeTextView;
    TextView smaltimentoTextView;
    TextView descrizioneTextView;
    TextView punteggioTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_info);

        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.card_toolbar);
        setSupportActionBar(mToolbar);
        // background DayNight
        mToolbar.setBackgroundColor(getThemeColor(SearchInfoActivity.this, R.attr.colorPrimary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // to launch the activity
        Intent in = getIntent();

        // variabile passata
        String stringName = in.getStringExtra("com.far.nowaste.NAME");

        // firebase
        firebaseFirestore = FirebaseFirestore.getInstance();

        // query (usando il nome come id)
        firebaseFirestore.collection("rifiuti").document(stringName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                String name = DocumentSnapshot.getString("nome");
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