package com.example.dietappproject.mealtab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietappproject.R;
import com.example.dietappproject.dbobject.Meal;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class MealAdapter extends FirestoreRecyclerAdapter<Meal, MealAdapter.MealHolder> {
    private OnItemClickListener listener;

    public MealAdapter(@NonNull FirestoreRecyclerOptions<Meal> options) {
        super(options);
        }

    @Override
    protected void onBindViewHolder(@NonNull MealHolder holder, int position, @NonNull Meal model) {
        holder.textViewMealCategory.setText(model.getCategory());
        holder.textViewMealDate.setText((String.valueOf(model.getDate())).substring(0, 16));
        holder.textViewMealInfo.setText("- Fat: " + String.format("%.0f", model.getFat()) +
                "\n- Carbs: " + String.format("%.0f", model.getCarbs()) +
                "\n- Protein: " + String.format("%.0f", model.getProtein()) +
                "\n- Calories: " + String.format("%.0f", model.getCalories()));
        holder.imageViewMealIcon.setImageResource(mealTypeImageSelector(model.getCategory()));
    }
    @NonNull
    @Override
    public MealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meal_item, parent, false);
        return new MealHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class MealHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMealIcon;
        TextView textViewMealCategory;
        TextView textViewMealInfo;
        TextView textViewMealDate;


        public MealHolder(View itemView) {
            super(itemView);
            textViewMealCategory = itemView.findViewById(R.id.textview_mealitem_category);
            textViewMealInfo = itemView.findViewById(R.id.textview_mealitem_info);
            textViewMealDate = itemView.findViewById(R.id.textview_mealitem_date);
            imageViewMealIcon = itemView.findViewById(R.id.imageview_mealitem_icon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private int mealTypeImageSelector(String type) {
        switch (type) {
            case "Breakfast":
                return R.drawable.ic_breakfast;
            case "Snack":
                return R.drawable.ic_snack;
            case "Fika":
                return R.drawable.ic_fika;
            case "Lunch":
                return R.drawable.ic_lunch;
            case "Dinner":
                return R.drawable.ic_meal;
            default:
                return R.drawable.ic_meal;
        }
    }
}
