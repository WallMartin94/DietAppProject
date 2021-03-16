package com.example.dietappproject.statstab;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dietappproject.R;
import com.example.dietappproject.dbobject.Meal;
import com.example.dietappproject.dbobject.User;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;


public class StatsFragment extends Fragment {
    private static final String TAG = "StatsFragment";
    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mealRef = db.collection("Meals");
    private CollectionReference userRef = db.collection("Users");

    //Firebase auth
    private FirebaseAuth auth;
    private String user;

    //Data
    double weekBreakfastCalories;
    double weekLunchCalories;
    double weekDinnerCalories;
    double weekSnackCalories;
    double weekFikaCalories;

    //Barchart weekly category
    HorizontalBarChart barChartCategory;
    ArrayList<BarEntry> barEntriesCategory;
    ArrayList<String> barChartLabelsCategory;
    ArrayList<StatsBarChartCategoryData> barChartCategoryData = new ArrayList<>();

    TextView textViewStatsWeeklyTotal;
    TextView textViewStatsWeeklyAvg;
    TextView textViewStatsUser;
    ImageView imageViewStatsGender;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);
        textViewStatsWeeklyTotal = v.findViewById(R.id.textview_stats_weekly_total);
        textViewStatsWeeklyAvg = v.findViewById(R.id.textview_stats_weekly_avg);
        textViewStatsUser = v.findViewById(R.id.textview_stats_user);
        imageViewStatsGender = v.findViewById(R.id.imageview_stats_gender);
        barChartCategory = v.findViewById(R.id.barchart_stats_weekly_category);

        //Firebase auth
        auth = FirebaseAuth.getInstance();
        user = auth.getUid();

        //Load firebase data
        loadUser();
        loadWeeklyMeals();

        return v;
    }

    private void setupBarChartCategory() {
        barChartCategoryData.add(new StatsBarChartCategoryData("Breakfast", weekBreakfastCalories / 7));
        barChartCategoryData.add(new StatsBarChartCategoryData("Lunch", weekLunchCalories / 7));
        barChartCategoryData.add(new StatsBarChartCategoryData("Dinner", weekDinnerCalories / 7));
        barChartCategoryData.add(new StatsBarChartCategoryData("Snacks", weekSnackCalories / 7));
        barChartCategoryData.add(new StatsBarChartCategoryData("Fika", weekFikaCalories/ 7));

        barEntriesCategory = new ArrayList<>();
        barChartLabelsCategory = new ArrayList<>();
        for(int i = 0; i < barChartCategoryData.size(); i++) {
            String category = barChartCategoryData.get(i).getCategory();
            double amount = barChartCategoryData.get(i).getAmount();
            barChartLabelsCategory.add(category);
            barEntriesCategory.add(new BarEntry(i, (float) amount));
        }

        //Dataset
        BarDataSet barDataSetCategory = new BarDataSet(barEntriesCategory, "Avg daily calories / meal category");
        barDataSetCategory.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSetCategory.setValueTextSize(15);
        barDataSetCategory.setBarBorderWidth(0.5f);
        BarData barDataCategory = new BarData(barDataSetCategory);
        barDataCategory.setBarWidth(0.8f);

        //Formatting
        XAxis xAxis = barChartCategory.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(barChartLabelsCategory));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(15f);

        xAxis.setLabelCount(barChartLabelsCategory.size());

        //BarChart
        barChartCategory.setTouchEnabled(false);
        barChartCategory.getDescription().setEnabled(false);
        barChartCategory.setDrawBarShadow(true);
        barChartCategory.setDrawValueAboveBar(true);
        barChartCategory.setMaxVisibleValueCount(3000);
        barChartCategory.setPinchZoom(false);
        barChartCategory.getLegend().setEnabled(false);
        barChartCategory.setDrawValueAboveBar(false);
        barChartCategory.setData(barDataCategory);

        //Barchart animation
        barChartCategory.animateY(1000);
        barChartCategory.invalidate();
    }

    private void loadUser() {
        userRef.document(user)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        user.setDocumentId(documentSnapshot.getId());

                        double weight = user.getWeight();
                        double height = user.getHeight();

                        //Display user data
                        textViewStatsUser.setText("Weight: " + String.format("%.1f", weight) + " kg" +
                                "\nHeight: " + String.format("%.2f", height / 100) + " m" +
                                "\nBMI: " + String.format("%.1f", bmiCalculator(height, weight)));

                        //Select gender icon
                        if (user.getGender().matches("Male")) {
                            imageViewStatsGender.setImageResource(R.drawable.ic_male);
                        } else {
                            imageViewStatsGender.setImageResource(R.drawable.ic_female);
                        }
                    }
                });
    }

    private void loadWeeklyMeals() {
        //Get dates from a week back
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                0, 0 ,0);
        Calendar oneWeek = Calendar.getInstance();
        oneWeek.set(oneWeek.get(Calendar.YEAR),
                oneWeek.get(Calendar.MONTH),
                oneWeek.get(Calendar.DAY_OF_MONTH),
                0, 0 ,0);
        oneWeek.add(Calendar.DAY_OF_MONTH, -7);

        //Query Firebase for meals
        mealRef.whereLessThan("date", today.getTime())
                .whereGreaterThan("date", oneWeek.getTime())
                .whereEqualTo("mealUser", user)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Reset accumulators
                        double weekFat = 0;
                        double weekCarbs = 0;
                        double weekProtein = 0;
                        double weekCalories = 0;
                        double weekNumberOfMeals = 0;   // Double instead of Int for avg
                        weekBreakfastCalories = 0;
                        weekLunchCalories = 0;
                        weekDinnerCalories = 0;
                        weekSnackCalories = 0;
                        weekFikaCalories = 0;
                        for(DocumentSnapshot documentsnapshot : queryDocumentSnapshots) {
                            Meal meal = documentsnapshot.toObject(Meal.class);
                            meal.setDocumentId(documentsnapshot.getId());

                            weekFat += meal.getFat();
                            weekCarbs += meal.getCarbs();
                            weekProtein += meal.getProtein();
                            weekCalories += meal.getCalories();
                            weekNumberOfMeals++;

                            switch (meal.getCategory()) {
                                case "Breakfast":
                                    weekBreakfastCalories += meal.getCalories();
                                    break;
                                case "Lunch":
                                    weekLunchCalories += meal.getCalories();
                                    break;
                                case "Dinner":
                                    weekDinnerCalories += meal.getCalories();
                                    break;
                                case "Snack":
                                    weekSnackCalories += meal.getCalories();
                                case "Fika":
                                    weekFikaCalories += meal.getCalories();
                            }
                        }

                        textViewStatsWeeklyTotal.setText("Meals: " + String.format("%.0f", weekNumberOfMeals) +
                                "\nKcal: " + String.format("%.1f", weekCalories));

                        textViewStatsWeeklyAvg.setText("Meals/day: " + String.format("%.1f", weekNumberOfMeals / 7) +
                                "\nAvg kcal/day: " + String.format("%.1f", weekCalories / 7) +
                                "\nAvg kcal/meal: " + String.format("%.0f", weekCalories / weekNumberOfMeals));

                        //Create Barchart
                        setupBarChartCategory();
                    }
                });
    }

    private double bmiCalculator (double height, double weight) {
        return weight / ((height / 100) * (height / 100));
    }
}
