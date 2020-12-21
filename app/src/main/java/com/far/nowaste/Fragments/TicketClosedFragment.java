package com.far.nowaste.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TicketClosedFragment extends Fragment {

    // definizione variabili
    RecyclerView mFirestoreList;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_close_layout,container,false);

        // recyclerView + FireBase
        fAuth = FirebaseAuth.getInstance();
        mFirestoreList = view.findViewById(R.id.fragment_ticketsClose_recyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            // query
            Query query = firebaseFirestore.collection("tickets").whereEqualTo("email",fAuth.getCurrentUser().getEmail())
                    .whereEqualTo("stato",false)
                    .orderBy("year", Query.Direction.DESCENDING).orderBy("month", Query.Direction.DESCENDING)
                    .orderBy("day", Query.Direction.DESCENDING).orderBy("hour", Query.Direction.DESCENDING)
                    .orderBy("minute", Query.Direction.DESCENDING).orderBy("second", Query.Direction.DESCENDING);

            // recyclerOptions
            FirestoreRecyclerOptions<Tickets> options = new FirestoreRecyclerOptions.Builder<Tickets>().setQuery(query, Tickets.class).build();

            adapter = new FirestoreRecyclerAdapter<Tickets, TicketClosedFragment.TicketsViewHolder>(options) {
                @NonNull
                @Override
                public TicketClosedFragment.TicketsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_tickets_item, parent, false);
                    return new TicketClosedFragment.TicketsViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull TicketClosedFragment.TicketsViewHolder holder, int position, @NonNull Tickets model) {
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
                            Intent detailSearchActivity = new Intent(getContext(), TicketChatActivity.class);
                            String ora_corr= model.getHour() + ":" + model.getMinute()+ ":" + model.getSecond();
                            detailSearchActivity.putExtra("com.far.nowaste.identificativo", model.getEmail() + ora_corr);
                            detailSearchActivity.putExtra("com.far.nowaste.oggetto", model.getOggetto());
                            startActivity(detailSearchActivity);
                        }
                    });
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
