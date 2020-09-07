package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    RadioGroup rg;
    RadioButton rbselected;
    Button btnSubmit;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    FirebaseAuth mAuth;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_user_profile);

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");


        // hooks
        etName = (EditText) findViewById(R.id.et_profile_name);
        etContact = (EditText) findViewById(R.id.et_profile_contact);
        etAbout = (EditText) findViewById(R.id.et_profile_about);
        etDob = (EditText) findViewById(R.id.et_dob);
        rg = (RadioGroup) findViewById(R.id.rg_gender);


        btnSubmit = (Button) findViewById(R.id.btn_profile_submit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer rbSelectedId = rg.getCheckedRadioButtonId();
                rbselected = (RadioButton) findViewById(rbSelectedId);

                String _name = etName.getText().toString();
                String _contact = etContact.getText().toString();
                String _about = etAbout.getText().toString();
                String _dob = etDob.getText().toString();
                String _gender = rbselected.getText().toString();
//                String _email = fUser.getEmail();
//                String _emailParts[] = _email.split("@");
//                String _emailSecondPart = _emailParts[1].split(".")[0];
//                String child = _emailParts[0] + _emailSecondPart;

                // =====================================Experiment===============================================

                Intent intent = new Intent(CompleteUserProfile.this, SignUp.class);
                intent.putExtra("name", _name);
                intent.putExtra("contact", _contact);
                intent.putExtra("about", _about);
                intent.putExtra("gender", _gender);
                intent.putExtra("dob", _dob);
                startActivity(intent);

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

    private void clearInputs() {
        etName.setText("");
        etAbout.setText("");
        etContact.setText("");
        etDob.setText("");
    }
}