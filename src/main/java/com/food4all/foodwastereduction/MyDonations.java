package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyDonations extends AppCompatActivity {

    private RecyclerView rvMyDonations;
    private DonationAdapter mAdapter;
    private Button btnTest;

    private String TAG = MyDonations.class.getSimpleName();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Donation> mDonations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);

        rvMyDonations = (RecyclerView) findViewById(R.id.recycler_my_donations);
        rvMyDonations.setHasFixedSize(true);
        rvMyDonations.setLayoutManager(new LinearLayoutManager(this));

        mDonations = new ArrayList<>();

        db.collection("donations")
//                .whereEqualTo("donorEmail", "shoaib@gmail.com")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot docs : queryDocumentSnapshots.getDocuments()){
                            Donation donation = docs.toObject(Donation.class);
                            mDonations.add(donation);
                        }
                        mAdapter = new DonationAdapter(MyDonations.this, mDonations);
                        rvMyDonations.setAdapter(mAdapter);
                    }
                });

        btnTest = (Button) findViewById(R.id.btn_new_donation);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyDonations.this, DonateFoodItem.class);
                startActivity(intent);
            }
        });
    }
}