package com.example.ilpp.models;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.ilpp.classes.model.Collection;
import com.example.ilpp.classes.model.Data;
import com.example.ilpp.classes.model.Field;
import com.example.ilpp.classes.model.ModelDoc;
import com.example.ilpp.classes.model.ModelManager;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Collection("animals")
public class Animal extends ModelDoc {

    @Field
    public String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Field
    public Date birthdate;
    public Date getBirthdate() { return birthdate; }
    public void setBirthdate(Date birthdate) { this.birthdate = birthdate; }

    @Field
    public String description;
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Field
    public String imageUrl;
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Field("animalShelter")
    public String animalShelterId;
    public String getAnimalShelterId() { return animalShelterId; }
    public void setAnimalShelterId(String animalShelterId) { this.animalShelterId = animalShelterId; }

    @Field("animalCategory")
    public String animalCategoryId;
    public String getAnimalCategoryId() { return animalCategoryId; }
    public void setAnimalCategoryId(String animalCategoryId) { this.animalCategoryId = animalCategoryId; }


    @Field
    private final Address address = new Address();
    public Address getAddress() { return address; }

    @Field
    private final AnimalDetails details = new AnimalDetails();
    public AnimalDetails getDetails() { return details; }

    @Field
    private final AnimalServices services = new AnimalServices();
    public AnimalServices getServices() { return services; }

    @Field
    private final AnimalSchedule schedule = new AnimalSchedule();
    public AnimalSchedule getSchedule() { return schedule; }

    public Integer getAge(){
        Date birthDate = getBirthdate();
        if (birthDate == null) return null;
        return (int) ((new Date().getTime() - birthDate.getTime()) / 1000 / 60 / 60 / 24 / 365);
    }

    public Animal(){ super(); }

    /**
     * Obtiene el listado de animales
     */
    public static CompletableFuture
            <List<Animal>> getList(@Nullable ModelManager.OnPrepareListListener listener) {
        return ModelManager.getList(Animal.class, query -> {
            if (listener != null) query = listener.onPrepare(query);
            query.orderBy("name", Query.Direction.ASCENDING);
            return query;
        });
    }

    public static CompletableFuture<Animal> get(String id) { return ModelManager.get(Animal.class, id); }

    public static Animal random(String category){
        Animal animal = new Animal();

        // Nombre aleatorio
        String[] names = new String[]{"Fuet", "Chincheta", "Calçot", "Sugus", "Maduixa", "Nugget", "Mojo", "Balandrau", "Puigmal", "Puigsacalm", "Aneto", "Brownie", "Samarreta"};
        animal.setName(names[(int) (Math.random() * names.length)]);

        // 1 - 10 años
        animal.setBirthdate(new Date(new Date().getTime() - (long) (Math.random() * 10 * 365 * 24 * 60 * 60 * 1000)));

        // Descripción aleatoria
        String[] descriptions = new String[]{
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam.",
                "Eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.",
                "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit.",
        };
        animal.setDescription(descriptions[(int) (Math.random() * descriptions.length)]);

        // Imagen
        String displayCategory = category;
        if (category.equals("dog")) {
            displayCategory = "perro";
            animal.setImageUrl("https://placedog.net/640/480?random=" + (int) (Math.random() * 1000000));
        }else if (category.equals("cat")) {
            //animal.setImageUrl("https://cataas.com/cat?random=" + (int) (Math.random() * 1000000));
            displayCategory = "gato";
            animal.setImageUrl("https://thecatapi.com/api/images/get?format=src&type=jpg&random" + (int) (Math.random() * 1000000));
        }else {
            if (category.equals("TestCategory")) displayCategory = "crocodile";
            animal.setImageUrl("https://loremflickr.com/400/300/" + displayCategory + "?random=" + (int) (Math.random() * 1000000));
        }

        // Detalles
        String[] genders = new String[]{
                "Macho", "Hembra"
        };
        animal.details.setGender(genders[(int) (Math.random() * genders.length)]);
        animal.details.setBreed("Test");

        String captialize = displayCategory.substring(0, 1).toUpperCase() + displayCategory.substring(1);
        animal.details.setType(captialize);

        // 1-40kg
        animal.details.setWeight((int) (Math.random() * 40) + "kg");

        // Dirección
        animal.address.setAddress("Calle " + (int) (Math.random() * 100));
        animal.address.setCity("Ciudad " + (int) (Math.random() * 100));
        // 5 dígitos
        animal.address.setPostalCode((int) (Math.random() * 100000) + "");

        // Otros
        animal.animalShelterId = "TestAnimalShelter";
        animal.animalCategoryId = category;

        // Random
        if (Math.random() > 0.5) animal.getServices().setAdoption(true);
        if (Math.random() > 0.5) animal.getServices().setHosting(true);
        animal.getServices().setWalks(true);
        animal.getSchedule().setCanMon(Math.random() > 0.5);
        animal.getSchedule().setCanTue(Math.random() > 0.5);
        animal.getSchedule().setCanWed(Math.random() > 0.5);
        animal.getSchedule().setCanThu(Math.random() > 0.5);
        animal.getSchedule().setCanFri(Math.random() > 0.5);
        animal.getSchedule().setCanSat(Math.random() > 0.5);
        animal.getSchedule().setCanSun(Math.random() > 0.5);
        // Random. Ex: 13:00
        animal.getSchedule().setStartTime((int) (Math.random() * 24) + ":00");
        animal.getSchedule().setEndTime((int) (Math.random() * 24) + ":00");

        return animal;
    }

    @Override
    public String getSearch() {
        return getName() + ", " + ", " + getDescription() + getDetails().getSearchText() + ", " + getAddress().getSearchText();
    }

    @Override
    public CompletableFuture<Void> delete() {
        return super.delete()
                .thenCompose(aVoid -> {
                    // Eliminar los paseos
                    return AnimalWalkBooking.deleteByAnimalId(getId());
                });
    }
}
