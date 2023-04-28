package com.example.ilpp.activities.panel.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ilpp.R;
import com.example.ilpp.classes.Format;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.classes.Validate;
import com.example.ilpp.models.Animal;
import com.example.ilpp.models.AnimalWalkBooking;
import com.example.ilpp.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class AnimalWalkBookingDialog extends DialogFragment {

    private TextInputEditText et_Date;
    private TextInputLayout tf_Date;
    private TextView tv_Date, tv_Time;
    private View ll_Details, tv_AlreadyBooked;
    private View btn_Book;


    private Animal animal;
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_animal_walk_booking, container, false);

        // Obtener referencias
        et_Date = view.findViewById(R.id.et_Date);
        tf_Date = view.findViewById(R.id.tf_Date);
        tv_Date = view.findViewById(R.id.tv_Date);
        tv_Time = view.findViewById(R.id.tv_Time);
        tv_AlreadyBooked = view.findViewById(R.id.tv_AlreadyBooked);
        ll_Details = view.findViewById(R.id.ll_Details);
        TextView tv_Title = view.findViewById(R.id.tv_Title);
        btn_Book = view.findViewById(R.id.btn_Book);

        // Inicializar propiedades
        ll_Details.setVisibility(View.GONE);
        tv_AlreadyBooked.setVisibility(View.GONE);
        tv_Title.setText(getResources().getString(R.string.animal_walk_booking_title, animal.getName()));

        // Establecer eventos
        tf_Date.setEndIconOnClickListener(v -> {
            AnimalWalkBookingDateDialog dialog = new AnimalWalkBookingDateDialog();
            Date date = Format.toDate(et_Date.getText().toString().trim());
            if (date != null) dialog.setDate(date);
            dialog.setOnSelectedDateListener(value -> {
                selectDate(value);
            });
            dialog.show(getChildFragmentManager(), "AnimalWalkBookingDateDialog");
        });
        btn_Book.setOnClickListener(v -> {
            sendForm().whenComplete((result, error) -> {
                if (error != null) {
                    Message.exception(getContext(), error, "Error al enviar el formulario");
                    return;
                }

                if (!result) return; // Si el formulario no es válido, no se envía

                Message.show(getContext(), "Paseo reservado correctamente");
                this.dismiss();
            });
        });

        return view;
    }

    private CompletableFuture<Boolean> sendForm(){
        String date = et_Date.getText().toString().trim();

        if (!Validate.date(et_Date, date)) return CompletableFuture.completedFuture(false);

        User user = UserManager.getUser().getUserData();

        AnimalWalkBooking booking = new AnimalWalkBooking();
        booking.setAnimalId(animal.getId());
        booking.setUserId(user.getId());
        booking.setAnimalShelterId(animal.getAnimalShelterId());
        booking.setDate(Format.toDate(date));

        return booking.create().thenApply(result -> {
            return true;
        }).exceptionally(error -> {
            Message.exception(getContext(), error, "Error al enviar el formulario");
            return false;
        });

    }

    private void selectDate(Date date){
        et_Date.setText(Format.toDateString(date));

        tv_AlreadyBooked.setVisibility(View.GONE);
        ll_Details.setVisibility(View.GONE);
        btn_Book.setEnabled(false);

        AnimalWalkBooking.getByAnimalDate(animal.getId(), date).whenComplete((result, error) -> {
            if (error != null) {
                Message.exception(getContext(), error, "Error al obtener las reservas");
                return;
            }

            ll_Details.setVisibility(View.VISIBLE);
            String text = animal.getSchedule().getDisplayTime();
            tv_Time.setText(text);
            text = Format.toDateString(date) + " (" + getResources().getStringArray(R.array.days)[date.getDay()] + ")";
            tv_Date.setText(text);

            if (result.size() > 0) {
                tv_AlreadyBooked.setVisibility(View.VISIBLE);
            }else{
                btn_Book.setEnabled(true);
            }
        });

    }

}
