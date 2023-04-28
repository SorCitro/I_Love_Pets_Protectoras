package com.example.ilpp.activities.panel.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ilpp.R;
import com.example.ilpp.activities.panel.sections.AnimalCategoriesFragment;
import com.example.ilpp.activities.panel.sections.AnimalCategoryFragment;
import com.example.ilpp.activities.panel.sections.AnimalProfileFragment;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.controls.AnimalSearch;
import com.example.ilpp.classes.Message;
import com.example.ilpp.models.AnimalCategory;
import com.example.ilpp.models.User;

public class HomeFragment extends Fragment {

    public static void open(NavController from) {
        from.navigate(
                R.id.act_ToHome,
                new Bundle()
        );
    }
    public static void open(Fragment from){
        open(NavHostFragment.findNavController(from));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener referencias
        View btn_Cats = view.findViewById(R.id.btn_Cats);
        View btn_Dogs = view.findViewById(R.id.btn_Dogs);
        View btn_Others = view.findViewById(R.id.btn_Others);
        View br_AnimalsManager = view.findViewById(R.id.br_AnimalsManager);
        View tv_ViewAllCategories = view.findViewById(R.id.tv_ViewAllCategories);
        ProgressBar pb = view.findViewById(R.id.pb_Home);
        AnimalSearch vw_AnimalSearch = view.findViewById(R.id.vw_AnimalSearch);

        vw_AnimalSearch.initData();
        User user = UserManager.getUser().getUserData();
        if (!user.getIsAnimalShelter())
            br_AnimalsManager.setVisibility(View.GONE);


        // Establecer eventos
        vw_AnimalSearch.setOnItemClicked(animal -> {
            AnimalProfileFragment.open(this, animal);
        });
        tv_ViewAllCategories.setOnClickListener(v -> {
            AnimalCategoriesFragment.open(this);
        });
        btn_Cats.setOnClickListener(v -> { openCategory("cat", pb); });
        btn_Dogs.setOnClickListener(v -> { openCategory("dog", pb); });
        btn_Others.setOnClickListener(v -> {
            AnimalCategoriesFragment.open(this);
        });

    }

    private void openCategory(String categoryId, ProgressBar progress){

        getView().setEnabled(false);
        progress.setVisibility(View.VISIBLE);

        AnimalCategory.get(categoryId)
            .thenAccept(category -> {
                AnimalCategoryFragment.open(this, category);
            })
            .exceptionally(e -> {
                Message.exception(getContext(), e, "Error al obtener la categoría");
                return null;
            })
            .whenComplete((r, e) -> {
                getView().setEnabled(true);
                progress.setVisibility(View.GONE);
            });

    }

}