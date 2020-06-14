package com.emargystudio.massyanew.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.emargystudio.massyanew.AddFoodActivity;
import com.emargystudio.massyanew.ApplicationController;
import com.emargystudio.massyanew.CategoryActivity;
import com.emargystudio.massyanew.LoginActivity;
import com.emargystudio.massyanew.R;
import com.emargystudio.massyanew.helperClasses.CacheRequest;
import com.emargystudio.massyanew.helperClasses.SharedPreferenceManger;
import com.emargystudio.massyanew.helperClasses.Urls;
import com.emargystudio.massyanew.helperClasses.common;
import com.emargystudio.massyanew.model.Category;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //widget vars
    private TabLayout tabLayout;
    private ViewAdapter viewAdapter;

    private static final int STORAGE_PERMISSION_CODE = 123;

    private ArrayList<Category> categoriesArray = new ArrayList<>();


    //helper vars
    private SharedPreferenceManger sharedPreferenceManger;
    private boolean is_logged_in;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ToolBar init
        final Toolbar toolbar =  findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            if (common.lang.equals("ar")){
                getSupportActionBar().setTitle("");
            }else {
                getSupportActionBar().setTitle("");

            }
        }
        tabLayout = findViewById(R.id.htab_tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);
        getCategory();

        viewAdapter = new ViewAdapter( getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,categoriesArray);
        changeToolbarFont(toolbar,MainActivity.this);
        rtlSupport(common.lang);



        sharedPreferenceManger = SharedPreferenceManger.getInstance(this);
        is_logged_in = sharedPreferenceManger.getLogginInStatus();




        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(viewAdapter);


        requestStoragePermission();

        if (common.isFirstOpen){
            ApplicationController.getInstance().getRequestQueue().getCache().clear();
            common.isFirstOpen=false;

        }




    }






    //init menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!is_logged_in){
            getMenuInflater().inflate(R.menu.login_menu, menu);
            if (common.lang.equals("ar")) {
                // menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_lock));
                menu.getItem(2).setTitle("تسجيل دخول");
                menu.getItem(0).setTitle("تحديث");
            }

        }else {
            getMenuInflater().inflate(R.menu.main_menu, menu);

        }




        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.log_in:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
            case R.id.user:
                sharedPreferenceManger.storeUserStatus(false);
                finish();
                startActivity(getIntent());
                break;
            case R.id.refresh:
                ApplicationController.getInstance().getRequestQueue().getCache().clear();
                finish();
                startActivity(getIntent());
                break;
            case R.id.food:
                startActivity(new Intent(MainActivity.this, AddFoodActivity.class));
                break;
            case R.id.category:
                startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                break;
            case android.R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.change_lang:
                if (common.lang.equals("ar")){
                    common.lang = "en";
                }else {
                    common.lang = "ar";

                }
                finish();
                startActivity(getIntent());
                break;


        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }



//    //categories sync
//    private void syncCategory() {
//        if (!categoriesArray.isEmpty()) {
//            categoriesArray.clear();
//
//        }
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.categoryQuery,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            if (!jsonObject.getBoolean("error")) {
//
//                                JSONArray jsonObjectTables =  jsonObject.getJSONArray("categorys");
//                                for(int i = 0 ; i<jsonObjectTables.length(); i++){
//                                    JSONObject jsonCategory = jsonObjectTables.getJSONObject(i);
//
//                                    Category category = new Category(
//                                            jsonCategory.getInt("id"),
//                                            jsonCategory.getString("name"),
//                                            jsonCategory.getString("en_name"),
//                                            jsonCategory.getInt("versionNumber")
//                                    );
//
//                                    Log.d(TAG, "Server category id "+jsonCategory.getInt("id"));
//
//
//                                    categoriesArray.add(category);
//
//                                }
//
//
//                                viewAdapter.notifyDataSetChanged();
//                                tabLayout.removeAllTabs();
//                                for (Category category : categoriesArray) {
//                                    if (common.lang.equals("ar")) {
//                                        tabLayout.addTab(tabLayout.newTab().setText(category.getName()));
//                                    }else {
//                                        tabLayout.addTab(tabLayout.newTab().setText(category.getEn_name()));
//
//                                    }
//
//
//                                }
//                                changeTabsFont();
//
//
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                }
//        ) ;
//        VolleyHandler.getInstance(MainActivity.this).addRequetToQueue(stringRequest);
//    }

    private void getCategory(){

        CacheRequest cacheRequest = new CacheRequest(Request.Method.POST, Urls.categoryQuery, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    JSONObject jsonObject = new JSONObject(jsonString);

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

                            Log.d(TAG, "Server category id "+jsonCategory.getInt("id"));


                            categoriesArray.add(category);

                        }


                        viewAdapter.notifyDataSetChanged();
                        tabLayout.removeAllTabs();
                        for (Category category : categoriesArray) {
                            if (common.lang.equals("ar")) {
                                tabLayout.addTab(tabLayout.newTab().setText(category.getName()));
                            }else {
                                tabLayout.addTab(tabLayout.newTab().setText(category.getEn_name()));

                            }


                        }
                        changeTabsFont();


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
                    if (common.lang.equals("ar")){
                        ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/Cairo-Bold.ttf"));
                    } else {
                        ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/Kabrio-Bold.ttf"));

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



    //helper methods
    private void rtlSupport(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());
        if(lang.equals("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        }else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        }
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }




}
