package com.example.ilpp.classes.model.data;

import android.os.Bundle;

public class ItemBoolean extends Item {
    @Override
    public void addToBundle(Bundle bundle, String key) {
        bundle.putBoolean(key, getValue(false));
    }

    private Boolean value;

    @Override
    public Boolean getValue(Boolean toFS) {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (Boolean) value;
    }
}
