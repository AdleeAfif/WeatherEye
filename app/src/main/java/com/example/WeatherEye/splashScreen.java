package com.example.WeatherEye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;

public class splashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;

    Animation textAnimation;
    GifImageView splashLogo;
    TextView splashText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        textAnimation = AnimationUtils.loadAnimation(this, R.anim.textanimation);

        splashLogo = findViewById(R.id.splashIcon);
        splashText = findViewById(R.id.splashText);

        splashText.setAnimation(textAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splashScreen.this, Homepage.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);

    }
}