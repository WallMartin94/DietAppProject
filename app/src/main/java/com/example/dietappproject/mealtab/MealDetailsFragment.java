package com.example.dietappproject.mealtab;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietappproject.R;
import com.example.dietappproject.dbobject.FoodItem;
import com.example.dietappproject.dbobject.Meal;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MealDetailsFragment extends Fragment {
    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mealRef = db.collection("Meals");
    private CollectionReference foodRef = db.collection("FoodItems");

    //Received mealId from bundle
    private String mealId;

    //RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<MealItemDetails> foodItemList = new ArrayList<>();

    //Accumulators for Total of foodItem nutrition values
    private double totalFat = 0;
    private double totalCarbs = 0;
    private double totalProtein = 0;
    private double totalCalories = 0;

    TextView textViewMealName;
    TextView textViewMealDate;
    TextView textViewTotalFat;
    TextView textViewTotalCarbs;
    TextView textViewTotalProtein;
    TextView textViewTotalCalories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meal_details, container, false);
        textViewMealName = v.findViewById(R.id.textview_meal_name);
        textViewMealDate = v.findViewById(R.id.textview_meal_date);
        textViewTotalFat = v.findViewById(R.id.textview_total_fat);
        textViewTotalCarbs = v.findViewById(R.id.textview_total_carbs);
        textViewTotalProtein = v.findViewById(R.id.textview_total_protein);
        textViewTotalCalories = v.findViewById(R.id.textview_total_calories);

        //Get mealId from MealFragment
        mealId = getArguments().getString("mealId");

        //RecyclerView - List of foodItems
        mRecyclerView = v.findViewById(R.id.recycler_view_meal_details);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(v.getContext());
        mAdapter = new MealDetailsAdapter(foodItemList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        loadMealDetails();
        return v;
    }

    public void loadMealDetails() {
        //Query meal for foodItems and amounts
        mealRef.document(mealId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Meal meal = documentSnapshot.toObject(Meal.class);
                        meal.setDocumentId(documentSnapshot.getId());

                        textViewMealName.setText(meal.getCategory());
                        textViewMealDate.setText(String.valueOf(meal.getDate()));

                        String mealCategory = meal.getCategory();
                        for (String foodItemId : meal.getMealItems().keySet()) {
                            double amount = meal.getMealItems().get(foodItemId);

                            //Query each foodItem on the meal
                            foodRef.document(foodItemId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            FoodItem foodItem = documentSnapshot.toObject(FoodItem.class);
                                            foodItem.setDocumentId(documentSnapshot.getId());

                                            double itemTotalFat = amount * foodItem.getFat();
                                            double itemTotalCarbs = amount * foodItem.getCarbs();
                                            double itemTotalProtein = amount * foodItem.getProtein();
                                            double itemTotalCalories = amount * foodItem.getCalories();

                                            foodItemList.add(new MealItemDetails(foodItem.getName(),
                                                    itemTotalProtein,
                                                    itemTotalFat,
                                                    itemTotalCarbs,
                                                    itemTotalCalories));
                                            Log.i("TEST FOODITEM: ", String.valueOf(foodItem.getCalories()));
                                            mAdapter.notifyDataSetChanged();

                                            totalFat += itemTotalFat;
                                            totalCarbs += itemTotalCarbs;
                                            totalProtein += itemTotalProtein;
                                            totalCalories += itemTotalCalories;
                                            textViewTotalFat.setText(String.format("%.0f", totalFat));
                                            textViewTotalCarbs.setText(String.format("%.0f", totalCarbs));
                                            textViewTotalProtein.setText(String.format("%.0f", totalProtein));
                                            textViewTotalCalories.setText(String.format("%.0f", totalCalories));
                                        }
                                    });
                        }
                    }
                });
    }
}
