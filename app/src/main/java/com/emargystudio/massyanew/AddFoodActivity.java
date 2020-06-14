package com.emargystudio.massyanew;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.massyanew.helperClasses.SharedPreferenceManger;
import com.emargystudio.massyanew.helperClasses.Urls;
import com.emargystudio.massyanew.helperClasses.common;
import com.emargystudio.massyanew.main.MainActivity;
import com.emargystudio.massyanew.model.Category;
import com.emargystudio.massyanew.model.Food;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.Locale;

public class AddFoodActivity extends AppCompatActivity {

    private ArrayList<Category> categoriesArray = new ArrayList<>();
    private SharedPreferenceManger sharedPreferenceManger;
    private boolean is_logged_in;


    FragmentTransaction ft;
    TabLayout tabLayout;
    Food food;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        //ToolBar init
        final Toolbar toolbar = findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            if (common.lang.equals("ar")) {
                getSupportActionBar().setTitle("");
            } else {
                getSupportActionBar().setTitle("");

            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        changeToolbarFont(toolbar, AddFoodActivity.this);


        //change app language Depending on user choice
        rtlSupport(common.lang);


        //refresh vars
        sharedPreferenceManger = SharedPreferenceManger.getInstance(this);




        //check for user status
        is_logged_in = sharedPreferenceManger.getLogginInStatus();
        tabLayout = findViewById(R.id.htab_tabs);


        ft = getSupportFragmentManager().beginTransaction();
        Fragment foodFragment = new FoodFragment();




        if (getIntent().getParcelableExtra("food") != null) {
            food = getIntent().getParcelableExtra("food");
            Bundle args = new Bundle();
            args.putParcelable("food", food);
            args.putParcelableArrayList("category",categoriesArray);
            foodFragment.setArguments(args);
            ft.replace(R.id.your_placeholder, foodFragment, "food");
            ft.addToBackStack("food");
            ft.commit();

        } else {
            // show addFood Fragment if there is no intent on app start
            Bundle args = new Bundle();
            args.putParcelableArrayList("category",categoriesArray);
            foodFragment.setArguments(args);
            ft.replace(R.id.your_placeholder, foodFragment, "food");
            ft.addToBackStack("food");
            ft.commit();
        }


        //tabLayout init

        syncCategory();
        initTab();

    }

    private void rtlSupport(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());
        if (lang.equals("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        }
    }


    //categories sync
    private void syncCategory() {
        if (!categoriesArray.isEmpty()) {
            categoriesArray.clear();

        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.categoryQuery,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {

                                JSONArray jsonObjectTables =  jsonObject.getJSONArray("categorys");
                                for(int i = 0 ; i<jsonObjectTables.length(); i++){
                                    JSONObject jsonCategory = jsonObjectTables.getJSONObject(i);

                                    Category category = new Category(
                                            jsonCategory.getInt("id"),
                                            jsonCategory.getString("name"),
                                            jsonCategory.getString("en_name"),
                                            jsonCategory.getInt("versionNumber")
                                    );



                                    categoriesArray.add(category);

                                }


                                tabLayout.removeAllTabs();
                                for (Category category : categoriesArray) {

                                    if (common.lang.equals("ar")) {
                                        TabLayout.Tab tab = tabLayout.newTab().setText(category.getName());
                                        tabLayout.addTab(tab);
                                        if (food!=null && category.getCategory_id()==food.getCategory_id()){
                                            tab.select();
                                        }
                                    }else {
                                        TabLayout.Tab tab = tabLayout.newTab().setText(category.getEn_name());
                                        tabLayout.addTab(tab);
                                        if (food!=null && category.getCategory_id()==food.getCategory_id()){
                                            tab.select();
                                        }

                                    }

                                }

                                changeTabsFont();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) ;
        ApplicationController.getInstance().addToRequestQueue(stringRequest);
    }

    private void initTab() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int categoryID = categoriesArray.get(tab.getPosition()).getCategory_id();
                if (food!=null){

                    if (categoryID == food.getCategory_id()){
                        return;
                    }

                }
                common.tabNumber = categoriesArray.get(tab.getPosition()).getCategory_id();

                switch (checkForCurrentFragment()) {
                    case "category":
                        CategoryFragment category = (CategoryFragment) getSupportFragmentManager().findFragmentByTag("category");
                        if (category != null)
                            //category.changeEditText(categoryID);
                            break;

                    case "food":
                        FoodFragment food = (FoodFragment) getSupportFragmentManager().findFragmentByTag("food");
                        if (food != null)
                            food.getCategoryFromTabs(tab.getText().toString(), categoryID);
                        break;


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                common.tabNumber = categoriesArray.get(tab.getPosition()).getCategory_id();
                int categoryID = categoriesArray.get(tab.getPosition()).getCategory_id();

                switch (checkForCurrentFragment()) {
                    case "category":
                        CategoryFragment category = (CategoryFragment) getSupportFragmentManager().findFragmentByTag("category");
                        if (category != null)
                            //category.changeEditText(categoryID);
                            break;
                    case "food":
                        FoodFragment food = (FoodFragment) getSupportFragmentManager().findFragmentByTag("food");
                        if (food != null)
                            food.getCategoryFromTabs(tab.getText().toString(), categoryID);
                        break;

                }
            }
        });
    }

    //init menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!is_logged_in) {
            getMenuInflater().inflate(R.menu.login_menu, menu);
            if (common.lang.equals("ar")) {
                // menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_lock));
                menu.getItem(2).setTitle("تسجيل دخول");
                menu.getItem(0).setTitle("تحديث");

            }

        } else {
            getMenuInflater().inflate(R.menu.main_menu, menu);

        }


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.log_in:
                Intent intent = new Intent(AddFoodActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
            case R.id.refresh:
                ApplicationController.getInstance().getRequestQueue().getCache().clear();
                finish();
                startActivity(getIntent());
                break;
            case R.id.user:
                sharedPreferenceManger.storeUserStatus(false);
                finish();
                startActivity(getIntent());

                break;
            case R.id.food:
                //startActivity(new Intent(AddFoodActivity.this, AddFoodActivity.class));
                break;
            case R.id.category:
                startActivity(new Intent(AddFoodActivity.this, CategoryActivity.class));
                break;
            case android.R.id.home:
                startActivity(new Intent(AddFoodActivity.this, MainActivity.class));
                finish();
                break;

            case R.id.change_lang:
                if (common.lang.equals("ar")) {
                    common.lang = "en";
                } else {
                    common.lang = "ar";

                }
                finish();
                startActivity(getIntent());
                break;


        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


    //get them of the current active fragment
    private String checkForCurrentFragment() {
        String fragmentName = "";


        Fragment category = getSupportFragmentManager().findFragmentByTag("category");
        Fragment food = getSupportFragmentManager().findFragmentByTag("food");
        if (category != null && category.isVisible()) {
            fragmentName = "category";
        } else if (food != null && food.isVisible()) {
            fragmentName = "food";
        }


        return fragmentName;
    }

    //make back always go back to mainFragment when pressed
    @Override
    public void onBackPressed() {
        finish();
    }


    //change toolbar and tabLayout fonts
    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = tabChildsCount; i > 0; i--) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    if (common.lang.equals("ar")) {
                        ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(AddFoodActivity.this.getAssets(), "fonts/Cairo-Bold.ttf"));
                    } else {
                        ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(AddFoodActivity.this.getAssets(), "fonts/Kabrio-Bold.ttf"));

                    }
                }
            }
        }
    }

    public static void changeToolbarFont(Toolbar toolbar, Activity context) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    applyFont(tv, context);
                    break;
                }
            }
        }
    }

    public static void applyFont(TextView tv, Activity context) {
        tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Cairo-Bold.ttf"));
    }


}
