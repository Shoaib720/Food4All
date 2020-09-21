package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

public class CompleteUserProfile extends AppCompatActivity {

    EditText etDob, etName, etContact, etAbout;
    RadioGroup rgGender, rgUserType;
    RadioButton rbGenderSelected, rbUserTypeSelected;
    Button btnSubmit;
    Spinner spinnerDistrict;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    FirebaseAuth mAuth;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_user_profile);

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        final String[] _selectedDistrict = new String[1];
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");


        // hooks
        etName = (EditText) findViewById(R.id.et_profile_name);
        etContact = (EditText) findViewById(R.id.et_profile_contact);
        etAbout = (EditText) findViewById(R.id.et_profile_about);
        etDob = (EditText) findViewById(R.id.et_dob);
        rgGender = (RadioGroup) findViewById(R.id.rg_gender);
        rgUserType = (RadioGroup) findViewById(R.id.rg_user_type);
        spinnerDistrict = (Spinner) findViewById(R.id.spinner_district);

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


        btnSubmit = (Button) findViewById(R.id.btn_profile_submit);



        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CompleteUserProfile.this, android.R.layout.simple_spinner_item, district);
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


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rbGenderSelectedId = rgGender.getCheckedRadioButtonId();
                rbGenderSelected = (RadioButton) findViewById(rbGenderSelectedId);

                int rbUserTypeSelectedId = rgUserType.getCheckedRadioButtonId();
                rbUserTypeSelected = (RadioButton) findViewById(rbUserTypeSelectedId);

                String _name = etName.getText().toString();
                String _contact = etContact.getText().toString();
                String _about = etAbout.getText().toString();
                String _dob = etDob.getText().toString();
                String _gender = rbGenderSelected.getText().toString();
                String _userType = rbUserTypeSelected.getText().toString();
//                String _email = fUser.getEmail();
//                String _emailParts[] = _email.split("@");
//                String _emailSecondPart = _emailParts[1].split(".")[0];
//                String child = _emailParts[0] + _emailSecondPart;

                // =====================================Experiment===============================================

                Boolean areInputsValid = checkInputValidity(_name, _contact, _about, _dob, _gender, _userType, _selectedDistrict[0]);

                if (areInputsValid){
                    Intent intent = new Intent(CompleteUserProfile.this, SignUp.class);
                    intent.putExtra("name", _name);
                    intent.putExtra("contact", _contact);
                    intent.putExtra("about", _about);
                    intent.putExtra("gender", _gender);
                    intent.putExtra("dob", _dob);
                    intent.putExtra("userType", _userType);
                    intent.putExtra("district", _selectedDistrict[0]);
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CompleteUserProfile.this);
                    builder.setTitle("Invalid inputs!")
                            .setMessage("Please check all inputs are given properly!")
                            .setPositiveButton("Ok", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }



                // ==============================================================================================


                // ====================================Uncomment=============================================
//                UserHelper newUser = new UserHelper(_name, _contact, _email, _about, _dob, _gender);
//                dbRef.child(child).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Intent intent = new Intent(CompleteUserProfile.this, Dashboard.class);
//                        startActivity(intent);
//                        clearInputs();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(CompleteUserProfile.this, "Unable to register. Please try again", Toast.LENGTH_LONG).show();
//                    }
//                });

                // ==================================================================================================
            }
        });


        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int date = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CompleteUserProfile.this,
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
                etDob.setText(date);
            }
        };
    }

    private Boolean checkInputValidity(String name, String contact, String about, String dob, String gender, String userType, String district) {
        if (name != null && contact != null && about != null && dob != null && gender != null && userType != null && district != null){
            if (name.equals("") || contact.equals("") || about.equals("") || dob.equals("") || gender.equals("") || userType.equals("") || district.equals("")){
                return false;
            }
            return true;
        }
        return false;
    }

    private void clearInputs() {
        etName.setText("");
        etAbout.setText("");
        etContact.setText("");
        etDob.setText("");
    }
}