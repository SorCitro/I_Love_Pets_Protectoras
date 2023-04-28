package com.example.ilpp.activities.panel.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ilpp.R;
import com.example.ilpp.controls.AnimalSearch;
import com.example.ilpp.models.AnimalCategory;

public class AnimalCategoryFragment extends Fragment {

    public static void open(Fragment from, AnimalCategory category){
        NavHostFragment.findNavController(from).navigate(
            R.id.act_ToAnimalCategory,
                category.toBundle()
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_animal_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AnimalCategory category = new AnimalCategory();
        category.fromBundle(getArguments());

        // Obtener referencias
        AnimalSearch vw_AnimalSearch = view.findViewById(R.id.vw_AnimalSearch);
        TextView tv_CategoryName = view.findViewById(R.id.tv_CategoryName);

        // Inicializar propiedades
        tv_CategoryName.setText(category.getNames());

        // Configurar filtro
        vw_AnimalSearch.filter.setCategoryId(category.getId());
        vw_AnimalSearch.initData();

        // Establecer eventos
        vw_AnimalSearch.setOnItemClicked(animal -> {
            AnimalProfileFragment.open(this, animal);
        });

    }

}