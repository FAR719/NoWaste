package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.nowaste.objects.Funzionalita;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DetailFunzionalitaActivity extends AppCompatActivity {

    // variabili
    Toolbar mToolbar;

    // view
    TextView testoTextView, domandaTextView, rispTextView;
    ImageView mArrowBtn;
    RecyclerView mFAQList;
    FirestoreRecyclerAdapter adapter;

    String nome;
    String testo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_funzionalita);

        // toolbar
        mToolbar = findViewById(R.id.detailFunzionalita_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // associazione view
        testoTextView = findViewById(R.id.detailFunzionalita_testoTextView);
        mFAQList = findViewById(R.id.FAQ_recyclerView);
        mArrowBtn = findViewById(R.id.recView_faq_ImageBtn);

        // variabili passate
        Intent in = getIntent();
        nome = in.getStringExtra("com.far.nowaste.FUNZ_NOME");
        testo = in.getStringExtra("com.far.nowaste.FUNZ_TESTO");

        // cambia il titolo della toolbar
        mToolbar.setTitle(nome);
        testoTextView.setText(testo);

        // query
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        Query query = fstore.collection("funzionalita").document(nome).collection("faq").orderBy("nome", Query.Direction.ASCENDING);

        // recyclerOptions
        FirestoreRecyclerOptions<Funzionalita> options = new FirestoreRecyclerOptions.Builder<Funzionalita>().setQuery(query, Funzionalita.class).build();

        adapter = new FirestoreRecyclerAdapter<Funzionalita, DetailFunzionalitaActivity.FAQViewHolder>(options) {
            @NonNull
            @Override
            public DetailFunzionalitaActivity.FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_faq_item ,parent, false);
                return new DetailFunzionalitaActivity.FAQViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DetailFunzionalitaActivity.FAQViewHolder holder, int position, @NonNull Funzionalita model) {
                holder.rDomanda.setText(model.getNome());
                holder.rRisposta.setText(model.getTesto());
            }
        };

        // View Holder
        mFAQList.setHasFixedSize(true);
        mFAQList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mFAQList.setAdapter(adapter);
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

    private class FAQViewHolder extends RecyclerView.ViewHolder{

        TextView rDomanda, rRisposta;
        ImageView rArrowBtn;
        ConstraintLayout itemLayout;

        public FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            rDomanda = itemView.findViewById(R.id.recView_faq_domandaTextView);
            rRisposta = itemView.findViewById(R.id.recView_faq_rispostaTextView);
            rArrowBtn = itemView.findViewById(R.id.recView_faq_ImageBtn);
            itemLayout = itemView.findViewById(R.id.recView_assItem_constraintLayout);
        }
    }
}