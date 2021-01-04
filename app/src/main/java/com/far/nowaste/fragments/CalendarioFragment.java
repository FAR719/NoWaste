package com.far.nowaste.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.far.nowaste.ListaEventiActivity;
import com.far.nowaste.MainActivity;
import com.far.nowaste.ui.main.CurrentDayDecorator;
import com.far.nowaste.ui.main.EventDecorator;
import com.far.nowaste.objects.Evento;
import com.far.nowaste.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.LinkedList;
import java.util.List;

public class CalendarioFragment extends Fragment {

    // definizione view
    MaterialCalendarView mCalendarView;
    TextView mTitleTextView, mDescTextView;
    MaterialCardView eventCardView;
    FloatingActionButton addEventListBtn;

    // definizione variabili
    List<Evento> eventi;
    List<CalendarDay> dates;
    CalendarDay currentDay;
    int year, month, day;

    // firebase
    FirebaseAuth fAuth;

    Typeface nunito, nunito_bold;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // associazione view
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        mCalendarView = view.findViewById(R.id.calendarView);
        mTitleTextView = view.findViewById(R.id.calendar_event_title);
        mDescTextView = view.findViewById(R.id.calendar_event_desc);
        eventCardView = view.findViewById(R.id.cardViewEvent);
        addEventListBtn = view.findViewById(R.id.calendar_eventListActionButton);

        // typefaces
        nunito = ResourcesCompat.getFont(getContext(), R.font.nunito);
        nunito_bold = ResourcesCompat.getFont(getContext(), R.font.nunito_bold);

        // inizializzazione liste
        eventi = new LinkedList<>();
        dates = new LinkedList<>();

        // inizializzazione firestore
        fAuth = FirebaseAuth.getInstance();

        // imposta la data odierna
        currentDay = CalendarDay.today();
        mCalendarView.setDateSelected(CalendarDay.today(), true);
        mCalendarView.addDecorator(new CurrentDayDecorator(getContext()));

        year = currentDay.getYear();
        month = currentDay.getMonth();
        day = currentDay.getDay();

        if (fAuth.getCurrentUser() == null) {
            addEventListBtn.setVisibility(View.GONE);
            mTitleTextView.setText("Eventi");
            mDescTextView.setText("Accedi per visualizzare i tuoi eventi");
            eventCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).goToLogin();
                }
            });
        } else {
            if (MainActivity.CURRENTUSER.isOperatore()){
                addEventListBtn.setVisibility(View.VISIBLE);
                addEventListBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), ListaEventiActivity.class));
                    }
                });
            } else {
                addEventListBtn.setVisibility(View.GONE);
            }
            // imposta il default "Nessun evento"
            mDescTextView.setVisibility(View.GONE);
            mTitleTextView.setText("Nessun evento");
            mTitleTextView.setTypeface(nunito);

            // onClick day
            mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                    if (fAuth.getCurrentUser() != null) {
                        mTitleTextView.setText("Nessun evento");
                        mDescTextView.setVisibility(View.GONE);
                        mTitleTextView.setTypeface(nunito);
                        for (Evento evento : eventi) {
                            if (date.getYear() == evento.getYear() && date.getMonth() == evento.getMonth() && date.getDay() == evento.getDay()) {
                                mTitleTextView.setText(evento.getTitle());
                                mDescTextView.setText(evento.getDescription());
                                mDescTextView.setVisibility(View.VISIBLE);
                                mTitleTextView.setTypeface(nunito_bold);
                            }
                        }
                        year = date.getYear();
                        month = date.getMonth();
                        day = date.getDay();
                    }

                }
            });
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // prendo gli eventi dal database
        if (fAuth.getCurrentUser() != null) {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("events").whereEqualTo("email", fAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Evento evento = document.toObject(Evento.class);
                        eventi.add(evento);
                        // prelevo le date dai documenti e le metto in dates
                        CalendarDay calendarDay = CalendarDay.from(evento.getYear(), evento.getMonth(), evento.getDay());
                        dates.add(calendarDay);
                        // aggiungo la scheda per l'evento di oggi
                        if (currentDay.getYear() == evento.getYear() && currentDay.getMonth() == evento.getMonth() && currentDay.getDay() == evento.getDay()) {
                            mTitleTextView.setText(evento.getTitle());
                            mDescTextView.setText(evento.getDescription());
                            mDescTextView.setVisibility(View.VISIBLE);
                            mTitleTextView.setTypeface(nunito_bold);
                        }
                    }
                    // aggiungi i dot agli eventi
                    mCalendarView.addDecorator(new EventDecorator(ContextCompat.getColor(getContext(), R.color.cinnamon_satin), dates));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                }
            });
        }
    }
}