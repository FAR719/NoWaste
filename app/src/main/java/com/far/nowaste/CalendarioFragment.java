package com.far.nowaste;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CalendarioFragment extends Fragment {

    MaterialCalendarView mCalendarView;
    CalendarDay currentDay;
    CalendarDay calendarDay;
    TextView mDateTextView;
    List<CalendarDay> dates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        mCalendarView = view.findViewById(R.id.calendarView);
        mDateTextView = view.findViewById(R.id.dateTextView);

        mCalendarView.state().edit().commit();
        currentDay = CalendarDay.today();
        calendarDay = CalendarDay.from(2020, 12, 12);
        dates = new LinkedList<>();
        /*dates.add(currentDay);
        dates.add(calendarDay);*/
        mCalendarView.setDateSelected(CalendarDay.today(), true);
        mDateTextView.setText(currentDay.getDay() + "/" + currentDay.getMonth() + "/" + currentDay.getYear());
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mDateTextView.setText(date.getDay() + "/" + date.getMonth() + "/" + date.getYear());
            }
        });
        mCalendarView.addDecorator(new EventDecorator(ContextCompat.getColor(getContext(), R.color.cinnamon_satin), dates));
        return view;
    }
}