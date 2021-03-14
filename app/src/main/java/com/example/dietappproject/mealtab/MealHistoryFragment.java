package com.example.dietappproject.mealtab;


import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.example.dietappproject.R;
import com.example.dietappproject.dbobject.Meal;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;


public class MealHistoryFragment extends Fragment {
    private static final String TAG = "MealHistoryFragment";

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mealRef = db.collection("Meals");
    private MealAdapter adapter;

    //Firebase auth
    private FirebaseAuth auth;
    private String mealUser;

    //Search
    DatePicker datePickerFrom;
    DatePicker datePickerTo;
    ImageButton imageButtonSearch;
    Calendar queryCalFrom;
    Calendar queryCalTo;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meal_history, container, false);
        imageButtonSearch = view.findViewById(R.id.button_meal_history_search);
        datePickerFrom = view.findViewById(R.id.datepicker_meal_history_from);
        datePickerTo = view.findViewById(R.id.datepicker_meal_history_to);

        //Firebase auth
        auth = FirebaseAuth.getInstance();
        mealUser = auth.getUid();

        getPickerDates();
        setUpRecyclerView();

        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPickerDates();
                updateRecyclerView();
            }
        });

        return view;
    }

    private void getPickerDates() {
        queryCalFrom = Calendar.getInstance();
        queryCalTo = Calendar.getInstance();

        if (Build.VERSION.SDK_INT < 23){
            queryCalFrom.set(datePickerFrom.getYear(),
                    datePickerFrom.getMonth(),
                    datePickerFrom.getDayOfMonth(),
                    00, 00,00);
        } else {
            queryCalFrom.set(datePickerFrom.getYear(),
                    datePickerFrom.getMonth(),
                    datePickerFrom.getDayOfMonth(),
                    00, 00,00);
        }

        if (Build.VERSION.SDK_INT < 23){
            queryCalTo.set(datePickerTo.getYear(),
                    datePickerTo.getMonth(),
                    datePickerTo.getDayOfMonth(),
                    23, 59,59);
        } else {
            queryCalTo.set(datePickerTo.getYear(),
                    datePickerTo.getMonth(),
                    datePickerTo.getDayOfMonth(),
                    23, 59,59);
        }
    }

    private void updateRecyclerView() {
        Query query = mealRef
                .whereLessThan("date", queryCalTo.getTime())
                .whereGreaterThan("date", queryCalFrom.getTime())
                .whereEqualTo("mealUser", mealUser)
                .limit(100)
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Meal> options = new FirestoreRecyclerOptions.Builder<Meal>()
                .setQuery(query, Meal.class)
                .build();
        adapter.updateOptions(options);
    }


    private void setUpRecyclerView() {

        Query query = mealRef
                .whereLessThan("date", queryCalTo.getTime())
                .whereGreaterThan("date", queryCalFrom.getTime())
                .whereEqualTo("mealUser", mealUser)
                .limit(10)
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Meal> options = new FirestoreRecyclerOptions.Builder<Meal>()
                .setQuery(query, Meal.class)
                .build();

        adapter = new MealAdapter(options);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_meal_history);
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

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}