package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class testActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Button btnTest;
    EditText etName, etPass, etEmail;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser == null){
            Toast.makeText(testActivity.this, "user null returned",
                    Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(testActivity.this, "user returned",
                    Toast.LENGTH_SHORT).show();
            System.out.println("hey look here idiot +++++++++++: " + currentUser);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        etName = (EditText) findViewById(R.id.et_name);
        etPass = (EditText) findViewById(R.id.et_pass);
        etEmail = (EditText) findViewById(R.id.et_email);
        btnTest = (Button) findViewById(R.id.btn_test);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = etEmail.getText().toString();
                String userName = etName.getText().toString();
                String userPwd = etPass.getText().toString();

                storeUserDataToDatabase(userEmail,userName,userPwd);

                clearDataFromFields();

                mAuth.createUserWithEmailAndPassword(userEmail, userPwd)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("success", "success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("error", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(testActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });

            }
        });



    }



    private void clearDataFromFields() {
        etEmail.setText("");
        etName.setText("");
        etPass.setText("");
    }

    private void storeUserDataToDatabase(String userEmail, String userName, String userPwd) {

        String trimmedEmail[] = userEmail.split("@");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/"+trimmedEmail[0]);

//        UserHelper addUser = new UserHelper(userName, userPwd, userEmail);

//        reference.setValue(addUser);

    }
}