package com.example.ilpp.classes.model.data;

import android.os.Bundle;

public abstract class Item {

    public abstract void addToBundle(Bundle bundle, String key);
    public abstract Object getValue(Boolean toFS);
    public abstract void setValue(Object value);

}
