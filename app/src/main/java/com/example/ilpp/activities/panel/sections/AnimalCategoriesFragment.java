package com.example.ilpp.activities.panel.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ilpp.R;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.ModelAdapter;
import com.example.ilpp.models.AnimalCategory;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

public class AnimalCategoriesFragment extends Fragment {

    private ProgressBar pb_List;
    private GridView gv_List;

    public static void open(Fragment from) {
        NavHostFragment.findNavController(from).navigate(
            R.id.act_ToAnimalCategories
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_animal_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener referencias
        pb_List = view.findViewById(R.id.pb_AnimalCategories);
        gv_List = view.findViewById(R.id.gv_AnimalCategories);

        // Inicializar propiedades

        // Obtener las categorías
        loadDataCategories();
    }

    // Preparar el elemento de lista
    private void onListItemCreated(View view, AnimalCategory category) {

        // Obtener referencias
        TextView tv_Name = view.findViewById(R.id.tv_Name);
        ShapeableImageView iv_Image = view.findViewById(R.id.iv_Image);

        // Establecer valores
        tv_Name.setText(category.getNames());
        Picasso.get().load(category.getImageUrl()).into(iv_Image);

        // Establecer eventos
        view.setOnClickListener(v -> {
            AnimalCategoryFragment.open(this, category);
        });

    }

    private void loadDataCategories() {

        pb_List.setVisibility(View.VISIBLE);
        AnimalCategory.getList()
                .thenAccept(data -> {

                    if (data.isEmpty()) {
                        Message.show(getContext(), getString(R.string.no_data));
                        return;
                    }

                    // Crea el adaptador
                    ModelAdapter<AnimalCategory> adapter = new ModelAdapter<>(getContext(), data, R.layout.gridview_item_category);
                    adapter.setOnCreateViewListener((view, position) -> {
                        onListItemCreated(view, adapter.getItem(position));
                    });
                    gv_List.setAdapter(adapter);

                    pb_List.setVisibility(View.GONE);
                })
                .exceptionally(e -> {
                    Message.exception(getContext(), e, "Error al obtener los datos");
                    return null;
                })
                .whenComplete((v,e) -> {
                    pb_List.setVisibility(View.GONE);
                });

    }

}