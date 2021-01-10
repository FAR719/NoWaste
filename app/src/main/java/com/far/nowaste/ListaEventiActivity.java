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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ActionMode;

import com.far.nowaste.objects.Evento;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class ListaEventiActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    RecyclerView recView;
    ActionMode mActionMode;

    MaterialCardView selectedCard;
    String selectedEventId;
    Evento selectedEvent;

    RelativeLayout layout;
    Typeface nunito;

    FloatingActionButton newEventBtn;

    FirestoreRecyclerAdapter adapter;

    CalendarDay currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventi);

        // toolbar
        mToolbar = findViewById(R.id.eventList_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
        layout = findViewById(R.id.eventList_layout);

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        recView = findViewById(R.id.eventList_recyclerView);

        currentDay = CalendarDay.today();

        // query (Firebase permette di utilizzare whereGreaterThan in un solo campo quindi filtro per anno
        Query query = fStore.collection("events").whereGreaterThanOrEqualTo("year", currentDay.getYear())
                .orderBy("year",Query.Direction.ASCENDING).orderBy("month", Query.Direction.ASCENDING)
                .orderBy("day", Query.Direction.ASCENDING);

        // recyclerOptions
        FirestoreRecyclerOptions<Evento> options = new FirestoreRecyclerOptions.Builder<Evento>().setQuery(query,Evento.class).build();

        adapter = new FirestoreRecyclerAdapter<Evento, ListaEventiActivity.EventViewHolder>(options) {
            @NonNull
            @Override
            public ListaEventiActivity.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_event_item, parent, false);
                return new ListaEventiActivity.EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListaEventiActivity.EventViewHolder holder, int position, @NonNull Evento model) {
                // filtro mese e giorno
                if (model.getMonth() >= currentDay.getMonth() && model.getDay() >= currentDay.getDay()) {
                    String day, month;
                    if (model.getDay() < 10) {
                        day = "0" + model.getDay();
                    } else {
                        day = model.getDay() + "";
                    }
                    if (model.getMonth() < 10) {
                        month = "0" + model.getMonth();
                    } else  {
                        month = model.getMonth() + "";
                    }

                    holder.rdata.setText(day + "/" + month + "/" + model.getYear());
                    holder.rtitolo.setText(model.getTitle());
                    holder.remail.setText("Destinatario: " + model.getEmail());
                    holder.rdescrizione.setText(model.getDescription());
                    holder.card.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            if (holder.card.isSelected()) {
                                mActionMode.finish();
                            }
                        }
                    });
                    holder.card.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (mActionMode != null) {
                                return false;
                            }

                            selectedEventId = getSnapshots().getSnapshot(position).getId();
                            selectedEvent = model;

                            // cambia lo sfondo della statusbar e della card dopo 190 millisecondi
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getWindow().setStatusBarColor(getResources().getColor(R.color.material_grey_900));
                                }
                            }, 190);

                            // attiva la contextual action bar
                            mActionMode = ListaEventiActivity.this.startActionMode(mActionModeCallback);
                            v.setSelected(true);
                            selectedCard = (MaterialCardView) v;
                            return true;
                        }
                    });
                } else {
                    holder.itemLayout.setVisibility(View.GONE);
                    holder.itemLayout.setMaxHeight(0);
                }
            }
        };

        // View Holder
        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);

        newEventBtn = findViewById(R.id.newEvent_ActionButton);
        newEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewEventActivity.class);
                intent.putExtra("com.far.nowaste.REQUESTCODE", 1);
                startActivityForResult(intent, 1);
            }
        });
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate the menu
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_action_bar_events, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Called each time ActionMode is shown. Always called after onCreateActionMode.
            // Return false if nothing is done
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.modify:
                    Intent intent = new Intent(getApplicationContext(), NewEventActivity.class);
                    intent.putExtra("com.far.nowaste.REQUESTCODE", 2);
                    intent.putExtra("com.far.nowaste.EVENTO_ID", selectedEventId);
                    intent.putExtra("com.far.nowaste.EVENTO_EMAIL", selectedEvent.getEmail());
                    intent.putExtra("com.far.nowaste.EVENTO_TITLE", selectedEvent.getTitle());
                    intent.putExtra("com.far.nowaste.EVENTO_DESCRIPTION", selectedEvent.getDescription());
                    intent.putExtra("com.far.nowaste.EVENTO_YEAR", selectedEvent.getYear());
                    intent.putExtra("com.far.nowaste.EVENTO_MONTH", selectedEvent.getMonth());
                    intent.putExtra("com.far.nowaste.EVENTO_DAY", selectedEvent.getDay());
                    startActivityForResult(intent, 2);
                    return true;
                case R.id.delete:
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ListaEventiActivity.this, R.style.ThemeOverlay_NoWaste_AlertDialog);
                    builder.setTitle("Elimina");
                    builder.setMessage("Vuoi eliminare l'evento selezionato?");
                    builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                            fStore.collection("events").document(selectedEventId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showSnackbar("Evento eliminato.");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                                    showSnackbar("Evento non eliminato correttamente!");
                                }
                            });
                            mode.finish();
                        }
                    });
                    builder.show();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // cambia lo sfondo della statusbar e della card dopo 190 millisecondi
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
                }
            }, 190);

            selectedCard.setSelected(false);
            selectedCard = null;
            mActionMode = null;
            selectedEventId = null;
            selectedEvent = null;
        }
    };

    private class EventViewHolder extends RecyclerView.ViewHolder {

        TextView rtitolo, remail, rdescrizione, rdata;
        ConstraintLayout itemLayout;
        MaterialCardView card;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.recView_eventItem_constraintLayout);
            card = itemView.findViewById(R.id.event_cardView);
            rtitolo = itemView.findViewById(R.id.recView_eventItem_titoloTextView);
            remail = itemView.findViewById(R.id.recView_eventItem_emailTextView);
            rdescrizione = itemView.findViewById(R.id.recView_eventItem_descrizioneTextView);
            rdata = itemView.findViewById(R.id.recView_eventItem_dataTextView);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            boolean eventoCreato = data.getBooleanExtra("com.far.nowaste.NEW_EVENT_REQUEST", false);
            if (eventoCreato) {
                showSnackbar("Evento creato!");
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            boolean eventoModificato = data.getBooleanExtra("com.far.nowaste.MODIFY_EVENT_REQUEST", false);
            if (eventoModificato) {
                showSnackbar("Evento modificato!");
            } else {
                showSnackbar("Evento non modificato correttamente.");
            }
        }
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    private void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT).setAnchorView(newEventBtn)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}