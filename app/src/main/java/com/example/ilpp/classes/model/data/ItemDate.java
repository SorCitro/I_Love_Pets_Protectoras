package com.example.ilpp.classes.model.data;

import android.os.Bundle;

import com.google.firebase.Timestamp;

import java.util.Date;

public class ItemDate extends Item {

    private Date value;

    @Override
    public void addToBundle(Bundle bundle, String key) {
        bundle.putSerializable(key, value);
    }

    @Override
    public Object getValue(Boolean toFS) {
        if (toFS) {
            try {
                return new Timestamp(value);
            } catch (Exception e) {
                return new Timestamp(0, 0);
            }
        }
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (Date) value;
    }
}
