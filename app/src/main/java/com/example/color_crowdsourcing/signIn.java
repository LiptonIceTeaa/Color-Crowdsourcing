package com.example.color_crowdsourcing;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.transition.Fade;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;


public class signIn extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false; // Used for double back press to exit
    private Button btnSignIn;
    private FloatingActionButton floatingActionButton;
    private EditText emailField;
    private EditText passwordField;



    private TextView createAccountInstead;
    private TextView wrongCredentialsTxtView;

    private boolean userSelectedLanguage = false;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);

        setDefaultLanguage();

        setContentView(R.layout.activity_sign_in);

        emailField = (EditText) findViewById(R.id.emailTextField);
        passwordField = (EditText) findViewById(R.id.passwordTextField);
        mAuth = FirebaseAuth.getInstance();
        wrongCredentialsTxtView = (TextView) findViewById(R.id.txtWrongCredentials);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        /** setting style for txtCreateAccountInstead text view **/
        createAccountInstead = (TextView)findViewById(R.id.txtCreateAccountInstead);
        createAccountInstead.setPaintFlags(createAccountInstead.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        /** Finished setting style for txtLoginInInstead text view **/

        /** Working on styling the "log in instead" to add sinking effect when pressed **/
        createAccountInstead.setOnTouchListener(new View.OnTouchListener() {
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
        /** Finished working on styling the "log in instead" to add sinking effect when pressed **/
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        /** Workign on adding depth effect to the sign in button **/

        /** Working on styling the "log in instead" to add sinking effect when pressed **/

        btnSignIn.setOnTouchListener(new View.OnTouchListener() {
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
        /** Finished working on styling the "log in instead" to add sinking effect when pressed **/


        /** clicking on create account instead **/

        createAccountInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(signIn.this, MainActivity.class);

                // Get the shared element (the View) that will be transitioned between activities
                View sharedElement = findViewById(R.id.logoView);
                View inputForm = findViewById(R.id.inputForm);
                View formTitle = findViewById(R.id.formTitle);

                // Create the transition animation options
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signIn.this,
//                       Pair.create(sharedElement,"shared_logo_transition"),
//                     //   Pair.create(inputForm,"inputForm_transition"),
//                        Pair.create(formTitle,"shared_title_transition"));

                // Start the new activity with the transition animation
               // startActivity(intent, options.toBundle());

                startActivity(intent);
               // overridePendingTransition( R.anim.slide_in_bottom, R.anim.slide_out_bottom);

                //finish();
                //finish();

                //startActivity(new Intent(signIn.this, MainActivity.class));
            }
        });
        /** Finished clicking on create account instead **/


        /** Working on double back press to exit app **/
        // Register a callback to handle the back button press
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(signIn.this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        };
        // Add the callback to the onBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);


    /** Finished Working on double back press to exit app **/

    /** Working on what happens if floating action button is pressed **/
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverlayMenu();
            }
        });


        /** Working on what happens when log in is clicked **/

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = emailField.getText().toString().trim();
                password = passwordField.getText().toString().trim();

                if(!email.isEmpty() && !password.isEmpty()){
                    btnSignIn.setVisibility(View.GONE);
                    createAccountInstead.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    //Toast.makeText(signIn.this, "Fields are full, now signing you in", Toast.LENGTH_SHORT).show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(signIn.this, "Authentication passed.",Toast.LENGTH_SHORT).show();

                                        // Now we add the user's email to a shared preference variable
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("userEmail", email); // Store the user's email
                                        editor.apply();

                                        // Now we redirect to another activity
                                        Intent myIntent = new Intent(signIn.this, displayTasks.class);
                                        startActivity(myIntent);

                                    } else {
                                        wrongCredentialsTxtView.setVisibility(View.VISIBLE);
                                    }

                                    btnSignIn.setVisibility(View.VISIBLE);
                                    createAccountInstead.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }else{
                    Toast.makeText(signIn.this, "Please make sure all fields are completed", Toast.LENGTH_SHORT).show();

                }

            }
        });
        /** Finished working on what happens when log in is clicked **/
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

    }else{
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selectedLanguage", currentLanguage); // Store the english language language
        editor.apply();
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
        Button buttonOption1 = dialog.findViewById(R.id.leaderboardButton);
        Button buttonOption2 = dialog.findViewById(R.id.contactButton);
        Button buttonOption3 = dialog.findViewById(R.id.logoutButton);
        Spinner langSpinner = dialog.findViewById(R.id.languageSpinner);
        String currentLocale = getCurrentLocale();

        if(currentLocale.equals("en"))
            langSpinner.setSelection(0);
        else
            langSpinner.setSelection(1);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if (userSelectedLanguage) {
                    String selectedLanguage = parent.getItemAtPosition(position).toString();
                    //Toast.makeText(MainActivity.this, selectedLanguage + " selected", Toast.LENGTH_SHORT).show();
                    if(selectedLanguage.equals("English"))
                        setLocale("en");
                    else
                        setLocale("ar");

               }
               userSelectedLanguage = true; // Set the flag to true after the first selection
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set click listeners for menu items
        buttonOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle option 1 click
               //
                //dialog.dismiss(); // Dismiss the menu after click

                // Now we redirect to another activity
                Intent myIntent = new Intent(signIn.this, leaderboard.class);
                startActivity(myIntent);

            }
        });

        buttonOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle option 2 click
                dialog.dismiss(); // Dismiss the menu after click
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
        //Toast.makeText(MainActivity.this, "Language passed to setLocale method is: "+languageCode, Toast.LENGTH_SHORT).show();
        //Toast.makeText(MainActivity.this, "Language passed to sahred prefrences is: "+preferences.getString("selectedLanguage","nothing found"), Toast.LENGTH_SHORT).show();


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

    //Focus to be removed from input fields whenever I touch anything on the screen
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Restarts the app
    public void restartApp() {
        Intent intent = new Intent(this, signIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

}