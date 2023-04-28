package com.example.ilpp.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.example.ilpp.R;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.ModelAdapter;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.classes.Utils;
import com.example.ilpp.models.Animal;
import com.example.ilpp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class AnimalSearch extends RelativeLayout {

    private GridView gv_List;
    private ProgressBar pb_List;
    private ViewGroup vw_ControlContent;
    public final Filter filter = new Filter();

    public interface OnItemClicked { void onItemClicked(Animal animal); }
    private OnItemClicked onItemClicked;
    public void setOnItemClicked(OnItemClicked onItemClicked) { this.onItemClicked = onItemClicked; }

    public AnimalSearch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AnimalSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimalSearch(Context context) {
        super(context);
        init();
    }

    private CompletableFuture<?> lastTask;
    private void init() {

        // Inflar la vista con el layout
        View view = inflate(getContext(), R.layout.control_search_animals, this);

        // Obtener referencias
        pb_List = view.findViewById(R.id.pb_Animals);
        gv_List = view.findViewById(R.id.gv_Animals);
        vw_ControlContent = view.findViewById(R.id.vw_ControlContent);
        SearchView sv_Search = view.findViewById(R.id.sv_Search);

        // Inicializar propiedades
        sv_Search.setIconified(true);
        sv_Search.setIconifiedByDefault(false);

        // Establecer eventos
        sv_Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter.setSearch(query);
                loadData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter.setSearch(newText);
                loadData();
                return false;
            }
        });

    }

    public void initData(){
        // Obtener los datos de animales
       // Utils.generateRandomData().thenAccept(a -> {
        if (!isInEditMode())
            loadData();
        //});
    }

    @Override
    public void addView(View child) {
        if (vw_ControlContent != null) {
            vw_ControlContent.addView(child);
        }else{
            super.addView(child);
        }
    }

    @Override
    public void addView(View child, int index) {
        if (vw_ControlContent != null) {
            vw_ControlContent.addView(child, index);
        }else{
            super.addView(child, index);
        }
    }

    @Override
    public void addView(View child, int width, int height) {
        if (vw_ControlContent != null) {
            vw_ControlContent.addView(child, width, height);
        }else{
            super.addView(child, width, height);
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (vw_ControlContent != null) {
            vw_ControlContent.addView(child, params);
        }else{
            super.addView(child, params);
        }
    }

    // Preparar el elemento de lista
    private void onListItemCreated(View view, Animal animal, @Nullable List<User.AnimalFav> favs){
        if (favs == null) {
            favs = new ArrayList<>();
        }

        // Obtener referencias
        ImageView iv_Picture = view.findViewById(R.id.iv_Image);
        TextView tv_Caption = view.findViewById(R.id.tv_Caption);
        TextView tv_Details = view.findViewById(R.id.tv_Details);
        TextView tv_Address = view.findViewById(R.id.tv_Address);
        ButtonFav btn_Fav = view.findViewById(R.id.btn_Fav);

        // Inicializar propiedades
        btn_Fav.setFavored(favs.stream().anyMatch(f -> f.getAnimalId().equals(animal.getId())));
        iv_Picture.setImageResource(0); // Vaciar imagen de prueba
        tv_Caption.setText(animal.getName());
        tv_Details.setText(
                String.format(
                    "%s, %s",
                    animal.getDetails().getType(),
                    animal.getDetails().getWeight()
                )
        );
        tv_Address.setText(animal.getAddress().getLongDisplay());
        Picasso.get().load(animal.getImageUrl()).into(iv_Picture);

        // Establecer eventos
        view.setOnClickListener(v -> {
            if (onItemClicked != null) {
                onItemClicked.onItemClicked(animal);
            }
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
    }

    private void loadData(){

        if (lastTask != null) {
            lastTask.cancel(true);
            lastTask = null;
        }

        pb_List.setVisibility(View.VISIBLE);

        // Asegurar que el los favoritos estén cargados en caché
        CompletableFuture<List<User.AnimalFav>> taskFavs = UserManager.getUser().getUserData().getAnimalFavs();
        // Obtener los datos de animales
        String categoryId = this.filter.getCategoryId();
        String animalShelterId = this.filter.getAnimalShelterId();
        Boolean onlyFavs = this.filter.getOnlyFavorites();
        Boolean onlyAdoption = this.filter.getOnlyAdoption();
        String searchRaw = this.filter.getSearch();

        if (searchRaw != null && searchRaw.isEmpty()) searchRaw = null;
        if (searchRaw != null) searchRaw = searchRaw.toLowerCase(Locale.ROOT).trim();

        String search = searchRaw;

        CompletableFuture<List<Animal>> taskList = Animal.getList(query -> {
            if (categoryId != null && !categoryId.isEmpty()) {
                query = query.whereEqualTo("animalCategory", categoryId);
            }

            if (animalShelterId != null && !animalShelterId.isEmpty()) {
                query = query.whereEqualTo("animalShelter", animalShelterId);
            }

            if (onlyAdoption != null && onlyAdoption) {
                query = query.whereEqualTo("services.adoption", true);
            }

            return query;
        });
        ;
        taskList
            .thenAccept(data -> {

                if (data.isEmpty()) {
                    gv_List.setAdapter(null);
                    Message.show(getContext(), getResources().getString(R.string.no_data));
                    return;
                }

                // Esperar a que los favoritos estén cargados
                taskFavs
                    .whenComplete((favs, e) -> {

                        // Aplicar el filtro manualmente
                        List<Animal> filteredData = new ArrayList<>();

                        for (Animal animal : data) {
                            boolean valid = true;

                            // Aplicar filtro de texto
                            if (search != null && !search.isEmpty()) {
                                if (!animal.isSearchResult(search)) {
                                    valid = false;
                                }
                            }

                            // Aplicar filtro de favoritos
                            if (onlyFavs) {
                                if (!favs.stream().anyMatch(f -> f.getAnimalId().equals(animal.getId()))) {
                                    valid = false;
                                }
                            }

                            if (valid) {
                                filteredData.add(animal);
                            }

                        }

                        if (filteredData.isEmpty()) {
                            gv_List.setAdapter(null);
                            Message.show(getContext(), getResources().getString(R.string.no_data));
                            return;
                        }

                        // Crea el adaptador
                        ModelAdapter<Animal> adapter = new ModelAdapter<>(getContext(), filteredData, R.layout.gridview_item_animal);
                        adapter.setOnCreateViewListener((view, position) -> {
                            onListItemCreated(view, adapter.getItem(position), favs);
                        });
                        gv_List.setAdapter(adapter);
                        pb_List.setVisibility(View.GONE);
                    });
            })
            .exceptionally(e -> {
                pb_List.setVisibility(View.GONE);
                Message.exception(getContext(), e, "Error al obtener los datos");
                return null;
            });

        // Esperar a que las tareas terminen
        lastTask = CompletableFuture.allOf(
            taskList,
            taskFavs
        ).thenAccept(a -> pb_List.setVisibility(View.GONE))
        .whenComplete((r, e) -> {
            lastTask = null;
        });

    }

    public static class Filter {
        private String categoryId;

        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

        private Boolean onlyFavorites = false;
        public Boolean getOnlyFavorites() { return onlyFavorites; }

        private String animalShelterId;
        public String getAnimalShelterId() { return animalShelterId; }
        public void setAnimalShelterId(String animalShelterId) { this.animalShelterId = animalShelterId; }

        public void setOnlyFavorites(Boolean onlyFavorites) { this.onlyFavorites = onlyFavorites; }

        private Boolean onlyAdoption = false;
        public Boolean getOnlyAdoption() { return onlyAdoption; }
        public void setOnlyAdoption(Boolean onlyAdoption) { this.onlyAdoption = onlyAdoption; }

        private String search;

        public String getSearch() { return search; }
        public void setSearch(String search) { this.search = search; }
    }

}