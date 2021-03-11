package com.example.dietappproject;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dietappproject.fooditemtab.AddItemFragment;
import com.example.dietappproject.fooditemtab.FoodItemFragment;
import com.example.dietappproject.hometab.HomeFragment;
import com.example.dietappproject.mealtab.AddMealFragment;
import com.example.dietappproject.mealtab.MealFragment;
import com.example.dietappproject.statstab.StatsFragment;
import com.example.dietappproject.utils.BarcodeScannerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

//Firestore

public class MainActivity extends AppCompatActivity implements BarcodeScannerFragment.CameraListener {
    private static final String TAG = "MainActivity";

    //Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference refUsers = db.collection("Users");
    private CollectionReference refFoodItems = db.collection("FoodItems");
    private CollectionReference refMeals = db.collection("Meals");

    //Firebase User
    private FirebaseAuth auth;




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

        auth = FirebaseAuth.getInstance();
        Log.i(TAG, auth.toString() + "---" + auth.getUid());
    }

    //Navigation - Bottom tabs selection
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    String selectedFragmentTag = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            selectedFragmentTag = "HomeFragment";
                            break;
                        case R.id.nav_meal:
                            selectedFragment = new MealFragment();
                            selectedFragmentTag = "MealFragment";
                            break;
                        case R.id.nav_fooditem:
                            selectedFragment = new FoodItemFragment();
                            selectedFragmentTag = "FoodItemFragment";
                            break;
                        case R.id.nav_stats:
                            selectedFragment = new StatsFragment();
                            selectedFragmentTag = "StatsFragment";
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment, selectedFragmentTag)
                            .addToBackStack(null)
                            .commit();

                    return true;
                }
            };

    //Scanned input from Camera back to AddMeal
    @Override
    public void onInputCameraSent(String input) {

        Fragment frag = getSupportFragmentManager().findFragmentByTag("AddMealFragment");
        ((AddMealFragment) frag).inputFromCamera(input);
    }





}