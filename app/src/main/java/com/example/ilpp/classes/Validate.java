package com.example.ilpp.classes;

import android.util.Patterns;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Validate {

    public static boolean name(String name) {
        return !name.isEmpty();
    }
    public static boolean name(TextInputEditText editText, String value) {
        return apply(editText, name(value), "Nombre inválido");
    }

    public static boolean surname(String surname) { return name(surname); }
    public static boolean surname(TextInputEditText editText, String value) {
        return apply(editText, surname(value), "Apellidos inválidos");
    }

    public static boolean address(String address) { return name(address); }
    public static boolean address(TextInputEditText editText, String value) {
        return apply(editText, address(value), "Dirección inválida");
    }

    public static boolean city(String city) { return name(city); }
    public static boolean city(TextInputEditText editText, String value) {
        return apply(editText, city(value), "Ciudad inválida");
    }

    public static boolean date(String value){
        Date date = Format.toDate(value);
        return date != null;
    }
    public static boolean date(TextInputEditText editText, String value) {
        return apply(editText, date(value), "Fecha inválida");
    }

    public static boolean apply(EditText editText, boolean valid, String message) {
        if (valid) {
            editText.setError(null);
        } else {
            editText.setError(message);
        }
        return valid;
    }

    private static boolean applyPassword(TextInputEditText editText, int result) {
        switch (result) {
            case Validate.PASSWORD_RESULT_TOO_SHORT:
                editText.setError(
                        String.format(Locale.getDefault(), "Se necesitan más de %d caracteres", Validate.PASSWORD_MIN_LENGTH)
                );
                return false;
            case Validate.PASSWORD_RESULT_NO_NUMBER:
                editText.setError("Debe contener como mínimo un numero");
                return false;
            default:
                editText.setError(null);
                return true;
        }
    }

    public static boolean email(TextInputEditText editText, String value) {
        return apply(editText, email(value), "Correo inválido");
    }
    public static boolean email(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static final int PASSWORD_MIN_LENGTH = 8;

    public static final int PASSWORD_RESULT_OK = 0;
    public static final int PASSWORD_RESULT_TOO_SHORT = 1;
    public static final int PASSWORD_RESULT_NO_NUMBER = 2;

    public static boolean password(TextInputEditText editText, String value) {
        return applyPassword(editText, password(value));
    }

    public static int password(String password) {
        if (password.isEmpty() || password.length() < PASSWORD_MIN_LENGTH){
            return PASSWORD_RESULT_TOO_SHORT;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return PASSWORD_RESULT_NO_NUMBER;
        } else {
            return PASSWORD_RESULT_OK;
        }
    }

    public static boolean passwordRe(TextInputEditText editText, String password, String rePassword) {
        return apply(editText, passwordRe(password, rePassword), "Las contraseñas deben ser iguales");
    }

    public static boolean passwordRe(String password, String rePassword) {
        return password.equals(rePassword);
    }

}
