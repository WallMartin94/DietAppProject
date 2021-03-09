package com.example.dietappproject.fooditemtab;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.dietappproject.dbobject.FoodItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dietappproject.R;
import com.example.dietappproject.mealtab.AddMealFragment;

public class FoodItemFragment extends Fragment {
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fooditem, container, false);

       Button addItemButton = v.findViewById(R.id.addItemButton);



        addItemButton.setOnClickListener(view->{
            addItem();

        });
        return v;



    }

    public void addItem(){

        Fragment fragment = new AddItemFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

    }

    }

