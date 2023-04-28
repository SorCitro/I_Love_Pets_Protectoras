package com.example.ilpp.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.ilpp.R;
import com.example.ilpp.activities.panel.sections.AnimalInsertFragment;
import com.example.ilpp.activities.panel.sections.CalendarFragment;

public class AnimalsManagerBar extends LinearLayout {

    public AnimalsManagerBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AnimalsManagerBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimalsManagerBar(Context context) {
        super(context);
        init();
    }

    private void init(){

        // Inflar la vista con el layout
        View view = inflate(getContext(), R.layout.control_animals_manager_bar, this);

        // Obtener referencias
        View btn_InsertNew = view.findViewById(R.id.btn_InsertNew);
        View btn_Calendar = view.findViewById(R.id.btn_Calendar);

        // Establecer eventos

        btn_InsertNew.setOnClickListener(v -> {
            Fragment frag = findFragment();
            if (frag != null) AnimalInsertFragment.open(frag);
        });
        btn_Calendar.setOnClickListener(v -> {
            Fragment frag = findFragment();
            if (frag != null) CalendarFragment.open(frag);
        });

    }

    private Fragment findFragment(){
        ViewParent parent = getParent();
        while (parent != null && !(parent instanceof FragmentContainerView))
            parent = parent.getParent();
        return ((FragmentContainerView) parent).getFragment();
    }

}