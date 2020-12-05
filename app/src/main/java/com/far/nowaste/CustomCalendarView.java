package com.far.nowaste;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomCalendarView extends LinearLayout {

    // dichiarazione variabili
    TextView currentDate;
    GridView gridView;
    private static final int Max_Calendar_Days = 42;
    Calendar calendar = Calendar.getInstance(Locale.ITALIAN);
    Context context;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ITALIAN);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ITALIAN);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ITALIAN);

    // lista data e lista eventi
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }
}
