package com.example.ilpp.activities.portal;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ilpp.R;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.Scene;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.classes.Validate;
import com.example.ilpp.models.AnimalShelter;
import com.example.ilpp.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupProtectorasActivity extends AppCompatActivity {

    private TextView tv_Welcome, tv_Desc, tv_ExistUser;
    private ImageView iv_Logo;
    private TextInputLayout tf_Address, tf_Name, tf_Password, tf_RePassword, tf_Email;
    private MaterialButton btn_Register;
    private TextInputEditText et_Email, et_RePassword, et_Password, et_Name, et_Address, et_PostalCode, et_City, et_Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_protectoras);

        // Obtener referencias
        btn_Register = findViewById(R.id.btn_Register);
        iv_Logo = findViewById(R.id.iv_Logo);
        tv_Welcome = findViewById(R.id.tv_Title);
        tv_Desc = findViewById(R.id.tv_Desc);
        tf_Address = findViewById(R.id.tf_Address);
        tf_Name = findViewById(R.id.tf_Name);
        tf_Password = findViewById(R.id.tf_Password);
        tf_RePassword = findViewById(R.id.tf_RePassword);
        tf_Email = findViewById(R.id.tf_Email);
        tv_ExistUser = findViewById(R.id.tv_ExistUser);
        et_Email = findViewById(R.id.et_Email);
        et_RePassword = findViewById(R.id.et_RePassword);
        et_Password = findViewById(R.id.et_Password);
        et_Name = findViewById(R.id.et_Name);
        et_Address = findViewById(R.id.et_Address);
        et_PostalCode = findViewById(R.id.et_PostalCode);
        et_City = findViewById(R.id.et_City);
        et_Phone = findViewById(R.id.et_Phone);

        // Animaciones
        Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.fadein_up);
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.fadein_down);

        iv_Logo.setAnimation(anim2);
        tv_Welcome.setAnimation(anim2);
        tv_Desc.setAnimation(anim2);
        tf_Name.setAnimation(anim2);
        tf_Address.setAnimation(anim2);
        tf_Email.setAnimation(anim2);
        tf_Password.setAnimation(anim1);
        tf_RePassword.setAnimation(anim1);
        tv_ExistUser.setAnimation(anim1);
        btn_Register.setAnimation(anim1);

        // Establecer eventos
        tv_ExistUser.setOnClickListener(view -> goBack());
        btn_Register.setOnClickListener(view -> sendForm());
    }

    // Enviar formulario
    public void sendForm() {

        String email = et_Email.getText().toString().trim();
        String password = et_Password.getText().toString().trim();
        String rePassword = et_RePassword.getText().toString().trim();
        String name = et_Name.getText().toString().trim();
        String address = et_Address.getText().toString().trim();
        String postalCode = et_PostalCode.getText().toString().trim();
        String city = et_City.getText().toString().trim();
        String phone = et_Phone.getText().toString().trim();

        // Validar campos

        if (!Validate.name(et_Name, name)) return;
        if (!Validate.address(et_Address, address)) return;
        if (!Validate.city(et_City, city)) return;
        if (!Validate.email(et_Email, email)) return;
        if (!Validate.password(et_Password, password)) return;
        if (!Validate.passwordRe(et_RePassword, password, rePassword)) return;

        register(email, password, name, address, city, postalCode, phone, "");
    }


    // Registrar usuario
    public void register(
            String email,
            String password,
            String name,
            String address,
            String city,
            String postalCode,
            String phone,
            String photoUrl) {

        UserManager.register(this, email, password, name, photoUrl)
            .whenComplete((user, throwable) -> {
                if (throwable != null) {
                    Message.exception(this, throwable, "Error al registrar usuario");
                    return;
                }
                if (user == null) {
                    Message.show(this, "Fallo en el registro");
                    return;
                }

                User data = user.getUserData();

                // Crear o actualizar datos de la protectora
                AnimalShelter shelter = new AnimalShelter();
                shelter.setId(data.getId()); // El mismo id que el usuario
                shelter.setName(name);
                shelter.getAddress().setAddress(address);
                shelter.getAddress().setCity(city);
                shelter.getAddress().setPostalCode(postalCode);
                shelter.setPhone(phone);
                shelter.setImageUrl(photoUrl);
                shelter.update()
                    .whenComplete((v, e) -> {
                        if (e != null) {
                            Message.exception(this, e, "Error al registrar la protectora");
                            return;
                        }

                        // Navegar a la siguiente actividad
                        Scene.goAndFinish(this, Scene.MainUserShelterActivityClass);
                    });

                // Guardar datos adicionales del usuario
                data.setIsAnimalShelter(true);
                data.update(); // No es necesario esperar a que se actualicen los datos

            });

    }


    @Override
    public void onBackPressed() { goBack(); }

    // Volver a la sección de login
    private void goBack() {
        Scene.goAndFinish(this, LoginActivity.class);
    }

}


