package com.far.nowaste.ui.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.far.nowaste.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class CurrentDayDecorator implements DayViewDecorator {

    Context context;
    CalendarDay currentDay;
    Drawable drawable;

    public CurrentDayDecorator(Context context) {
        this.context = context;
        currentDay = CalendarDay.today();
        drawable = ContextCompat.getDrawable(context, R.drawable.selector_current_day);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(currentDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
