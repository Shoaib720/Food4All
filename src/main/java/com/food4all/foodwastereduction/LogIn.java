package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.Queue;
import java.util.zip.Inflater;

public class LogIn extends AppCompatActivity {

    // Declare variables for views
    EditText etEmail, etPassword, etForgotPass;
    TextView tvSignup, tvForgotPwd;
    Button btnLogin, btnResendEmail;

    // Declare Authentication variable
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        // =========================================================Uncomment this===========================================





//        btnResendEmail = (Button) findViewById(R.id.btn_resend_link);





        // ==================================================================================================================

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
                                mAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(LogIn.this, "Email sent", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
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

        // ===========================================Uncomment this================================================================












        // This button on clicked will resend the verification email to the current user
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
//                                    Toast.makeText(LogIn.this, "Validation link has been sent to your email!", Toast.LENGTH_LONG).show();
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
//                                                Toast.makeText(LogIn.this, "Email not sent please retry signup!",
//                                                        Toast.LENGTH_LONG).show();
//
//                                                // Transfer the flow to the Signup Activity
//                                                Intent intent = new Intent(LogIn.this, SignUp.class);
//                                                startActivity(intent);
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
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
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
//                    AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
//                    builder.setMessage("Hey there! You need to Signup first!")
//                            .setCancelable(false)
//                            .setPositiveButton("Signup", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    Intent intent = new Intent(LogIn.this, SignUp.class);
//                                    startActivity(intent);
//                                }
//                            }).setNegativeButton("Cancel", null);
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                }
//
//
//            }
//        });












        // =============================================================================================================================

        // User clicked on login button
        btnLogin.setOnClickListener(new View.OnClickListener() {

            // Get the current user
            FirebaseUser user = mAuth.getCurrentUser();


            @Override
            public void onClick(View view) {

                // if user is not null
                // means user is already logged in
                if(user != null){

                    // Reload the user state
                    // Coz any state is cached in the device
                    // So in order to update it we perform a reload
                    Task<Void> usertask = Objects.requireNonNull(mAuth.getCurrentUser()).reload();
                    usertask.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Check if the user is verified

                            if(user.isEmailVerified()){

                                loginOnlyIfEmailVerifiedAndUserDataExists(user);


                                // Email is verified
                                // transfer the flow to Complete user profile
//                                Intent loginIntent = new Intent(LogIn.this, CompleteUserProfile.class);
//                                startActivity(loginIntent);
                            }
                            else {

                                // email is not verified
                                // Toast the user about the same
                                // Perform not flow transfer
                                Toast.makeText(LogIn.this, "Email not verified",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // if user is null
                // existing user is attempting login
                else{

                    // get user's email and password
                    final String email = etEmail.getText().toString();
                    final String password = etPassword.getText().toString();

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
                                        Log.w("Failure", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LogIn.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }

            }
        });


        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // User clicked Signup Text View
                // Transfer the flow to Signup Activity
//                Intent intent = new Intent(LogIn.this, SignUp.class);
//                startActivity(intent);

                // ============================= Experimenting=========================================
                // Transfering the control to Complete user profile

                Intent intent = new Intent(LogIn.this, CompleteUserProfile.class);
                startActivity(intent);

                // ====================================================================================
            }
        });

    }

    // =================================================Experiment===================================================

    private void loginOnlyIfEmailVerifiedAndUserDataExists(FirebaseUser user) {
        final String email = user.getEmail().split("@")[0];

        FirebaseFirestore rootnode = FirebaseFirestore.getInstance();
        CollectionReference usersNode = rootnode.collection("users");
        Query query = usersNode.whereEqualTo("email", user.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        System.out.println(document.getId());
//                        System.out.println(document.getData());
                        if (document.exists()){
                            Intent loginIntent = new Intent(LogIn.this, Dashboard.class);
                            startActivity(loginIntent);
                        }
                        else {
                            Toast.makeText(LogIn.this, "User doesnt exist!!",
                            Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        dbRef.orderByChild("email").equalTo(email)
//        .addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                System.out.println(dataSnapshot);
//                System.out.println(dataSnapshot.child(email));
//                System.out.println(dataSnapshot.child(email).exists());
//                if(dataSnapshot.child(email).exists()){
//                    // Email is verified
//                    // transfer the flow to Dashboard
//                    Intent loginIntent = new Intent(LogIn.this, Dashboard.class);
//                    startActivity(loginIntent);
//                }
//                else {
//                    Toast.makeText(LogIn.this, "Hello",
//                            Toast.LENGTH_SHORT).show();
//                }
//                for(DataSnapshot data: dataSnapshot.getChildren()){
//                    if (data.child(email).exists()) {
//                        //do ur stuff
//                        Intent loginIntent = new Intent(LogIn.this, Dashboard.class);
//                        startActivity(loginIntent);
//                    } else {
//                        //do something if not exists
//                        Toast.makeText(LogIn.this, "Hello",
//                            Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(LogIn.this, "Unable to login please try again",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // ===========================================================================================================

    // This method updates the UI as per the user passed by the Login clicked method
    // User email verification has already been checked
    private void updateUI(FirebaseUser currentUser) {

        if (currentUser != null){
            // ============================================Experiment======================================
            loginOnlyIfEmailVerifiedAndUserDataExists(currentUser);
            // ============================================================================================

            // =======================================Uncomment this section ==============================
            // User is verified
            // Transfer flow to dashboard
//            Intent intent = new Intent(LogIn.this, Dashboard.class);
//            startActivity(intent);
            // ============================================================================================
        }
        else {

            // User is null
            // Clear the fields
            clearInputs();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        checkIfUserIsLoggedin(user);
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
                                .setMessage("If you are a new user please sign up. If not then please verify your email from Signup section! If you have already verified then please wait and try again after sometime!")
                                .setCancelable(false)
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