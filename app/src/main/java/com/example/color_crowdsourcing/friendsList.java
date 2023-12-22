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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;
import java.util.Locale;

public class friendsList extends AppCompatActivity {

    ArrayList<user> list = new ArrayList<user>();
    ArrayList<user> listSearchResults = new ArrayList<user>();

    String[] friendsArray;
    friendList_adapter friendsListAdapter;
    ListView listView;
    friendsSearchResultAdapter searchResultAdapter;
    private ConstraintLayout consLayout;
    private ImageView addFriend;
    private TextView viewTextFriendsList;
    private ImageView imageViewSearch;
    private EditText editTextSearchFriend;
    private ImageView imageViewCancelSearch;
    private ImageView imageViewEdit;
    private FirebaseFirestore db;
    private ListView listViewSearchResults;
    private TextView textViewNoFriends;
    private TextView textViewNoFriendsResults;
    private FloatingActionButton floatingActionButtonFriendRequests;
    private FloatingActionButton backBttn;
    private FloatingActionButton setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setDefaultLanguage();


        consLayout = (ConstraintLayout)findViewById(R.id.consLayout);
        addFriend = (ImageView) findViewById(R.id.imageViewAddFriend);
        viewTextFriendsList = (TextView)findViewById(R.id.textViewFriendsList);
        editTextSearchFriend = (EditText)findViewById(R.id.editTextSearchFriend);
        imageViewSearch = (ImageView)findViewById(R.id.imageViewSearch);
        imageViewCancelSearch = (ImageView) findViewById(R.id.imageViewCancelSearch);
        //imageViewEdit = (ImageView) findViewById(R.id.imageViewEdit);
        listView = findViewById(R.id.friendsListView);
        listViewSearchResults = findViewById(R.id.searchFriendsListView);
        textViewNoFriends = (TextView)findViewById(R.id.textViewNoFriends);
        textViewNoFriendsResults = (TextView)findViewById(R.id.textViewNoFriendsResults);
        floatingActionButtonFriendRequests = (FloatingActionButton)findViewById(R.id.floatingActionButtonFriendRequests);
        setting = (FloatingActionButton) findViewById(R.id.floatingActionButtonSettings);
        backBttn = (FloatingActionButton) findViewById(R.id.floatingActionButtonBack);
        db = FirebaseFirestore.getInstance();

        checkIfUserHasRequests();
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

        floatingActionButtonFriendRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(friendsList.this,friendRequests.class);
                startActivity(intent);
            }
        });

        addFriend.setOnTouchListener(new View.OnTouchListener() {
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

        imageViewCancelSearch.setOnTouchListener(new View.OnTouchListener() {
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

        imageViewSearch.setOnTouchListener(new View.OnTouchListener() {
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


        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addFriend.setVisibility(View.GONE);
                viewTextFriendsList.setVisibility(View.GONE);
                editTextSearchFriend.setVisibility(View.VISIBLE);
                imageViewSearch.setVisibility(View.VISIBLE);
                imageViewCancelSearch.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                textViewNoFriends.setVisibility(View.GONE);

            }
        });

        imageViewCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addFriend.setVisibility(View.VISIBLE);
                viewTextFriendsList.setVisibility(View.VISIBLE);
                editTextSearchFriend.setVisibility(View.GONE);
                imageViewSearch.setVisibility(View.GONE);
                imageViewCancelSearch.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listViewSearchResults.setVisibility(View.GONE);
                editTextSearchFriend.setText("");
                listSearchResults.removeAll(listSearchResults);





            }
        });

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetUsername = editTextSearchFriend.getText().toString().trim().toLowerCase();

                if(!targetUsername.isEmpty()) {
                    listSearchResults.clear();
                    searchResultAdapter = new friendsSearchResultAdapter(friendsList.this,listSearchResults);
                    searchResultAdapter.notifyDataSetChanged();
                    getUsername(targetUsername);
                }
            }
        });


        // Firstly we retrieve a list of a user's friends
        getFriendsEmailsFromFireStore();


    }


    // This method returns the search result ( a single row or nothing) based on the entered username
    // We search the DB and return the matchin result if found
    public void getUsername(String targetUsername){

        // Query to find the document with the specified email
        db.collection("users")
                .whereEqualTo("username", targetUsername)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String name = document.get("username").toString();
                                    String country = document.get("country").toString();
                                    String points = document.get("points").toString();
                                    String email = document.get("email").toString();
                                    user userObj = new user(name, country, points, email); // Assuming User class with appropriate fields
                                    listSearchResults.add(userObj);
                                }
                                runActivitySearchResults(listSearchResults);
                                listViewSearchResults.setVisibility(View.VISIBLE);
                                textViewNoFriendsResults.setVisibility(View.GONE);


                            }else{
                                textViewNoFriendsResults.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Handle errors
                            Log.e("FirestoreError", "Error getting documents: " + task.getException().getMessage());
                        }
                    }
                });
    }
    public void getFriendsEmailsFromFireStore(){
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
                            String friendsString = document.get("friends").toString();
                            String newListss = friendsString.replaceAll("[\\[\\]]", "");

                           // Toast.makeText(friendsList.this, "Friends list: "+newListss, Toast.LENGTH_LONG).show();

                            // Process the friends string and put them into an array
                            if (newListss != null && !newListss.isEmpty()) {
                                textViewNoFriends.setVisibility(View.GONE);

                                friendsArray = newListss.split(", ");
                                String testDlt = "";


//                               // Now 'friendsArray' contains individual friends
                                // For each friend, we will populate our global array list "list"
                                for (String friend : friendsArray) {
                                    // Do something with each friend, for example, log them
                                    testDlt+= friend;
                                    getFriendsListFromFirestore(friend);// we pass a single email
                                    // now the global "list" will add an entry for the user with this
                                    // email

                                }


                            } else {
                                textViewNoFriends.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);

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
                            list.add(userObj);
                            //  Toast.makeText(getApplicationContext(), "Hello1 "+list.size(), Toast.LENGTH_SHORT).show();
                        }

                        runActivity(list);
                        listView.setVisibility(View.VISIBLE);
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

    public void runActivity(ArrayList<user> list){

        // Toast.makeText(getApplicationContext(), "Hello "+list.size(), Toast.LENGTH_SHORT).show();

        friendsListAdapter = new friendList_adapter(this,list);

       // listView = findViewById(R.id.friendsListView);
        listView.setAdapter(friendsListAdapter);
        // prgBar.setVisibility(View.GONE);

    }

    public void runActivitySearchResults(ArrayList<user> list){


        searchResultAdapter = new friendsSearchResultAdapter(this,list);

        listViewSearchResults.setAdapter(searchResultAdapter);

    }

    public void checkIfUserHasRequests(){

        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //ArrayList<user> list = new ArrayList<user>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userEmailToSearch = preferences.getString("userEmail", "none");


        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        // Find the document with the specified email
        Query query = usersCollection.whereEqualTo("email", userEmailToSearch);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Update the friendRequests field by adding a new value
                    ArrayList<String> currentFriendRequests  = (ArrayList<String>) document.get("friendRequests");

                    if(!currentFriendRequests.isEmpty()){
                        floatingActionButtonFriendRequests.setImageResource(R.drawable.notifications_unread);
                    }



                }
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