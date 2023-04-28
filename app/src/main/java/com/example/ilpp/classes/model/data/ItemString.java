package com.example.ilpp.classes.model.data;

import android.os.Bundle;

public class ItemString extends Item {

    private String value;

    @Override
    public void addToBundle(Bundle bundle, String key) {
        bundle.putString(key, value);
    }

    @Override
    public String getValue(Boolean toFS) {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (String) value;
    }
}
