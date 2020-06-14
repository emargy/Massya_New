package com.emargystudio.massyanew.main;


import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emargystudio.massyanew.R;
import com.emargystudio.massyanew.helperClasses.SharedPreferenceManger;
import com.emargystudio.massyanew.helperClasses.common;
import com.emargystudio.massyanew.model.Food;
import com.squareup.picasso.Picasso;

import java.util.List;


public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>{


    private List<Food> foods;
    private Context context;
    private DetailsAdapterListener onClickListener;
    private boolean is_logged_in;



    public FoodItemAdapter(List<Food> foods, Context context, DetailsAdapterListener onClickListener) {
        this.foods = foods;
        this.context = context;
        this.onClickListener = onClickListener;
        SharedPreferenceManger sharedPreferenceManger = SharedPreferenceManger.getInstance(context);
        is_logged_in = sharedPreferenceManger.getLogginInStatus();
    }

    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);

        return new FoodItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodItemViewHolder holder, final int position) {
        Food food = foods.get(position);

        if (common.lang.equals("ar")){
            if (food.getPrice()==0){
                holder.priceTxt.setVisibility(View.GONE);
            }else {
                holder.priceTxt.setText(food.getPrice()+" ل.س");
            }

            holder.descriptionTxt.setText(food.getDescription());
            holder.nameTxt.setText(food.getName());

            if (food.getPrice2() != 0){
                holder.optionOne.setVisibility(View.VISIBLE);
                holder.priceTxt2.setText(food.getPrice2()+" ل.س");
                holder.descriptionTxt2.setText(food.getDescription2());
            }

            if (food.getPrice3() != 0){
                holder.optionTow.setVisibility(View.VISIBLE);
                holder.priceTxt3.setText(food.getPrice3()+" ل.س");
                holder.descriptionTxt3.setText(food.getDescription3());
            }

            if (food.getPrice4() != 0){
                holder.optionThree.setVisibility(View.VISIBLE);
                holder.priceTxt4.setText(food.getPrice4()+" ل.س");
                holder.descriptionTxt4.setText(food.getDescription4());
            }


        }else {
            if (food.getPrice()==0){
                holder.priceTxt.setVisibility(View.GONE);
            }else {
                holder.priceTxt.setText(food.getPrice()+" ل.س");
            }
            holder.descriptionTxt.setText(food.getEn_description());
            holder.nameTxt.setText(food.getEn_name());


            if (food.getPrice2() != 0){
                holder.optionOne.setVisibility(View.VISIBLE);
                holder.priceTxt2.setText(food.getPrice2()+" S.P");
                holder.descriptionTxt2.setText(food.getEn_description2());
            }

            if (food.getPrice3() != 0){
                holder.optionTow.setVisibility(View.VISIBLE);
                holder.priceTxt3.setText(food.getPrice3()+" S.P");
                holder.descriptionTxt3.setText(food.getEn_description3());
            }

            if (food.getPrice4() != 0){
                holder.optionThree.setVisibility(View.VISIBLE);
                holder.priceTxt4.setText(food.getPrice4()+" S.P");
                holder.descriptionTxt4.setText(food.getEn_description4());
            }
        }


        Glide.with(context).load(food.getImage_uri())
                .placeholder(R.drawable.white_bg)
                .into(holder.imageView);

        holder.deleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.deleteCartItem(holder,position);
            }
        });

        holder.editCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.editCartItem(holder,position);
            }
        });







    }

    @Override
    public int getItemCount() {
        if (foods == null) {
            return 0;
        }
        return foods.size();
    }


    public void setTasks(List<Food> taskEntries) {
        foods = taskEntries;

        notifyDataSetChanged();

    }

    void removeItem(int position) {
        foods.remove(position);
        notifyItemRemoved(position);
    }

    void restoreItem(Food item, int position) {
        foods.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public interface DetailsAdapterListener {

        void deleteCartItem(RecyclerView.ViewHolder v, int position);

        void editCartItem(RecyclerView.ViewHolder v, int position);

    }




    class FoodItemViewHolder extends RecyclerView.ViewHolder {


        ImageView imageView;

        TextView nameTxt , descriptionTxt , priceTxt;

        LinearLayout optionOne , optionTow , optionThree;



        TextView
                 descriptionTxt4 , priceTxt4
                , descriptionTxt2 , priceTxt2
                , descriptionTxt3 , priceTxt3;


        ImageButton deleteCart , editCart ;


        FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
          imageView = itemView.findViewById(R.id.food_image);

          //oneLayout
          nameTxt = itemView.findViewById(R.id.name_text);
          descriptionTxt = itemView.findViewById(R.id.description_text);
          priceTxt = itemView.findViewById(R.id.price_text);
          descriptionTxt4 = itemView.findViewById(R.id.description_text4);
          priceTxt4 = itemView.findViewById(R.id.price_text4);
          descriptionTxt2 = itemView.findViewById(R.id.description_text2);
          priceTxt2 = itemView.findViewById(R.id.price_text2);
          descriptionTxt3 = itemView.findViewById(R.id.description_text3);
          priceTxt3 = itemView.findViewById(R.id.price_text3);

          deleteCart= itemView.findViewById(R.id.delete_item);
          editCart  = itemView.findViewById(R.id.edit_cart);


          optionOne = itemView.findViewById(R.id.option_one);
          optionTow = itemView.findViewById(R.id.option_tow);
          optionThree = itemView.findViewById(R.id.option_three);




            if (!is_logged_in){
              deleteCart.setVisibility(View.GONE);
              editCart.setVisibility(View.GONE);
          }

            Typeface bold = Typeface.createFromAsset(context.getAssets(), "fonts/Cairo-Bold.ttf");
            Typeface regular = Typeface.createFromAsset(context.getAssets(), "fonts/Cairo-SemiBold.ttf");


            nameTxt.setTypeface(bold);
            descriptionTxt.setTypeface(regular);
            priceTxt.setTypeface(bold);

            descriptionTxt4.setTypeface(regular);
            priceTxt4.setTypeface(regular);
            descriptionTxt2.setTypeface(regular);
            priceTxt2.setTypeface(regular);
            descriptionTxt3.setTypeface(regular);
            priceTxt3.setTypeface(regular);
        }

    }
}