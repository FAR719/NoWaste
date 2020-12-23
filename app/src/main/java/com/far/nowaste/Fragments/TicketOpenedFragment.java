package com.far.nowaste.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.far.nowaste.MainActivity;
import com.far.nowaste.Objects.Tickets;
import com.far.nowaste.R;
import com.far.nowaste.TicketChatActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class TicketOpenedFragment extends Fragment {

    // definizione variabili
    RecyclerView mFirestoreList;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_open_layout,container,false);

        // recyclerView + FireBase
        fAuth = FirebaseAuth.getInstance();
        mFirestoreList = view.findViewById(R.id.fragment_ticketsOpen_recyclerView);

        if (fAuth.getCurrentUser() != null) {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            Query query;
            if(MainActivity.CURRENTUSER.isOperatore()) {
                // query per l'operatore
                query = fStore.collection("tickets")
                        .whereEqualTo("stato",true)
                        .orderBy("year", Query.Direction.DESCENDING).orderBy("month", Query.Direction.DESCENDING)
                        .orderBy("day", Query.Direction.DESCENDING).orderBy("hour", Query.Direction.DESCENDING)
                        .orderBy("minute", Query.Direction.DESCENDING).orderBy("second", Query.Direction.DESCENDING);
            } else {
                // query per l'utente
                query = fStore.collection("tickets")
                        .whereEqualTo("email",fAuth.getCurrentUser().getEmail()).whereEqualTo("stato",true)
                        .orderBy("year", Query.Direction.DESCENDING).orderBy("month", Query.Direction.DESCENDING)
                        .orderBy("day", Query.Direction.DESCENDING).orderBy("hour", Query.Direction.DESCENDING)
                        .orderBy("minute", Query.Direction.DESCENDING).orderBy("second", Query.Direction.DESCENDING);
            }

            // recyclerOptions
            FirestoreRecyclerOptions<Tickets> options = new FirestoreRecyclerOptions.Builder<Tickets>().setQuery(query, Tickets.class).build();

            adapter = new FirestoreRecyclerAdapter<Tickets, TicketOpenedFragment.TicketsViewHolder>(options) {
                @NonNull
                @Override
                public TicketOpenedFragment.TicketsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_tickets_item, parent, false);
                    return new TicketOpenedFragment.TicketsViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull TicketOpenedFragment.TicketsViewHolder holder, int position, @NonNull Tickets model) {
                    holder.rOggetto.setText(model.getOggetto());
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
                    holder.rData.setText(day + "/" + month + "/" + model.getYear());
                    if (MainActivity.CURRENTUSER.isOperatore()) {
                        holder.rEmail.setVisibility(View.VISIBLE);
                        holder.rEmail.setText(model.getEmail());
                    } else {
                        holder.rEmail.setVisibility(View.GONE);
                    }
                    holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // apro la chat
                            String ora_Ticket = model.getHour() + ":" + model.getMinute()+ ":" + model.getSecond();
                            Intent detailSearchActivity = new Intent(getContext(), TicketChatActivity.class);
                            detailSearchActivity.putExtra("com.far.nowaste.identificativo", model.getEmail() + ora_Ticket);
                            detailSearchActivity.putExtra("com.far.nowaste.oggetto", model.getOggetto());
                            startActivity(detailSearchActivity);
                        }
                    });

                    if(MainActivity.CURRENTUSER.isOperatore()) {

                        holder.itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                // apro il dialog per archiviare la chat
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                                builder.setTitle("Archivia");
                                builder.setMessage("Vuoi chiudere questo ticket?");
                                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                });
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // setta lo stato = chiuso del ticket
                                        String ora_Ticket = model.getHour() + ":" + model.getMinute()+ ":" + model.getSecond();
                                        String identificativo = model.getEmail() + ora_Ticket;

                                        Map<String, Object> statoTickets = new HashMap<>();
                                        statoTickets.put("stato",false);
                                        fStore.collection("tickets").document(identificativo).update(statoTickets)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Questo ticket è stato archiviato!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("TAG", "Error! " + e.toString());
                                            }
                                        });
                                    }
                                });
                                builder.show();
                                return true;
                            }
                        });
                    }


                }
            };
        }

        // View Holder
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFirestoreList.setAdapter(adapter);

        // divider nella recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mFirestoreList.addItemDecoration(dividerItemDecoration);


        return view;
    }

    private class TicketsViewHolder extends RecyclerView.ViewHolder{

        private TextView rOggetto;
        private TextView rData;
        private TextView rEmail;
        ConstraintLayout itemLayout;


        public TicketsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.recView_ticketsItem_constraintLayout);
            rOggetto = itemView.findViewById(R.id.recView_ticketsItem_oggettoTextView);
            rData = itemView.findViewById(R.id.recView_ticketsItem_dataTextView);
            rEmail = itemView.findViewById(R.id.recView_ticketsItem_emailTextView);
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

}
