package com.example.ilpp.classes.model.data;

import android.os.Bundle;

public class ItemDouble extends Item {

    private Double value;

    @Override
    public void addToBundle(Bundle bundle, String key) {
        bundle.putDouble(key, value);
    }

    @Override
    public Double getValue(Boolean toFS) {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (Double) value;
    }
}
