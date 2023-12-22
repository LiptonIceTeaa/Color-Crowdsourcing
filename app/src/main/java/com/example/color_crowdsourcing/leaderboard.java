package com.example.color_crowdsourcing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Locale;


public class leaderboard extends AppCompatActivity {

     final ArrayList<user> arrayList = new ArrayList<user>();
    LeaderboardAdapter leaderboardAdapter;
    ListView listView;
    ImageView refreshList;
    private TextView textViewLeaderboard;
    private TextView textViewTotalPoints;
    private FirebaseFirestore db;
    private FloatingActionButton backBttn;
    private FloatingActionButton setting;




    ProgressBar prgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setDefaultLanguage();


        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        prgBar = (ProgressBar) findViewById(R.id.progressBarListView);
        refreshList = (ImageView)findViewById(R.id.refreshListButton);
        textViewLeaderboard = (TextView)findViewById(R.id.textViewLeaderboard);
        textViewTotalPoints = (TextView)findViewById(R.id.textViewTotalPoints);
        setting = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        backBttn = (FloatingActionButton) findViewById(R.id.floatingActionButtonBack);
        db = FirebaseFirestore.getInstance();

        setLayoutHeight();


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


        /** Do confetti animations for leaderboard textview **/
       // ParticleSystem confetti = new ParticleSystem(this, 100, R.drawable.confetti, 3000);


        /** Finish working on confetti animations for leaderboard textview **/



        /** Set global points on activity loading **/
        retrieveTotalPointsValue();




        refreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.INVISIBLE);
                //fetchTotalPointsValue();
                getUserDataFromFirestore();

            }
        });


        getUserDataFromFirestore();



    }

    private void getUserDataFromFirestore() {
        prgBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<user> list = new ArrayList<user>();

        db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING) // Order users by points in descending order
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String name = document.get("username").toString();
                            String country = document.get("country").toString();
                            String points =  document.get("points").toString();
                            String email = document.get("email").toString();
                            user userObj = new user(name, country, points, email); // Assuming User class with appropriate fields
                            list.add(userObj);
                         //  Toast.makeText(getApplicationContext(), "Hello1 "+list.size(), Toast.LENGTH_SHORT).show();
                        }

                        runActivity(list);
                        prgBar.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        Log.e("Firestore Error", "Error getting user data: " + e.getMessage());
                        prgBar.setVisibility(View.GONE);

                    }
                });

    }

    public void runActivity(ArrayList<user> list){

       // Toast.makeText(getApplicationContext(), "Hello "+list.size(), Toast.LENGTH_SHORT).show();

        leaderboardAdapter = new LeaderboardAdapter(this,list);

        listView = findViewById(R.id.listVieww);
        listView.setAdapter(leaderboardAdapter);
       // prgBar.setVisibility(View.GONE);

    }


    // Set user points in textview from our Firestore DB
    public void retrieveTotalPointsValue() {
        // Reference to the document with ID "1"
        DocumentReference totalPointsRef = db.collection("total_points").document("1");

        // Fetch the current document to get the current value
        totalPointsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get the current value of the total_Points field
                            long currentPoints = document.getLong("total_Points");
                            textViewTotalPoints.setText(String.valueOf(currentPoints));

                            // Now you have the current value, you can use it as needed
                            Log.d("Firestore", "Current value of total_Points: " + currentPoints);
                        } else {
                            // Document does not exist
                            Log.d("Firestore", "Document does not exist");
                        }
                    } else {
                        // Handle query failure
                        Log.e("Firestore", "Error querying document: " + task.getException().getMessage());
                    }
                });
    }


    // Shows the overlay menu
    public void showOverlayMenu() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.overlay_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent background
        dialog.getWindow().setGravity(Gravity.CENTER); // Centered on screen

        // Initialize menu items
       // Button buttonOption1 = dialog.findViewById(R.id.leaderboardButton);
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
                // if (userSelectedLanguage) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                if(selectedLanguage.equals("English") && !currentLocale.equals("en"))
                    setLocale("en");

                else if(selectedLanguage.equals("Arabic") && !currentLocale.equals("ar"))
                    setLocale("ar");

                // }
                // userSelectedLanguage = true; // Set the flag to true after the first selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set click listeners for menu items
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

    // Restarts the app
    public void restartApp() {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    // Returns current locale being used
    public String getCurrentLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLanguage = preferences.getString("selectedLanguage", "en");

        return selectedLanguage;

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


    public void setLayoutHeight(){
        ConstraintLayout constraintLayout = findViewById(R.id.consLayout); // Replace with your actual ID

        // Get the display metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Calculate the percentage of screen height you want for linearLayout8 (e.g., 40%)
        float percentageOfScreenHeight = 0.6f;

        // Calculate the desired height in pixels
        int desiredHeightInPixels = (int) (displayMetrics.heightPixels * percentageOfScreenHeight);

        // Find the views
        LinearLayout linearLayout8 = findViewById(R.id.linearLayout8); // Replace with your actual ID
        LinearLayout linearLayout9 = findViewById(R.id.linearLayout9); // Replace with your actual ID

        // Set layout parameters for linearLayout8
        ConstraintLayout.LayoutParams paramsLinearLayout8 = (ConstraintLayout.LayoutParams) linearLayout8.getLayoutParams();
        paramsLinearLayout8.height = desiredHeightInPixels;

        // Set constraints for linearLayout8
        paramsLinearLayout8.topToBottom = R.id.linearLayout9; // Set the top constraint below linearLayout9
        paramsLinearLayout8.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        linearLayout8.setLayoutParams(paramsLinearLayout8);

    }


}