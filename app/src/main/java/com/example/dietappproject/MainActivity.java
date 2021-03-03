package com.example.dietappproject;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dietappproject.fooditemtab.FoodItemFragment;
import com.example.dietappproject.hometab.HomeFragment;
import com.example.dietappproject.mealtab.MealFragment;
import com.example.dietappproject.statstab.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

//Firestore

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference refUsers = db.collection("Users");
    private CollectionReference refFoodItems = db.collection("FoodItems");
    private CollectionReference refMeals = db.collection("Meals");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Navigation - Setup
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Navigation - Remain on same tab on rotation
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }

    //Navigation - Bottom tabs selection
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_meal:
                            selectedFragment = new MealFragment();
                            break;
                        case R.id.nav_fooditem:
                            selectedFragment = new FoodItemFragment();
                            break;
                        case R.id.nav_stats:
                            selectedFragment = new StatsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };
}