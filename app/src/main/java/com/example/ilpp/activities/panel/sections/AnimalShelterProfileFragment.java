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
import com.example.ilpp.models.AnimalShelter;
import com.example.ilpp.models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.CompletableFuture;

public class AnimalShelterProfileFragment extends BaseProfileFragment {

    private TextInputEditText et_Address, et_Phone, et_City, et_PostalCode;

    public static void open(NavController from) {
        from.navigate(
                R.id.act_ToAnimalShelterProfile,
                new Bundle()
        );
    }
    public static void open(Fragment from){
        open(NavHostFragment.findNavController(from));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_animal_shelter_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener referencias
        et_Address = view.findViewById(R.id.et_Address);
        et_Phone = view.findViewById(R.id.et_Phone);
        et_City = view.findViewById(R.id.et_City);
        et_PostalCode = view.findViewById(R.id.et_PostalCode);

        AnimalShelter data = UserManager.getUser().getAnimalShelterData();

        // Inicializar propiedades
        et_Address.setText(data.getAddress().getAddress());
        et_Phone.setText(data.getPhone());
        et_City.setText(data.getAddress().getCity());
        et_PostalCode.setText(data.getAddress().getPostalCode());

        // Configurar filtro

        // Establecer eventos

    }

    @Override
    protected CompletableFuture<Boolean> save() {

        String name = et_Name.getText().toString();
        String address = et_Address.getText().toString();
        String phone = et_Phone.getText().toString();
        String city = et_City.getText().toString();
        String postalCode = et_PostalCode.getText().toString();

        if (Validate.name(et_Name, name)
                && Validate.address(et_Address, address)
                && Validate.city(et_City, city)
        ) {

            CompletableFuture<Boolean> future = new CompletableFuture<>();

            Uri photo = iu_ProfilePicture.getImageUrl();
            String photoUrl = photo != null ? photo.toString() : null;

            User user = UserManager.getUser().getUserData();
            user.setName(name);
            user.setPhotoUrl(photoUrl);
            user.update();

            AnimalShelter animalShelter = UserManager.getUser().getAnimalShelterData();
            animalShelter.setName(name);
            animalShelter.setImageUrl(photoUrl);
            animalShelter.setPhone(phone);
            animalShelter.getAddress().setAddress(address);
            animalShelter.getAddress().setCity(city);
            animalShelter.getAddress().setPostalCode(postalCode);

            CompletableFuture.allOf(
                user.update(),
                animalShelter.update()
            ).thenRun(() -> future.complete(true))
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                }
            );

            return future;

        }else{
            return CompletableFuture.completedFuture(false);
        }

    }

}