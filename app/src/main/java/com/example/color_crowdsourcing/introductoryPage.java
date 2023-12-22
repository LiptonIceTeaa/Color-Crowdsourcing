package com.example.color_crowdsourcing;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class introductoryPage extends AppCompatActivity {

    private Button bttnSignIn;
    private Button bttnSignUp;
    private FloatingActionButton changeLanguage;

    private Button bttnGoToDashboard;
    private Button bttnLogout;



    // private Button

    private boolean doubleBackToExitPressedOnce = false; // Used for double back press to exit



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setDefaultLanguage();
        setContentView(R.layout.activity_introductory_page);
      //  setDefaultLanguage();

        setLayoutHeight();
        bttnSignIn = (Button)findViewById(R.id.buttonLogin);
        bttnSignUp = (Button)findViewById(R.id.buttonSignUp);
        bttnGoToDashboard = (Button)findViewById(R.id.buttonGoToDashboard);
        bttnLogout = (Button)findViewById(R.id.buttonLogout);
        changeLanguage = (FloatingActionButton) findViewById(R.id.floatingActionButtonSwitchLang);



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userEmail = preferences.getString("userEmail", "none"); // If this is the first time we are visiting the sign in page, then we assume the selected

        if(userEmail.equals("none")){
           // bttnSignIn = (Button)findViewById(R.id.buttonLogin);
           // bttnSignUp = (Button)findViewById(R.id.buttonSignUp);
            bttnSignIn.setVisibility(View.VISIBLE);
            bttnSignUp.setVisibility(View.VISIBLE);

        }else{
           // bttnGoToDashboard = (Button)findViewById(R.id.buttonGoToDashboard);
           // bttnLogout = (Button)findViewById(R.id.buttonLogout);
            bttnGoToDashboard.setVisibility(View.VISIBLE);
            bttnLogout.setVisibility(View.VISIBLE);
        }


       // changeLanguage = (FloatingActionButton) findViewById(R.id.floatingActionButtonSwitchLang);



        /** Working on double back press to exit app **/
        // Register a callback to handle the back button press
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    moveTaskToBack(true);
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(introductoryPage.this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        };
        // Add the callback to the onBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);


        /** Finished Working on double back press to exit app **/


        changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentLang = getCurrentLocale();

                if(currentLang.equals("en"))
                    setLocale("ar");
                else
                    setLocale("en");
            }
        });

        bttnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(introductoryPage.this, signIn.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }
        });

        bttnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(introductoryPage.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);




            }
        });

        bttnGoToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(introductoryPage.this, displayTasks.class);
                startActivity(intent);

            }
        });

        bttnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });



        /** Working on styling the create button to add dinking effect when presses **/

        bttnSignUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed, apply scale animation
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Button released or touch canceled, revert the scale animation
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });


        /** Working on styling the create button to add dinking effect when presses **/

        bttnSignIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed, apply scale animation
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Button released or touch canceled, revert the scale animation
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });


        bttnGoToDashboard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed, apply scale animation
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Button released or touch canceled, revert the scale animation
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });

        bttnLogout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed, apply scale animation
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Button released or touch canceled, revert the scale animation
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });
    }



    public void setDefaultLanguage() {
        /** Setting the language for the activity **/
        // Firstly we check the current language with the shared preference "selectedLanguage", if same we don't change anything
        // If different, we change the language of the activity
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        LocaleList localeList = configuration.getLocales();

        // Get the first locale from the list (usually the most preferred user locale)
        Locale currentLocale = localeList.get(0);

        String currentLanguage = currentLocale.getLanguage();// String contains the current language of the app

        // Get the language chosen by user
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLanguage = preferences.getString("selectedLanguage", "en"); // If this is the first time we are visiting the sign in page, then we assume the selected lanaguage is EN

        //Toast.makeText(introductoryPage.this," I am here: "+selectedLanguage, Toast.LENGTH_SHORT).show();


        if (!currentLanguage.equals(selectedLanguage)) {// This means that we should update the language of this activity
            configuration.setLocale(new Locale(selectedLanguage)); // Set the selected language
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

            // Restart the current activity to apply the language change
            Intent intent = getIntent();
            finish();
            startActivity(intent);

        }
        /** Finished Setting the language for the activity **/
    }

    // Returns current locale being used
    public String getCurrentLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLanguage = preferences.getString("selectedLanguage", "en");

        return selectedLanguage;

    }


    // Restarts the app
    public void restartApp() {
        Intent intent = new Intent(this, introductoryPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    // Used to change language
    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        // Save the current language being used by user
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selectedLanguage", languageCode); // Store the selected language
        editor.apply();

        // Restart the activity to apply the language changes
        restartApp();
        //recreate();
    }

    // Logs out a user from their session
    public void logoutUser(){
        // We remove the shared preference key "userEmail"
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        // Specify the key we want to remove
        String keyToRemove = "userEmail";

        // Remove the key and its value
        editor.remove(keyToRemove);
        editor.apply();

        // Redirect user to sign in page
        Intent myIntent = new Intent(introductoryPage.this, introductoryPage.class);
        startActivity(myIntent);

    }


    @Override
    public void onResume(){
        super.onResume();
        setDefaultLanguage();
    }


    public void setLayoutHeight(){
        ConstraintLayout constraintLayout = findViewById(R.id.consLayout); // Replace with your actual ID
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout); // Replace constraintLayout with the actual reference to your ConstraintLayout
        ImageView logoView2 = findViewById(R.id.logoView2);


        // Get the display metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Calculate the percentage of screen height you want for linearLayout8 (e.g., 40%)
        float percentageOfScreenHeight = 0.9f;

        // Calculate the desired height in pixels
        int desiredWidthInPixels = (int) (displayMetrics.widthPixels * percentageOfScreenHeight);

        // Find the views
        //TextView textView13 = findViewById(R.id.textView13);
        LinearLayout linearLayout7 = findViewById(R.id.elementsLayout); // Replace with your actual ID

        // Set layout parameters for textView13
       // ConstraintLayout.LayoutParams paramstextView13 = (ConstraintLayout.LayoutParams) textView13.getLayoutParams();
       // paramstextView13.width = desiredWidthInPixels;

        // Set layout parameters for linearLayout9
        ConstraintLayout.LayoutParams paramsLinearLayout7 = (ConstraintLayout.LayoutParams) linearLayout7.getLayoutParams();
        paramsLinearLayout7.width = desiredWidthInPixels;


        linearLayout7.setLayoutParams(paramsLinearLayout7);

        // Get the horizontal bias of view1
        float horizontalBias = paramsLinearLayout7.horizontalBias;

        // Apply the same horizontal bias to view2
        constraintSet.setHorizontalBias(R.id.logoView2, horizontalBias);
    }
}