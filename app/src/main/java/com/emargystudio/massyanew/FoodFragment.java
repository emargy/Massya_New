package com.emargystudio.massyanew;


import android.app.Dialog;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.core.graphics.BitmapCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.massyanew.helperClasses.Urls;
import com.emargystudio.massyanew.helperClasses.common;
import com.emargystudio.massyanew.model.Category;
import com.emargystudio.massyanew.model.Food;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class FoodFragment extends Fragment {

    private boolean readyToSave = true;
    private static final String TAG = "FoodFragment";

    private ImageView imageView;
    private Button choosePic , editDataBtn , editPhotoBtn ,saveBtn , emptyBtn;
    private ProgressBar progressBar;
    private EditText nameEdt ,nameEdtAR , descriptionEdt , priceEdt
            , descriptionEdtAR , descriptionEdt2 , priceEdt2
            , descriptionEdtAR2
            , descriptionEdt3 , priceEdt3
            , descriptionEdtAR3
            , descriptionEdt4 , priceEdt4
            , descriptionEdtAR4;
    private TextInputLayout nameContainer ,nameContainerAr , descriptionContainer , priceContainer
            , descriptionContainerAR , descriptionContainer2 , priceContainer2
            , descriptionContainerAR2
            , descriptionContainer3 , priceContainer3
            , descriptionContainerAR3
            , descriptionContainer4 , priceContainer4
            , descriptionContainerAR4;
    private LinearLayout editContainer , saveContainer;
    private ImageButton addOne , addTow, addThree;

    private final int GALLARY_PICK = 2;
    private Bitmap bitmap;




    //dialog views
    private ImageView  upload_image ;
    private FrameLayout rotateBtn ;
    private Button upload_btn ,cancel_btn;
    private int first_time = 0;

    private Category category;


    //Foods that queered from the server
    private ArrayList<Food> dataFoods = new ArrayList<>() ;
    private ArrayList<Category> categories = new ArrayList<>();


    private boolean is_edit;
    private Food food;


    private boolean towDiscOn = false;
    private boolean threeDiscOn = false;
    private boolean fourDiscOn = false;


    public FoodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //init method call
        initViewsAndFonts(view);



        if (getCategoryFromBundle()!=null){
            categories = getCategoryFromBundle();
        }




        queryFoodFromDataBase();



        //
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkEditTexts()) {
                    boolean isUsed = false ;
                    for (Food food : dataFoods) {
                        if (food.getName().equals(nameEdtAR.getText().toString())
                                ||food.getEn_name().equals(nameEdt.getText().toString())){
                            isUsed = true;
                        }
                    }

                    if (isUsed){
                        Toast.makeText(getContext(), "هذا الأسم مستخدما مسبقا الرجاء اختيار اسم اخر ثم المتابعة", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image/*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLARY_PICK);

                    }
                }
            }
        });


        if (readyToSave){
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (is_edit){
                        editFoodData(food,true);

                    }else {
                        uploadMultipart();
                        readyToSave = false;

                    }

                }
            });
        }



        emptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdt.setText("");
                descriptionEdt.setText("");
                priceEdt.setText("");
                nameEdtAR.setText("");
                descriptionEdtAR.setText("");
                category = null;
                is_edit = false;
                nameContainer.setHint("name");
                nameContainerAr.setHint("الأسم");
                descriptionContainerAR.setHint("الوصف");
                descriptionContainer.setHint("Description");
                priceContainer.setHint("السعر");
                readyToSave = true;
                towDiscOn = false;
                threeDiscOn = false;
                fourDiscOn = false;


                Fragment frg ;
                if (getActivity()!=null){
                    frg = getActivity().getSupportFragmentManager().findFragmentByTag("food");
                    final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    if (frg!=null){
                        ft.detach(frg);
                        ft.attach(frg);
                        ft.commit();
                    }

                }




            }
        });


        //init fragment and chose if it is an addFragment or EditFragment
        if (getFoodFromBundle()!=null){
            food = getFoodFromBundle();
            choosePic.setVisibility(View.GONE);
            editContainer.setVisibility(View.VISIBLE);
            is_edit= true;
            setupViews(food);
        }else {
            is_edit = false;
            choosePic.setVisibility(View.VISIBLE);
            editContainer.setVisibility(View.GONE);
        }

        //add one more description
        addOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                descriptionContainer2.setVisibility(View.VISIBLE);
                priceContainer2.setVisibility(View.VISIBLE);
                descriptionContainerAR2.setVisibility(View.VISIBLE);
                addTow.setVisibility(View.VISIBLE);
                towDiscOn = true;
                priceContainer.setVisibility(View.GONE);

            }
        });


        addTow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                descriptionContainer3.setVisibility(View.VISIBLE);
                priceContainer3.setVisibility(View.VISIBLE);
                descriptionContainerAR3.setVisibility(View.VISIBLE);
                threeDiscOn = true;
            }
        });

        addThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                descriptionContainer4.setVisibility(View.VISIBLE);
                priceContainer4.setVisibility(View.VISIBLE);
                descriptionContainerAR4.setVisibility(View.VISIBLE);
                fourDiscOn = true;
            }
        });

    }

    private void editFoodData(final Food food, final boolean withPhoto) {



        String nameStrAr = nameEdtAR.getText().toString();
        String nameStr = nameEdt.getText().toString();
        int versionNumber = food.getVersionNumber()+1;
        String descriptionStrAr = descriptionEdtAR.getText().toString();
        String descriptionStr = descriptionEdt.getText().toString();
        String priceStr = priceEdt.getText().toString();



        int category_id = 0;
        if (category!=null){
             category_id = category.getCategory_id();

        }else {
            Toast.makeText(getContext(), "حدث خطأ ما الرجاء المحاولة لاحقا", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }


        food.setVersionNumber(versionNumber);

        //set value of nameStarAr
        if (!TextUtils.isEmpty(nameStrAr) && !nameStrAr.equals(food.getName()) ){

                food.setName(nameStrAr);

        }else {
            nameStrAr = food.getName();

        }



        //set value of nameStar
        if (!TextUtils.isEmpty(nameStr) && !nameStr.equals(food.getEn_name()) ){

                food.setEn_name(nameStr);

        }else {
            nameStr = food.getEn_name();

        }

        //set value of descriptionStrAr
        if (!TextUtils.isEmpty(descriptionStrAr) && !descriptionStrAr.equals(food.getDescription())){
            food.setDescription(descriptionStrAr);
        }else {
            descriptionStrAr = food.getDescription();
        }

        //set value of descriptionStr
        if (!TextUtils.isEmpty(descriptionStr) && !descriptionStr.equals(food.getEn_description())){
            food.setDescription(descriptionStr);
        }else {
            descriptionStr = food.getEn_description();
        }



        if (!TextUtils.isEmpty(priceStr) && !priceStr.equals(String.valueOf(food.getPrice()))){
            food.setPrice(Integer.parseInt(priceStr));
        }else {
            priceStr = String.valueOf(food.getPrice());
        }


        if(category!=null){
            if(category.getCategory_id()!=food.getCategory_id()){
                food.setCategory_id(category.getCategory_id());
            }

        }


        if (withPhoto){

            try {
                sendEditRequestWithImage(food,nameStrAr, nameStr, versionNumber,
                        descriptionStrAr, descriptionStr, priceStr, category_id);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


        }else{
            sendEditRequest( nameStrAr, nameStr, versionNumber,
                    descriptionStrAr, descriptionStr, priceStr, category_id);
        }




    }

    private void sendEditRequest( final String nameStrAr,
                                 final String nameStr, final int versionNumber, final String descriptionStrAr,
                                 final String descriptionStr, final String priceStr, final int category_id) {

        String descriptionStrAr2 = "";
        String descriptionStr2 = "";
        String priceStr2 ="0";

        if (food.getPrice2()!=0){
            descriptionStrAr2 = descriptionEdtAR2.getText().toString();
            descriptionStr2 = descriptionEdt2.getText().toString();
            priceStr2 = priceEdt2.getText().toString();
        }

        String descriptionStrAr3 = "";
        String descriptionStr3 = "";
        String priceStr3 ="0";
        if (food.getPrice3()!=0){
            descriptionStrAr3 = descriptionEdtAR3.getText().toString();
            descriptionStr3 = descriptionEdt3.getText().toString();
            priceStr3 = priceEdt3.getText().toString();
        }

        String descriptionStrAr4 = "";
        String descriptionStr4 = "";
        String priceStr4 ="0";
        if (food.getPrice4()!=0){
            descriptionStrAr4 = descriptionEdtAR4.getText().toString();
            descriptionStr4 = descriptionEdt4.getText().toString();
            priceStr4 = priceEdt4.getText().toString();
        }




        progressBar.setVisibility(View.VISIBLE);

        final String finalDescriptionStr = descriptionStr2;
        final String finalDescriptionStrAr = descriptionStrAr2;
        final String finalPriceStr = priceStr2;
        final String finalDescriptionStr1 = descriptionStr3;
        final String finalDescriptionStrAr1 = descriptionStrAr3;
        final String finalPriceStr1 = priceStr3;
        final String finalDescriptionStr2 = descriptionStr4;
        final String finalDescriptionStrAr2 = descriptionStrAr4;
        final String finalPriceStr2 = priceStr4;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.edit_food,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);
                        progressBar.setVisibility(View.GONE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){
                                progressBar.setVisibility(View.GONE);



                                Toast.makeText(getContext(), "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();

                                if(getActivity()!=null)
                                getActivity().finish();


                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            }


                        }catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        error.printStackTrace();

                        Toast.makeText(getContext(),"تأكد من أتصالك بالأنترنت",Toast.LENGTH_LONG).show();

                    }
                }


        ){

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> imageMap = new HashMap<>();
                imageMap.put("name",nameStrAr);
                imageMap.put("en_name",nameStr);
                imageMap.put("en_description",descriptionStr);
                imageMap.put("description",descriptionStrAr);
                imageMap.put("price",priceStr);

                imageMap.put("en_description2", finalDescriptionStr);
                imageMap.put("description2", finalDescriptionStrAr);
                imageMap.put("price2", finalPriceStr);

                imageMap.put("en_description3", finalDescriptionStr1);
                imageMap.put("description3", finalDescriptionStrAr1);
                imageMap.put("price3", finalPriceStr1);

                imageMap.put("en_description4", finalDescriptionStr2);
                imageMap.put("description4", finalDescriptionStrAr2);
                imageMap.put("price4", finalPriceStr2);


                imageMap.put("category_id",String.valueOf(category_id));
                imageMap.put("versionNumber",String.valueOf(versionNumber));
                imageMap.put("id",String.valueOf(food.getFood_id()));
                return  imageMap;
            }
        };//end of string Request

        ApplicationController.getInstance().addToRequestQueue(stringRequest);


    }


    private void sendEditRequestWithImage(Food food ,final String nameStrAr,
                                          final String nameStr, final int versionNumber, final String descriptionStrAr,
                                          final String descriptionStr, final String priceStr, final int category_id) throws MalformedURLException {

        String descriptionStrAr2 = "";
        String descriptionStr2 = "";
        String priceStr2 ="0";

        if (food.getPrice2()!=0){
            descriptionStrAr2 = descriptionEdtAR2.getText().toString();
            descriptionStr2 = descriptionEdt2.getText().toString();
            priceStr2 = priceEdt2.getText().toString();
        }

        String descriptionStrAr3 = "";
        String descriptionStr3 = "";
        String priceStr3 ="0";
        if (food.getPrice3()!=0){
            descriptionStrAr3 = descriptionEdtAR3.getText().toString();
            descriptionStr3 = descriptionEdt3.getText().toString();
            priceStr3 = priceEdt3.getText().toString();
        }

        String descriptionStrAr4 = "";
        String descriptionStr4 = "";
        String priceStr4 ="0";
        if (food.getPrice4()!=0){
            descriptionStrAr4 = descriptionEdtAR4.getText().toString();
            descriptionStr4 = descriptionEdt4.getText().toString();
            priceStr4 = priceEdt4.getText().toString();
        }

        URL url = new URL(food.getImage_uri());
        String old_image = url.getPath().substring(9);

        progressBar.setVisibility(View.VISIBLE);
        final String image_name = String.valueOf(dateOfImage());
        String path = saveImageToInternalStorage(bitmap,image_name);
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(getContext(), uploadId, Urls.edit_food)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", nameStrAr)
                    .addParameter("image_name", image_name)
                    .addParameter("en_description", descriptionStr)
                    .addParameter("description", descriptionStrAr)
                    .addParameter("price", priceStr)
                    .addParameter("old_image",old_image)
                    .addParameter("en_description2", descriptionStr2)
                    .addParameter("description2", descriptionStrAr2)
                    .addParameter("price2", priceStr2)

                    .addParameter("en_description3", descriptionStr3)
                    .addParameter("description3", descriptionStrAr3)
                    .addParameter("price3", priceStr3)

                    .addParameter("en_description4", descriptionStr4)
                    .addParameter("description4", descriptionStrAr4)
                    .addParameter("price4", priceStr4)


                    .addParameter("category_id", String.valueOf(category_id))
                    .addParameter("versionNumber", String.valueOf(versionNumber))
                    .addParameter("en_name",nameStr)//Adding text parameter to the request
                    .addParameter("id",String.valueOf(food.getFood_id()))
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setUtf8Charset()
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
                            if(getActivity()!=null)
                                getActivity().finish();

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }




    private void uploadMultipart() {
        //getting the actual path of the image
        progressBar.setVisibility(View.VISIBLE);
        final String image_name = String.valueOf(dateOfImage());
        String path = saveImageToInternalStorage(bitmap,image_name);

        String price = priceEdt.getText().toString();

        String arDiscrp2 = "";
        String enDiscrp2 = "";
        String price2 = "0";


        //if he add more option show them
        if (towDiscOn){
            if (TextUtils.isEmpty(descriptionEdtAR2.getText().toString()) || TextUtils.isEmpty(priceEdt2.getText().toString())
                    ||TextUtils.isEmpty(descriptionEdt2.getText().toString())){
                Toast.makeText(getContext(), "الرجاء تعبئة كافة الحقول والمحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                return;
            }else {
                arDiscrp2 = descriptionEdtAR2.getText().toString();
                enDiscrp2 = descriptionEdt2.getText().toString();
                price2 = priceEdt2.getText().toString();
                price = "0";
            }

        }
        String arDiscrp3 = "";
        String enDiscrp3 = "";
        String price3 = "0";
        if (threeDiscOn){
            if (TextUtils.isEmpty(descriptionEdtAR3.getText().toString()) || TextUtils.isEmpty(priceEdt3.getText().toString())
                    ||TextUtils.isEmpty(descriptionEdt3.getText().toString())){
                Toast.makeText(getContext(), "الرجاء تعبئة كافة الحقول والمحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                return;
            }else {
                arDiscrp3 = descriptionEdtAR3.getText().toString();
                enDiscrp3 = descriptionEdt3.getText().toString();
                price3 = priceEdt3.getText().toString();
                price = "0";
            }
        }
        String arDiscrp4 = "";
        String enDiscrp4 = "";
        String price4 = "0";
        if (fourDiscOn){
            if (TextUtils.isEmpty(descriptionEdtAR4.getText().toString()) || TextUtils.isEmpty(priceEdt4.getText().toString())
                    ||TextUtils.isEmpty(descriptionEdt4.getText().toString())){
                Toast.makeText(getContext(), "الرجاء تعبئة كافة الحقول والمحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                return;
            }else {
                arDiscrp4 = descriptionEdtAR4.getText().toString();
                enDiscrp4 = descriptionEdt4.getText().toString();
                price4 = priceEdt4.getText().toString();
                price = "0";
            }
        }

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(getContext(), uploadId, Urls.add_new_food)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", nameEdtAR.getText().toString())
                    .addParameter("image_name", image_name)
                    .addParameter("en_description", descriptionEdt.getText().toString())
                    .addParameter("description", descriptionEdtAR.getText().toString())
                    .addParameter("price", price)

                    .addParameter("en_description2", enDiscrp2)
                    .addParameter("description2", arDiscrp2)
                    .addParameter("price2", price2)

                    .addParameter("en_description3", enDiscrp3)
                    .addParameter("description3", arDiscrp3)
                    .addParameter("price3", price3)

                    .addParameter("en_description4", enDiscrp4)
                    .addParameter("description4", arDiscrp4)
                    .addParameter("price4", price4)

                    .addParameter("category_id", String.valueOf(category.getCategory_id()))
                    .addParameter("versionNumber", "1")
                    .addParameter("en_name",nameEdt.getText().toString())//Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setUtf8Charset()
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                            Toast.makeText(context, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                            Toast.makeText(getContext(), "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    private Long dateOfImage(){
        Date date = new Date();


        return date.getTime();

    }


    //find fragment views and set fonts
    private void initViewsAndFonts(@NonNull View view) {
        imageView = view.findViewById(R.id.imageView);
        choosePic  = view.findViewById(R.id.add1_btn);
        progressBar = view.findViewById(R.id.progressBar);
        nameEdt = view.findViewById(R.id.name_edt);
        editDataBtn = view.findViewById(R.id.edit_data_btn);
        editPhotoBtn = view.findViewById(R.id.edit_photo_btn);
        nameContainer =view.findViewById(R.id.name_container);
        nameContainerAr =view.findViewById(R.id.name_container_ar);

        descriptionContainer = view.findViewById(R.id.description_container);
        descriptionContainerAR = view.findViewById(R.id.description_container_ar);
        descriptionEdtAR = view.findViewById(R.id.description_edt_ar);
        priceContainer = view.findViewById(R.id.price_container);
        descriptionEdt = view.findViewById(R.id.description_edt);
        priceEdt = view.findViewById(R.id.price_edt);

        //2
        descriptionContainer2 = view.findViewById(R.id.description_container2);
        descriptionContainerAR2 = view.findViewById(R.id.description_container_ar2);
        descriptionEdtAR2 = view.findViewById(R.id.description_edt_ar2);
        priceContainer2 = view.findViewById(R.id.price_container2);
        descriptionEdt2 = view.findViewById(R.id.description_edt2);
        priceEdt2 = view.findViewById(R.id.price_edt2);

        //3
        descriptionContainer3 = view.findViewById(R.id.description_container3);
        descriptionContainerAR3 = view.findViewById(R.id.description_container_ar3);
        descriptionEdtAR3 = view.findViewById(R.id.description_edt_ar3);
        priceContainer3 = view.findViewById(R.id.price_container3);
        descriptionEdt3 = view.findViewById(R.id.description_edt3);
        priceEdt3 = view.findViewById(R.id.price_edt3);

        //4
        descriptionContainer4 = view.findViewById(R.id.description_container4);
        descriptionContainerAR4 = view.findViewById(R.id.description_container_ar4);
        descriptionEdtAR4 = view.findViewById(R.id.description_edt_ar4);
        priceContainer4 = view.findViewById(R.id.price_container4);
        descriptionEdt4 = view.findViewById(R.id.description_edt4);
        priceEdt4 = view.findViewById(R.id.price_edt4);

        nameEdtAR = view.findViewById(R.id.name_edt_ar);
        saveBtn = view.findViewById(R.id.save_btn);
        emptyBtn = view.findViewById(R.id.empty_btn);
        editContainer = view.findViewById(R.id.edit_container);
        saveContainer = view.findViewById(R.id.save_container);
        addOne = view.findViewById(R.id.add_one);
        addTow = view.findViewById(R.id.add_tow);
        addThree = view.findViewById(R.id.add_three);

        if (getContext()!=null){
            Typeface face = Typeface.createFromAsset(getContext().getAssets(),"fonts/Cairo-Bold.ttf");
            Typeface faceRegular = Typeface.createFromAsset(getContext().getAssets(),"fonts/Cairo-SemiBold.ttf");
            nameEdt.setTypeface(faceRegular);
            nameEdtAR.setTypeface(faceRegular);
            nameContainer.setTypeface(faceRegular);
            nameContainerAr.setTypeface(faceRegular);
            descriptionEdt.setTypeface(faceRegular);
            descriptionEdtAR.setTypeface(faceRegular);
            descriptionContainer.setTypeface(faceRegular);
            descriptionContainerAR.setTypeface(faceRegular);
            priceContainer.setTypeface(faceRegular);
            priceEdt.setTypeface(faceRegular);
            editPhotoBtn.setTypeface(face);
            choosePic.setTypeface(face);
            editDataBtn.setTypeface(face);
            emptyBtn.setTypeface(face);
            saveBtn.setTypeface(face);
        }

    }

    //if this fragment open to edit food set the value of old food on the editTexts
    private void setupViews(final Food food) {


        nameEdt.setText(food.getEn_name());
        nameEdtAR.setText(food.getName());
        descriptionEdtAR.setText(food.getDescription());
        descriptionEdt.setText(food.getEn_description());
        Picasso.get().load(new File(food.getImage_uri())).into(imageView);
        priceEdt.setText(String.valueOf(food.getPrice()));

        if (food.getPrice2()!=0){

            priceContainer.setVisibility(View.GONE);

            descriptionContainer2.setVisibility(View.VISIBLE);
            descriptionContainerAR2.setVisibility(View.VISIBLE);
            priceContainer2.setVisibility(View.VISIBLE);


            descriptionEdtAR2.setText(food.getDescription2());
            descriptionEdt2.setText(food.getEn_description2());
            priceEdt2.setText(String.valueOf(food.getPrice2()));
        }

        if (food.getPrice3()!=0){

            addTow.setVisibility(View.VISIBLE);
            descriptionContainer3.setVisibility(View.VISIBLE);
            descriptionContainerAR3.setVisibility(View.VISIBLE);
            priceContainer3.setVisibility(View.VISIBLE);


            descriptionEdtAR3.setText(food.getDescription3());
            descriptionEdt3.setText(food.getEn_description3());
            priceEdt3.setText(String.valueOf(food.getPrice3()));
        }


        if (food.getPrice4()!=0){


            addThree.setVisibility(View.VISIBLE);
            descriptionContainer4.setVisibility(View.VISIBLE);
            descriptionContainerAR4.setVisibility(View.VISIBLE);
            priceContainer4.setVisibility(View.VISIBLE);


            descriptionEdtAR4.setText(food.getDescription4());
            descriptionEdt4.setText(food.getEn_description4());
            priceEdt4.setText(String.valueOf(food.getPrice4()));
        }








        editDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(nameEdt.getText().toString())
                        && TextUtils.isEmpty(nameEdtAR.getText().toString())
                        && TextUtils.isEmpty(descriptionEdt.getText().toString())
                        && TextUtils.isEmpty(descriptionEdtAR.getText().toString())
                        && TextUtils.isEmpty(priceEdt.getText().toString())){

                    if (category != null && food.getCategory_id()!=category.getCategory_id()){
                        editFoodData(food,false);
                    }else{
                        Toast.makeText(getContext(), "لم تقم بتغير أي من البيانات ", Toast.LENGTH_SHORT).show();

                    }


                }else {
                    editFoodData(food,false);

                }



            }
        });

        editPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLARY_PICK);

            }
        });
    }
    private Food getFoodFromBundle() {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable("food");
        } else {
            return null;
        }
    }

    private ArrayList<Category> getCategoryFromBundle() {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelableArrayList("category");
        } else {
            return null;
        }
    }



    void getCategoryFromTabs(final String text , final int category_id){
        if (is_edit){
            if (first_time == 0){
                for (Category category1 : categories){
                    if (category1.getCategory_id()==food.getCategory_id()){
                        category = category1;
                    }
                }
                first_time = 1;
            }else {
                alertSend(text,category_id);
            }
        }else {
            for (Category category1 : categories){
                if (category1.getCategory_id() == category_id){
                    category = category1;
                }
            }
        }
    }
    private void alertSend(String text, final int category_id) {
        if (getContext() != null) {

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("تعديل التصنيف");
            if (common.lang.equals("ar")){
                alert.setMessage("هل تريد نقل "+food.getName()+" إلى "+text);

            }else {
                alert.setMessage("هل تريد نقل "+food.getEn_name()+" إلى "+text);

            }

            alert.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (Category category1 : categories){
                        if (category1.getCategory_id() == category_id){
                            category = category1;
                        }
                    }
                }
            });
            alert.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = alert.create();

            dialog.show();
            TextView textView =  dialog.getWindow().findViewById(android.R.id.message);
            TextView alertTitle =  dialog.getWindow().findViewById(R.id.alertTitle);
            Button button1 =  dialog.getWindow().findViewById(android.R.id.button1);
            Button button2 =  dialog.getWindow().findViewById(android.R.id.button2);


            Typeface faceRegular = Typeface.createFromAsset(getContext().getAssets(),"fonts/Cairo-Regular.ttf");
            Typeface face = Typeface.createFromAsset(getContext().getAssets(),"fonts/Cairo-SemiBold.ttf");

            alertTitle.setTypeface(face);
            textView.setTypeface(faceRegular);
            button1.setTypeface(face);
            button2.setTypeface(face);

            alertTitle.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);




        }
    }




    private static Bitmap rotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) 90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    //return true if any edit text has no text on it
    private boolean checkEditTexts(){
        boolean isEmpty = false;
        if (category==null){
            isEmpty=true;
            Toast.makeText(getContext(), "الرجاء اختيار تصنيف من الأعلى قبل المتابعة", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(nameEdt.getText().toString())){
            isEmpty=true;
            Toast.makeText(getContext(), "الرجاء ادخال الأسم قبل المتابعة", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(descriptionEdt.getText().toString())){
            isEmpty=true;
            Toast.makeText(getContext(), "الرجاء اضافة الوصف بالإنكليزية قبل المتابعة", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(priceEdt.getText().toString())){
            isEmpty=true;
            Toast.makeText(getContext(), "الرجاء ادخال السعر قبل المتابعة", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(descriptionEdtAR.getText().toString())){
            isEmpty=true;
            Toast.makeText(getContext(), "الرجاء ادخال الوصف بالعربي قبل المتابعة", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(nameEdtAR.getText().toString())){
            isEmpty=true;
            Toast.makeText(getContext(), "الرجاء ادخال الأسم قبل المتابعة", Toast.LENGTH_SHORT).show();
        }

        return isEmpty;
    }

    //dialog init views and fonts
    private void changeTxtViewFonts() {
        if (getContext()!=null){
            Typeface face = Typeface.createFromAsset(getContext().getAssets(),"fonts/Cairo-Bold.ttf");
            Typeface faceLight = Typeface.createFromAsset(getContext().getAssets(),"fonts/Cairo-Light.ttf");
            cancel_btn.setTypeface(faceLight);
            upload_btn.setTypeface(face);
        }


    }
    private void initDialogViews(Dialog dialog) {

        rotateBtn = dialog.findViewById(R.id.rotate);
        upload_image = dialog.findViewById(R.id.update_image);
        upload_btn    = dialog.findViewById(R.id.upload_btn);
        cancel_btn = dialog.findViewById(R.id.cancel_btn);
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLARY_PICK){

            if(resultCode == RESULT_OK){


                if (data.getData()!=null) {

                    Uri uri = data.getData();

                    try {

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), uri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);

                        }

                        if (bitmap != null) {
                            uploadImageDialog();
                        } else {
                            Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


        }
    }


    private void queryFoodFromDataBase(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.foodQuery,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {

                                JSONArray jsonObjectTables =  jsonObject.getJSONArray("foods");


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
                                    dataFoods.add(food);

                                }

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

    private void uploadImageDialog(){
        final Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow()!=null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setContentView(R.layout.update_image_dialog);


        initDialogViews(dialog);
        changeTxtViewFonts();

        upload_image.setImageBitmap(bitmap);


        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = rotateBitmap(bitmap);
                upload_image.setImageBitmap(bitmap);
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                imageView.setImageBitmap(bitmap);
                choosePic.setVisibility(View.GONE);
                editContainer.setVisibility(View.GONE);
                saveContainer.setVisibility(View.VISIBLE);


            }
        });

        dialog.show();
    }


    private  String saveImageToInternalStorage(Bitmap bitmap, String name){
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(getContext());

        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("Images",MODE_PRIVATE);

        // Create a file to save the image
        file = new File(file, dateOfImage()+name+".png");

        try{
            // Initialize a new OutputStream
            OutputStream stream ;

            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);
            int picSize ;
            if (BitmapCompat.getAllocationByteCount(bitmap)>1048576){
                picSize = 25 ;
            }else if(BitmapCompat.getAllocationByteCount(bitmap)>2097152){
                picSize = 25;
            }
            else {
                picSize = 100;

            }
            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,picSize,stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Return the saved image Uri
        return file.getAbsolutePath();
    }


}
