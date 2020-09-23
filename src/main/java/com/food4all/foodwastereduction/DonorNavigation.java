package com.food4all.foodwastereduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.context.AttributeContext;

public class DonorNavigation extends AppCompatActivity {

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_navigation);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (currentUser != null){
            UserSQLiteHelper sqLiteHelper = new UserSQLiteHelper(this);
            SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
            Cursor mCursor = sqLiteDatabase.rawQuery("select name from user where email='" + currentUser.getEmail() + "'", null);
            if (mCursor != null){
                mCursor.moveToFirst();
                String loggedInUserName = mCursor.getString(0);
                if (loggedInUserName != null){
                    toolbar.setTitle("Welcome, " + loggedInUserName);
                }else {
                    toolbar.setTitle("Welcome");
                }
                mCursor.close();
            }
        }
        setSupportActionBar(toolbar);

        nav = (NavigationView) findViewById(R.id.nav_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        toggle = new ActionBarDrawerToggle(DonorNavigation.this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.custom_container, new MyDonationsDonorFragment()).commit();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_donor_new_donation:
                        fragment = new NewFoodUploadDonorFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.custom_container, fragment).commit();
                        break;

                    case R.id.nav_donor_my_donations:
                        fragment = new MyDonationsDonorFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.custom_container, fragment).commit();
                        break;

                    case R.id.nav_about_app:
                        fragment = new AboutAppFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.custom_container, fragment).commit();
                        break;

                    case R.id.nav_logout:
                        signOutWithAlert();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void signOutWithAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DonorNavigation.this);
        builder.setTitle("Confirm sign out")
                .setMessage("Are you sure you want to Sign out?")
                .setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoadingSpinner loadingSpinner = new LoadingSpinner(DonorNavigation.this, "Signing out...");
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        loadingSpinner.startLoadingSpinner();
                        mAuth.signOut();
                        loadingSpinner.stopLoadingSpinner();
                        Intent intent = new Intent(DonorNavigation.this, LogIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        signOutWithAlert();
    }
}