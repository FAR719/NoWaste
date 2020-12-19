package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.far.nowaste.Objects.Tickets;
import com.far.nowaste.Other.ReportErrorActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TicketListActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    RecyclerView mFirestoreList;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;

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
        setContentView(R.layout.activity_ticket_list);

        // toolbar
        mToolbar = findViewById(R.id.ticketsList_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // recyclerView + FireBase
        fAuth = FirebaseAuth.getInstance();
        mFirestoreList = findViewById(R.id.tickets_listaCard_recyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            // query
            Query query = firebaseFirestore.collection("tickets").whereEqualTo("email",fAuth.getCurrentUser().getEmail())
                    .orderBy("year").orderBy("month").orderBy("day").orderBy("hour").orderBy("minute").orderBy("second");

            // recyclerOptions
            FirestoreRecyclerOptions<Tickets> options = new FirestoreRecyclerOptions.Builder<Tickets>().setQuery(query, Tickets.class).build();

            adapter = new FirestoreRecyclerAdapter<Tickets, TicketListActivity.TicketsViewHolder>(options) {
                @NonNull
                @Override
                public TicketListActivity.TicketsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_tickets_item, parent, false);
                    return new TicketListActivity.TicketsViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull TicketListActivity.TicketsViewHolder holder, int position, @NonNull Tickets model) {
                    holder.rOggetto.setText(model.getOggetto());
                    holder.rData.setText(model.getDay() + "/" + model.getMonth() + "/" + model.getYear());
                    holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // apro la chat
                            Intent detailSearchActivity = new Intent(getApplicationContext(), TicketChatActivity.class);
                            String ora_corr= model.getHour() + ":" + model.getMinute()+ ":" + model.getSecond();
                            detailSearchActivity.putExtra("com.far.nowaste.identificativo", model.getEmail() + ora_corr);
                            startActivity(detailSearchActivity);
                        }
                    });
                }
            };
        }

        // View Holder
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

        // divider nella recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mFirestoreList.addItemDecoration(dividerItemDecoration);

        // imposta l'animazione del floating button
        initFloatingMenu();

    }


    private class TicketsViewHolder extends RecyclerView.ViewHolder{

        private TextView rOggetto;
        private TextView rData;
        ConstraintLayout itemLayout;


        public TicketsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.recView_ticketsItem_constraintLayout);
            rOggetto = itemView.findViewById(R.id.recView_ticketsItem_oggettoTextView);
            rData = itemView.findViewById(R.id.recView_ticketsItem_dataTextView);
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
        Intent intentError = new Intent(this, ReportErrorActivity.class);

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
            newTicketBtn.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
            checkBtn.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
            errorBtn.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

            Drawable defaulImage = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_clear);
            newTicketBtn.setImageDrawable(defaulImage);

            isMenuOpen = false;
        }else {
            newTicketBtn.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
            checkBtn.animate().translationY(15f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
            errorBtn.animate().translationY(15f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();

            Drawable defaulImage = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_create);
            newTicketBtn.setImageDrawable(defaulImage);

            isMenuOpen = true;
        }
    }
}
