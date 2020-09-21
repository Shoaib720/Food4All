package com.food4all.foodwastereduction;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReceivedDonationsBeneficiaryFragment extends Fragment {

    private RecyclerView rvAllDonations;
    private DonationAdapter mAdapter;
    private TextView tvShowNoDonations;

    private String TAG = AllDonationsBeneficiaryFragment.class.getSimpleName();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Donation> mDonations;
    private String loggedInUserDistrict;


    public ReceivedDonationsBeneficiaryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_received_donations_beneficiary, container, false);

        tvShowNoDonations = (TextView) view.findViewById(R.id.tv_beneficiary_no_received_donations);

        rvAllDonations = (RecyclerView) view.findViewById(R.id.recycler_set_received_donations);
        rvAllDonations.setHasFixedSize(true);
        rvAllDonations.setLayoutManager(new LinearLayoutManager(getContext()));

        mDonations = new ArrayList<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            db.collection("donations")
                .whereEqualTo("receiverEmail", currentUser.getEmail())
                .whereEqualTo("status", Donation.RECEIVED)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot docs : queryDocumentSnapshots.getDocuments()){
                            Donation donation = docs.toObject(Donation.class);
                            mDonations.add(donation);
                        }
                        if (mDonations.isEmpty()){
                            tvShowNoDonations.setVisibility(View.VISIBLE);
                        }
                            mAdapter = new DonationAdapter(getContext(), mDonations);
                            rvAllDonations.setAdapter(mAdapter);

                    }
                });


        }

        return view;
    }
}