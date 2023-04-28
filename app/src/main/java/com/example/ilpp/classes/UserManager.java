package com.example.ilpp.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import com.example.ilpp.R;
import com.example.ilpp.activities.panel.sections.AnimalShelterProfileFragment;
import com.example.ilpp.activities.panel.sections.ProfileFragment;
import com.example.ilpp.models.AnimalShelter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.CompletableFuture;

public class UserManager {


    private static FirebaseAuth fbAuth;

    static {
        fbAuth = FirebaseAuth.getInstance();
    }

    /**
     * Clase que representa un usuario.
     */
    public static class User {
        private final FirebaseUser firebaseUser;
        public FirebaseUser getFirebaseUser() { return firebaseUser; }

        private final GoogleSignInAccount googleAccount;
        @Nullable
        public GoogleSignInAccount getGoogleAccount() { return googleAccount; }

        private final com.example.ilpp.models.User data;
        public com.example.ilpp.models.User getUserData() { return data; }
        private final AnimalShelter animalShelterData;
        public AnimalShelter getAnimalShelterData() { return animalShelterData; }

        public User(com.example.ilpp.models.User data, AnimalShelter animalShelterData, FirebaseUser firebaseUser, @Nullable GoogleSignInAccount googleAccount) {
            this.firebaseUser = firebaseUser;
            this.googleAccount = googleAccount;
            this.data = data;
            this.animalShelterData = animalShelterData;
        }

        public void openProfile(NavController nav){
            if (this.getUserData().getIsAnimalShelter()){
                AnimalShelterProfileFragment.open(nav);
            }else{
                ProfileFragment.open(nav);
            }
        }
    }

    private static User user;
    public static Boolean isLogged() { return user != null; }
    public static User getUser() { return user; }

    private static CompletableFuture<User> openUser(FirebaseUser firebaseUser, @Nullable GoogleSignInAccount googleAccount){
        CompletableFuture<User> future = new CompletableFuture<>();

        // Verifica si el usuario existe en la base de datos.
        com.example.ilpp.models.User.get(firebaseUser.getUid())
            .whenComplete((user, e) -> {
                if (user == null) {
                    // Si no existe, lo crea.
                    user = new com.example.ilpp.models.User();
                    user.setId(firebaseUser.getUid());
                    user.setName(firebaseUser.getDisplayName());
                    user.setEmail(firebaseUser.getEmail());
                    Uri photoUrl = firebaseUser.getPhotoUrl();
                    if (photoUrl != null) user.setPhotoUrl(photoUrl.toString());
                    user.update(); // No se espera a que termine.
                }

                // Si es una protectora, esperar también sus datos.
                if (user.getIsAnimalShelter()) {
                    com.example.ilpp.models.User finalUser = user;
                    AnimalShelter.get(finalUser.getId())
                        .whenComplete((animalShelter, e2) -> {
                            if (e2 != null) future.completeExceptionally(e2);
                            else if (animalShelter == null) future.completeExceptionally(new Exception("No se encontró la protectora del usuario."));
                            future.complete(UserManager.user = new User(finalUser, animalShelter, firebaseUser, googleAccount));
                        });
                }else {
                    future.complete(UserManager.user = new User(user, null, firebaseUser, googleAccount));
                }
            });
        return future;
    }

    /**
     * Reestablece la contraseña de un usuario.
     */
    public static CompletableFuture<Boolean> resetPassword(String email) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        fbAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(true);
                    } else {
                        future.complete(false);
                    }
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    /**
     * Empieza el proceso de login con Google.
     */
    public static void loginWidthGoogle(Activity activity, int code) {
        String webClientId = activity.getString(R.string.default_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail() // Nombre de usuario
                .requestProfile() // Datos del usuario (nombre, email, foto)
                .build();

        Intent intent = GoogleSignIn
                .getClient(activity, gso)
                .getSignInIntent();

        activity.startActivityForResult(intent, code);
    }

    public static CompletableFuture<User> loginWidthGoogle_Result(Activity activity, Intent data) {
        CompletableFuture<User> future = new CompletableFuture<>();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(activity, account.getIdToken())
                .thenAccept(firebaseUser -> {
                    if (firebaseUser != null){
                        openUser(firebaseUser, account)
                            .thenAccept(future::complete)
                            .exceptionally(e -> {
                                future.completeExceptionally(e);
                                return null;
                            });
                    }else {
                        future.complete(null);
                    }
                });
        } catch (ApiException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    //Metodo que guarda el token para iniciar sesion con google
    private static CompletableFuture<FirebaseUser> firebaseAuthWithGoogle(Activity activity, String idToken) {
        CompletableFuture<FirebaseUser> future = new CompletableFuture<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    future.complete(
                        task.isSuccessful() ? fbAuth.getCurrentUser() : null
                    );
                });
        return future;
    }

    /**
     * Intenta recuperar el último usuario logueado.
     */
    public static CompletableFuture<User> loginLastUser(Context context) {
        FirebaseUser fb = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(context);
        if (fb != null) {
            return openUser(fb, gg);
        } else {
            CompletableFuture f = new CompletableFuture<>();
            f.complete(null);
            return f;
        }
    }

    /**
     * Inicia sesión con un usuario y contraseña.
     */
    public static CompletableFuture<User> login(Activity activity, String email, String password) {
        CompletableFuture<User> future = new CompletableFuture<>();
        fbAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity, (OnCompleteListener<AuthResult>) task -> {
                if (task.isSuccessful()){
                    openUser(fbAuth.getCurrentUser(), null)
                        .thenAccept(future::complete)
                        .exceptionally(e -> {
                            future.completeExceptionally(e);
                            return null;
                        });
                }else{
                    future.complete(null);
                }
            });
        return future;
    }

    public static void logout(Activity activity) {
        user = null;
        fbAuth.signOut();
    }

    /**
     * Registra un usuario con un email y contraseña.
     */
    public static CompletableFuture<User> register(Activity activity, String email, String password, String displayName, String photoUrl) {
        CompletableFuture<User> future = new CompletableFuture<>();
        fbAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = fbAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .setPhotoUri(Uri.parse(photoUrl))
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        openUser(user, null)
                                            .whenComplete((u, e) -> {
                                                if (e != null) future.completeExceptionally(e);
                                                future.complete(u);
                                            });
                                    } else {
                                        future.complete(null);
                                    }
                                })
                                .addOnFailureListener(e -> future.complete(null));
                    }else{
                        future.complete(null);
                    }

                });
        return future;
    }

}