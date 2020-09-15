package com.food4all.foodwastereduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FoodItemDonorDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_donor_detail);

        TextView tvTitle = (TextView) findViewById(R.id.donor_food_item_name);
        TextView tvDesc = (TextView) findViewById(R.id.donor_food_item_description);
        TextView tvExpDate = (TextView) findViewById(R.id.donor_food_item_exp_date);
        TextView tvPrice = (TextView) findViewById(R.id.donor_food_item_price);
        ImageView ivImage = (ImageView) findViewById(R.id.donor_food_item_image);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String expDate = intent.getStringExtra("expDate");
        Uri imageURL = Uri.parse(intent.getStringExtra("imageURL"));
        int price = intent.getIntExtra("price", Donation.FREE);

        tvTitle.setText(title);
        tvDesc.setText(desc);
        tvExpDate.setText("Expiry Date: " + expDate);
        Picasso.get().load(imageURL).into(ivImage);
        if (price == Donation.FREE){
            tvPrice.setText("Price: FREE");
        }else{
            tvPrice.setText("Price: " + price + " INR");
        }

    }
}