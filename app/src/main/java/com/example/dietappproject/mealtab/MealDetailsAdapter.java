package com.example.dietappproject.mealtab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietappproject.R;

import java.util.ArrayList;

public class MealDetailsAdapter extends RecyclerView.Adapter<MealDetailsAdapter.MealDetailsViewHolder> {

    private ArrayList<MealItemDetails> mFoodItemList;

    public static class MealDetailsViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewFood;
        public TextView mTextViewProtein;
        public TextView mTextViewFat;
        public TextView mTextViewCarbs;
        public TextView mTextViewCalories;

        public MealDetailsViewHolder (View itemView) {
            super(itemView);
            mTextViewFood = itemView.findViewById(R.id.text_view_food);
            mTextViewProtein = itemView.findViewById(R.id.text_view_protein);
            mTextViewFat = itemView.findViewById(R.id.text_view_fat);
            mTextViewCarbs = itemView.findViewById(R.id.text_view_carbs);
            mTextViewCalories = itemView.findViewById(R.id.text_view_calories);
        }
    }

    public MealDetailsAdapter(ArrayList<MealItemDetails> FoodItemList) {
        mFoodItemList = FoodItemList;
    }

    @NonNull
    @Override
    public MealDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item_details, parent, false);
        MealDetailsViewHolder mealDetailsViewHolder = new MealDetailsViewHolder(v);
        return mealDetailsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MealDetailsViewHolder holder, int position) {
        MealItemDetails currentItem = mFoodItemList.get(position);

        holder.mTextViewFood.setText(currentItem.getName());
        holder.mTextViewProtein.setText(String.format("%.0f", currentItem.getProtein()));
        holder.mTextViewFat.setText(String.format("%.0f", currentItem.getFat()));
        holder.mTextViewCarbs.setText(String.format("%.0f", currentItem.getCarbs()));
        holder.mTextViewCalories.setText(String.format("%.0f", currentItem.getCalories()));
    }

    @Override
    public int getItemCount() {
        return mFoodItemList.size();
    }
}
