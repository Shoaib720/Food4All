package com.food4all.foodwastereduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Splash screen duration
    private static int SPLASH_SCREEN_DURATION = 3000;

    // Declaring variables for views
    Animation topAnim, bottomAnim;
    ImageView logo;
    TextView appName, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hooks
        logo = (ImageView) findViewById(R.id.logo);
        appName = (TextView) findViewById(R.id.app_name);
        slogan = (TextView) findViewById(R.id.app_slogan);

        // Loading animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        logo.setAnimation(topAnim);
        appName.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);


        // Transfer flow to Login Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_DURATION);
    }
}