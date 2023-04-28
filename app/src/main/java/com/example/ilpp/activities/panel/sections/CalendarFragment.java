package com.example.ilpp.activities.panel.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ilpp.R;
import com.example.ilpp.classes.Format;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.controls.Calendar;
import com.example.ilpp.models.Animal;
import com.example.ilpp.models.AnimalShelter;
import com.example.ilpp.models.AnimalWalkBooking;
import com.example.ilpp.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CalendarFragment extends Fragment {

    private Calendar cc_Calendar;
    private View tv_NoData;
    private View ll_Details;
    private LinearLayout ll_WalkDetails;

    public static void open(NavController from) {
        from.navigate(
                R.id.act_ToCalendar,
                new Bundle()
        );
    }
    public static void open(Fragment from){
        open(NavHostFragment.findNavController(from));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener referencias
        cc_Calendar = view.findViewById(R.id.cc_Calendar);
        tv_NoData = view.findViewById(R.id.tv_NoData);
        ll_Details = view.findViewById(R.id.ll_Details);
        ll_WalkDetails = view.findViewById(R.id.ll_WalkDetails);

        // Inicializar propiedades
        ll_Details.setVisibility(View.GONE);
        cc_Calendar.setEnabled(false);

        // Configurar filtro

        // Establecer eventos
        cc_Calendar.setOnDayCreatedListener((dayView, day) -> {
            if (findWalks(day.date).size() > 0){
                day.viewMark.setVisibility(View.VISIBLE);
            }
        });

        // Leer datos de paseos
        loadWalks();

        cc_Calendar.setOnDateSelectedListener(date -> {
            openWalk();
        });

    }

    private List<AnimalWalkBooking> findWalks(Date date){
        List<AnimalWalkBooking> result = new ArrayList<>();
        for (AnimalWalkBooking walk : walks){
            if (walk.getDate().equals(date)){
                result.add(walk);
            }
        }
        return result;
    }

    private List<AnimalWalkBooking> walks = new ArrayList<>();
    private void loadWalks(){

        AnimalShelter data = UserManager.getUser().getAnimalShelterData();
        AnimalWalkBooking.getByAnimalShelter(data.getId())
                .whenComplete((walks, error) -> {
                    if (error != null){
                        Message.exception(getContext(), error, "No se pudieron obtener los paseos.");
                        return;
                    }

                    this.walks = walks;
                    cc_Calendar.setEnabled(true);
                    cc_Calendar.renderCalendar();
                    openWalk();
                });

    }

    private void openWalk(){
        List<AnimalWalkBooking> walks = findWalks(cc_Calendar.getCurrentDate());

        ll_Details.setVisibility(View.GONE);
        ll_WalkDetails.removeAllViews();

        if (walks.size() == 0){
            ll_Details.setVisibility(View.VISIBLE);
            tv_NoData.setVisibility(View.VISIBLE);
        } else {

            List<CompletableFuture<Animal>> futures = new ArrayList<>();
            for (AnimalWalkBooking walk : walks){
                futures.add(createWalkDetails(walk));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((result, error) -> {
                    if (error != null){
                        Message.exception(getContext(), error, "No se pudieron obtener los detalles de los paseos.");
                        return;
                    }

                    tv_NoData.setVisibility(View.GONE);
                    ll_Details.setVisibility(View.VISIBLE);
                });

        }
    }

    private CompletableFuture<Animal> createWalkDetails(AnimalWalkBooking walk){
        return Animal.get(walk.getAnimalId())
            .whenComplete((animal, error) -> {
                if (error != null){
                    Message.exception(getContext(), error, "No se pudo obtener el animal.");
                    return;
                }

                User.get(walk.getUserId())
                    .whenComplete((user, error2) -> {
                        if (error2 != null){
                            Message.exception(getContext(), error2, "No se pudo obtener el usuario.");
                            return;
                        }

                        View view = getLayoutInflater().inflate(R.layout.fragment_calendar_walk_details, null);

                        TextView tv_AnimalName = view.findViewById(R.id.tv_AnimalName);
                        TextView tv_Date = view.findViewById(R.id.tv_Date);
                        TextView tv_Time = view.findViewById(R.id.tv_Time);
                        TextView tv_UserName = view.findViewById(R.id.tv_UserName);

                        tv_AnimalName.setText(animal.getName());
                        tv_Date.setText(Format.toDateString(walk.getDate()));
                        tv_Time.setText(animal.getSchedule().getDisplayTime());
                        tv_UserName.setText(user.getDisplayName());

                        ll_WalkDetails.addView(view);
                    });
            });
    }

}