package com.shakenbeer.nutrition.calendar;


import com.shakenbeer.nutrition.model.Day;

import java.util.Date;
import java.util.List;

public interface CalendarContract {

    interface View {
        void showDays(List<Day> days);
        void showDayUi(Date date);
    }

    interface Presenter {
        void obtainDays(int page, int offset);
        void onDayClicked(Day day);
    }
}
