package com.example.ilpp.models;

import com.example.ilpp.classes.model.Collection;
import com.example.ilpp.classes.model.Data;
import com.example.ilpp.classes.model.Field;
import com.example.ilpp.classes.model.ModelDoc;
import com.example.ilpp.classes.model.ModelManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Data
@Collection("users")
public class User extends ModelDoc {

    @Field
    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Field
    private String surname;
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    @Field
    private String email;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Field
    private String photoUrl;
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    @Field
    private Boolean isAnimalShelter = false;
    public Boolean getIsAnimalShelter() { return isAnimalShelter; }
    public void setIsAnimalShelter(Boolean isAnimalShelter) { this.isAnimalShelter = isAnimalShelter; }

    public static CompletableFuture<User> get(String id) { return ModelManager.get(User.class, id); }

    public String getDisplayName(){
        String surname = getSurname();
        String result = getName();
        if (surname != null && !surname.isEmpty()) result += " " + surname;
        return result;
    }

    @Override
    public String getSearch() {
        return getDisplayName();
    }

    // Animal favoritos

    @Data
    @Collection("users/{id}/animal_favs")
    public static class AnimalFav extends ModelDoc {

        @Field("animal")
        private String animalId;

        public String getAnimalId() { return animalId; }
        public void setAnimalId(String animalId) { this.animalId = animalId; }

        @Field("user")
        private String userId;
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        @Field
        private Date createdAt = new Date();
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

        @Override
        public String collectionName(){
            return "users/" + getUserId() + "/animal_favs";
        }

        @Override
        public String getSearch() { return null; }
    }

    private List<AnimalFav> cachedAnimalFavs;
    public CompletableFuture<List<AnimalFav>> getAnimalFavs() {
        if (cachedAnimalFavs != null) return CompletableFuture.completedFuture(cachedAnimalFavs);
        return ModelManager.getList(AnimalFav.class, query -> {
            return FirebaseFirestore.getInstance()
                    .collection("users/" + getId() + "/animal_favs")
                    .orderBy("createdAt", Query.Direction.DESCENDING);
        }).thenApply(animalFavs -> {
            cachedAnimalFavs = animalFavs;
            return animalFavs;
        });
    }

    public CompletableFuture<Void> setAnimalFav(String animalId, Boolean fav) {
        if (fav) {
            AnimalFav animalFav = new AnimalFav();
            animalFav.setAnimalId(animalId);
            animalFav.setUserId(getId());
            return animalFav.create().thenRun(() -> {
                if (cachedAnimalFavs != null) cachedAnimalFavs.add(animalFav);
            });
        } else {
            AnimalFav animalFav = cachedAnimalFavs.stream()
                    .filter(favAnimal -> favAnimal.getAnimalId().equals(animalId))
                    .findFirst()
                    .orElse(null);
            if (animalFav == null) return CompletableFuture.completedFuture(null);
            return animalFav.delete().thenRun(() -> {
                if (cachedAnimalFavs != null) cachedAnimalFavs.remove(animalFav);
            });
        }
    }

}
