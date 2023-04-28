package com.example.ilpp.classes.model;

import java.util.concurrent.CompletableFuture;

public abstract class ModelDoc extends Model {

    @Field
    private String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ModelDoc(){ super(); }

    public String collectionName(){
        return ModelManager.collectionName(this.getClass());
    }

    /**
     * Actualizar el documento en la base de datos
     */
    public CompletableFuture<Void> update(){
        return ModelManager.updateDocument(this);
    }

    public CompletableFuture<Void> create() { return ModelManager.createDocument(this); }

    public CompletableFuture<Void> delete() {
        return ModelManager.deleteDocument(this);
    }

    public abstract String getSearch();

    public boolean isSearchResult(String search){
        if (search == null || search.isEmpty())
            return true;
        String s = this.getSearch();
        if (s == null)
            return false;
        return s.contains(search);
    }


}
