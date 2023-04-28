package com.example.ilpp.classes.model;

import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public abstract class Model {
    protected Model(){}
    public Model(Bundle data){
        ModelManager.fromBundle(this, data);
    }

    public Bundle toBundle() {
        return ModelManager.toBundle(this);
    }
    public void fromBundle(Bundle data){ ModelManager.fromBundle(this, data); }

    public Map<String, Object> toMap(Boolean toFS) { return ModelManager.toData(this).toMap(toFS); }

}
