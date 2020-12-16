package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.acl.LastOwnerException;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    // Instantiating Authentication object
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Declare variables
    EditText etEmail,etPass,etConfirmPass;
    Button btnRegister, btnResendEmail;
    TextView tvSignin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // hooks
        tvSignin = (TextView) findViewById(R.id.tv_signup_signin);
        etEmail = (EditText) findViewById(R.id.et_signup_email);
        etPass = (EditText) findViewById(R.id.et_signup_password);
        etConfirmPass = (EditText) findViewById(R.id.et_signup_confirm_password);
        btnRegister = (Button) findViewById(R.id.btn_signup_signup);
//        btnResendEmail = (Button) findViewById(R.id.btn_resend_link);


        // Adding underline to signin textview
        tvSignin.setPaintFlags(tvSignin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Clicking signin textview
        // transfering control to login
        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(SignUp.this, LogIn.class);
                startActivity(loginIntent);
            }
        });

        // Click on register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting values from inputs
                final String _email = etEmail.getText().toString();
                final String _password = etPass.getText().toString();
                final String _confirmPassword = etConfirmPass.getText().toString();

                //Validate the inputs
                validateSignupInputs(_email, _password, _confirmPassword);
            }
        });
    }


    private void validateSignupInputs(String email, String password, String confirmPassword) {
        // Check for validations
        if (email.equals("") && password.equals("") && confirmPassword.equals("")){
            Toast.makeText(SignUp.this, "Input fields are blank !!", Toast.LENGTH_LONG).show();
        }
        else if(email.equals("") || password.equals("") || confirmPassword.equals("")){
            Toast.makeText(SignUp.this, "Please fill all fields !!", Toast.LENGTH_LONG).show();
        }
        else if(!password.equals(confirmPassword)){
            Toast.makeText(SignUp.this, "Passwords does'nt match !!", Toast.LENGTH_LONG).show();
            etConfirmPass.requestFocus();
        }
        else {
            signUpNewUser(email, password);
        }
    }

    private void signUpNewUser(final String email, String password) {

        final String _name = getIntent().getStringExtra("name");
        final String _contact = getIntent().getStringExtra("contact");
        final String _about = getIntent().getStringExtra("about");
        final String _dob = getIntent().getStringExtra("dob");
        final String _gender = getIntent().getStringExtra("gender");
        final String _userType = getIntent().getStringExtra("userType");
        final String _district = getIntent().getStringExtra("district");
        final String child = email.split("@")[0];
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final LoadingSpinner loadingSpinnerCreateUser = new LoadingSpinner(SignUp.this, "Creating user...");
        final LoadingSpinner loadingSpinnerSendEmail = new LoadingSpinner(SignUp.this, "Sending verification email...");
        loadingSpinnerCreateUser.startLoadingSpinner();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("Success", "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){
                                loadingSpinnerSendEmail.startLoadingSpinner();
                                user.sendEmailVerification()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                loadingSpinnerSendEmail.stopLoadingSpinner();
                                                UserHelper newUser = new UserHelper(_name, _contact, email, _about, _dob, _gender, _district, _userType);
                                                db.collection("users")
                                                        .add(newUser)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                loadingSpinnerCreateUser.stopLoadingSpinner();
                                                                Toast.makeText(SignUp.this, "Validation link has been sent to your email!", Toast.LENGTH_LONG).show();
                                                                updateUI(user);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                loadingSpinnerCreateUser.stopLoadingSpinner();
                                                                Log.d("onFailure","Data not added: " + e.getMessage());
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loadingSpinnerSendEmail.stopLoadingSpinner();
                                                Log.d("onFailure","Email not sent: " + e.getMessage());
                                                updateUI(null);
                                            }
                                        });
                            }


                        }
                        else {
                            // If sign in fails, display a message to the user.
                            loadingSpinnerCreateUser.stopLoadingSpinner();
                            Log.w("SigninFailure", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // handle not null user
            clearInputs();
            Intent loginIntent = new Intent(SignUp.this, LogIn.class);
            startActivity(loginIntent);
        }
        else {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null){
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()){
                            Toast.makeText(SignUp.this, "Email not sent please retry signup!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                clearInputs();
            }

        }
    }

    private void clearInputs() {
        etEmail.setText("");
        etPass.setText("");
        etConfirmPass.setText("");
    }
}