package com.example.ilpp.controls;

import com.example.ilpp.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Botón de favorito que permite intercambiar entre dos estados
 */
public class ButtonFav extends LinearLayout {

    private View btn_InnerFav;
    private View btn_InnerFavUndo;

    private Boolean isFavored = false;
    public Boolean isFavored() { return isFavored; }
    public void setFavored(Boolean isFavored) {
        this.isFavored = isFavored;
        this.update();
    }

    public ButtonFav(Context context) {
        this(context, null);
    }

    public ButtonFav(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonFav(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        // Obtener atributos
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ButtonFav,
                0, R.style.button_Fav);
        try {
            isFavored = a.getBoolean(R.styleable.ButtonFav_isFavored, false);
        } finally {
            a.recycle();
        }

        // Inflar la vista con el layout
        View view = inflate(getContext(), R.layout.control_button_fav, this);

        // Obtener referencias
        btn_InnerFav = view.findViewById(R.id.btn_InnerFav);
        btn_InnerFavUndo = view.findViewById(R.id.btn_InnerFavUndo);

        // Actualizar estado
        this.update();

        // Establecer eventos
        btn_InnerFav.setOnClickListener(v -> {
            this.callOnClick();
        });
        btn_InnerFavUndo.setOnClickListener(v -> {
            this.callOnClick();
        });
    }

    public void update(){
        if (this.isFavored){
            btn_InnerFav.setVisibility(INVISIBLE);
            btn_InnerFavUndo.setVisibility(VISIBLE);
        } else {
            btn_InnerFav.setVisibility(VISIBLE);
            btn_InnerFavUndo.setVisibility(INVISIBLE);
        }
    }

}