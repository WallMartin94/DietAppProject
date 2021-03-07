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
        holder.textViewMealType.setText(model.getCategory());
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
        TextView textViewMealType;
        TextView textViewMealInfo;

        public MealHolder(View itemView) {
            super(itemView);
            textViewMealType = itemView.findViewById(R.id.textview_meal_type);
            textViewMealInfo = itemView.findViewById(R.id.textview_meal_info);
            imageViewMealIcon = itemView.findViewById(R.id.imageview_meal_icon);

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
                return R.drawable.ic_meal;
            case "Snack":
                return R.drawable.ic_snack;
            default:
                return R.drawable.ic_meal;
        }
    }
}
