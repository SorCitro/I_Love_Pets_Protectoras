package com.example.ilpp.activities.panel.sections;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ilpp.R;
import com.example.ilpp.classes.Format;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.classes.Validate;
import com.example.ilpp.controls.ImageUploader;
import com.example.ilpp.models.Animal;
import com.example.ilpp.models.AnimalCategory;
import com.example.ilpp.models.AnimalSchedule;
import com.example.ilpp.models.AnimalShelter;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AnimalInsertFragment extends Fragment {

    protected ImageUploader iu_Image;
    protected MaterialAutoCompleteTextView et_Category;
    protected TextInputEditText et_Name;
    protected TextInputEditText et_Birthdate;
    protected TextInputEditText et_Gender;
    protected TextInputEditText et_Breed;
    protected TextInputEditText et_Weight;
    protected TextInputEditText et_Address;
    protected TextInputEditText et_City;
    protected TextInputEditText et_PostalCode;
    protected TextInputEditText et_Description;
    protected CheckBox cb_Walks;
    protected CheckBox cb_Hosting;
    protected CheckBox cb_Adoption;
    protected TextInputEditText et_StartTime;
    protected TextInputEditText et_EndTime;
    protected CheckBox[] cb_Days;
    protected TextView tv_Title;
    private View btn_Save;

    protected List<AnimalCategory> categories;

    public static void open(NavController from) {
        from.navigate(
                R.id.act_ToAnimalInsert,
                new Bundle()
        );
    }
    public static void open(Fragment from){
        open(NavHostFragment.findNavController(from));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_animal_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener referencias
        et_Name = view.findViewById(R.id.et_Name);
        et_Category = view.findViewById(R.id.et_Category);
        et_Birthdate = view.findViewById(R.id.et_Birthdate);
        et_Gender = view.findViewById(R.id.et_Gender);
        et_Breed = view.findViewById(R.id.et_Breed);
        et_Weight = view.findViewById(R.id.et_Weight);
        et_Address = view.findViewById(R.id.et_Address);
        et_City = view.findViewById(R.id.et_City);
        iu_Image = view.findViewById(R.id.iu_Image);
        et_PostalCode = view.findViewById(R.id.et_PostalCode);
        tv_Title = view.findViewById(R.id.tv_Title);
        et_Description = view.findViewById(R.id.et_Description);

        cb_Walks = view.findViewById(R.id.cb_Walks);
        cb_Hosting = view.findViewById(R.id.cb_Hosting);
        cb_Adoption = view.findViewById(R.id.cb_Adoption);
        et_StartTime = view.findViewById(R.id.et_StartTime);
        et_EndTime = view.findViewById(R.id.et_EndTime);

        cb_Days = new CheckBox[]{
            view.findViewById(R.id.cb_Day_Sun),
            view.findViewById(R.id.cb_Day_Mon),
            view.findViewById(R.id.cb_Day_Tue),
            view.findViewById(R.id.cb_Day_Wed),
            view.findViewById(R.id.cb_Day_Thu),
            view.findViewById(R.id.cb_Day_Fri),
            view.findViewById(R.id.cb_Day_Sat)
        };

        btn_Save = view.findViewById(R.id.btn_Save);

        // Inicializar propiedades
        AnimalCategory.getList().whenComplete((list, error) -> {
            if (error != null) {
                Message.exception(getContext(), error, "Error al obtener las categorías");
                return;
            }

            categories = list;
            ArrayList<String> names = new ArrayList<>();
            for (AnimalCategory category : list) {
                names.add(category.getName());
            }

            et_Category.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, names));
            doLoad();
        });

        // Establecer eventos
        btn_Save.setOnClickListener(v -> sendForm());

    }

    private void sendForm(){

        String name = et_Name.getText().toString().trim();
        String category = et_Category.getText().toString().trim();
        String birthdate = et_Birthdate.getText().toString().trim();

        String description = et_Description.getText().toString().trim();

        String gender = et_Gender.getText().toString().trim();
        String breed = et_Breed.getText().toString().trim();
        String weight = et_Weight.getText().toString().trim();

        String address = et_Address.getText().toString().trim();
        String city = et_City.getText().toString().trim();
        String postalCode = et_PostalCode.getText().toString().trim();

        boolean walks = cb_Walks.isChecked();
        boolean hosting = cb_Hosting.isChecked();
        boolean adoption = cb_Adoption.isChecked();

        // Validar datos

        if (!(Validate.name(et_Name, name))) return;

        String category_id = null;
        for (AnimalCategory animalCategory : categories) {
            if (animalCategory.getName().equals(category)) {
                category_id = animalCategory.getId();
                break;
            }
        }

        if (!Validate.apply(et_Category, category_id != null, "La categoría no es válida")) return;
        if (!Validate.date(et_Birthdate, birthdate)) return;

        Animal animal = new Animal();
        animal.setAnimalShelterId(UserManager.getUser().getAnimalShelterData().getId());
        animal.setName(et_Name.getText().toString());
        animal.setAnimalCategoryId(category_id);
        animal.setDescription(description);
        Uri imageUrl = iu_Image.getImageUrl();
        if (imageUrl != null) animal.setImageUrl(imageUrl.toString());

        animal.setBirthdate(Format.toDate(birthdate));

        animal.getDetails().setGender(gender);
        animal.getDetails().setBreed(breed);
        animal.getDetails().setWeight(weight);
        animal.getDetails().setType(category);

        animal.getAddress().setAddress(address);
        animal.getAddress().setCity(city);
        animal.getAddress().setPostalCode(postalCode);

        animal.getServices().setWalks(walks);
        animal.getServices().setHosting(hosting);
        animal.getServices().setAdoption(adoption);

        AnimalSchedule schedule = animal.getSchedule();
        schedule.setStartTime(et_StartTime.getText().toString());
        schedule.setEndTime(et_EndTime.getText().toString());

        for (int i = 0; i < cb_Days.length; i++) {
            schedule.setDay(i, cb_Days[i].isChecked());
        }

        // Guardar
        btn_Save.setEnabled(false);

        doSave(animal)
                .whenComplete((result, error) -> {
                    btn_Save.setEnabled(true);

                    if (error != null) {
                        Message.exception(getContext(), error, "Error al guardar el animal");
                        return;
                    }

                    Message.show(getContext(), "Animal guardado correctamente");

                    doEnd();
                });

    }

    protected void doLoad(){
        AnimalShelter data = UserManager.getUser().getAnimalShelterData();
        et_Address.setText(data.getAddress().getAddress());
        et_City.setText(data.getAddress().getCity());
        et_PostalCode.setText(data.getAddress().getPostalCode());
    }

    protected CompletableFuture<Void> doSave(Animal animal){
        return animal.create();
    }

    protected void doEnd(){
        NavController navController = NavHostFragment.findNavController(this);

        // Eliminar el fragmento actual de la pila
        navController.popBackStack();

        MyAnimalsFragment.open(this);
    }

}