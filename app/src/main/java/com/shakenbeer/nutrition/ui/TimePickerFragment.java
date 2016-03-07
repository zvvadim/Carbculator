package com.shakenbeer.nutrition.ui;

import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class TimePickerFragment extends DialogFragment {
    private OnTimeSetListener listener;
    private Date initDate;

    public static TimePickerFragment newInstance(OnTimeSetListener listener, Date date) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setListener(listener);
        fragment.setInitDate(date);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(initDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), listener, hour, minute, true);
        return dialog;
    }

    public void setListener(OnTimeSetListener listener) {
        this.listener = listener;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }
}
