package com.example.ilpp.activities.panel.sections;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ilpp.R;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.controls.ImageUploader;
import com.example.ilpp.models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.CompletableFuture;

public abstract class BaseProfileFragment extends Fragment {

    protected TextInputEditText et_Email, et_Name;
    protected View btn_Save;
    protected ImageUploader iu_ProfilePicture;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener referencias
        et_Email = view.findViewById(R.id.et_Email);
        et_Name = view.findViewById(R.id.et_Name);
        btn_Save = view.findViewById(R.id.btn_Save);
        ProgressBar pb_Profile = view.findViewById(R.id.pb_Profile);
        iu_ProfilePicture = view.findViewById(R.id.iu_ProfilePicture);

        User data = UserManager.getUser().getUserData();

        // Inicializar propiedades
        String photoUrl = data.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty())
            iu_ProfilePicture.setImageUrl(Uri.parse(photoUrl));
        et_Email.setText(data.getEmail());
        et_Name.setText(data.getName());
        et_Email.setEnabled(false); // El email no se puede modificar
        pb_Profile.setVisibility(View.GONE);

        // Establecer eventos
        btn_Save.setOnClickListener(v -> {
            pb_Profile.setVisibility(View.VISIBLE);
            btn_Save.setEnabled(false);
            this.save().whenComplete((ok, e) -> {
                pb_Profile.setVisibility(View.GONE);
                btn_Save.setEnabled(true);
                if (e != null){
                    Message.exception(getContext(), e, "Error al guardar los datos");
                    return;
                }
                if (ok){
                    // Cerrar el fragmento
                    HomeFragment.open(this);
                }
            });
        });

    }

    protected abstract CompletableFuture<Boolean> save();
}
