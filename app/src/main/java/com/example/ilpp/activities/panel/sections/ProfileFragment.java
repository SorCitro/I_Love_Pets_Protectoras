package com.example.ilpp.activities.panel.sections;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ilpp.R;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.classes.Validate;
import com.example.ilpp.models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.CompletableFuture;

public class ProfileFragment extends BaseProfileFragment {

    private TextInputEditText et_Surname;

    public static void open(NavController from) {
        from.navigate(
                R.id.act_ToProfile,
                new Bundle()
        );
    }
    public static void open(Fragment from){
        open(NavHostFragment.findNavController(from));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener referencias
        et_Surname = view.findViewById(R.id.et_Surname);

        User data = UserManager.getUser().getUserData();
        // Inicializar propiedades
        et_Surname.setText(data.getSurname());

        // Configurar filtro

        // Establecer eventos

    }

    @Override
    protected CompletableFuture<Boolean> save() {

        String name = et_Name.getText().toString();
        String surname = et_Surname.getText().toString();

        if (Validate.name(et_Name, name)
                && Validate.surname(et_Surname, surname)) {

            CompletableFuture<Boolean> future = new CompletableFuture<>();

            User user = UserManager.getUser().getUserData();
            user.setName(name);
            Uri photo = iu_ProfilePicture.getImageUrl();
            if (photo != null) user.setPhotoUrl(photo.toString());
            user.setSurname(surname);
            user.update()
                    .thenAccept(a -> future.complete(true))
                    .exceptionally(e -> {
                        future.complete(false);
                        return null;
                    });

            return future;

        }else{
            return CompletableFuture.completedFuture(false);
        }

    }
}