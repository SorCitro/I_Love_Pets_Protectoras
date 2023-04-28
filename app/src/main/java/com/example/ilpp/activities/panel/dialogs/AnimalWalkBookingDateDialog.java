package com.example.ilpp.activities.panel.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ilpp.R;
import com.example.ilpp.controls.Calendar;

import java.util.Date;

public class AnimalWalkBookingDateDialog extends DialogFragment {
    private Calendar calendar;

    private Date date;
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public interface OnSelectedDateListener { void onSelectedDate(Date value); }
    private OnSelectedDateListener onSelectedDateListener;
    public void setOnSelectedDateListener(OnSelectedDateListener onSelectedDateListener) { this.onSelectedDateListener = onSelectedDateListener; }
    public OnSelectedDateListener getOnSelectedDateListener() { return onSelectedDateListener; }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_animal_walk_booking_date, container, false);

        // Obtener referencias
        calendar = view.findViewById(R.id.cc_Calendar);

        // Inicializar propiedades
        if (this.date != null)
            calendar.setCurrentDate(this.date);

        // Establecer eventos
        calendar.setOnDateSelectedListener(value -> {
            this.date = value;
            if (this.onSelectedDateListener != null)
                this.onSelectedDateListener.onSelectedDate(value);
            this.dismiss();
        });

        return view;
    }
}
