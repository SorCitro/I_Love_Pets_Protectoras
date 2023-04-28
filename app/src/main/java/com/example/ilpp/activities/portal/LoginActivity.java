package com.example.ilpp.activities.portal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ilpp.R;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.Scene;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.classes.Utils;
import com.example.ilpp.classes.Validate;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 100;

    private ProgressBar pb_Login;
    private TextInputEditText et_Email, et_Password;
    private TextView tv_Welcome, tv_Desc, tv_RecoverPass, tv_NewUser, tv_NewProt;
    private ImageView iv_Logo;
    private TextInputLayout tf_Email, tf_Password;

    private Button btn_Login;
    private SignInButton btn_SignInGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Obtener referencias
        pb_Login = findViewById(R.id.pb_Login);
        et_Email = findViewById(R.id.et_Email);
        iv_Logo = findViewById(R.id.iv_Logo);
        tv_Welcome = findViewById(R.id.tv_Title);
        tv_Desc = findViewById(R.id.tv_Desc);
        tv_RecoverPass = findViewById(R.id.tv_RecoverPass);
        tv_NewUser = findViewById(R.id.tv_NewUser);
        tf_Email = findViewById(R.id.tf_Email);
        tf_Password = findViewById(R.id.tf_Password);
        btn_Login = findViewById(R.id.btn_Login);
        btn_SignInGoogle = findViewById(R.id.btn_SignInGoogle);
        et_Password = findViewById(R.id.et_Password);
        tv_NewProt = findViewById(R.id.tv_NewProt);

        // Inicializar propiedades

        if (Utils.isDarkMode(this)){
            btn_SignInGoogle.setColorScheme(SignInButton.COLOR_DARK);
        }else{
            btn_SignInGoogle.setColorScheme(SignInButton.COLOR_LIGHT);
        }

        // Animaciones
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein_up);
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.fadein_down);

        iv_Logo.setAnimation(anim2);
        tv_Welcome.setAnimation(anim2);
        tv_Desc.setAnimation(anim2);
        tf_Email.setAnimation(anim2);
        tf_Password.setAnimation(anim2);
        tv_RecoverPass.setAnimation(anim);
        btn_Login.setAnimation(anim);
        tv_NewUser.setAnimation(anim);
        tv_NewProt.setAnimation(anim);


        View[] transitionViews = {
            iv_Logo,
            tv_Welcome,
            tv_Desc,
            tf_Email,
            tf_Password,
            btn_Login,
            tv_NewUser,
            tv_NewProt
        };

        // Establecer eventos

        tv_NewUser.setOnClickListener(view -> {
            // Ir a la pantalla de registro
            Scene.goWithTransitionAndFinish(LoginActivity.this, SignupActivity.class, transitionViews);
        });

        tv_NewProt.setOnClickListener(view -> {
            // Ir a la pantalla de registro de protectoras
            Scene.goWithTransitionAndFinish(LoginActivity.this, SignupProtectorasActivity.class, transitionViews);
        });

        tv_RecoverPass.setOnClickListener(view -> {
            // Ir a la pantalla de recuperar contraseña
            Scene.goWithTransitionAndFinish(LoginActivity.this, ForgotPasswordActivity.class, transitionViews);
        });

        btn_Login.setOnClickListener(view -> {
            // Enviar el formulario
            sendForm();
        });

        btn_SignInGoogle.setOnClickListener(view -> {
            // Iniciar sesión con Google
            signInWithGoogle();
        });

        // Inicializar propiedades
        updateLoading(false);

    }

    private void signInWithGoogle() {
        updateLoading(true);
        try {
            UserManager.loginWidthGoogle(this, RC_SIGN_IN);
        } finally {
            updateLoading(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Respuesta de la actividad de inicio de sesión de Google
        if (requestCode == RC_SIGN_IN) {
            updateLoading(true);
            UserManager.loginWidthGoogle_Result(this, data)
                .thenAccept(user -> {
                    if (UserManager.isLogged()) {
                        Scene.goAndFinish(LoginActivity.this, Scene.MainUserActivityClass);
                    } else {
                        Message.show(this, "Fallo en el inicio de sesión");
                    }
                }).whenComplete((aVoid, throwable) -> {
                    updateLoading(false);
                    if (throwable != null) {
                        Message.exception(this, throwable, "Error al iniciar sesión con Google");
                    }
                });
        }
    }

    public void updateLoading(boolean loading) {

        if (isFinishing()) return;

        View[] views = {
            et_Email,
            et_Password,
            btn_Login,
            btn_SignInGoogle
        };

        for (View view : views) {
            view.setEnabled(!loading);
        }

        pb_Login.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    // Método para enviar el formulario
    public void sendForm() {

        String email = et_Email.getText().toString().trim();
        String password = et_Password.getText().toString().trim();

        if (!Validate.email(email)) {
            et_Email.setError("Correo inválido");
            return;
        } else {
            et_Email.setError(null);
        }

        switch (Validate.password(password)){
            case Validate.PASSWORD_RESULT_TOO_SHORT:
                et_Password.setError(
                        String.format(Locale.getDefault(), "Se necesitan más de %d caracteres", Validate.PASSWORD_MIN_LENGTH)
                );
                return;
            case Validate.PASSWORD_RESULT_NO_NUMBER:
                et_Password.setError("Debe contener como mínimo un numero");
                return;
            default:
                et_Password.setError(null);
        }

        login(email, password);

    }

    // Método para iniciar sesión
    private void login(String email, String password) {
        updateLoading(true);
        UserManager.login(this, email, password)
            .thenAccept(result -> {
                if (UserManager.isLogged()) {
                    Scene.goAndFinish(LoginActivity.this, Scene.MainUserActivityClass);
                } else {
                    Message.show(this, "Credenciales equivocadas, introdúzcalas de nuevo");
                }
            })
            .whenComplete((aVoid, throwable) -> updateLoading(false));

    }
}