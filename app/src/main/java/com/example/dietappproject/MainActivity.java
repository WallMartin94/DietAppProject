package com.example.dietappproject;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dietappproject.fooditemtab.AddItemFragment;
import com.example.dietappproject.fooditemtab.FoodItemFragment;
import com.example.dietappproject.hometab.HomeFragment;
import com.example.dietappproject.mealtab.AddMealFragment;
import com.example.dietappproject.mealtab.MealFragment;
import com.example.dietappproject.settingstab.SettingsFragment;
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
//Method to allow soft keyboard to remove should user touch outside input areas.
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
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
                        case R.id.nav_settings:
                            selectedFragment = new SettingsFragment();
                            selectedFragmentTag = "SettingsFragment";
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