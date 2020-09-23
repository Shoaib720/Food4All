package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class FoodItemDonorDetail extends AppCompatActivity {

    private static final String TAG = FoodItemDonorDetail.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_donor_detail);

        TextView tvTitle = (TextView) findViewById(R.id.donor_food_item_name);
        TextView tvDesc = (TextView) findViewById(R.id.donor_food_item_description);
        TextView tvExpDate = (TextView) findViewById(R.id.donor_food_item_exp_date);
        TextView tvPrice = (TextView) findViewById(R.id.donor_food_item_price);
        TextView tvRequestedBy = (TextView) findViewById(R.id.donor_food_item_requested_by);
        ImageView ivImage = (ImageView) findViewById(R.id.donor_food_item_image);
        Button btnGrantRequest = (Button) findViewById(R.id.btn_donor_grant_request_item);
        Button btnRejectRequest = (Button) findViewById(R.id.btn_donor_reject_request_item);
        LinearLayout btngroup = (LinearLayout) findViewById(R.id.btngroup_donor_detail);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String expDate = intent.getStringExtra("expDate");
        int status = intent.getIntExtra("status", Donation.INVALID);
        String receiverEmail = intent.getStringExtra("receiverEmail");
        final String itemID = intent.getStringExtra("itemID");
        Uri imageURL = Uri.parse(intent.getStringExtra("imageURL"));
        int price = intent.getIntExtra("price", Donation.FREE);

        tvTitle.setText(title);
        tvDesc.setText(desc);
        tvExpDate.setText("Expiry Date: " + expDate);
        Picasso.get().load(imageURL).into(ivImage);
        if (price == Donation.FREE) {
            tvPrice.setText("Price: FREE");
        } else {
            tvPrice.setText("Price: " + price + " INR");
        }

        if (receiverEmail != null && !receiverEmail.equals("")){
            if (status == Donation.REQUESTED){
                tvRequestedBy.setVisibility(View.VISIBLE);
                tvRequestedBy.setText("Requested by: " + receiverEmail);
                btngroup.setVisibility(View.VISIBLE);
            }
            else if (status == Donation.RECEIVED){
                tvRequestedBy.setVisibility(View.VISIBLE);
                tvRequestedBy.setText("Received by: " + receiverEmail);
            }
        }

        btnGrantRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodItemDonorDetail.this);
                builder.setTitle("Confirm request grant!")
                        .setMessage("Are you sure to grant the request of the requesting person?")
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (itemID != null && !itemID.equals("")){
                                    final LoadingSpinner loadingSpinnerGrantRequest = new LoadingSpinner(FoodItemDonorDetail.this, "Granting request...");
                                    loadingSpinnerGrantRequest.startLoadingSpinner();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference documentReference = db.collection("donations").document(itemID);
                                    documentReference.update("status", Donation.RECEIVED)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    loadingSpinnerGrantRequest.stopLoadingSpinner();
                                                    Toast.makeText(FoodItemDonorDetail.this, "Request granted!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(FoodItemDonorDetail.this, DonorNavigation.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    loadingSpinnerGrantRequest.stopLoadingSpinner();
                                                    Toast.makeText(FoodItemDonorDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else {
                                    Log.d(TAG, "itemID is null");
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnRejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodItemDonorDetail.this);
                builder.setTitle("Confirm request reject!")
                        .setMessage("Are you sure to reject the request of the requesting person?")
                        .setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (itemID != null && !itemID.equals("")){
                                    final LoadingSpinner loadingSpinnerRejectRequest = new LoadingSpinner(FoodItemDonorDetail.this, "Rejecting request...");
                                    loadingSpinnerRejectRequest.startLoadingSpinner();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference documentReference = db.collection("donations").document(itemID);
                                    documentReference.update("status", Donation.AVAILABLE)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    loadingSpinnerRejectRequest.stopLoadingSpinner();
                                                    Toast.makeText(FoodItemDonorDetail.this, "Request granted!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(FoodItemDonorDetail.this, DonorNavigation.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    loadingSpinnerRejectRequest.stopLoadingSpinner();
                                                    Toast.makeText(FoodItemDonorDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else {
                                    Log.d(TAG, "itemID is null");
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }
}