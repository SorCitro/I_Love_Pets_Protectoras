package com.example.ilpp.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.ilpp.models.Animal;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

public class Utils {

    public static <T> CompletableFuture taskToCompletableFuture(Task<T> task){
        CompletableFuture future = new CompletableFuture();
        task.addOnSuccessListener(future::complete);
        task.addOnFailureListener(future::completeExceptionally);
        return future;
    }

    public static boolean isDarkMode(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return context.getResources().getConfiguration().isNightModeActive();
        } else {
            return (context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                    == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        }
    }

    public interface TimeoutCallback{
        void onTimeout();
    }
    public static void setTimeout(long ms, TimeoutCallback callback){
        new Handler(Looper.getMainLooper()).postDelayed(callback::onTimeout, ms);
    }

    public static CompletableFuture<Void> generateRandomData(){
        CompletableFuture<Void> future = new CompletableFuture<>();
        Animal.getList(null)
                .thenAccept(animals -> {

                    ArrayList<CompletableFuture> futures = new ArrayList<>();

                    int count = 20;

                    for (Animal animal : animals) {
                        if (animal.getId().equals("TestAnimal")) continue;
                        futures.add(animal.delete());
                    }

                    for (int i = 0; i < count; i++) {
                        futures.add(Animal.random("dog").create());
                    }
                    for (int i = 0; i < count; i++) {
                        futures.add(Animal.random("cat").create());
                    }
                    for (int i = 0; i < count; i++) {
                        futures.add(Animal.random("TestCategory").create());
                    }

                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenAccept(v -> {
                                future.complete(null);
                            })
                            .exceptionally(e -> {
                                future.completeExceptionally(e);
                                return null;
                            });

                })
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });

        return future;
    }

    public static void startActivityForResult(FragmentActivity act, Intent in, int requestCode, @Nullable String tag, OnActivityResult cb) {
        Fragment aux = new FragmentForResult(cb);
        FragmentManager fm = act.getSupportFragmentManager();
        fm.beginTransaction().add(aux, tag).commit();
        fm.executePendingTransactions();
        aux.startActivityForResult(in, requestCode);
    }

    public interface OnActivityResult {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    @SuppressLint("ValidFragment")
    public static class FragmentForResult extends Fragment {
        private OnActivityResult cb;
        public FragmentForResult(OnActivityResult cb) {
            this.cb = cb;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (cb != null)
                cb.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (chars.length() * Math.random());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public static CompletableFuture<Uri> uploadImage(String path, Uri uri){
        // Subir la imagen a Firebase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child(path);
        String randomName = Utils.randomString(10);

        // Extraer la extensión del archivo
        int index = uri.toString().lastIndexOf(".");
        String extension = "";
        if (index > 0) {
            extension = uri.toString().substring(index);
        }

        randomName += extension;

        StorageReference imageRef = imagesRef.child(randomName);
        return Utils.taskToCompletableFuture(imageRef.putFile(uri))
                .thenCompose(taskSnapshot -> Utils.taskToCompletableFuture(imageRef.getDownloadUrl()));
    }

}
