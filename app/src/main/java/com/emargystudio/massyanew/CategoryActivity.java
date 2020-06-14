package com.emargystudio.massyanew;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.emargystudio.massyanew.helperClasses.SharedPreferenceManger;
import com.emargystudio.massyanew.helperClasses.Urls;
import com.emargystudio.massyanew.helperClasses.common;
import com.emargystudio.massyanew.main.MainActivity;
import com.emargystudio.massyanew.model.Category;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {


    private TabLayout tabLayout;

    private static final String TAG = "CategoryActivity";


    private SharedPreferenceManger sharedPreferenceManger;
    private boolean is_logged_in;

    private ArrayList<Category> categoriesArray = new ArrayList<>();


    //widget
    private EditText updateEdtAr, addEdtAr , updateEdt , addEdt ;
    private ProgressBar progressBar;
    private TextInputLayout update_containerAr , update_container,add_container,add_containerAr;
    private Button updateBtn ,addBtn , deleteBtn , emptyBtn;




    private Category category;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initFragmentViews();


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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        tabLayout = findViewById(R.id.htab_tabs);
        syncCategory();

        changeToolbarFont(toolbar,CategoryActivity.this);
        rtlSupport(common.lang);

        sharedPreferenceManger = new SharedPreferenceManger(this);
        is_logged_in = sharedPreferenceManger.getLogginInStatus();





        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(addEdtAr.getText().toString()) &&!TextUtils.isEmpty(addEdt.getText().toString())){
                    boolean usedName = false;
                    for (Category category : categoriesArray){

                        if (category.getName().equals(addEdtAr.getText().toString())
                                ||category.getEn_name().equals(addEdt.getText().toString())){
                            usedName = true;
                        }
                    }

                    if (usedName){
                        Toast.makeText(CategoryActivity.this, "هذا الأسم مستخدم مسبقا الرجاء اختيار اسم اخر ثم المتابعة", Toast.LENGTH_SHORT).show();

                    }else {
                        saveCategoryToTheServer();

                    }

                }else {
                    Toast.makeText(CategoryActivity.this, "الرجاء إدخال اسم التصنيف قبل المتابعة", Toast.LENGTH_SHORT).show();
                }

            }


        });

        emptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEdt.setText("");
                addEdtAr.setText("");
                add_container.setHint("Category Name");
                add_containerAr.setHint("اسم التصنيف");

                addBtn.setVisibility(View.VISIBLE);
                emptyBtn.setVisibility(View.GONE);

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory();
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category!= null){
                        editCategory();
                }
            }
        });


        initTab();

    }

    private void editCategory() {
        update();

    }

    private void update() {
        if (!TextUtils.isEmpty(updateEdtAr.getText().toString())){
            category.setName(updateEdtAr.getText().toString());
        }
        if (!TextUtils.isEmpty(updateEdt.getText().toString())){
            category.setEn_name(updateEdt.getText().toString());
        }
        final int versionNumber = category.getVersionNumber()+1;
        category.setVersionNumber(versionNumber);


        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.edit_category,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){


                                Toast.makeText(CategoryActivity.this, "تم تعديل التصنيف", Toast.LENGTH_SHORT).show();
                                addBtn.setVisibility(View.GONE);
                                emptyBtn.setVisibility(View.VISIBLE);





                            }else{

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CategoryActivity.this,"حدث خطأ ما",Toast.LENGTH_LONG).show();
                            }


                        }catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CategoryActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        error.printStackTrace();

                        Toast.makeText(CategoryActivity.this,"تأكد من أتصالك بالأنترنت",Toast.LENGTH_LONG).show();

                    }
                }


        ){

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> imageMap = new HashMap<>();
                imageMap.put("name",category.getName());
                imageMap.put("en_name",category.getEn_name());
                imageMap.put("versionNumber", String.valueOf(versionNumber));
                imageMap.put("id",String.valueOf(category.getCategory_id()));

                return  imageMap;
            }
        };//end of string Request

        ApplicationController.getInstance().addToRequestQueue(stringRequest);



    }

    private void initTab(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                category = categoriesArray.get(tab.getPosition());
                setEditTextsHint(category.getName(),category.getEn_name());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                category = categoriesArray.get(tab.getPosition());
                setEditTextsHint(category.getName(),category.getEn_name());
            }
        });
    }

    private void deleteCategory() {

        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.delete_category+category.getCategory_id(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){


                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CategoryActivity.this, "تم حذفه", Toast.LENGTH_SHORT).show();


                            }else{
                                Toast.makeText(CategoryActivity.this,"حدث خطأ ما",Toast.LENGTH_LONG).show();
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

                        Toast.makeText(CategoryActivity.this,"حدث خطأ ما",Toast.LENGTH_LONG).show();
                    }
                }
        );
        ApplicationController.getInstance().addToRequestQueue(stringRequest);

    }

    private void setEditTextsHint(String arHint ,String enHint){
        updateEdtAr.setText(arHint);
        updateEdt.setText(enHint);

    }

    private void saveCategoryToTheServer() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.add_category,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){


                                Toast.makeText(CategoryActivity.this, "تم إضافة التصنيف", Toast.LENGTH_SHORT).show();
                                addBtn.setVisibility(View.GONE);
                                emptyBtn.setVisibility(View.VISIBLE);





                            }else{

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CategoryActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            }


                        }catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CategoryActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        error.printStackTrace();

                        Toast.makeText(CategoryActivity.this,"تأكد من أتصالك بالأنترنت",Toast.LENGTH_LONG).show();

                    }
                }


        ){

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> imageMap = new HashMap<>();
                imageMap.put("name",addEdtAr.getText().toString());
                imageMap.put("en_name",addEdt.getText().toString());
                imageMap.put("versionNumber","1");
                return  imageMap;
            }
        };//end of string Request

        ApplicationController.getInstance().addToRequestQueue(stringRequest);




    }


    //header methods:
    //find views and set typefaces
    private void initFragmentViews() {
        updateBtn = findViewById(R.id.updateBtn);
        addBtn = findViewById(R.id.addbtn);
        updateEdtAr = findViewById(R.id.update_category_ar);
        addEdtAr = findViewById(R.id.add_category_ar);
        updateEdt =findViewById(R.id.update_category);
        addEdt =findViewById(R.id.add_category);
        progressBar = findViewById(R.id.progressBar);
        update_container = findViewById(R.id.update_container);
        TextView textView = findViewById(R.id.textView);
        add_container = findViewById(R.id.add_container);
        update_containerAr = findViewById(R.id.name_container_ar);
        add_containerAr = findViewById(R.id.add_container_ar);
        deleteBtn = findViewById(R.id.delete);
        emptyBtn =findViewById(R.id.empty);


        Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/Cairo-Bold.ttf");
        Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/Cairo-SemiBold.ttf");

        textView.setTypeface(bold);
        updateBtn.setTypeface(bold);
        addBtn.setTypeface(bold);
        update_containerAr.setTypeface(regular);
        update_container.setTypeface(regular);
        add_container.setTypeface(regular);
        updateEdtAr.setTypeface(regular);
        addEdtAr.setTypeface(regular);
        updateEdt.setTypeface(regular);
        addEdt.setTypeface(regular);
        add_containerAr.setTypeface(regular);
        deleteBtn.setTypeface(bold);
        emptyBtn.setTypeface(bold);
    }

    //categories sync
    private void syncCategory() {

        categoriesArray.clear();
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
                                        tabLayout.addTab(tabLayout.newTab().setText(category.getName()));
                                    }else {
                                        tabLayout.addTab(tabLayout.newTab().setText(category.getEn_name()));

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
                Intent intent = new Intent(CategoryActivity.this, LoginActivity.class);
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
                startActivity(new Intent(CategoryActivity.this, AddFoodActivity.class));
                break;
            case R.id.category:
                //startActivity(new Intent(CategoryActivity.this, CategoryActivity.class));
                break;
            case android.R.id.home:
                startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                finish();
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



        return super.onOptionsItemSelected(item);
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
                        ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(CategoryActivity.this.getAssets(), "fonts/Cairo-Bold.ttf"));
                    } else {
                        ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(CategoryActivity.this.getAssets(), "fonts/Kabrio-Bold.ttf"));

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


}
