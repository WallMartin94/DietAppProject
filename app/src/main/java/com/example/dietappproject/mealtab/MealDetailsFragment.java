package com.example.dietappproject.mealtab;

import android.graphics.Color;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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

    //Piechart
    PieChart pieChart;
    ArrayList<PieEntry> pieEntries;
    ArrayList<MealDetailsPieData> pieList = new ArrayList<>();
    PieDataSet pieDataSet;
    private int finishedSubQueries = 0; //Accumulator inside Firebase query

    //Layout
    TextView textViewMealName;
    TextView textViewMealDate;
    TextView textViewTotalFat;
    TextView textViewTotalCarbs;
    TextView textViewTotalProtein;
    TextView textViewTotalCalories;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meal_details, container, false);
        textViewMealName = view.findViewById(R.id.textview_meal_name);
        textViewMealDate = view.findViewById(R.id.textview_meal_date);
        textViewTotalFat = view.findViewById(R.id.textview_total_fat);
        textViewTotalCarbs = view.findViewById(R.id.textview_total_carbs);
        textViewTotalProtein = view.findViewById(R.id.textview_total_protein);
        textViewTotalCalories = view.findViewById(R.id.textview_total_calories);

        //Get mealId from MealFragment
        mealId = getArguments().getString("mealId");

        //RecyclerView - List of foodItems
        mRecyclerView = view.findViewById(R.id.recycler_view_meal_details);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mAdapter = new MealDetailsAdapter(foodItemList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        loadMealDetails();
        return view;
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

                                            //Create Piechart when all foodItem subqueries are finished
                                            finishedSubQueries++;
                                            if (finishedSubQueries == meal.getMealItems().size()) {
                                                createPieChart();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void createPieChart() {
        //Piechart
        pieChart = view.findViewById(R.id.piechart_meal_details);
        //Create list of data
        pieEntries = new ArrayList<>();
        pieList.add(new MealDetailsPieData("fat", totalFat * 9));        // 1g fat = 9kcal
        pieList.add(new MealDetailsPieData("carbs", totalCarbs* 4));     // 1g carb = 4kcal
        pieList.add(new MealDetailsPieData("protein", totalProtein* 4)); // 1g fat = 4kcal

        for (int i = 0; i < pieList.size(); i++) {
            String macroType = pieList.get(i).getMacroType();
            double amount = pieList.get(i).getAmount();
            pieEntries.add(new PieEntry((float) amount, macroType));
        }

        //Piechart settings
        pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setValueTextSize(15);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart));
        pieDataSet.setValueLinePart1Length(0.5f);
        pieDataSet.setValueLinePart2Length(0.3f);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLineWidth(2f);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setData(pieData);
        pieChart.setDescription(null);
        pieChart.setDrawCenterText(true);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(40, 0, 75, 8);
        pieChart.setCenterText("Total: " + String.format("%.0f", totalCalories) + " kcal");
        //Piechart legend settings
        Legend legend = pieChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setYEntrySpace(10f);
        legend.setYOffset(50f);
        legend.setXOffset(-50f);
        legend.setTextSize(15);

        //Piechart animation
        pieChart.animateXY(2000, 2000);
        pieChart.animate();
    }
}
