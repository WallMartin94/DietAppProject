package com.example.dietappproject.mealtab;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dietappproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MealFragment extends Fragment {
    View view;
    Resources res;

    //Navigation - Top tabs
    TabLayout tabLayout;
    ViewPager2 viewPager;
    MealNavAdapter mealNavAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meal, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        res = view.getResources();
        //Navigation - Top tabs
        mealNavAdapter = new MealNavAdapter(this);
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(mealNavAdapter);

        tabLayout = view.findViewById(R.id.tablayout);
        ArrayList<String> tabList = new ArrayList<>();
        tabList.add(res.getString(R.string.meal_nav_today_label));
        tabList.add(res.getString(R.string.meal_nav_history_label));

        MealTodayFragment todayFragment = new MealTodayFragment();
        MealHistoryFragment historyFragment = new MealHistoryFragment();
        mealNavAdapter.addFragment(todayFragment, tabList.get(0));
        mealNavAdapter.addFragment(historyFragment, tabList.get(1));


        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabList.get(position))
        ).attach();

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_today);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_history);
    }

    private class MealNavAdapter extends FragmentStateAdapter {
        ArrayList<String> tabList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title) {
            tabList.add(title);
            fragmentList.add(fragment);
        }

        public MealNavAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }

}
