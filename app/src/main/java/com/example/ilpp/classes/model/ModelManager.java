package com.example.ilpp.classes.model;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.ilpp.classes.Utils;
import com.example.ilpp.classes.model.data.Data;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModelManager  {

    public static void fromData(Model model, Data data) { data.applyToModel(model); }
    public static Data toData(Model model) { return new Data(model); }

    public static void fromBundle(Model model, Bundle data) { fromData(model, new Data(data)); }
    public static Bundle toBundle(Model model) { return toData(model).toBundle(); }

    public static void fromDocumentSnapshot(ModelDoc model, DocumentSnapshot data) {
        fromData(model, new Data(data));
    }

    public static String document(Class<? extends ModelDoc> modelClass, String id) {
        return String.format("%s/%s", collectionName(modelClass), id);
    }

    /**
     * Obtener el nombre de la colección a partir de la anotación @Collection
     */
    public static String collectionName(Class<? extends ModelDoc> modelClass) {
        Collection collection = modelClass.getAnnotation(Collection.class);
        if (collection == null) {
            throw new RuntimeException("No se ha definido el nombre de la colección");
        }
        return collection.value();
    }

    /**
     * Actualizar el documento en la base de datos
     */
    public static CompletableFuture<Void> updateDocument(ModelDoc model){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection(model.collectionName());
        Task<Void> r = collection.document(model.getId()).set(model.toMap(true));
        return Utils.taskToCompletableFuture(r);
    }

    /**
     * Crear el documento en la base de datos
     */
    public static CompletableFuture<Void> createDocument(ModelDoc model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection(model.collectionName());
        DocumentReference newRef = collection.document();
        CompletableFuture f = Utils.taskToCompletableFuture(newRef.set(model.toMap(true)));
        if (model.getId() == null)
            model.setId(newRef.getId());
        return f;
    }

    /**
     * Eliminar el documento de la base de datos
     */
    public static CompletableFuture<Void> deleteDocument(ModelDoc model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection(model.collectionName());
        return Utils.taskToCompletableFuture(collection.document(model.getId()).delete());
    }

    public interface OnPrepareListListener {
        Query onPrepare(Query query);
    }

    public static <T extends ModelDoc> CompletableFuture<List<T>> getList(Class<T> modelClass) {
        return getList(modelClass, null);
    }
    /**
     * Obtener un lista de documentos de la base de datos
     * @param listener Permite modificar la consulta antes de ejecutarla
     */
    public static <T extends ModelDoc> CompletableFuture<List<T>> getList(Class<T> modelClass, @Nullable OnPrepareListListener listener) {
        CompletableFuture<List<T>> future = new CompletableFuture<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(collectionName(modelClass));

        if (listener != null) {
            Query newQuery = listener.onPrepare(query);
            if (newQuery != null)
                query = newQuery;
        }

        query
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<T> list = new ArrayList<>();
                        QuerySnapshot queryS = task.getResult();
                        List<DocumentSnapshot> docs = queryS.getDocuments();
                        for (DocumentSnapshot d : docs) {
                            try {
                                T dataModal = modelClass.getDeclaredConstructor().newInstance();
                                fromDocumentSnapshot(dataModal, d);
                                list.add(dataModal);
                            } catch (Exception e) {
                                future.completeExceptionally(e);
                                return;
                            }
                        }
                        future.complete(list);
                    }else{
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    public static <T extends ModelDoc> CompletableFuture<T> get(Class<T> modelClass, String id) {
        CompletableFuture<T> future = new CompletableFuture<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(collectionName(modelClass)).document(id);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        T dataModal = modelClass.getDeclaredConstructor().newInstance();
                        fromDocumentSnapshot(dataModal, document);
                        future.complete(dataModal);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                } else {
                    future.completeExceptionally(new Exception("No existe el documento"));
                }
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

}
