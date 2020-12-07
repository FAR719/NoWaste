package com.far.nowaste;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarioFragment extends Fragment {

    // dichiarazione variabili
    ImageButton nextButton, previousButton;
    TextView CurrentDate;
    GridView gridView;
    private static final int Max_Calendar_Days = 42;
    Calendar calendar = Calendar.getInstance(Locale.ITALIAN);

    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ITALIAN);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ITALIAN);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ITALIAN);

    // lista data e lista eventi
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        nextButton = view.findViewById(R.id.nextBtn);
        previousButton = view.findViewById(R.id.previousBtn);
        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridView);

        SetUpCalendar();

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,-1);
                SetUpCalendar();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,1);
                SetUpCalendar();
            }
        });

        return view;
    }

    private  void SetUpCalendar(){
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);
    }
}