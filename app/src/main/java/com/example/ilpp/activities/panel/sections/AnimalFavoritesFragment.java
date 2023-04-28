package com.example.ilpp.activities.panel.sections;

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
import com.example.ilpp.controls.AnimalSearch;

public class AnimalFavoritesFragment extends Fragment {

    public static void open(NavController from) {
        from.navigate(
                R.id.act_ToAnimalFavorites,
                new Bundle()
        );
    }
    public static void open(Fragment from){
        open(NavHostFragment.findNavController(from));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_animal_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener referencias
        AnimalSearch vw_AnimalSearch = view.findViewById(R.id.vw_AnimalSearch);

        // Inicializar propiedades

        // Configurar filtro
        vw_AnimalSearch.filter.setOnlyFavorites(true);
        vw_AnimalSearch.initData();

        // Establecer eventos
        vw_AnimalSearch.setOnItemClicked(animal -> {
            AnimalProfileFragment.open(this, animal);
        });

    }

}