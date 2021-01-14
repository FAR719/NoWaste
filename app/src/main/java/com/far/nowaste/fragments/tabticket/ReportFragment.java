package com.far.nowaste.fragments.tabticket;

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
import com.far.nowaste.objects.Report;
import com.far.nowaste.R;
import com.far.nowaste.ui.main.ReportBottomSheetDialog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ReportFragment extends Fragment {

    // definizione variabili
    RecyclerView mFirestoreList;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_report,container,false);

        // recyclerView + FireBase
        fAuth = FirebaseAuth.getInstance();
        mFirestoreList = view.findViewById(R.id.fragment_report_recyclerView);

        if (fAuth.getCurrentUser() != null) {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            Query query;
            if(MainActivity.CURRENT_USER.isOperatore()){
                // query per l'operatore
                query = fStore.collection("reports")
                        .orderBy("year", Query.Direction.DESCENDING).orderBy("month", Query.Direction.DESCENDING)
                        .orderBy("day", Query.Direction.DESCENDING).orderBy("hour", Query.Direction.DESCENDING)
                        .orderBy("minute", Query.Direction.DESCENDING).orderBy("second", Query.Direction.DESCENDING);

            } else {
                // query per l'utente
                query = fStore.collection("reports").whereEqualTo("email",fAuth.getCurrentUser().getEmail())
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

                    if (MainActivity.CURRENT_USER.isOperatore()) {
                        holder.rSubtitle.setText(model.getEmail());
                    } else {
                        holder.rSubtitle.setText(model.getCassonetto());
                    }

                    holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isOperatore", MainActivity.CURRENT_USER.isOperatore());
                            bundle.putString("titolo", model.getTipologia());
                            bundle.putString("cassonetto", model.getCassonetto());
                            bundle.putString("email", model.getEmail());
                            bundle.putString("indirizzo", model.getIndirizzo());
                            bundle.putString("commento", model.getCommento());
                            bundle.putInt("year", model.getYear());
                            bundle.putInt("month", model.getMonth());
                            bundle.putInt("day", model.getDay());

                            ReportBottomSheetDialog bottomSheet = new ReportBottomSheetDialog();
                            bottomSheet.setArguments(bundle);
                            bottomSheet.show(getActivity().getSupportFragmentManager(), "ReportBottomSheet");
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

        ConstraintLayout itemLayout;
        TextView rTipologia;
        TextView rData;
        TextView rSubtitle;

        public TicketsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.recView_ticketsItem_constraintLayout);
            rTipologia = itemView.findViewById(R.id.recView_ticketsItem_oggettoTextView);
            rData = itemView.findViewById(R.id.recView_ticketsItem_dataTextView);
            rSubtitle = itemView.findViewById(R.id.recView_ticketsItem_emailTextView);
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
