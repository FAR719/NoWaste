package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.far.nowaste.objects.Funzionalita;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
    FirestoreRecyclerAdapter adapter;

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

        // query
        Query query = fStore.collection("funzionalita").orderBy("nome", Query.Direction.ASCENDING);

        // recyclerOptions
        FirestoreRecyclerOptions<Funzionalita> options = new FirestoreRecyclerOptions.Builder<Funzionalita>().setQuery(query, Funzionalita.class).build();

        adapter = new FirestoreRecyclerAdapter<Funzionalita, AssistenzaActivity.FunzionalitaViewHolder>(options) {
            @NonNull
            @Override
            public AssistenzaActivity.FunzionalitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_assistenza_item, parent, false);
                return new AssistenzaActivity.FunzionalitaViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AssistenzaActivity.FunzionalitaViewHolder holder, int position, @NonNull Funzionalita model) {
                holder.rName.setText(model.getNome());
                holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailFunzionalitaActivity = new Intent(getApplicationContext(), DetailFunzionalitaActivity.class);
                        detailFunzionalitaActivity.putExtra("com.far.nowaste.FUNZ_NOME", model.getNome());
                        detailFunzionalitaActivity.putExtra("com.far.nowaste.FUNZ_TESTO", model.getTesto());
                        startActivity(detailFunzionalitaActivity);
                    }
                });
            }
        };

        // View Holder
        mAssistenzaList.setHasFixedSize(true);
        mAssistenzaList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAssistenzaList.setAdapter(adapter);

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

    private class FunzionalitaViewHolder extends RecyclerView.ViewHolder{

        TextView rName;
        ConstraintLayout itemLayout;

        public FunzionalitaViewHolder(@NonNull View itemView) {
            super(itemView);
            rName = itemView.findViewById(R.id.recView_assItem_funzionalitaTextView);
            itemLayout = itemView.findViewById(R.id.recView_assItem_constraintLayout);
        }
    }

    //start&stop listening
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT).setAnchorView(mNewBugBtn)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}