package com.shakenbeer.nutrition.ui;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DatePickerFragment extends DialogFragment {

    private OnDateSetListener listener;
    private Date initDate;

    public static DatePickerFragment newInstance(OnDateSetListener listener,
            Date date) {

        DatePickerFragment fragment = new DatePickerFragment();
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
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener,
                year, month, day);
        return dialog;
    }
    

    private void setListener(OnDateSetListener listener) {
        this.listener = listener;
    }

    private void setInitDate(Date date) {
        this.initDate = date;
    }

}
