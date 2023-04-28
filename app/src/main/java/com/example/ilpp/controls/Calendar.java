package com.example.ilpp.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.ilpp.R;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Calendar extends LinearLayout {

    public interface OnDateSelectedListener { void onDateSelected(Date date); }
    private OnDateSelectedListener onDateSelectedListener;
    public OnDateSelectedListener getOnDateSelectedListener() { return onDateSelectedListener; }
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) { this.onDateSelectedListener = onDateSelectedListener; }

    public static class Day {
        public Date date;
        public View view;
        public View viewMark;
    }
    public interface OnDayCreatedListener { void onDayCreated(View dayView, Day day); }
    private OnDayCreatedListener onDayCreatedListener;
    public OnDayCreatedListener getOnDayCreatedListener() { return onDayCreatedListener; }
    public void setOnDayCreatedListener(OnDayCreatedListener onDayCreatedListener) { this.onDayCreatedListener = onDayCreatedListener; }

    private TableLayout tb_Calendar;

    private Date currentDate;
    private TextView tv_CalendarMonth, tv_CalendarYear;
    private View btn_CalendarBack, btn_CalendarNext;

    public Date getCurrentDate() { return currentDate; }
    public void setCurrentDate(Date currentDate) {
        setCurrentDate(currentDate, true);
    }
    private void setCurrentDate(Date currentDate, Boolean triggerEvent) {
        this.currentDate = trunc(currentDate);
        renderCalendar();
        if (triggerEvent && onDateSelectedListener != null)
            onDateSelectedListener.onDateSelected(this.currentDate);
    }

    public Calendar(Context context) {
        super(context);
        init();
    }

    public Calendar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Calendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        View view = inflate(getContext(), R.layout.control_calendar, this);

        // Obtener referencias
        tb_Calendar = view.findViewById(R.id.tb_Calendar);
        tv_CalendarMonth = view.findViewById(R.id.tv_CalendarMonth);
        tv_CalendarYear = view.findViewById(R.id.tv_CalendarYear);
        btn_CalendarBack = view.findViewById(R.id.btn_CalendarBack);
        btn_CalendarNext = view.findViewById(R.id.btn_CalendarNext);

        // Inicializar propiedades
        setCurrentDate(new Date());

        // Establecer eventos
        btn_CalendarBack.setOnClickListener(v -> {
            Date date = new Date(currentDate.getTime());
            date.setMonth(date.getMonth() - 1);
            setCurrentDate(date, false);
        });

        btn_CalendarNext.setOnClickListener(v -> {
            Date date = new Date(currentDate.getTime());
            date.setMonth(date.getMonth() + 1);
            setCurrentDate(date, false);
        });

    }

    private TableRow createRow(){
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        return row;
    }

    public void renderCalendar(){

        // Configuración
        String[] weekNames = getResources().getStringArray(R.array.days_very_short);
        String[] monthNames = getResources().getStringArray(R.array.months);
        List<Integer> freeDays = Arrays.asList(0, 6);
        boolean firstDayIsMonday = true;
        if (firstDayIsMonday){
            weekNames = new String[]{weekNames[1], weekNames[2], weekNames[3], weekNames[4], weekNames[5], weekNames[6], weekNames[0]};
        }

        // Eliminar todos los elementos del grid
        tb_Calendar.removeAllViews();

        // Actualizar mes y año
        String month = monthNames[currentDate.getMonth()];
        tv_CalendarMonth.setText(month);
        String year = currentDate.getYear() + 1900 + "";
        tv_CalendarYear.setText(year);

        // Generar los días de la semana
        TableRow row = createRow();
        for(int i = 0; i < 7; i++){
            // Crear el elemento
            View view = inflate(getContext(), R.layout.control_calendar_day_header, null);

            // Obtener referencias
            TextView tv_CalendarDayHeader = view.findViewById(R.id.tv_CalendarDayHeader);

            // Inicializar propiedades
            String text = weekNames[i];
            tv_CalendarDayHeader.setText(text);
            view.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

            // Agregar el elemento al grid
            row.addView(view);
        }

        // Agregar la fila al grid
        tb_Calendar.addView(row);

        // Buscar el primer día del mes
        Date firstDay = new Date(currentDate.getYear(), currentDate.getMonth(), 1, 0, 0, 0);
        int firstDayWeek = firstDay.getDay();

        // Buscar el primer día a mostrar
        Date firstDayToShow = new Date(firstDay.getTime());
        firstDayToShow.setDate(firstDayToShow.getDate() - firstDayWeek);
        if (firstDayIsMonday){
            firstDayToShow.setDate(firstDayToShow.getDate() + 1);
        }

        // Buscar el último día del mes
        Date lastDay = new Date(currentDate.getYear(), currentDate.getMonth() + 1, 0, 0, 0, 0);
        int lastDayWeek = lastDay.getDay();

        // Buscar el último día a mostrar
        Date lastDayToShow = new Date(lastDay.getTime());
        lastDayToShow.setDate(lastDayToShow.getDate() + (6 - lastDayWeek));
        if (firstDayIsMonday){
            lastDayToShow.setDate(lastDayToShow.getDate() + 1);
        }

        // Generar los días del mes
        Date day = new Date(firstDayToShow.getTime());
        while(day.getTime() <= lastDayToShow.getTime()){
            // Crear la fila
            row = createRow();

            // Generar los días de la semana
            for(int i = 0; i < 7; i++){

                // Crear el elemento
                View view = inflate(getContext(), R.layout.control_calendar_day, null);

                // Obtener referencias
                TextView tv_CalendarDay = view.findViewById(R.id.tv_CalendarDay);
                View vw_CalendarSelector = view.findViewById(R.id.vw_CalendarSelector);
                View vw_CalendarMark = view.findViewById(R.id.vw_CalendarMark);

                // Inicializar propiedades
                String text = day.getDate() + "";
                tv_CalendarDay.setText(text);
                view.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
                tv_CalendarDay.setTextAppearance(R.style.calendar_Text);
                vw_CalendarMark.setVisibility(INVISIBLE);

                // Si es la fecha actual mostrar el selector
                if (day.getTime() == currentDate.getTime()){
                    vw_CalendarSelector.setVisibility(VISIBLE);
                    tv_CalendarDay.setTextAppearance(R.style.calendar_Text_Selected);
                }else{
                    vw_CalendarSelector.setVisibility(INVISIBLE);

                    // Si no es el mes actual aplicar transparencia
                    if (day.getMonth() != currentDate.getMonth()){
                        tv_CalendarDay.setAlpha(0.3f);
                    }

                    // Si es un día libre aplicar estilo
                    if (freeDays.contains(day.getDay())) {
                        tv_CalendarDay.setTextAppearance(R.style.calendar_Text_FreeDay);
                    }
                }

                if (this.onDayCreatedListener != null){
                    this.onDayCreatedListener.onDayCreated(view, new Day() {
                        {
                            date = day;
                            view = view;
                            viewMark = vw_CalendarMark;
                        }
                    });
                }

                view.setTag(new Date(day.getTime()));

                // Establecer eventos
                view.setOnClickListener(v -> {
                    Date d = (Date) v.getTag();
                    setCurrentDate(d);
                    renderCalendar();
                });

                // Agregar el elemento al grid
                row.addView(view);

                // Avanzar al siguiente día
                day.setDate(day.getDate() + 1);

            }

            // Agregar la fila al grid
            tb_Calendar.addView(row);

        }

    }

    private Date trunc(Date date){
        return new Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0, 0);
    }

}
