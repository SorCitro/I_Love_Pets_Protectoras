package com.example.ilpp.activities.portal;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import com.example.ilpp.R;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.Scene;
import com.example.ilpp.classes.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.CompletableFuture;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Scene.goAndFinish(this, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Obtener referencias
        TextInputEditText et_Email = findViewById(R.id.et_Email);
        MaterialButton btn = findViewById(R.id.btn_RecoverPassword);

        // Establecer eventos
        btn.setOnClickListener(v -> {

            String email = et_Email.getText().toString().trim();

            resetPassword(email);

        });

    }

    private void resetPassword(String email) {
        CompletableFuture<Boolean> r = UserManager.resetPassword(email);
        r.whenComplete((result, throwable) -> {

            if (throwable != null) {
                Message.exception(this, throwable, "Error desconocido al enviar el correo electrónico de restablecimiento de contraseña");
                return;
            }

            if (result) {
                Message.show(this, "Se ha enviado un correo electrónico de restablecimiento de contraseña");
            } else {
                Message.show(this, "Error al enviar el correo electrónico de restablecimiento de contraseña");
            }

            onBackPressed();
        });
    }
}