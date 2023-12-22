package com.example.color_crowdsourcing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Locale;

public class friendRequests extends AppCompatActivity {

    ArrayList<user> listSearchResults = new ArrayList<user>();
    friendRequestsAdapter friend_RequestsAdapter;

    private ListView listViewFriendRequests;
    private FirebaseFirestore db;
    String[] friendRequestsArray;
    private FloatingActionButton backBttn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        setDefaultLanguage();
        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        listViewFriendRequests = (ListView) findViewById(R.id.friendsRequests);
        backBttn = (FloatingActionButton) findViewById(R.id.floatingActionButtonBack);
        db = FirebaseFirestore.getInstance();

        setLayoutHeight();
        getFriendsRequestsFromFireStore();


        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void getFriendsRequestsFromFireStore(){
        // Get the current email of user
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userEmailToSearch = preferences.getString("userEmail", "none");


        // Get the reference to the collection
        db.collection("users")
                .whereEqualTo("email", userEmailToSearch)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Document found, retrieve friends field
                            String friendsString = document.get("friendRequests").toString();
                            String newListss = friendsString.replaceAll("[\\[\\]]", "");

                            // Toast.makeText(friendsList.this, "Friends list: "+newListss, Toast.LENGTH_LONG).show();

                            // Process the friends string and put them into an array
                            if (newListss != null && !newListss.isEmpty()) {
                                friendRequestsArray = newListss.split(", ");
                                String testDlt = "";


//                               // Now 'friendsArray' contains individual friends
                                // For each friend, we will populate our global array list "list"
                                for (String friend : friendRequestsArray) {
                                    // Do something with each friend, for example, log them
                                    testDlt+= friend;
                                    getFriendsListFromFirestore(friend);// we pass a single email
                                    // now the global "list" will add an entry for the user with this
                                    // email

                                }


                            } else {
                                //textViewNoFriends.setVisibility(View.VISIBLE);
                               // listView.setVisibility(View.GONE);

                            }
                        }
                    } else {
                        // Handle errors
                        Log.e("FirestoreError", "Error getting documents: " + task.getException().getMessage());
                    }
                });


    }

    private void getFriendsListFromFirestore( String friendUsername) {
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //ArrayList<user> list = new ArrayList<user>();

        db.collection("users")
                .whereEqualTo("username", friendUsername)
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
                            listSearchResults.add(userObj);
                            //  Toast.makeText(getApplicationContext(), "Hello1 "+list.size(), Toast.LENGTH_SHORT).show();
                        }

                        runActivityFriendRequests(listSearchResults);
                        listViewFriendRequests.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        Log.e("Firestore Error", "Error getting user data: " + e.getMessage());

                    }
                });

    }


    public void runActivityFriendRequests(ArrayList<user> list){


        friend_RequestsAdapter = new friendRequestsAdapter(this,list);

        listViewFriendRequests.setAdapter(friend_RequestsAdapter);

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

// Calculate the percentage of screen height you want (e.g., 50%)
        float percentageOfScreenHeight = 0.65f;

// Calculate the desired height in pixels
        int desiredHeightInPixels = (int) (displayMetrics.heightPixels * percentageOfScreenHeight);

// Find the LinearLayout
        LinearLayout linearLayout = findViewById(R.id.linearLayout8); // Replace with your actual ID

// Set the calculated height to the LinearLayout
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                desiredHeightInPixels
        );

// Set constraints for the LinearLayout
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        // layoutParams.topToBottom = R.id.viewAboveLinearLayout; // Replace with the ID of the view above the LinearLayout

// Apply the layout parameters to the LinearLayout
        linearLayout.setLayoutParams(layoutParams);

    }
}