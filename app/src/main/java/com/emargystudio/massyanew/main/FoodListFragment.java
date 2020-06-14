package com.emargystudio.massyanew.main;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.massyanew.AddFoodActivity;
import com.emargystudio.massyanew.ApplicationController;
import com.emargystudio.massyanew.R;
import com.emargystudio.massyanew.helperClasses.CacheRequest;
import com.emargystudio.massyanew.helperClasses.Urls;
import com.emargystudio.massyanew.helperClasses.common;
import com.emargystudio.massyanew.model.Category;
import com.emargystudio.massyanew.model.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class FoodListFragment extends Fragment {

    private static final String TAG = "FoodListFragment";

    private Category category;
    private FoodItemAdapter foodItemAdapter;
    private RecyclerView recyclerView;
    private List<Food> foods = new ArrayList<>();
    private ProgressBar progressBar;




    public FoodListFragment(Category category) {
        this.category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView  = view.findViewById(R.id.cart_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        TextView textView = view.findViewById(R.id.category_header_name);
        progressBar = view.findViewById(R.id.progressBar);

        Typeface bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Cairo-Bold.ttf");
        textView.setTypeface(bold);
        if (common.lang.equals("ar")){
            textView.setText(category.getName());

        }else {
            textView.setText(category.getEn_name());

        }

        foodItemAdapter = new FoodItemAdapter(foods, getContext(), new FoodItemAdapter.DetailsAdapterListener() {
            @Override
            public void deleteCartItem(RecyclerView.ViewHolder v, int position) {

                try {
                    deleteCategory(foods.get(position));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void editCartItem(RecyclerView.ViewHolder v, int position) {

                editFood(foods.get(position));

            }


        });
        recyclerView.setAdapter(foodItemAdapter);
        recyclerView.setHasFixedSize(true);
        getFood();
    }




    private void getFood(){
        CacheRequest cacheRequest = new CacheRequest(Request.Method.POST, Urls.food_by_id+category.getCategory_id(), new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    JSONObject jsonObject = new JSONObject(jsonString);

                    JSONArray jsonObjectTables =  jsonObject.getJSONArray("food");
                    for(int i = 0 ; i<jsonObjectTables.length(); i++) {
                        JSONObject jsonFood = jsonObjectTables.getJSONObject(i);


                        final Food food = new Food(


                                jsonFood.getInt("id"),
                                jsonFood.getInt("category_id"),
                                jsonFood.getString("name"),
                                jsonFood.getString("description"),
                                jsonFood.getString("description2"),
                                jsonFood.getString("description3"),
                                jsonFood.getString("image"),
                                jsonFood.getString("en_name"),
                                jsonFood.getString("en_description"),
                                jsonFood.getString("en_description2"),
                                jsonFood.getString("en_description3"),
                                jsonFood.getString("en_description4"),
                                jsonFood.getString("description4"),
                                jsonFood.getInt("price"),
                                jsonFood.getInt("price2"),
                                jsonFood.getInt("price3"),
                                jsonFood.getInt("price4"),
                                jsonFood.getInt("versionNumber")

                        );
                        foods.add(food);

                    }

                    if (!recyclerView.isComputingLayout()){
                        foodItemAdapter.setTasks(foods);
                    }


                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ApplicationController.getInstance().addToRequestQueue(cacheRequest);
    }

    private void editFood(Food food) {

        Intent intent = new Intent(getContext(), AddFoodActivity.class);
        intent.putExtra("food",food);
        startActivity(intent);
    }




    private void deleteCategory(final Food food) throws MalformedURLException{
        progressBar.setVisibility(View.VISIBLE);
        URL url = new URL(food.getImage_uri());
        String path = url.getPath().substring(9);
        Log.d(TAG, "deleteCategory: "+path);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.delete_food+food.getFood_id()+"&image_url="+path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){

                                Toast.makeText(getContext(), "تم الحزف بنجاح", Toast.LENGTH_SHORT).show();
                                if (getActivity()!=null)
                                    getActivity().getSupportFragmentManager().popBackStack();

                            }else{
                                Toast.makeText(getContext(),"حدث خطأ ما",Toast.LENGTH_LONG).show();
                            }



                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(getContext(),"حدث خطأ ما",Toast.LENGTH_LONG).show();
                    }
                }
        );
        ApplicationController.getInstance().addToRequestQueue(stringRequest);
    }

}
