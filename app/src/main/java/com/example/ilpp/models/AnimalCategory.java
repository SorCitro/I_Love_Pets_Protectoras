package com.example.ilpp.models;

import android.os.Bundle;

import com.example.ilpp.classes.model.Collection;
import com.example.ilpp.classes.model.Data;
import com.example.ilpp.classes.model.Field;
import com.example.ilpp.classes.model.ModelDoc;
import com.example.ilpp.classes.model.ModelManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Collection("animal_categories")
public class AnimalCategory extends ModelDoc {

    @Field
    private String name;
    public String getName() { return name; }

    @Field
    private String names;
    public String getNames() { return names; }

    @Field
    private String imageUrl;
    public String getImageUrl() { return imageUrl; }

    public AnimalCategory(){ super(); }


    public static String document(String id){
        return ModelManager.document(AnimalCategory.class, id);
    }

    public static CompletableFuture<AnimalCategory> get(String id){
        return ModelManager.get(AnimalCategory.class, id);
    }

    public static CompletableFuture<List<AnimalCategory>> getList(){
        return ModelManager.getList(AnimalCategory.class);
    }

    @Override
    public String getSearch() {
        return getName();
    }
}
