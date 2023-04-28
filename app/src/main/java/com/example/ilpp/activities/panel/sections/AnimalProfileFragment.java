package com.example.ilpp.activities.panel.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ilpp.R;
import com.example.ilpp.activities.panel.dialogs.AnimalWalkBookingDialog;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.controls.ButtonFav;
import com.example.ilpp.models.Animal;
import com.example.ilpp.models.AnimalShelter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

public class AnimalProfileFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_animal_profile, container, false);
    }

    private com.google.android.material.imageview.ShapeableImageView iv_Profile;
    private ImageView im_ProfileBar;
    private TextView tv_Name, tv_Desc, tv_Age, tv_Gender, tv_Address, tv_Weight, tv_ProfileBarName;
    private ButtonFav btn_Fav;
    private Button btn_Walk;
    private ScrollView sv_Content;
    private ShimmerFrameLayout sf_AnimalShelter;
    private View ll_AnimalShelter, btn_Edit, btn_Delete;

    public static void open(Fragment from, Animal animal){
        NavHostFragment.findNavController(from).navigate(
                R.id.act_ToAnimalProfile,
                animal.toBundle()
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener referencias
        iv_Profile = view.findViewById(R.id.iv_Profile);
        tv_Name = view.findViewById(R.id.tv_Name);
        btn_Fav = view.findViewById(R.id.btn_Fav);
        btn_Edit = view.findViewById(R.id.btn_Edit);
        btn_Delete = view.findViewById(R.id.btn_Delete);
        tv_Address = view.findViewById(R.id.tv_Address);
        tv_Desc = view.findViewById(R.id.tv_Desc);
        tv_Gender = view.findViewById(R.id.tv_Gender);
        tv_Age = view.findViewById(R.id.tv_Age);
        tv_Weight = view.findViewById(R.id.tv_Weight);
        btn_Walk = view.findViewById(R.id.btn_Walk);
        sv_Content = view.findViewById(R.id.sv_Content);
        ll_AnimalShelter = view.findViewById(R.id.ll_AnimalShelter);
        sf_AnimalShelter = view.findViewById(R.id.sf_AnimalShelter);
        tv_ProfileBarName = view.findViewById(R.id.tv_ProfileBarName);
        im_ProfileBar = view.findViewById(R.id.im_ProfileBar);


        Bundle args = getArguments();

        // Inicializar propiedades
        tv_Name.setText(args.getString("name"));
        tv_Address.setText("");
        tv_Desc.setText("");
        tv_Gender.setText("");
        tv_Age.setText("");
        tv_Weight.setText("");
        iv_Profile.setImageResource(0);
        btn_Fav.setVisibility(View.INVISIBLE);
        btn_Delete.setVisibility(View.GONE);
        btn_Edit.setVisibility(View.GONE);

        Picasso.get().load(args.getString("imageUrl")).into(iv_Profile);

        Animal animal = new Animal();
        animal.fromBundle(args);

        if (!animal.getServices().getWalks()) {
            btn_Walk.setVisibility(View.GONE);
        }

        UserManager.User user = UserManager.getUser();

        if (user.getUserData().getIsAnimalShelter() &&
                user.getAnimalShelterData().getId().equals(animal.getAnimalShelterId())) {
            btn_Delete.setVisibility(View.VISIBLE);
            btn_Edit.setVisibility(View.VISIBLE);
        }

        updateAnimalProfile(animal);
        loadAnimalShelter(animal.getAnimalShelterId());

        // Obtener estado de favorito
        UserManager.getUser().getUserData().getAnimalFavs()
            .thenAccept(favs -> {
                Boolean isFavored = favs.stream().anyMatch(f -> f.getAnimalId().equals(animal.getId()));
                btn_Fav.setFavored(isFavored);
                btn_Fav.setVisibility(View.VISIBLE);
            });

        // Establecer eventos
        btn_Delete.setOnClickListener(v -> {
            Message.confirm(getContext(), "¿Estás seguro de que quieres eliminar este animal?", (dialog, which) -> {
                animal.delete()
                    .whenComplete((r, e) -> {
                        if (e != null) {
                            Message.exception(getContext(), e, "No se ha podido eliminar el animal");
                            return;
                        }

                        Message.show(getContext(), "Animal eliminado");

                        NavController navController = NavHostFragment.findNavController(this);

                        // Eliminar el fragmento actual de la pila
                        navController.popBackStack();

                        MyAnimalsFragment.open(this);
                    });
            });
        });
        btn_Edit.setOnClickListener(v -> {
            AnimalEditFragment.open(this, animal);
        });
        btn_Fav.setOnClickListener(v -> {
            btn_Fav.setEnabled(false);
            Boolean isFavored = !btn_Fav.isFavored();
            UserManager.getUser().getUserData().setAnimalFav(animal.getId(), isFavored)
                .thenAccept(a -> {
                    btn_Fav.setFavored(isFavored);
                })
                .whenComplete((r, e) -> {
                    btn_Fav.setEnabled(true);
                });
        });
        btn_Walk.setOnClickListener(v -> {
            AnimalWalkBookingDialog dialog = new AnimalWalkBookingDialog();
            dialog.setAnimal(animal);
            dialog.show(getParentFragmentManager(), null);
        });
    }

    private void updateAnimalProfile(Animal animal) {

        Picasso.get().load(animal.getImageUrl()).into(iv_Profile);
        tv_Name.setText(animal.getName());
        tv_Address.setText(animal.getAddress().getLongDisplay());
        tv_Desc.setText(animal.getDescription());
        tv_Weight.setText(animal.getDetails().getWeight());
        tv_Age.setText(String.format("%s años", animal.getAge()));
        tv_Gender.setText(animal.getDetails().getGender());

    }

    private void loadAnimalShelter(String animalShelterId) {
        sf_AnimalShelter.setVisibility(View.VISIBLE);
        ll_AnimalShelter.setVisibility(View.GONE);
        sf_AnimalShelter.startShimmer();

        AnimalShelter.get(animalShelterId)
            .thenAccept(this::onAnimalShelterLoaded)
            .exceptionally(e -> {
                Message.exception(getContext(), e, "Error al cargar el perfil del refugio");
                return null;
            })
            .whenComplete((v, e) -> {
                if (e != null) return;
                sf_AnimalShelter.stopShimmer();
                sf_AnimalShelter.setVisibility(View.GONE);
                ll_AnimalShelter.setVisibility(View.VISIBLE);
            });
    }

    private void onAnimalShelterLoaded(AnimalShelter data) {
        String url = data.getImageUrl();
        if (url != null && !url.isEmpty()) Picasso.get().load(url).into(im_ProfileBar);
        tv_ProfileBarName.setText(data.getName());
    }

}