package com.far.nowaste.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.far.nowaste.MainActivity;
import com.far.nowaste.Objects.Report;
import com.far.nowaste.Objects.Tickets;
import com.far.nowaste.R;
import com.far.nowaste.TicketChatActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ReportFragment extends Fragment {

    // definizione variabili
    RecyclerView mFirestoreList;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_report_layout,container,false);

        // recyclerView + FireBase
        fAuth = FirebaseAuth.getInstance();
        mFirestoreList = view.findViewById(R.id.fragment_report_recyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            Query query;
            if(MainActivity.CURRENTUSER.isOperatore()){
                // query per l'operatore
                query = firebaseFirestore.collection("reports")
                        .orderBy("year", Query.Direction.DESCENDING).orderBy("month", Query.Direction.DESCENDING)
                        .orderBy("day", Query.Direction.DESCENDING).orderBy("hour", Query.Direction.DESCENDING)
                        .orderBy("minute", Query.Direction.DESCENDING).orderBy("second", Query.Direction.DESCENDING);

            } else {
                // query per l'utente
                query = firebaseFirestore.collection("reports").whereEqualTo("email",fAuth.getCurrentUser().getEmail())
                        .orderBy("year", Query.Direction.DESCENDING).orderBy("month", Query.Direction.DESCENDING)
                        .orderBy("day", Query.Direction.DESCENDING).orderBy("hour", Query.Direction.DESCENDING)
                        .orderBy("minute", Query.Direction.DESCENDING).orderBy("second", Query.Direction.DESCENDING);
            }

            // recyclerOptions
            FirestoreRecyclerOptions<Report> options = new FirestoreRecyclerOptions.Builder<Report>().setQuery(query, Report.class).build();

            adapter = new FirestoreRecyclerAdapter<Report, ReportFragment.TicketsViewHolder>(options) {
                @NonNull
                @Override
                public ReportFragment.TicketsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_tickets_item, parent, false);
                    return new ReportFragment.TicketsViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull ReportFragment.TicketsViewHolder holder, int position, @NonNull Report model) {
                    holder.rTipologia.setText(model.getTipologia());
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

                    holder.itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            // apro le informazioni del report

                            // set custom dialogs layout params
                            LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                            // main layout
                            LinearLayout mainLayout = new LinearLayout(getContext());
                            mainLayout.setLayoutParams(mainParams);

                            View layout = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_report, mainLayout, false);

                            // declare and set TextView
                            TextView tipologia = layout.findViewById(R.id.tipologiaDialog_TextView);
                            TextView email = layout.findViewById(R.id.emailaDialog_reportTextView);
                            TextView data = layout.findViewById(R.id.dataDialog_TextView);
                            TextView indirizzo = layout.findViewById(R.id.indirizzoDialog_textView);
                            TextView cassonetto = layout.findViewById(R.id.cassonettoDialog_textView);
                            TextView commento = layout.findViewById(R.id.commentoDialog_textView);


                            mainLayout.addView(layout);

                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                            builder.setView(mainLayout);
                            builder.setNeutralButton("CHIUDI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();

                            return true;
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

        private TextView rTipologia;
        private TextView rData;
        private TextView rEmail;
        ConstraintLayout itemLayout;


        public TicketsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.recView_ticketsItem_constraintLayout);
            rTipologia = itemView.findViewById(R.id.recView_ticketsItem_oggettoTextView);
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
