package com.food4all.foodwastereduction;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class NewFoodUploadDonorFragment extends Fragment {

    private static final int CHOOSE_IMAGE_FROM_DEVICE_REQUEST = 1, CLICK_IMAGE_FROM_CAMERA = 2;
    private Button btnChooseImage, btnUpload, btnChooseImageFromDevice, btnClickImageFromCamera;
    private ImageView ivFoodImage;
    private EditText etFoodName, etDescription, etExpiryDate, etImageName, etDonorEmail;
    private Spinner spinnerDistrict;
    private Uri imageUri, photoURI;
    private Button btnTest;
    private AlertDialog dialogImageUploadChoice;
    DatePickerDialog.OnDateSetListener mDateSetListener;


    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = DonateFoodItem.class.getSimpleName();

    final String[] _selectedDistrict = new String[1];

    public NewFoodUploadDonorFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_donor_new_food_item_upload, container, false);

        btnChooseImage = (Button) v.findViewById(R.id.btn_frag_choose_image);
        btnUpload = (Button) v.findViewById(R.id.btn_frag_upload);
        ivFoodImage = (ImageView ) v.findViewById(R.id.iv_frag_image);
        etFoodName = (EditText) v.findViewById(R.id.et_frag_food_item_name);
        etDescription = (EditText) v.findViewById(R.id.et_frag_description);
        etExpiryDate = (EditText) v.findViewById(R.id.et_frag_exp_date);
        etImageName = (EditText) v.findViewById(R.id.et_frag_image_name);
//        etDonorEmail = (EditText) v.findViewById(R.id.et_frag_donor_email);
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
                final Activity activity = getActivity();
                if (activity != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View uploadImageView = inflater.inflate(R.layout.upload_image_layout, null);
                    btnChooseImageFromDevice = (Button) uploadImageView.findViewById(R.id.btn_upload_layout_choose);
                    btnClickImageFromCamera = (Button) uploadImageView.findViewById(R.id.btn_upload_layout_click);
                    btnChooseImageFromDevice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openFileChooser();
                        }
                    });
                    btnClickImageFromCamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (
                                    activity.getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || activity.getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            ){
                                requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                            else{
                                openCamera();
                            }
                        }
                    });
                    builder.setView(uploadImageView);
                    dialogImageUploadChoice = builder.create();
                    dialogImageUploadChoice.show();
                }
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
        ContentResolver cr = Objects.requireNonNull(getActivity()).getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImage() {
        final LoadingSpinner loadingSpinner = new LoadingSpinner(getActivity(), "Uploading image...");
        if (imageUri != null){
            loadingSpinner.startLoadingSpinner();
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
                                                loadingSpinner.stopLoadingSpinner();
                                                String imageFirebaseUrl = task.getResult().toString();
                                                storeToDatabase(imageFirebaseUrl);
                                            }else{
                                                loadingSpinner.stopLoadingSpinner();
                                                Log.e(TAG, "No result returned");
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loadingSpinner.stopLoadingSpinner();
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingSpinner.stopLoadingSpinner();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_LONG).show();
        }
    }

    private void storeToDatabase(String imageFirebaseUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            String itemName = etFoodName.getText().toString();
            String donorEmail = currentUser.getEmail();
            String donorCity = _selectedDistrict[0];
            String description = etDescription.getText().toString();
            int status = Donation.AVAILABLE;
            String expiryDate = etExpiryDate.getText().toString();
            int price = Donation.FREE;

            final LoadingSpinner loadingSpinner = new LoadingSpinner(getActivity(), "Uploading to database...");
            Donation newDonation = new Donation(itemName, donorEmail, null, donorCity, imageFirebaseUrl, description, status, expiryDate, null, price);
            loadingSpinner.startLoadingSpinner();
            db.collection("donations")
                    .add(newDonation)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String id = documentReference.getId();
                            db.collection("donations").document(id).update("itemID", id);
                            loadingSpinner.stopLoadingSpinner();
                            Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), DonorNavigation.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingSpinner.stopLoadingSpinner();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    private void openCamera() {
        Intent clickImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (clickImageIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()),
                        "com.example.android.fileprovider",
                        photoFile);
                clickImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(clickImageIntent, CLICK_IMAGE_FROM_CAMERA);
            }
            
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Objects.requireNonNull(getActivity()).getFilesDir();
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE_FROM_DEVICE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else {
                Toast.makeText(getActivity(), "You need to allow the permissions to use Camera!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
   public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CLICK_IMAGE_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                        ivFoodImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d(TAG, "result not ok in camera");
                }
                break;
            case CHOOSE_IMAGE_FROM_DEVICE_REQUEST:
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    imageUri = data.getData();
                    ivFoodImage.setImageURI(imageUri);
                }
                else {
                    Log.d(TAG, "result not ok in choose from device");
                }
                break;

        }
        dialogImageUploadChoice.dismiss();
    }

}