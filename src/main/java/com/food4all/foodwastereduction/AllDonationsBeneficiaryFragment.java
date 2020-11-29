package com.food4all.foodwastereduction;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class AllDonationsBeneficiaryFragment extends Fragment {

    private RecyclerView rvAllDonations;
    private DonationAdapter mAdapter;
    private TextView tvShowNoDonations;

    private String TAG = AllDonationsBeneficiaryFragment.class.getSimpleName();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Donation> mDonations;
    private String loggedInUserDistrict;


    public AllDonationsBeneficiaryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_all_donations_beneficiary, container, false);

        tvShowNoDonations = (TextView) view.findViewById(R.id.tv_beneficiary_no_donations);

        rvAllDonations = (RecyclerView) view.findViewById(R.id.recycler_set_all_donations);
        rvAllDonations.setHasFixedSize(true);
        rvAllDonations.setLayoutManager(new LinearLayoutManager(getContext()));

        mDonations = new ArrayList<>();
        final LoadingSpinner loadingSpinner = new LoadingSpinner(getActivity(), "Retrieving items...");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            loadingSpinner.startLoadingSpinner();
                UserSQLiteHelper sqLiteHelper = new UserSQLiteHelper(view.getContext());
                SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
                Cursor mCursor = sqLiteDatabase.rawQuery("select district from user where email='" + currentUser.getEmail() + "'", null);
                if (mCursor != null){
                    mCursor.moveToFirst();
                    loggedInUserDistrict = mCursor.getString(0);
                    if (loggedInUserDistrict != null){
                        db.collection("donations")
                                .whereEqualTo("donorCity", loggedInUserDistrict)
                                .whereEqualTo("status", Donation.AVAILABLE)
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
                                            tvShowNoDonations.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            loadingSpinner.stopLoadingSpinner();
                                            mAdapter = new DonationAdapter(getContext(), mDonations);
                                            rvAllDonations.setAdapter(mAdapter);
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
                    }else {
                        loadingSpinner.stopLoadingSpinner();
                        tvShowNoDonations.setVisibility(View.VISIBLE);
                    }
                    mCursor.close();
                }


        }

        return view;
    }
}