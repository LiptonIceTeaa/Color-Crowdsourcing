package com.example.color_crowdsourcing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.LocaleList;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

public class UserNameActivity extends AppCompatActivity {


    private Button bttnCreate;
    private EditText usernameField;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    FloatingActionButton setting;
    private boolean userSelectedLanguage = false;
    private FloatingActionButton backBttn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_name);

        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setDefaultLanguage();


        bttnCreate = (Button) findViewById(R.id.createButton);
        usernameField = (EditText) findViewById(R.id.usernameTextField);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        setting = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        backBttn = (FloatingActionButton) findViewById(R.id.floatingActionButtonBack);


        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverlayMenu();
            }
        });





        /** Recieve email string from register page **/

        Intent intent = getIntent();
        Map<String, Object> userMap = (Map<String, Object>) intent.getSerializableExtra("userMap");
        String nextDocumentTitle = intent.getStringExtra("nextDocumentTitle");
        String userPassword = intent.getStringExtra("password");


        /** Finished receiveing email **/

        /** What happens on create button click **/

        bttnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString().trim();
                if(username.isEmpty()){
                    Toast.makeText(UserNameActivity.this, "Please fill field !", Toast.LENGTH_SHORT).show();
                }else if (username.length() < 4){
                    Toast.makeText(UserNameActivity.this, "Username can't be less than 4 letters !", Toast.LENGTH_SHORT).show();
                }else{// Create user entry in database

                    String password = userPassword;
                    String email = userMap.get("email").toString();

                    progressBar.setVisibility(View.VISIBLE);
                    bttnCreate.setVisibility((View.GONE));

                // First we create user credentials (email and password)
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                userMap.put("username",username);// add username to the usermap
                                // Add the new user document with the dynamically generated title
                                db.collection("users").document(nextDocumentTitle).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(UserNameActivity.this, "User registered :)", Toast.LENGTH_SHORT).show();

                                        // When user is done registering, redirect to sign in page
                                        // No data is passed here
                                        Intent intent = new Intent(UserNameActivity.this, signIn.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserNameActivity.this, "Error occurred !", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{ // Couldnt create a user with these credentials
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    // If an email already exists in our user credentials DB)
                                    Toast.makeText(UserNameActivity.this, "User with this email already exists.", Toast.LENGTH_SHORT).show();
                                }else { // Some other error occurred
                                    Toast.makeText(UserNameActivity.this, "User failed to register :)", Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);// Remove the progress bar
                            bttnCreate.setVisibility(View.VISIBLE); // Get back the create button

                        }



                    });
                    //progressBar.setVisibility(View.GONE);// Remove the progress bar
                   // bttnCreate.setVisibility(View.VISIBLE); // Get back the create button

                }
            }
        });


        /** Working on styling the create button to add dinking effect when presses **/

        bttnCreate.setOnTouchListener(new View.OnTouchListener() {
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

    }


    public void registerUsername(String username){

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

        if (currentLocale.equals("en"))
            langSpinner.setSelection(0);
        else
            langSpinner.setSelection(1);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (userSelectedLanguage) {
                    String selectedLanguage = parent.getItemAtPosition(position).toString();
                    //Toast.makeText(MainActivity.this, selectedLanguage + " selected", Toast.LENGTH_SHORT).show();
                    if (selectedLanguage.equals("English"))
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
                dialog.dismiss(); // Dismiss the menu after click
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

    // Returns current locale being used
    public String getCurrentLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLanguage = preferences.getString("selectedLanguage", "en");

        return selectedLanguage;

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
        Intent intent = new Intent(this, UserNameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }


}