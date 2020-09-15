package com.food4all.foodwastereduction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnChooseImage, btnUpload;
    private ImageView ivFoodImage;
    private EditText etFoodName, etDescription, etExpiryDate, etImageName, etDonorEmail;
    private Spinner spinnerDistrict;
    private Uri imageUri;
    private Button btnTest;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = DonateFoodItem.class.getSimpleName();

    final String[] _selectedDistrict = new String[1];

    public HomeFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        btnChooseImage = (Button) v.findViewById(R.id.btn_frag_choose_image);
        btnUpload = (Button) v.findViewById(R.id.btn_frag_upload);
        ivFoodImage = (ImageView ) v.findViewById(R.id.iv_frag_image);
        etFoodName = (EditText) v.findViewById(R.id.et_frag_food_item_name);
        etDescription = (EditText) v.findViewById(R.id.et_frag_description);
        etExpiryDate = (EditText) v.findViewById(R.id.et_frag_exp_date);
        etImageName = (EditText) v.findViewById(R.id.et_frag_image_name);
        etDonorEmail = (EditText) v.findViewById(R.id.et_frag_donor_email);
        spinnerDistrict = (Spinner) v.findViewById(R.id.spinner_frag_district);

        final String[] district = {
                "Ahmadnagar",
                "Akola",
                "Amravati",
                "Aurangabad",
                "Bhandara",
                "Beed",
                "Buldhana",
                "Chandrapur",
                "Dhule",
                "Gadchiroli",
                "Gondiya",
                "Hingoli",
                "Jalgaon",
                "Jalna",
                "Kolhapur",
                "Latur",
                "Mumbai",
                "Mumbai Suburban",
                "Nagpur",
                "Nanded",
                "Nandurbar",
                "Nashik",
                "Osmanabad",
                "Parbhani",
                "Pune",
                "Raigad",
                "Sangli",
                "Satara",
                "Sindhudurg",
                "Solapur",
                "Thane",
                "Wardha",
                "Washim",
                "Yavatmal"
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, district);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(arrayAdapter);
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                _selectedDistrict[0] = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                _selectedDistrict[0] = null;
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        etExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int date = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        v.getContext(),
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mDateSetListener,
                        year,
                        month,
                        date
                );
                Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#731873")));
                datePickerDialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                etExpiryDate.setText(date);
            }
        };


        return v;
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImage() {
        if (imageUri != null){
            StorageReference fileReference = storageReference.child(etImageName.getText().toString().trim() + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.getResult() != null){
                                                String imageFirebaseUrl = task.getResult().toString();
                                                Toast.makeText(getView().getContext(), imageFirebaseUrl, Toast.LENGTH_LONG).show();
                                                storeToDatabase(imageFirebaseUrl);
                                            }else{
                                                Log.e(TAG, "No result returned");
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_LONG).show();
        }
    }

    private void storeToDatabase(String imageFirebaseUrl) {
        String itemName = etFoodName.getText().toString();
        String donorEmail = etDonorEmail.getText().toString();
        String donorCity = _selectedDistrict[0];
        String description = etDescription.getText().toString();
        int status = Donation.AVAILABLE;
        String expiryDate = etExpiryDate.getText().toString();
        int price = Donation.FREE;

        Donation newDonation = new Donation(itemName, donorEmail, donorCity, imageFirebaseUrl, description, status, expiryDate, price);
        db.collection("donations")
                .add(newDonation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
   public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            ivFoodImage.setImageURI(imageUri);
        }
    }

}