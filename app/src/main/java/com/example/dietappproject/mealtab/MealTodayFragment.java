package com.example.dietappproject.mealtab;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietappproject.R;
import com.example.dietappproject.dbobject.Meal;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;

public class MealTodayFragment extends Fragment {
    //Firestore Recyclerview
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mealRef = db.collection("Meals");
    private MealAdapter adapter;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meal_today, container, false);

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

        setUpRecyclerView();
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
        Query query = mealRef
                .whereLessThan("date", calLate.getTime())
                .whereGreaterThan("date", calEarly.getTime())
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