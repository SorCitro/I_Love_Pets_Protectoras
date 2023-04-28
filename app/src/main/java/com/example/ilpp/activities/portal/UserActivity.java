package com.example.ilpp.activities.portal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ilpp.R;
import com.example.ilpp.classes.Scene;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserActivity extends AppCompatActivity {

    private ImageView iv_ILP;
    private MaterialButton btn_LogOut;
    private TextView tv_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Obtener referencias
        iv_ILP = findViewById(R.id.iv_ILP);
        btn_LogOut = findViewById(R.id.btn_LogOut);
        tv_Email = findViewById(R.id.tv_Email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tv_Email.setText(user.getEmail());
        }

        // Establecer eventos
        btn_LogOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Scene.goAndFinish(this, LoginActivity.class);
        });


    }
}