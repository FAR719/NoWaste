package com.far.nowaste.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.far.nowaste.Other.EventDecorator;
import com.far.nowaste.Objects.Evento;
import com.far.nowaste.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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
    TextView mDateTextView, mTitleTextView, mDescTextView;
    MaterialCardView materialCardView;

    // definizione variabili
    List<Evento> eventi;
    List<CalendarDay> dates;
    CalendarDay currentDay;

    // firebase
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // associazione view
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        mCalendarView = view.findViewById(R.id.calendarView);
        mDateTextView = view.findViewById(R.id.calendar_event_date);
        mTitleTextView = view.findViewById(R.id.calendar_event_title);
        mDescTextView = view.findViewById(R.id.calendar_event_desc);
        materialCardView = view.findViewById(R.id.cardViewEvent);

        // inizializzazione liste
        eventi = new LinkedList<>();
        dates = new LinkedList<>();

        // inizializzazione firestore
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        // imposta la data odierna
        currentDay = CalendarDay.today();
        mCalendarView.setDateSelected(CalendarDay.today(), true);
        materialCardView.setVisibility(View.GONE);
        mDateTextView.setText(currentDay.getDay() + "/" + currentDay.getMonth() + "/" + currentDay.getYear());

        // onClick day
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mDateTextView.setText(date.getDay() + "/" + date.getMonth() + "/" + date.getYear());
                if (fAuth.getCurrentUser() != null) {
                    materialCardView.setVisibility(View.GONE);
                    for (Evento evento : eventi) {
                        if (date.getYear() == evento.getYear() && date.getMonth() == evento.getMonth() && date.getDay() == evento.getDay()) {
                            mTitleTextView.setText(evento.getTitle());
                            mDescTextView.setText(evento.getDescription());
                            materialCardView.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // prendo gli eventi dal database
        if (fAuth.getCurrentUser() != null) {
            fStore.collection("events").whereEqualTo("email", fAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Evento evento = document.toObject(Evento.class);
                            eventi.add(evento);
                            // prelevo le date dai documenti e le metto in dates
                            CalendarDay calendarDay = CalendarDay.from(evento.getYear(), evento.getMonth(), evento.getDay());
                            dates.add(calendarDay);
                            // aggiungo la scheda per l'evento di oggi
                            if (currentDay.getYear() == evento.getYear() && currentDay.getMonth() == evento.getMonth() && currentDay.getDay() == evento.getDay()) {
                                mTitleTextView.setText(evento.getTitle());
                                mDescTextView.setText(evento.getDescription());
                                materialCardView.setVisibility(View.VISIBLE);
                            }
                        }
                        // aggiungi i dot agli eventi
                        mCalendarView.addDecorator(new EventDecorator(ContextCompat.getColor(getContext(), R.color.cinnamon_satin), dates));
                    }
                }
            });
        }
    }
}