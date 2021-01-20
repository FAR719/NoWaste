package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.far.nowaste.objects.Curiosity;
import com.far.nowaste.objects.Rifiuto;
import com.far.nowaste.objects.Saving;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CategoriaActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;

    ConstraintLayout mCardLayout, mListLayout;
    MaterialCardView mCuriositaCard;
    ValueLineChart mLineChart;
    TextView mCategoriaTitle, mCuriositaTV;
    ImageButton mArrowBtn;

    RecyclerView mFirestoreList;
    FirestoreRecyclerAdapter adapter;

    String categoria;
    int nCategoria;

    boolean isExpanded;

    Curiosity curiosity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        // toolbar
        mToolbar = findViewById(R.id.categoria_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // variabile passata
        Intent in = getIntent();
        categoria = in.getStringExtra("com.far.nowaste.CATEGORIA");
        nCategoria = in.getIntExtra("com.far.nowaste.NCATEGORIA", 0);

        // cambia il titolo della toolbar
        mToolbar.setTitle(categoria);

        mCardLayout = findViewById(R.id.cards_layout);
        mListLayout = findViewById(R.id.listaCategoria_layout);

        mCuriositaCard = findViewById(R.id.categoria_curiosita_card);
        mCategoriaTitle = findViewById(R.id.categoria_titolo);
        mLineChart = findViewById(R.id.categoria_lineChart);
        mCuriositaTV = findViewById(R.id.categoria_curiosita_TV);
        mArrowBtn = findViewById(R.id.categoria_arrow);

        mFirestoreList = findViewById(R.id.categoria_recyclerView);

        isExpanded = false;

        initArrowBtn();

        mCategoriaTitle.setText(Html.fromHtml("Andamento risparmio CO<sub><small><small>2</small></small></sub>"));
        setLineChartData(MainActivity.CARBON_DIOXIDE_ARRAY_LIST);

        loadCuriosita();

        loadRecyclerView();
    }

    private void  setLineChartData(ArrayList<ArrayList<Saving>> arrayOfArray) {
        ValueLineSeries series = new ValueLineSeries();

        // assegna il colore corrispondente
        switch (nCategoria) {
            case 0:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.plastica), 150));
                break;
            case 1:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.organico), 150));
                break;
            case 2:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.secco), 150));
                break;
            case 3:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.carta), 150));
                break;
            case 4:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.vetro), 150));
                break;
            case 5:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.metalli), 150));
                break;
            case 6:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.elettrici), 150));
                break;
            case 7:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.speciali), 150));
                break;
        }

        for (Saving item : arrayOfArray.get(nCategoria)) {
            String month;
            if (item.getMonth() < 10) {
                month = "0" + item.getMonth();
            } else {
                month = item.getMonth() + "";
            }
            series.addPoint(new ValueLinePoint(month + "/" + item.getYear(), (float)item.getPunteggio()));
        }

        mLineChart.addSeries(series);
        mLineChart.startAnimation();
    }

    private void loadCuriosita() {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("curiosity").whereEqualTo("etichetta", categoria).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    mCuriositaCard.setVisibility(View.VISIBLE);
                    List<Curiosity> curiosityList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Curiosity curiosity = document.toObject(Curiosity.class);
                        curiosityList.add(curiosity);
                    }
                    int curiosityCount = curiosityList.size();
                    int randomNumber= new Random().nextInt(curiosityCount);

                    curiosity = curiosityList.get(randomNumber);
                    mCuriositaTV.setText(curiosity.getDescrizione());
                } else {
                    mCuriositaCard.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LOG", "Error! " + e.getLocalizedMessage());
            }
        });
    }

    private void initArrowBtn() {
        mArrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable arrow;
                ViewGroup.LayoutParams cardParams = mCardLayout.getLayoutParams();
                ViewGroup.LayoutParams listParams = mListLayout.getLayoutParams();
                if (isExpanded) {
                    cardParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    listParams.height = 0;
                    arrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_up);
                } else {
                    cardParams.height = 0;
                    listParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    arrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_down);
                }
                mListLayout.requestLayout();
                mCardLayout.requestLayout();
                mArrowBtn.setImageDrawable(arrow);
                isExpanded = !isExpanded;
            }
        });
    }

    private void loadRecyclerView() {
        // query
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("rifiuti").whereEqualTo("smaltimento", categoria)
                .orderBy("nome", Query.Direction.ASCENDING);

        // recyclerOptions
        FirestoreRecyclerOptions<Rifiuto> options = new FirestoreRecyclerOptions.Builder<Rifiuto>().setQuery(query, Rifiuto.class).build();

        adapter = new FirestoreRecyclerAdapter<Rifiuto, CategoriaActivity.RifiutoViewHolder>(options) {
            @NonNull
            @Override
            public CategoriaActivity.RifiutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_lista_categoria_item, parent, false);
                return new RifiutoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoriaActivity.RifiutoViewHolder holder, int position, @NonNull Rifiuto model) {
                holder.rName.setText(model.getNome());
                holder.rMateriale.setText(model.getMateriale());
                holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailSearchActivity = new Intent(getApplicationContext(), DetailRifiutoActivity.class);
                        detailSearchActivity.putExtra("com.far.nowaste.NAME", model.getNome());
                        startActivity(detailSearchActivity);
                    }
                });
            }
        };

        // View Holder
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);
    }

    private class RifiutoViewHolder extends RecyclerView.ViewHolder{

        TextView rName, rMateriale;
        ConstraintLayout itemLayout;

        public RifiutoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.recView_listaItemCategoria_constraintLayout);
            rName = itemView.findViewById(R.id.recView_listaItemCategoria_nameTextView);
            rMateriale = itemView.findViewById(R.id.recView_listaItemCategoria_materialeTextView);
        }
    }

    //start&stop listening
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
}