package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FoodItemBeneficiaryDetail extends AppCompatActivity {
    Intent intent;
    private String title, desc, expDate, receiverEmail, donorEmail;
    private String donorContact;
    private String itemID;
    private Uri imageURL;
    private int price;
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_beneficiary_detail);

        TextView tvTitle = (TextView) findViewById(R.id.beneficiary_food_item_name);
        TextView tvDesc = (TextView) findViewById(R.id.beneficiary_food_item_description);
        TextView tvExpDate = (TextView) findViewById(R.id.beneficiary_food_item_exp_date);
        TextView tvPrice = (TextView) findViewById(R.id.beneficiary_food_item_price);
        ImageView ivImage = (ImageView) findViewById(R.id.beneficiary_food_item_image);
        TextView tvDonorEmail = (TextView) findViewById(R.id.beneficiary_food_item_donor_email);
        Button btnRequestItem = (Button) findViewById(R.id.btn_beneficiary_request_item);


        intent = getIntent();
        title = intent.getStringExtra("title");
        desc = intent.getStringExtra("desc");
        expDate = intent.getStringExtra("expDate");
        imageURL = Uri.parse(intent.getStringExtra("imageURL"));
        price = intent.getIntExtra("price", Donation.FREE);
        itemID = intent.getStringExtra("itemID");
        status = intent.getIntExtra("status", Donation.INVALID);
        receiverEmail = intent.getStringExtra("receiverEmail");
        donorEmail = intent.getStringExtra("donorEmail");
        if (donorEmail != null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("email", donorEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for(QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())){
                                    donorContact = (String) documentSnapshot.get("contact");
                                }
                                TextView tvDonorContact = (TextView) findViewById(R.id.beneficiary_food_item_donor_contact);
                                tvDonorContact.setText("Donor Contact: " + donorContact);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FoodItemBeneficiaryDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        tvDonorEmail.setText("Donated by: " + donorEmail);
        tvTitle.setText(title);
        tvDesc.setText(desc);
        tvExpDate.setText("Expiry Date: " + expDate);
        Picasso.get().load(imageURL).into(ivImage);
        if (price == Donation.FREE){
            tvPrice.setText("Price: FREE");
        }else{
            tvPrice.setText("Price: " + price + " INR");
        }

        if (receiverEmail != null && (status == Donation.RECEIVED || status == Donation.REQUESTED)){
            btnRequestItem.setVisibility(View.GONE);
        }
        btnRequestItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodItemBeneficiaryDetail.this);
                builder.setTitle("Confirm Request")
                        .setMessage("Are you sure you want to request this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onConfirmRequest(itemID);
                            }
                        })
                        .setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }

    private void onConfirmRequest(String itemID) {
        if (itemID != null){
            FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null){
                DocumentReference documentReference = mDatabase.collection("donations").document(itemID);
                documentReference.update("status", Donation.REQUESTED);
                documentReference.update("receiverEmail", currentUser.getEmail())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FoodItemBeneficiaryDetail.this);
                                builder.setTitle("Request sent!")
                                        .setMessage("Request has been successfully sent to the donor. You will be contacted by the donor via email.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(FoodItemBeneficiaryDetail.this, BeneficiaryNavigation.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setCancelable(false);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FoodItemBeneficiaryDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }else {
            Toast.makeText(this, "ItemID is null!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}