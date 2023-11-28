package com.example.color_crowdsourcing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {


    private boolean doubleBackToExitPressedOnce = false;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        Fade fade = new Fade();
        fade.setDuration(5000); // Duration of the fade animation in milliseconds

        // Set the fade transition for enter and exit transitions
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);



        setContentView(R.layout.activity_splash_screen);

        // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
        // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE
        ImageView logoFirstHalf = (ImageView) findViewById(R.id.logoFirstHalf);
        Animation slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide_logo_1st_half);
        logoFirstHalf.startAnimation(slideAnimation);

        ImageView logoSecondHalf = (ImageView) findViewById(R.id.logoSecondHalf);
        Animation slideAnimation1 = AnimationUtils.loadAnimation(this, R.anim.side_slide_logo_2nd_half);
        logoSecondHalf.startAnimation(slideAnimation1);



        ImageView logoView = (ImageView)findViewById(R.id.logoView);


        Handler handler = new Handler();



        slideAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Following is to facilitate the animation from the splash screen to the sign in screen
                // Change the visibility of the fist and second half's of the logo
                logoFirstHalf.setVisibility(View.INVISIBLE);
                logoSecondHalf.setVisibility(View.INVISIBLE);
                logoView.setVisibility(View.VISIBLE);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // We check first if a user is signed in.
                        // We do this by checking the shared preferences
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String userSession = preferences.getString("userEmail", "none");

                        if(!userSession.equals("none"))
                            intent = new Intent(SplashScreen.this, displayTasks.class);
                        else
                            intent = new Intent(SplashScreen.this, signIn.class);



                        //Intent intent = new Intent(SplashScreen.this, signIn.class);
                        View sharedElement = findViewById(R.id.logoView);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,
                                Pair.create(sharedElement,"shared_logo_transition"));
                        startActivity(intent, options.toBundle());
                        //finish();

                        //finishAfterTransition();

                        //finishAfterTransition();

                    }
                }, 500); // Delay in milliseconds (500 milliseconds = 0.5 seconds)
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeat
            }
        });












    }
}