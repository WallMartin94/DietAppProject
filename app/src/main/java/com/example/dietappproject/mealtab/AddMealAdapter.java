package com.example.dietappproject.mealtab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietappproject.R;

import java.util.ArrayList;

public class AddMealAdapter extends RecyclerView.Adapter<AddMealAdapter.AddMealViewHolder> {
    private ArrayList<com.example.dietappproject.mealtab.AddMealItem> mAddMealList;

    public static class AddMealViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewAddMealFoodItem;
        public TextView mTextViewAddMealAmount;
        public ImageButton mImageButtonDelete;

        public AddMealViewHolder(View itemView) {
            super(itemView);
            mTextViewAddMealFoodItem = itemView.findViewById(R.id.textview_addmeal_fooditem);
            mTextViewAddMealAmount = itemView.findViewById(R.id.textview_addmeal_amount);
            mImageButtonDelete = itemView.findViewById(R.id.button_delete_listitem);
        }
    }

    public AddMealAdapter (ArrayList<com.example.dietappproject.mealtab.AddMealItem> addMealList) {
        mAddMealList = addMealList;
    }

    @NonNull
    @Override
    public AddMealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_meal_item, parent, false);
        AddMealViewHolder addMealViewHolder = new AddMealViewHolder(v);
        return addMealViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddMealViewHolder holder, int position) {
        com.example.dietappproject.mealtab.AddMealItem currentItem = mAddMealList.get(position);

        holder.mTextViewAddMealFoodItem.setText(currentItem.getName());
        holder.mTextViewAddMealAmount.setText(String.format("%.0f", currentItem.getAmount()));

        holder.mImageButtonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                com.example.dietappproject.mealtab.AddMealItem removeItem = mAddMealList.get(position);
                // remove your item from data base
                mAddMealList.remove(position);  // remove the item from list
                notifyItemRemoved(position); // notify the adapter about the removed item
                notifyItemRangeChanged(position,getItemCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAddMealList.size();
    }
}
