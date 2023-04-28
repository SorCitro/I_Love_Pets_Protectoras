package com.example.ilpp.classes.model.data;

import android.os.Bundle;

import com.example.ilpp.BuildConfig;
import com.example.ilpp.classes.model.Field;
import com.example.ilpp.classes.model.Model;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data extends HashMap<String, Item> {

    private static final Map<Class<? extends Item>, Class<?>> dataTypes = new LinkedHashMap<>();
    static {
        dataTypes.put(ItemString.class, String.class);
        dataTypes.put(ItemDouble.class, Double.class);
        dataTypes.put(ItemDate.class, Date.class);
        dataTypes.put(ItemBoolean.class, Boolean.class);
        dataTypes.put(ItemData.class, Data.class);
    }

    public Data(Bundle bundle){
        for (String key : bundle.keySet()) {
            boolean found = false;
            for (Class<? extends Item> dataType : dataTypes.keySet()) {
                Object value = bundle.get(key);
                if (value == null) continue;
                if (value instanceof Bundle) value = new Data((Bundle) value);
                Class<?> type = dataTypes.get(dataType);
                if (value.getClass().isAssignableFrom(type)) {
                    found = true;
                    this.put(key, Data.createItem(dataType, value));
                    break;
                }
            }
            if (!found && BuildConfig.DEBUG)
                throw new RuntimeException(
                    String.format("No se encontró un tipo de dato para el valor %s", key)
                );
        }
    }

    public Data(Object model) {
        // Comprobar que es un modelo
        Class<?> modelClass = model.getClass();
        if (modelClass.getAnnotation(com.example.ilpp.classes.model.Data.class) == null) {
            throw new RuntimeException(
                    String.format("La clase %s no es un modelo", modelClass.getName())
            );
        }

        List<java.lang.reflect.Field> fields = getModelFields(modelClass);
        for (java.lang.reflect.Field field : fields){
            Field annotation = field.getAnnotation(Field.class);
            if (annotation == null) continue;
            String key = annotation.value();
            if (key.isEmpty()) key = field.getName();
            try {
                field.setAccessible(true);
                Object value = field.get(model);
                if (value == null) continue;
                Class<?> type = value.getClass();
                boolean found = false;
                for (Class<? extends Item> dataType : dataTypes.keySet()) {
                    Class<?> dataTypeClass = dataTypes.get(dataType);
                    if (dataTypeClass.isAssignableFrom(type)) {
                        this.put(key, Data.createItem(dataType, value));
                        found = true;
                        break;
                    }
                }
                if (!found)
                    this.put(key, Data.createItem(ItemData.class, new Data(value)));

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Data(DocumentSnapshot doc) {
        List<String> keys = new ArrayList<>();
        keys.add("id");
        keys.addAll(doc.getData().keySet());
        for (String key : keys) {
            Object value = key.equals("id")
                    ? doc.getId()
                    : doc.get(key);
            if (value == null) continue;
            if (value instanceof DocumentReference) value = ((DocumentReference) value).getId();
            if (value instanceof Timestamp) value = ((Timestamp) value).toDate();
            if (value instanceof HashMap) value = new Data((HashMap<String, Object>) value);
            boolean found = false;
            for (Class<? extends Item> dataType : dataTypes.keySet()) {
                Class<?> type = dataTypes.get(dataType);
                if (value.getClass().isAssignableFrom(type)) {
                    found = true;
                    this.put(key, Data.createItem(dataType, value));
                    break;
                }
            }
            if (!found && BuildConfig.DEBUG)
                throw new RuntimeException(
                        String.format("No se encontró un tipo de dato para el valor %s", key)
                );
        }
    }

    public Data(Map<String, Object> map) {
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value == null) continue;
            for (Class<? extends Item> dataType : dataTypes.keySet()) {
                Class<?> type = dataTypes.get(dataType);
                if (value.getClass().isAssignableFrom(type)) {
                    this.put(key, Data.createItem(dataType, value));
                    break;
                }
            }
        }
    }

    private static Item createItem(Class<? extends Item> dataType, Object value) {
        try {
            Item item = dataType.newInstance();
            item.setValue(value);
            return item;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        for (String key : this.keySet()) {
            this.get(key).addToBundle(bundle, key);
        }
        return bundle;
    }

    public Map<String, Object> toMap(Boolean toFS) {
        Map<String, Object> map = new HashMap<>();
        for (String key : this.keySet()) {
            if (key.equals("id")) continue;
            Object value = this.get(key).getValue(toFS);
            map.put(key, value);
        }
        return map;
    }

    public void applyToModel(Object model){
        List<java.lang.reflect.Field> fields = getModelFields(model.getClass());
        for (String key : this.keySet()) {
            Item item = this.get(key);
            for (java.lang.reflect.Field field : fields) {
                Field annotation = field.getAnnotation(Field.class);
                if (annotation == null) continue;
                String fieldKey = annotation.value();
                if (fieldKey.isEmpty()) fieldKey = field.getName();
                if (!key.equals(fieldKey)) continue;
                try {
                    field.setAccessible(true);
                    if (item instanceof ItemData) {
                        ((ItemData) item).getData().applyToModel(field.get(model));
                    } else {
                        field.set(model, item.getValue(false));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private List<java.lang.reflect.Field> getModelFields(Class<?> cls){
        List<java.lang.reflect.Field> fields = new ArrayList<>();
        Class<?> parent = cls;
        while (parent != null) {
            for (java.lang.reflect.Field field : parent.getDeclaredFields()) {
                if (field.getAnnotation(Field.class) != null)
                    fields.add(field);
            }
            parent = parent.getSuperclass();
        }
        return fields;
    }

}
