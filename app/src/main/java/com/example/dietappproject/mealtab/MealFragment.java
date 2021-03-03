package com.example.dietappproject.mealtab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietappproject.R;
import com.example.dietappproject.dbobject.Meal;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class MealFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mealRef = db.collection("Meals");

    private MealAdapter adapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meal, container, false);
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        //Query for today's meals
        Date todayEarly = new Date();
        todayEarly.setHours(0);
        todayEarly.setMinutes(0);
        todayEarly.setSeconds(0);

        Query query = mealRef
                .whereLessThan("date", new Date())
                .whereGreaterThan("date", todayEarly)
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
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
