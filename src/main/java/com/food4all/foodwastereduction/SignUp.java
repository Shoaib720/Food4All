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
        btnResendEmail = (Button) findViewById(R.id.btn_resend_link);


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

//                String _name = getIntent().getStringExtra("name");
//                String _contact = getIntent().getStringExtra("contact");
////                String _email = getIntent().getStringExtra("email");
//                String _about = getIntent().getStringExtra("about");
//                String _dob = getIntent().getStringExtra("dob");
//                String _gender = getIntent().getStringExtra("gender");



                // Getting values from inputs
                final String _email = etEmail.getText().toString();
                final String _password = etPass.getText().toString();
                final String _confirmPassword = etConfirmPass.getText().toString();

                //Validate the inputs
                validateSignupInputs(_email, _password, _confirmPassword);
            }
        });

//    ================================================Experimenting=======================================================














//        btnResendEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final FirebaseUser user = mAuth.getCurrentUser();
//
//                // If user not null then send the verification email
//                // It simply means that the user is logged in
//                if (user != null){
//                    user.sendEmailVerification()
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                    // Email sent successfully to the user
//                                    // Toast the user about the same
//                                    Toast.makeText(SignUp.this, "Validation link has been sent to your email!", Toast.LENGTH_LONG).show();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                    // Email wasn't sent to the user
//                                    // Log the error
//                                    Log.d("Error", "Email not sent: " + e.getMessage());
//
//                                    // Delete the user created by the Firebase
//                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//
//                                            // If user deleted successfully
//                                            // Toast the user to retry signup
//                                            if (task.isComplete()){
//                                                Toast.makeText(SignUp.this, "Email not sent please retry signup!",
//                                                        Toast.LENGTH_LONG).show();
//                                            }
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//
//                                            // If user deletion failed
//                                            // Show the user alert dialog
//                                            // that his/her email is not verified
//                                            // resend the verification link
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
//                                            builder.setTitle("Email issue!")
//                                                    .setMessage("Dear user, your email is not verified! If you didn't received our mail, then please click on resend mail button!")
//                                                    .setCancelable(false)
//                                                    .setPositiveButton("Ok", null);
//                                            AlertDialog dialog = builder.create();
//                                            dialog.show();
//                                        }
//                                    });
//                                }
//                            });
//                }else {
//
//                    // User is null
//                    // It means an existing user / new user is attempting to resend mail
//                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
//                    builder.setMessage("Hey there! You need to Signup first!")
//                            .setPositiveButton("Ok", null);
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                }
//
//
//            }
//        });
//



//        ===========================================================================================================




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
//        String _emailSecondPart = _emailParts[1].split(".")[0];
//        final String child = _emailParts[0] + _emailSecondPart;

//        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("Success", "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){
                                user.sendEmailVerification()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                UserHelper newUser = new UserHelper(_name, _contact, email, _about, _dob, _gender, _district, _userType);
                                                db.collection("users")
                                                        .add(newUser)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Toast.makeText(SignUp.this, "Validation link has been sent to your email!", Toast.LENGTH_LONG).show();
                                                                updateUI(user);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("onFailure","Data not added: " + e.getMessage());
                                                            }
                                                        });
//                                            dbRef.child(child).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Toast.makeText(SignUp.this, "Validation link has been sent to your email!", Toast.LENGTH_LONG).show();
//                                                    updateUI(user);
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Log.d("onFailure","Data not added: " + e.getMessage());
//                                                }
//                                            });
//                                            Toast.makeText(SignUp.this, "Validation link has been sent to your email!", Toast.LENGTH_LONG).show();
//                                            updateUI(user);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("onFailure","Email not sent: " + e.getMessage());
                                                updateUI(null);
                                            }
                                        });
                            }


                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w("SigninFailure", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = mAuth.getCurrentUser();
//        checkIfUserIsLoggedin(user);
//    }
//
//    private void checkIfUserIsLoggedin(FirebaseUser user) {
//        if (user != null){
//            Task<Void> usertask = Objects.requireNonNull(mAuth.getCurrentUser()).reload();
//            usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    if(user.isEmailVerified()){
//                        Intent loginIntent = new Intent(SignUp.this, Dashboard.class);
//                        startActivity(loginIntent);
//                    }
//                    else {
//                        Toast.makeText(SignUp.this, "Email not verified",
//                                Toast.LENGTH_SHORT).show();
//                        Intent loginIntent = new Intent(SignUp.this, LogIn.class);
//                        startActivity(loginIntent);
//                    }
//                }
//            });
//
//        }
//    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // handle not null user
            clearInputs();
            Intent loginIntent = new Intent(SignUp.this, LogIn.class);
            startActivity(loginIntent);


//            Intent loginIntent = new Intent(SignUp.this, LogIn.class);
//            startActivity(loginIntent);
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