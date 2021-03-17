package com.example.dietappproject.mealtab;

import  android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietappproject.R;
import com.example.dietappproject.dbobject.Meal;
import com.example.dietappproject.dbobject.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Calendar;
import java.util.List;

public class MealTodayFragment extends Fragment {
    private static final String TAG = "MealTodayFragment";
    //Firestore Recyclerview
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mealRef = db.collection("Meals");
    private MealAdapter adapter;
    Query query;

    //Firebase auth
    private FirebaseAuth auth;
    private String mealUser;

    private CollectionReference userRef = db.collection("Users");

    //Accumulator for today's meals
    double totalCalories;
    double totalFat;
    double totalCarbs;
    double totalProtein;
    double calorieGoal;
    double fatGoal;
    double carbsGoal;
    double proteinGoal;

    //Calorie indicator
    LinearProgressIndicator calorieIndicator;
    LinearProgressIndicator fatIndicator;
    LinearProgressIndicator carbsIndicator;
    LinearProgressIndicator proteinIndicator;
    TextView textViewCalories;
    TextView textViewFat;
    TextView textViewCarbs;
    TextView textViewProtein;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meal_today, container, false);

        calorieIndicator = view.findViewById(R.id.indicator_meal_today_calories);
        fatIndicator = view.findViewById(R.id.indicator_meal_today_fat);
        carbsIndicator = view.findViewById(R.id.indicator_meal_today_carbs);
        proteinIndicator = view.findViewById(R.id.indicator_meal_today_protein);
        textViewCalories = view.findViewById(R.id.textview_meal_today_calories);
        textViewFat = view.findViewById(R.id.textview_meal_today_fat);
        textViewCarbs = view.findViewById(R.id.textview_meal_today_carbs);
        textViewProtein = view.findViewById(R.id.textview_meal_today_protein);

        //Floating Add Meal Button
        FloatingActionButton fab = view.findViewById(R.id.button_add_meal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddMealFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment, "AddMealFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        //Firebase auth
        auth = FirebaseAuth.getInstance();
        mealUser = auth.getUid();

        setUpRecyclerView();
        setupProgressIndicators();
        return view;
    }

    private void setUpRecyclerView() {
        //Get time
        Calendar calCurrent = Calendar.getInstance();
        Calendar calEarly = Calendar.getInstance();
        Calendar calLate = Calendar.getInstance();
        calEarly.set(calCurrent.get(Calendar.YEAR),
                calCurrent.get(Calendar.MONTH),
                calCurrent.get(Calendar.DAY_OF_MONTH),
                00, 00, 00);
        calLate.set(calCurrent.get(Calendar.YEAR),
                calCurrent.get(Calendar.MONTH),
                calCurrent.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);

        //Query for today's meals
        query = mealRef
                .whereLessThan("date", calLate.getTime())
                .whereGreaterThan("date", calEarly.getTime())
                .whereEqualTo("mealUser", mealUser)
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Meal> options = new FirestoreRecyclerOptions.Builder<Meal>()
                .setQuery(query, Meal.class)
                .build();

        adapter = new MealAdapter(options);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_meal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        //Click on Meal to open Meal Details
        adapter.setOnItemClickListener(new MealAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Meal meal = documentSnapshot.toObject(Meal.class);
                String id = documentSnapshot.getId();

                Bundle bundle = new Bundle();
                bundle.putString("mealId", id);

                MealDetailsFragment fragment = new MealDetailsFragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setupProgressIndicators() {
        //Run when query for Firebase Recyclerview has results
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG, e.getMessage());
                    return;
                }

                //Convert snapshots to Meals
                List<Meal> meals = snapshot.toObjects(Meal.class);
                //Reset accumulators
                totalCalories = 0;
                totalFat = 0;
                totalCarbs = 0;
                totalProtein = 0;
                //Accumulate meal stats
                for(Meal meal : meals) {
                    totalCalories += meal.getCalories();
                    totalFat += meal.getFat();
                    totalCarbs += meal.getCarbs();
                    totalProtein += meal.getProtein();
                }
                //Query User data and populate indicators
                userRef.document(mealUser)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        user.setDocumentId(documentSnapshot.getId());

                        calorieGoal = user.getCalorieGoal();
                        fatGoal = user.getFatGoal();
                        carbsGoal = user.getCarbsGoal();
                        proteinGoal = user.getProteinGoal();

                        //Present Calories Indicator
                        calorieIndicator.setMax((int)calorieGoal);
                        calorieIndicator.setProgressCompat((int)totalCalories, true);
                        textViewCalories.setText(String.format("Calories: %.0f / %.0f", totalCalories, calorieGoal));
                        //Present Fat Indicator
                        fatIndicator.setMax((int)fatGoal);
                        fatIndicator.setProgressCompat((int)totalFat, true);
                        textViewFat.setText(String.format("Fat: %.0f / %.0f", totalFat, fatGoal));
                        //Present Carbs Indicator
                        carbsIndicator.setMax((int)carbsGoal);
                        carbsIndicator.setProgressCompat((int)totalCarbs, true);
                        textViewCarbs.setText(String.format("Carbs: %.0f / %.0f", totalCarbs, carbsGoal));
                        //Present Protein Indicator
                        proteinIndicator.setMax((int)proteinGoal);
                        proteinIndicator.setProgressCompat((int)totalProtein, true);
                        textViewProtein.setText(String.format("Protein: %.0f / %.0f", totalProtein, proteinGoal));
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //Firestore Recyclerview - start listening in foreground
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Firestore Recyclerview - stop listening in background
        adapter.stopListening();
    }
}