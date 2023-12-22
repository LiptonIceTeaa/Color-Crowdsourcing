package com.example.color_crowdsourcing;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

public class displayTasks extends AppCompatActivity {


    private FloatingActionButton settingButton;
    private boolean userSelectedLanguage = false;
    private FirebaseFirestore db;
    private TextView totalPointsView;
    private boolean doubleBackToExitPressedOnce = false; // Used for double back press to exit
    private Button bttnComplete;
    private MediaPlayer mp;
    private Vibrator vibe;
    private LinearLayout viewProfileLayout;
    private LinearLayout viewLeaderboardLayout;
    private LinearLayout viewMyCircleLayout;



    private String points;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_tasks);
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);
        setDefaultLanguage();
        setLayoutHeight();

        totalPointsView = (TextView) findViewById(R.id.totalPointsView);

       // logoutButton = (Button) findViewById(R.id.logoutButton);
        settingButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        bttnComplete = (Button)findViewById(R.id.continueBttn);
        mp = MediaPlayer.create(this, R.raw.game_start);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        viewProfileLayout = (LinearLayout)findViewById(R.id.viewProfileLayout);
        viewMyCircleLayout = (LinearLayout)findViewById(R.id.viewMyCircleLayout);
        viewLeaderboardLayout = (LinearLayout)findViewById(R.id.viewLeaderboardLayout);


        viewLeaderboardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(displayTasks.this,leaderboard.class);
                startActivity(intent);
            }
        });



        viewProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(displayTasks.this,userDetails.class);
                startActivity(intent);
            }
        });



        /** Working on styling the create button to add dinking effect when presses **/



        viewLeaderboardLayout.setOnTouchListener(new View.OnTouchListener() {
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


        viewMyCircleLayout.setOnTouchListener(new View.OnTouchListener() {
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


        viewProfileLayout.setOnTouchListener(new View.OnTouchListener() {
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

        /** Finished working on styling the create button to add sinking effect when pressed **/


        viewMyCircleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(displayTasks.this, friendsList.class);
                startActivity(intent);
            }
        });


        /** Wotking on listener for continue button **/
        bttnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                vibe.vibrate(1000);
                launchTasks();
            }
        });


        /** Working on listener for floating action button **/
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverlayMenu();
            }
        });




        /** Setting the text inside the total points view for a certain user **/
        // Firstly get current email
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String currentEmail = preferences.getString("userEmail","none");
        if (!currentEmail.equals("none")){
            setUserPoints(currentEmail);
        }
        else
            Toast.makeText(displayTasks.this, "Failed to retrieve data.\nPlease restart the applications", Toast.LENGTH_LONG).show();


        /** Working on double back press to exit app **/
        // Register a callback to handle the back button press
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    moveTaskToBack(true);
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(displayTasks.this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        };
        // Add the callback to the onBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);


        /** Finished Working on double back press to exit app **/

    }


    public void setDefaultLanguage(){
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

        if(!currentLanguage.equals(selectedLanguage)){// This means that we should update the language of this activity
            configuration.setLocale(new Locale(selectedLanguage)); // Set the selected language
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

            // Restart the current activity to apply the language change
            Intent intent = getIntent();
            finish();
            startActivity(intent);

        }
        /** Finished Setting the language for the activity **/
    }


    // Shows the overlay menu
    public void showOverlayMenu() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.overlay_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent background
        dialog.getWindow().setGravity(Gravity.CENTER); // Centered on screen

        // Initialize menu items
        //Button buttonOption1 = dialog.findViewById(R.id.leaderboardButton);
        Button buttonOption2 = dialog.findViewById(R.id.contactButton);
        Button buttonOption3 = dialog.findViewById(R.id.logoutButton);
        buttonOption3.setVisibility(View.VISIBLE);
        Spinner langSpinner = dialog.findViewById(R.id.languageSpinner);
        String currentLocale = getCurrentLocale();

        if(currentLocale.equals("en"))
            langSpinner.setSelection(0);
        else
            langSpinner.setSelection(1);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                if(selectedLanguage.equals("English") && !currentLocale.equals("en"))
                    setLocale("en");

                else if(selectedLanguage.equals("Arabic") && !currentLocale.equals("ar"))
                    setLocale("ar");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        // Set click listeners for menu items
//        buttonOption1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle option 1 click
//                dialog.dismiss(); // Dismiss the menu after click
//            }
//        });

        buttonOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle option 2 click
                dialog.dismiss(); // Dismiss the menu after click
            }
        });

        // Button used for logout
        buttonOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        dialog.show(); // Show the menu
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
       // recreate();
    }

    // Returns current locale being used
    public String getCurrentLocale(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLanguage = preferences.getString("selectedLanguage", "en");

        return selectedLanguage;

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
        Intent myIntent = new Intent(displayTasks.this, introductoryPage.class);
        startActivity(myIntent);

    }


    // Set user points in textview from our Firestore DB
    public void setUserPoints(String userEmail){
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", userEmail) // Query based on user's email
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Loop through the documents (usually only one document)
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                // Get the "points" variable from the document
                                String pointss = document.getLong("points").toString();
                               // Toast.makeText(displayTasks.this, "Current points: "+pointss, Toast.LENGTH_LONG).show();

                                // Now we have the current points from the DB, so we set it in the text view of the current points
                                totalPointsView.setText(pointss);
                            }
                        } else {
                            Toast.makeText(displayTasks.this, "No document found with the given email.\nPlease log out then log in again !", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        Toast.makeText(displayTasks.this, "Failed to retrieve data.\nPlease restart the applications", Toast.LENGTH_LONG).show();

                    }
                });
    }

    // Restarts the app
    public void restartApp() {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    public void launchTasks(){
        Intent intentt = new Intent(this, carryOutTasks.class);
        startActivity(intentt);
    }


    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        totalPointsView = (TextView) findViewById(R.id.totalPointsView);

        /** Setting the text inside the total points view for a certain user **/
        // Firstly get current email
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String currentEmail = preferences.getString("userEmail","none");
        if (!currentEmail.equals("none")){
            setUserPoints(currentEmail);
        }
        else
            Toast.makeText(displayTasks.this, "Failed to retrieve data.\nPlease restart the applications", Toast.LENGTH_LONG).show();


    }


    public void setLayoutHeight(){



        ConstraintLayout constraintLayout = findViewById(R.id.rootLayout); // Replace with your actual ID

// Get the display metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

// Calculate the percentage of screen width and height you want (e.g., 80% and 40%)
        float percentageOfScreenWidth = 0.9f;
        float percentageOfScreenHeight = 0.35f;

// Calculate the desired width and height in pixels
        int desiredWidthInPixels = (int) (displayMetrics.widthPixels * percentageOfScreenWidth);
        int desiredHeightInPixels = (int) (displayMetrics.heightPixels * percentageOfScreenHeight);

// Find the views
        LinearLayout inputForm = findViewById(R.id.linearLayout5); // Replace with your actual ID
        View lineSepView = findViewById(R.id.lineSepView);
        LinearLayout circles = findViewById(R.id.linearLayout6); // Replace with your actual ID




    // Set layout parameters for inputForm
        ConstraintLayout.LayoutParams paramsInputForm = (ConstraintLayout.LayoutParams) inputForm.getLayoutParams();
        paramsInputForm.width = desiredWidthInPixels;
        paramsInputForm.height = desiredHeightInPixels;

        // Set layout parameters for lineSepView
        ConstraintLayout.LayoutParams paramsInputForm2 = (ConstraintLayout.LayoutParams) lineSepView.getLayoutParams();
        paramsInputForm2.width = desiredWidthInPixels;

        // Set layout parameters for lineSepView
        ConstraintLayout.LayoutParams paramsInputForm3 = (ConstraintLayout.LayoutParams) circles.getLayoutParams();
        paramsInputForm3.width = desiredWidthInPixels;


        inputForm.setLayoutParams(paramsInputForm);
        lineSepView.setLayoutParams(paramsInputForm2);
        circles.setLayoutParams(paramsInputForm3);

    }
}