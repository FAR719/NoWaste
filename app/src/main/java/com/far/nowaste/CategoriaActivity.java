package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.AutoTransition;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.nowaste.objects.Curiosity;
import com.far.nowaste.objects.Rifiuto;
import com.far.nowaste.objects.Saving;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
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

    ConstraintLayout mCardLayout, mListLayout, mExpandLayout;
    MaterialCardView mGraficoCard, mCuriositaCard, mHintCard;
    ValueLineChart mLineChart;
    TextView mCuriositaTV, mWarning, mHintBody;
    ImageView mArrowBtn;
    MaterialButton mSavngBtn;

    RecyclerView mFirestoreList;
    FirestoreRecyclerAdapter adapter;

    String tipo;

    String categoria;
    int nCategoria;

    boolean isExpanded;

    Curiosity curiosity;

    OvershootInterpolator interpolator;

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

        mGraficoCard = findViewById(R.id.grafico_card);
        mCardLayout = findViewById(R.id.cards_layout);
        mHintCard = findViewById(R.id.hint_card);
        mListLayout = findViewById(R.id.listaCategoria_layout);
        mExpandLayout = findViewById(R.id.categoria_expand);

        mCuriositaCard = findViewById(R.id.categoria_curiosita_card);
        mLineChart = findViewById(R.id.categoria_lineChart);
        mCuriositaTV = findViewById(R.id.categoria_curiosita_TV);
        mArrowBtn = findViewById(R.id.categoria_arrow);
        mWarning = findViewById(R.id.categoria_warning);
        mSavngBtn = findViewById(R.id.categoria_savingButton);
        mHintBody = findViewById(R.id.hint_body);

        mFirestoreList = findViewById(R.id.categoria_recyclerView);

        interpolator = new OvershootInterpolator();
        isExpanded = false;

        mExpandLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateList();
            }
        });

        if (nCategoria != 2 && nCategoria != 7) {
            mGraficoCard.setVisibility(View.VISIBLE);
            mHintCard.setVisibility(View.GONE);
            tipo = "co2";
            setLineChartData(tipo, MainActivity.CARBON_DIOXIDE_ARRAY_LIST);
            mSavngBtn.setText(Html.fromHtml("CO<sub><small><small>2</small></small></sub>"));

            mSavngBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (tipo) {
                        case "co2":
                            if (nCategoria == 1) {
                                mSavngBtn.setText("Fertiliz.");
                                tipo = "fertilizzante";
                                setLineChartData(tipo, MainActivity.OTHER_ARRAY_LIST);
                            } else {
                                mSavngBtn.setText("Energia");
                                tipo = "energia";
                                setLineChartData(tipo, MainActivity.ENERGY_ARRAY_LIST);
                            }
                            break;
                        case "energia":
                            if (nCategoria == 6) {
                                mSavngBtn.setText(Html.fromHtml("CO<sub><small><small>2</small></small></sub>"));
                                tipo = "co2";
                                setLineChartData(tipo, MainActivity.CARBON_DIOXIDE_ARRAY_LIST);
                            } else {
                                mSavngBtn.setText("Petrolio");
                                tipo = "petrolio";
                                setLineChartData(tipo, MainActivity.OIL_ARRAY_LIST);
                            }
                            break;
                        case "petrolio":
                            if (nCategoria == 4) {
                                mSavngBtn.setText("Sabbia");
                                tipo = "sabbia";
                                setLineChartData(tipo, MainActivity.OTHER_ARRAY_LIST);
                            } else if ( nCategoria == 5) {
                                mSavngBtn.setText(Html.fromHtml("CO<sub><small><small>2</small></small></sub>"));
                                tipo = "co2";
                                setLineChartData(tipo, MainActivity.CARBON_DIOXIDE_ARRAY_LIST);
                            } else {
                                mSavngBtn.setText("Acqua");
                                tipo = "acqua";
                                setLineChartData(tipo, MainActivity.OTHER_ARRAY_LIST);
                            }
                            break;
                        default:
                            mSavngBtn.setText(Html.fromHtml("CO<sub><small><small>2</small></small></sub>"));
                            tipo = "co2";
                            setLineChartData(tipo, MainActivity.CARBON_DIOXIDE_ARRAY_LIST);
                    }
                }
            });
        } else {
            mGraficoCard.setVisibility(View.GONE);
            mHintCard.setVisibility(View.VISIBLE);
            if (nCategoria == 2) {
                mHintBody.setText("Ricordati di gettare i rifiuti non riciclabili nel bidone del secco residuo! " +
                        "I rifiuti non smaltiti correttamente possono restare nell'ambiente anche per migliaia di anni, " +
                        "basti pensare che un semplice pannolino impiega 500 anni per decomporsi naturalmente.");
            } else {
                mHintBody.setText("I rifiuti speciali sono generalmente riciclati dall'azienda che svolge il servizio " +
                        "ambientale locale. Contatta un operatore ecologico o porta il tuo rifiuto ad un'isola ecologica, " +
                        "qui i vari materiali di cui è composto verranno smaltiti separatamente ed eventualmente recuperati.");
            }

        }

        loadCuriosita();

        loadRecyclerView();
    }

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

    private void  setLineChartData(String tipo, ArrayList<ArrayList<Saving>> arrayOfArray) {
        mLineChart.clearChart();
        ValueLineSeries series = new ValueLineSeries();

        // assegna il colore corrispondente
        switch (tipo) {
            case "co2":
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.vetro), 150));
                mLineChart.setIndicatorTextUnit("g");
                break;
            case "energia":
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.carmine), 150));
                mLineChart.setIndicatorTextUnit("Wh");
                break;
            case "petrolio":
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.secco_home), 150));
                mLineChart.setIndicatorTextUnit("g");
                break;
            case "acqua":
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.carta), 150));
                mLineChart.setIndicatorTextUnit("mL");
                break;
            case "sabbia":
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.plastica), 150));
                mLineChart.setIndicatorTextUnit("g");
                break;
            case "fertilizzante":
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.organico_home), 150));
                mLineChart.setIndicatorTextUnit("g");
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

        if (series.getSeries().size() < 2) {
            mLineChart.setShowIndicator(false);
            mWarning.setVisibility(View.VISIBLE);
        } else {
            mWarning.setVisibility(View.GONE);
            mLineChart.setShowIndicator(true);
            mLineChart.addSeries(series);
            mLineChart.startAnimation();
        }
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

    private void animateList() {
        ViewGroup.LayoutParams cardParams = mCardLayout.getLayoutParams();
        ViewGroup.LayoutParams listParams = mListLayout.getLayoutParams();
        if (isExpanded) {
            cardParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            listParams.height = 0;
            mCardLayout.animate().alpha(1f).setInterpolator(interpolator).setDuration(1000).start();
            mArrowBtn.animate().rotation(0f).setDuration(400).start();
        } else {
            cardParams.height = 0;
            listParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mCardLayout.animate().alpha(0f).setInterpolator(interpolator).setDuration(1000).start();
            mArrowBtn.animate().rotation(180f).setDuration(400).start();
        }

        ViewGroup root = findViewById(R.id.categoria_layout);
        TransitionManager.beginDelayedTransition(root);
        AutoTransition transition = new AutoTransition();
        transition.setDuration(2000);
        TransitionManager.beginDelayedTransition(root, transition);

        mListLayout.requestLayout();
        mCardLayout.requestLayout();
        isExpanded = !isExpanded;
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

    // ends this activity (back arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isExpanded) {
            animateList();
        } else {
            this.finish();
        }
    }
}