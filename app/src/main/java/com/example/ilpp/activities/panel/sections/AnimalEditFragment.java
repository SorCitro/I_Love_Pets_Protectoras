package com.example.ilpp.activities.panel.sections;

import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ilpp.R;
import com.example.ilpp.classes.Format;
import com.example.ilpp.models.Animal;
import com.example.ilpp.models.AnimalCategory;

import java.util.concurrent.CompletableFuture;

public class AnimalEditFragment extends AnimalInsertFragment {

    private Animal animal;

    public static void open(NavController from, Animal animal) {
        from.navigate(
            R.id.act_ToAnimalEdit,
            animal.toBundle()
        );
    }
    public static void open(Fragment from, Animal animal){
        open(NavHostFragment.findNavController(from), animal);
    }

    @Override
    protected void doLoad() {
        super.doLoad();

        Animal animal = new Animal();
        animal.fromBundle(getArguments());
        this.animal = animal;

        String categoryName = null;
        String categoryId = animal.getAnimalCategoryId();
        if (categoryId != null && !categoryId.isEmpty()) {
            for (AnimalCategory category : categories) {
                if (category.getId().equals(categoryId)) {
                    categoryName = category.getName();
                    break;
                }
            }
        }

        String imageUrl = animal.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            iu_Image.setImageUrl(Uri.parse(imageUrl));
        }
        tv_Title.setText(getString(R.string.edit));
        et_Category.setText(categoryName);
        et_Name.setText(animal.getName());
        et_Description.setText(animal.getDescription());
        et_Birthdate.setText(Format.toDateString(animal.getBirthdate()));
        et_Gender.setText(animal.getDetails().getGender());
        et_Breed.setText(animal.getDetails().getBreed());
        et_Weight.setText(animal.getDetails().getWeight());
        et_Address.setText(animal.getAddress().getAddress());
        et_City.setText(animal.getAddress().getCity());
        et_PostalCode.setText(animal.getAddress().getPostalCode());
        cb_Walks.setChecked(animal.getServices().getWalks());
        cb_Hosting.setChecked(animal.getServices().getHosting());
        cb_Adoption.setChecked(animal.getServices().getAdoption());
        et_StartTime.setText(animal.getSchedule().getStartTime());
        et_EndTime.setText(animal.getSchedule().getEndTime());
        //this.cb_Days.(animal.getDays());

        cb_Days[0].setChecked(animal.getSchedule().getCanSun());
        cb_Days[1].setChecked(animal.getSchedule().getCanMon());
        cb_Days[2].setChecked(animal.getSchedule().getCanTue());
        cb_Days[3].setChecked(animal.getSchedule().getCanWed());
        cb_Days[4].setChecked(animal.getSchedule().getCanThu());
        cb_Days[5].setChecked(animal.getSchedule().getCanFri());
        cb_Days[6].setChecked(animal.getSchedule().getCanSat());
    }

    @Override
    protected CompletableFuture<Void> doSave(Animal animal) {
        animal.setId(this.animal.getId());
        this.animal = animal;
        return animal.update();
    }

    @Override
    protected void doEnd() {
        // super.doEnd(); No llamar al padre

        NavController navController = NavHostFragment.findNavController(this);

        // Eliminar el fragmento actual de la pila
        navController.popBackStack();
        // Eliminar el fragmento anterior de la pila
        navController.popBackStack();

        AnimalProfileFragment.open(this, this.animal);
    }
}