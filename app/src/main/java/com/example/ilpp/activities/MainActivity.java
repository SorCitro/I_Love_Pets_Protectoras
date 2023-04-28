package com.example.ilpp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ilpp.R;
import com.example.ilpp.activities.portal.LoginActivity;
import com.example.ilpp.classes.Scene;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.classes.Utils;

public class MainActivity extends AppCompatActivity {

    private static final int DURACION_ANIM = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ocultar barra de estado
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Obtener referencias
        TextView tv_ILP = findViewById(R.id.tv_ILP);
        TextView tv_Prote = findViewById(R.id.tv_Prote);
        ImageView iv_Portada = findViewById(R.id.iv_Portada);

        // Animaciones

        View[] views = {iv_Portada, tv_ILP, tv_Prote};

        int duration = DURACION_ANIM;
        int frame = duration / (views.length);

        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein_down);
            anim.setDuration(frame);
            anim.setStartOffset(i * frame);
            view.setAnimation(anim);
        }

        // Esperar animaciones
        Utils.setTimeout(duration, () -> {
            // Comprobar si el usuario ya está logueado
            UserManager.loginLastUser(MainActivity.this)
                    .whenComplete((u, t) -> {
                        // Ir a la actividad correspondiente
                        if (UserManager.isLogged()) {
                            Scene.goAndFinish(this, Scene.MainUserActivityClass);
                        } else {
                            Scene.goAndFinish(this, LoginActivity.class);
                        }

                    });
        });
    }
}
