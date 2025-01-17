package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LogIn extends AppCompatActivity {

    // Declare variables for views
    EditText etEmail, etPassword, etForgotPass;
    TextView tvSignup, tvForgotPwd;
    Button btnLogin, btnResendEmail;

    // Declare Authentication variable
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String TAG = LogIn.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Hooks
        etEmail = (EditText) findViewById(R.id.et_signin_email);
        etPassword = (EditText) findViewById(R.id.et_signin_password);
        tvSignup = (TextView) findViewById(R.id.tv_signin_signup);
        tvForgotPwd = (TextView) findViewById(R.id.tv_signin_forgot_password);
        btnLogin = (Button) findViewById(R.id.btn_signin);

        // Adding underline to signin textview
        tvSignup.setPaintFlags(tvSignup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvForgotPwd.setPaintFlags(tvForgotPwd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Forgot Password Click listener
        // This will send a password reset mail to user.
        tvForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Inflating the alert dialog with the custom activity
                // in order to ask user for his/her email

                // Creating a view to inflate
                View forgotPwdView = LayoutInflater.from(LogIn.this).inflate(R.layout.activity_forgot_password_alert, null);

                // Hooking the Edit text view of the custom alert dialog activity
                etForgotPass = (EditText) forgotPwdView.findViewById(R.id.et_alert_forgot_password);

                // Creating a builder for the alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                builder.setTitle("Reset Passord!")
                        .setMessage("Please enter your Email")
                        .setView(forgotPwdView)
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String email = etForgotPass.getText().toString();
                                final LoadingSpinner loadingSpinnerPasswordReset = new LoadingSpinner(LogIn.this, "Sending password reset email...");
                                loadingSpinnerPasswordReset.startLoadingSpinner();
                                mAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loadingSpinnerPasswordReset.stopLoadingSpinner();
                                                Toast.makeText(LogIn.this, "Email sent", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loadingSpinnerPasswordReset.stopLoadingSpinner();
                                                Toast.makeText(LogIn.this, "Unable to send email. Please try again", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false);

                // Instantiating the alert dialog using the builder
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // User clicked on login button
        btnLogin.setOnClickListener(new View.OnClickListener() {

            // Get the current user
            FirebaseUser user = mAuth.getCurrentUser();


            @Override
            public void onClick(View view) {
                if(user == null){
                    // get user's email and password
                    final String email = etEmail.getText().toString();
                    final String password = etPassword.getText().toString();

                    if (email.equals("") || password.equals("")) {
                        Toast.makeText(LogIn.this, "Please enter valid credentials!", Toast.LENGTH_SHORT).show();
                    }else {
                        // signin using email and password
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LogIn.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){

                                            // Signin success
                                            // get the current user
                                            // update the UI
                                            Log.d("Success", "sign in success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            updateUI(user);
                                        }
                                        else {

                                            // Signin failed
                                            // Taost user about the same
                                            // Update the UI accordingly
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            Toast.makeText(LogIn.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }
                                    }
                                });
                    }

                }
            }
        });


        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Transfering the control to Complete user profile
                Intent intent = new Intent(LogIn.this, CompleteUserProfile.class);
                startActivity(intent);
            }
        });

    }

    private void loginOnlyIfEmailVerifiedAndUserDataExists(FirebaseUser user) {

        if (user.getEmail() != null){
            final String email = user.getEmail().split("@")[0];
            final LoadingSpinner loadingSpinner = new LoadingSpinner(LogIn.this, "Logging in...");
            loadingSpinner.startLoadingSpinner();
            FirebaseFirestore rootnode = FirebaseFirestore.getInstance();
            CollectionReference usersNode = rootnode.collection("users");
            Query query = usersNode.whereEqualTo("email", user.getEmail());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (document.exists()){

                                    try {
                                        UserSQLiteHelper sqLiteHelper = new UserSQLiteHelper(LogIn.this);
                                        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put("uid", document.getId());
                                        values.put("name", Objects.requireNonNull(document.getData().get("name")).toString());
                                        values.put("contact", Objects.requireNonNull(document.getData().get("contact")).toString());
                                        values.put("about", Objects.requireNonNull(document.getData().get("about")).toString());
                                        values.put("email", Objects.requireNonNull(document.getData().get("email")).toString());
                                        values.put("dob", Objects.requireNonNull(document.getData().get("dob")).toString());
                                        values.put("district", Objects.requireNonNull(document.getData().get("district")).toString());
                                        values.put("userType", Objects.requireNonNull(document.getData().get("userType")).toString());

                                        long row = db.replace("user", null, values);
                                        Log.d(TAG, row + "");

                                        Log.d(TAG, "Sqlite inserted user");
                                    }catch (Error e){
                                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                                    }

                                    if ((Objects.requireNonNull(document.getData().get("userType")).toString()).equals("Donor")){
                                        loadingSpinner.stopLoadingSpinner();
                                        Intent loginDonorIntent = new Intent(LogIn.this, DonorNavigation.class);
                                        startActivity(loginDonorIntent);
                                    }else if ((Objects.requireNonNull(document.getData().get("userType")).toString()).equals("Beneficiary")){
                                        loadingSpinner.stopLoadingSpinner();
                                        Intent loginBeneficiaryIntent = new Intent(LogIn.this, BeneficiaryNavigation.class);
                                        startActivity(loginBeneficiaryIntent);
                                    }



                                }
                                else {
                                    Toast.makeText(LogIn.this, "User doesnt exist!!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else {
                            Log.d(TAG, "Task is null");
                        }

                    }
                    loadingSpinner.stopLoadingSpinner();
                }
            });
        }else {
            Log.d(TAG, "User email is returned null!!!");
        }
    }


    // This method updates the UI as per the user passed by the Login clicked method
    // User email verification has already been checked
    private void updateUI(FirebaseUser currentUser) {

        if (currentUser != null){
            loginOnlyIfEmailVerifiedAndUserDataExists(currentUser);
        }
        else {
            // User is null
            // Clear the fields
            clearInputs();
        }
    }

    @Override
    protected void onStart() {
        if (
                getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        checkIfUserIsLoggedin(user);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(LogIn.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LogIn.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(LogIn.this, "External write Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LogIn.this, "External write Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkIfUserIsLoggedin(FirebaseUser user) {
        if (user != null){
            Task<Void> usertask = Objects.requireNonNull(mAuth.getCurrentUser()).reload();
            usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user.isEmailVerified()){
                        loginOnlyIfEmailVerifiedAndUserDataExists(user);
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                        builder.setTitle("Email not verified!!")
                                .setMessage("If you are a new user please sign up. If not then please verify your email from Signup section! If you have already verified then please wait and try again after sometime or resend the verification mail")
                                .setCancelable(false)
                                .setNeutralButton("Resend", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        final FirebaseUser user = mAuth.getCurrentUser();

                                        // If user not null then send the verification email
                                        // It simply means that the user is logged in
                                        if (user != null){
                                            final LoadingSpinner loadingSpinnerEmailVerification = new LoadingSpinner(LogIn.this, "Sending verification email...");
                                            loadingSpinnerEmailVerification.startLoadingSpinner();
                                            user.sendEmailVerification()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            // Email sent successfully to the user
                                                            // Toast the user about the same
                                                            loadingSpinnerEmailVerification.stopLoadingSpinner();
                                                            Toast.makeText(LogIn.this, "Validation link has been sent to your email!", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            // Email wasn't sent to the user
                                                            // Log the error
                                                            loadingSpinnerEmailVerification.stopLoadingSpinner();
                                                            Log.d(TAG, "Email not sent: " + e.getMessage());

                                                            // Delete the user created by the Firebase
                                                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    // If user deleted successfully
                                                                    // Toast the user to retry signup
                                                                    if (task.isComplete()){
                                                                        Toast.makeText(LogIn.this, "Email not sent please retry signup!",
                                                                                Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    // If user deletion failed
                                                                    // Show the user alert dialog
                                                                    // that his/her email is not verified
                                                                    // resend the verification link
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                                                                    builder.setTitle("Email issue!")
                                                                            .setMessage("Dear user, your email is not verified! If you didn't received our mail, then please click on resend mail button!")
                                                                            .setCancelable(false)
                                                                            .setPositiveButton("Ok", null);
                                                                    AlertDialog dialog = builder.create();
                                                                    dialog.show();
                                                                }
                                                            });
                                                        }
                                                    });
                                        }else {

                                            // User is null
                                            // It means an existing user / new user is attempting to resend mail
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                                            builder.setMessage("Hey there! You need to Signup first!")
                                                    .setPositiveButton("Ok", null);
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    }
                                })
                                .setPositiveButton("Signup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent loginIntent = new Intent(LogIn.this, CompleteUserProfile.class);
                                        startActivity(loginIntent);
                                    }
                                })
                                .setNegativeButton("Cancel", null);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });

        }
    }


    // this method clears the input fields
    private void clearInputs() {
        etEmail.setText("");
        etPassword.setText("");
    }

}