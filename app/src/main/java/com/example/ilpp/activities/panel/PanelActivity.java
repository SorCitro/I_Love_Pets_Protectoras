package com.example.ilpp.activities.panel;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.example.ilpp.R;
import com.example.ilpp.activities.panel.sections.AdoptionAnimalsFragment;
import com.example.ilpp.activities.panel.sections.AnimalFavoritesFragment;
import com.example.ilpp.activities.panel.sections.AnimalInsertFragment;
import com.example.ilpp.activities.panel.sections.CalendarFragment;
import com.example.ilpp.activities.panel.sections.MyAnimalsFragment;
import com.example.ilpp.activities.portal.LoginActivity;
import com.example.ilpp.classes.Scene;
import com.example.ilpp.classes.UserManager;
import com.example.ilpp.databinding.ActivityPanelBinding;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

public class PanelActivity extends AppCompatActivity {

    private NavController navController;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPanelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPanelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarPanel.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_Home)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_panel);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        MenuItem nav_Logout = navigationView.getMenu().findItem(R.id.nav_Logout);
        MenuItem nav_Favorites = navigationView.getMenu().findItem(R.id.nav_Favorites);
        MenuItem nav_MyAnimals = navigationView.getMenu().findItem(R.id.nav_MyAnimals);
        MenuItem nav_Calendar = navigationView.getMenu().findItem(R.id.nav_Calendar);
        MenuItem nav_AnimalInsert = navigationView.getMenu().findItem(R.id.nav_AnimalInsert);
        MenuItem nav_AdoptionAnimals = navigationView.getMenu().findItem(R.id.nav_AdoptionAnimals);

        if (!UserManager.getUser().getUserData().getIsAnimalShelter()){
            nav_AnimalInsert.setVisible(false);
            nav_Calendar.setVisible(false);
            nav_MyAnimals.setVisible(false);
        }

        nav_Logout.setOnMenuItemClickListener(v -> {
            UserManager.logout(this);
            Scene.goAndFinish(this, LoginActivity.class);
            return true;
        });

        nav_AdoptionAnimals.setOnMenuItemClickListener(v -> {
            AdoptionAnimalsFragment.open(navController);
            drawer.closeDrawer(GravityCompat.START, true);
            return true;
        });

        nav_Favorites.setOnMenuItemClickListener(v -> {
            AnimalFavoritesFragment.open(navController);
            drawer.closeDrawer(GravityCompat.START, true);
            return true;
        });

        nav_MyAnimals.setOnMenuItemClickListener(v -> {
            MyAnimalsFragment.open(navController);
            drawer.closeDrawer(GravityCompat.START, true);
            return true;
        });

        nav_Calendar.setOnMenuItemClickListener(v -> {
            CalendarFragment.open(navController);
            drawer.closeDrawer(GravityCompat.START, true);
            return true;
        });

        nav_AnimalInsert.setOnMenuItemClickListener(v -> {
            AnimalInsertFragment.open(navController);
            drawer.closeDrawer(GravityCompat.START, true);
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.panel, menu);

        MenuItem ac_Logout = menu.findItem(R.id.ac_Logout);
        MenuItem ac_Profile = menu.findItem(R.id.ac_Profile);

        ac_Profile.setOnMenuItemClickListener(v -> {
            UserManager.getUser().openProfile(navController);
            return true;
        });

        ac_Logout.setOnMenuItemClickListener(v -> {
            UserManager.logout(this);
            Scene.goAndFinish(this, LoginActivity.class);
            return true;
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_panel);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}