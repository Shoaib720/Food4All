package com.food4all.foodwastereduction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MyDonationsDonorFragment extends Fragment {

    private RecyclerView rvMyDonations;
    private TextView tvNoDonations;
    private DonationAdapter mAdapter;

    private String TAG = MyDonationsDonorFragment.class.getSimpleName();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Donation> mDonations;


    public MyDonationsDonorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_donor_my_donations, container, false);

        rvMyDonations = (RecyclerView) v.findViewById(R.id.recycler_set_my_donations);
        rvMyDonations.setHasFixedSize(true);
        rvMyDonations.setLayoutManager(new LinearLayoutManager(getContext()));
        tvNoDonations = (TextView) v.findViewById(R.id.tv_donor_no_donations);

        mDonations = new ArrayList<>();
        final LoadingSpinner loadingSpinner = new LoadingSpinner(getActivity(), "Retrieving items...");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            loadingSpinner.startLoadingSpinner();
            db.collection("donations")
                .whereEqualTo("donorEmail", currentUser.getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot docs : queryDocumentSnapshots.getDocuments()){
                                Donation donation = docs.toObject(Donation.class);
                                mDonations.add(donation);
                            }
                            if(mDonations.isEmpty()){
                                loadingSpinner.stopLoadingSpinner();
                                tvNoDonations.setVisibility(View.VISIBLE);
                            }
                            else {
                                loadingSpinner.stopLoadingSpinner();
                                mAdapter = new DonationAdapter(getContext(), mDonations);
                                rvMyDonations.setAdapter(mAdapter);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingSpinner.stopLoadingSpinner();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        return v;
    }
}