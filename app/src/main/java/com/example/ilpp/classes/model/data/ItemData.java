package com.example.ilpp.classes.model.data;

import android.os.Bundle;

import java.util.Map;

public class ItemData extends Item {

    private Data value;
    public Data getData() { return value; }

    @Override
    public void addToBundle(Bundle bundle, String key) {
        bundle.putBundle(key, value.toBundle());
    }

    @Override
    public Map<String, Object> getValue(Boolean toFS) {
        return value.toMap(toFS);
    }

    @Override
    public void setValue(Object value) {
        this.value = (Data) value;
    }
}
