package com.example.ilpp.models;

import android.os.Bundle;

import com.example.ilpp.classes.model.Collection;
import com.example.ilpp.classes.model.Data;
import com.example.ilpp.classes.model.Field;
import com.example.ilpp.classes.model.ModelDoc;
import com.example.ilpp.classes.model.ModelManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Data
@Collection("animal_shelters")
public class AnimalShelter extends ModelDoc {

    @Field
    private Address address = new Address();
    public Address getAddress() { return address; }

    @Field
    private String email;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Field
    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Field
    private String imageUrl;
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Field
    private String phone;
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }


    public AnimalShelter(){ super(); }

    public static CompletableFuture<AnimalShelter> get(String id){
        return ModelManager.get(AnimalShelter.class, id);
    }

    @Override
    public String getSearch() {
        return getName() + ", " + getAddress().getSearchText();
    }
}
