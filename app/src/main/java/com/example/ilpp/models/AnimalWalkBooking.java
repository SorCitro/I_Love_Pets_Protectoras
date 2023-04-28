package com.example.ilpp.models;

import com.example.ilpp.classes.model.Collection;
import com.example.ilpp.classes.model.Data;
import com.example.ilpp.classes.model.Field;
import com.example.ilpp.classes.model.ModelDoc;
import com.example.ilpp.classes.model.ModelManager;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Data
@Collection("animal_walk_bookings")
public class AnimalWalkBooking extends ModelDoc {

    @Field("animal")
    private String animalId;

    public String getAnimalId() { return animalId; }
    public void setAnimalId(String animalId) { this.animalId = animalId; }

    @Field("animalShelter")
    private String animalShelterId;
    public String getAnimalShelterId() { return animalShelterId; }
    public void setAnimalShelterId(String animalShelterId) { this.animalShelterId = animalShelterId; }

    @Field("user")
    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @Field
    private Date date;
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    @Override
    public String getSearch() { return null; }

    public static CompletableFuture<List<AnimalWalkBooking>> getByAnimalDate(String animalId, Date date) {
        return ModelManager.getList(AnimalWalkBooking.class, query -> {
            query = query.whereEqualTo("animal", animalId);
            query = query.whereEqualTo("date", date);
            return query;
        });
    }

    public static CompletableFuture<List<AnimalWalkBooking>> getByAnimalShelter(String animalShelterId) {
        return ModelManager.getList(AnimalWalkBooking.class, query -> {
            query = query.whereEqualTo("animalShelter", animalShelterId);
            return query;
        });
    }

    public static CompletionStage<Void> deleteByAnimalId(String animalId) {
        return ModelManager.getList(AnimalWalkBooking.class, query -> {
            query = query.whereEqualTo("animal", animalId);
            return query;
        }).thenCompose(animalWalkBookings -> {
            CompletableFuture[] futures = new CompletableFuture[animalWalkBookings.size()];
            for (int i = 0; i < animalWalkBookings.size(); i++) {
                futures[i] = animalWalkBookings.get(i).delete();
            }
            return CompletableFuture.allOf(futures);
        });
    }

}
