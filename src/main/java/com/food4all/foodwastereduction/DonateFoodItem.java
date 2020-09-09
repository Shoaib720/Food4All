package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class DonateFoodItem extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_food_item);



        // hooks
        btnChooseImage = (Button) findViewById(R.id.btn_donate_choose_image);
        btnUpload = (Button) findViewById(R.id.btn_donate_upload);
        ivFoodImage = (ImageView ) findViewById(R.id.iv_donate_image);
        etFoodName = (EditText) findViewById(R.id.et_donate_food_item_name);
        etDescription = (EditText) findViewById(R.id.et_donate_description);
        etExpiryDate = (EditText) findViewById(R.id.et_donate_exp_date);
        etImageName = (EditText) findViewById(R.id.et_donate_image_name);
        etDonorEmail = (EditText) findViewById(R.id.et_donate_donor_email);
        spinnerDistrict = (Spinner) findViewById(R.id.spinner_donate_district);


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

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DonateFoodItem.this, android.R.layout.simple_spinner_item, district);
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

        btnTest = (Button) findViewById(R.id.btn_my_donations);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DonateFoodItem.this, MyDonations.class);
                startActivity(intent);
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
                        DonateFoodItem.this,
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

    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
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
                                                Toast.makeText(DonateFoodItem.this, imageFirebaseUrl, Toast.LENGTH_LONG).show();
                                                storeToDatabase(imageFirebaseUrl);
                                            }else{
                                                Log.e(TAG, "No result returned");
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DonateFoodItem.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DonateFoodItem.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }else {
            Toast.makeText(DonateFoodItem.this, "No image selected", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(DonateFoodItem.this, "Uploaded successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DonateFoodItem.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            ivFoodImage.setImageURI(imageUri);
        }
    }
}