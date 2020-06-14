package com.emargystudio.massyanew.main;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.emargystudio.massyanew.model.Category;

import java.util.ArrayList;
import java.util.List;

public class ViewAdapter extends FragmentStatePagerAdapter {

    private List<Category> categories;
    private Fragment fragment = null;

    public ViewAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<Category> categories) {
        super(fm, behavior);
        this.categories = categories;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        for (int i = 0; i < categories.size() ; i++) {
            if (i == position) {
                fragment =new FoodListFragment(categories.get(position));
                break;
            }
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return categories.size();
    }
}
