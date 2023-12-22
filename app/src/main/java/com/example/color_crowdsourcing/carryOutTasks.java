package com.example.color_crowdsourcing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class carryOutTasks extends AppCompatActivity {
    private boolean userSelectedLanguage = false;
    private FloatingActionButton settings;
    private FirebaseFirestore db;
    private TextView totalPointsView;
    private MyDatabaseHelper myDB = new MyDatabaseHelper(carryOutTasks.this);
    ;
    private Button nextColorBttn;
    private EditText colorNameField;
    private String colorHexValue;
    private List<ColorModel> colorList;
    private View viewColorBox;

    private int currentColorIndex = 0;
    private String colorHex;
    private int countColorsForUser;
    private boolean colorExists;
    private String onScreenHexValue = "null";
    private MediaPlayer mp;
    private MediaPlayer mpSkip;
    private Vibrator vibe;
    private FrameLayout animationContainer;
    private TextView textViewSkipColor;
    private ArrayList<String> valuesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carry_out_tasks);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setDefaultLanguage();

        totalPointsView = (TextView) findViewById(R.id.totalPointsView);
        settings = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        nextColorBttn = (Button) findViewById(R.id.buttonNextColor);
        colorNameField = (EditText) findViewById(R.id.colorNameTextField);
        viewColorBox = (View) findViewById(R.id.viewColorBox);
        mp = MediaPlayer.create(this, R.raw.point_gained);
        mpSkip = MediaPlayer.create(this, R.raw.slide_sound);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        animationContainer = findViewById(R.id.animationContainer);
        textViewSkipColor = (TextView)findViewById(R.id.textViewSkipColor);
        db = FirebaseFirestore.getInstance();

        setLayoutHeight();
        colorList = ColorUtils.readColorsFromJson(this, R.raw.colors); // returns full color list


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String currentEmail = preferences.getString("userEmail", "none");


        importJsonColors();
        manageCurrentColorList();
        // Now we have an updated version of our "valuesList" global variable
        // Now we carry operations on this list


        nextColorBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userResponseValue  = colorNameField.getText().toString();
                if (!userResponseValue.isEmpty()){
                    nextButtonClicked(userResponseValue,currentEmail);

                }else{
                    Toast.makeText(carryOutTasks.this, "Please fill field", Toast.LENGTH_LONG).show();
                }
               // nextButtonClicked();

            }
        });


        textViewSkipColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // checkIfColorUsedByUser(currentEmail, 0);
                // We just call the loadColor method again
                loadColor(0);
                mpSkip.start();
            }
        });

        /**
        nextColorBttn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String colorName = colorNameField.getText().toString().trim();

                if (!colorName.isEmpty()) {

                    nextColorBttn.setEnabled(false);


                    // Firstly we add the user input as a response to the color shown on the screen
                    addUserResponse(onScreenHexValue, currentEmail, colorName);

                    // Then we add this color hex as a history in the user's profile so it wont get
                    // repeated again
                    addColorToFirebase(onScreenHexValue, currentEmail);


                } else {
                    Toast.makeText(carryOutTasks.this, "Please fill field", Toast.LENGTH_LONG).show();
                }


            }
        });

         **/

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverlayMenu();
            }
        });


        /** Alerting the user that only Arabic color names are accepted into the input field **/
        //showLanguageErrorPopup();
        showPopUpWar();

        /** Setting the text inside the total points view for a certain user **/
        // Firstly get current email
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // String currentEmail = preferences.getString("userEmail","none");
        if (!currentEmail.equals("none")) {
            setUserPoints(currentEmail);
        } else
            Toast.makeText(carryOutTasks.this, "Failed to retrieve data.\nPlease restart the applications", Toast.LENGTH_LONG).show();


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

    // Returns current locale being used
    public String getCurrentLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLanguage = preferences.getString("selectedLanguage", "en");

        return selectedLanguage;

    }


    // Restarts the app
    public void restartApp() {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
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

        if (currentLocale.equals("en")) langSpinner.setSelection(0);
        else langSpinner.setSelection(1);

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

        // Button used for logout
        buttonOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        dialog.show(); // Show the menu
    }

    // Set user points in textview from our Firestore DB
    public void setUserPoints(String userEmail) {
        db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("email", userEmail) // Query based on user's email
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                            Toast.makeText(carryOutTasks.this, "No document found with the given email.\nPlease log out then log in again !", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        Toast.makeText(carryOutTasks.this, "Failed to retrieve data.\nPlease restart the applications", Toast.LENGTH_LONG).show();

                    }
                });
    }

    // Logs out a user from their session
    public void logoutUser() {
        // We remove the shared preference key "userEmail"
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        // Specify the key we want to remove
        String keyToRemove = "userEmail";

        // Remove the key and its value
        editor.remove(keyToRemove);
        editor.apply();

        // Redirect user to sign in page
        Intent myIntent = new Intent(carryOutTasks.this, introductoryPage.class);
        startActivity(myIntent);

    }

//    private void showLanguageErrorPopup() {
//        // Inflate the popup layout
//        View popupView = LayoutInflater.from(this).inflate(R.layout.language_warning, null);
//
//        // Create the PopupWindow
//        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//        // Set a background drawable with 0 alpha to allow the popup to close when clicking outside
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
//
//        // Show the popup at the center of the screen
//        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
//    }

    public void showPopUpWar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.language_warning, null);
        builder.setView(dialogView).setTitle("Language Warning").setPositiveButton("I understand", (dialog, which) -> {
            // Handle agreement logic if needed
        }).show();

    }

//    public String displayColor() {
//
//
//        int random_int = (int) Math.floor(Math.random() * (2 - 0 + 1) + 0);
//
//        //ColorModel currentColor = colorList.get(currentColorIndex);
//        ColorModel currentColor = colorList.get(random_int);
//
//
//        String hexValue = currentColor.getHexValue();
//        String name = currentColor.getName();
//        // Toast.makeText(carryOutTasks.this, "Hex value: "+hexValue+"\nName: "+name,Toast.LENGTH_LONG).show();
//
//        if (!myDB.doesEntryExist(hexValue)) {
//            viewColorBox.setBackgroundColor(Color.parseColor(hexValue));
//            return hexValue;
//        } else {
//            return "null";
//        }
//    }

//    public void addColorToFirebase(String hex, String email) {
//        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
//        CollectionReference usersRef = rootRef.collection("users");
//
//        // Specify the email to target
//        String targetEmail = email;
//
//        // Create a query to find the user with the specified email
//        Query query = usersRef.whereEqualTo("email", targetEmail);
//
//        // Execute the query
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                // Check if there is a user with the specified email
//                if (!task.getResult().isEmpty()) {
//                    // Get the document snapshot for the user
//                    DocumentSnapshot userSnapshot = task.getResult().getDocuments().get(0);
//                    String oldColor = userSnapshot.get("colors selected").toString();
//
//                    // Update the "colors selected" field for the user
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("colors selected", oldColor + "," + hex);
//
//                    // Update the document
//                    usersRef.document(userSnapshot.getId()).update(data).addOnSuccessListener(aVoid -> {
//                        // Update successful
//                        //System.out.println("Colors selected updated successfully!");
//                        colorHistorySize(targetEmail);
//                        checkIfColorUsedByUser(targetEmail, 1);
//                    }).addOnFailureListener(e -> {
//                        // Handle failure
//                        //System.out.println("Error updating colors selected: " + e.getMessage());
//                    });
//                } else {
//                    // No user found with the specified email
//                    System.out.println("No user found with email: " + targetEmail);
//                }
//            } else {
//                // Handle query failure
//                System.out.println("Error querying database: " + task.getException().getMessage());
//            }
//        });
//
//
//    }

    @SuppressLint("ResourceAsColor")
    public void checkIfColorUsedByUser(String email, int status) {
        int random_int = (int) Math.floor(Math.random() * (3 - 0 + 1) + 0);

        //ColorModel currentColor = colorList.get(currentColorIndex);
        ColorModel currentColor = colorList.get(random_int);
        String hexValue = currentColor.getHexValue();
        String name = currentColor.getName();

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference usersRef = rootRef.collection("users");

        // Specify the email you want to target
        String targetEmail = email;
        String colorToCheck = hexValue;

        // Create a query to find the user with the specified email
        Query query = usersRef.whereEqualTo("email", targetEmail);


        // Execute the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Check if there is a user with the specified email

                if (!task.getResult().isEmpty()) {
                    // Get the document snapshot for the user
                    DocumentSnapshot userSnapshot = task.getResult().getDocuments().get(0);


                    // Check if the specified color exists in the "colors selected" field
                    String colorsSelected = userSnapshot.get("colors selected").toString();
                    // Toast.makeText(carryOutTasks.this, "Result  : "+colorsSelected,Toast.LENGTH_LONG).show();


                    colorHistorySize(email);


                    if (colorsSelected != null && colorsSelected.contains(colorToCheck)) {
                        // Color exists in the "colors selected" field
                        //System.out.println("Color exists for the user!");
                        // colorHistorySize(email);
                        if (countColorsForUser == 4) {
                            nextColorBttn.setEnabled(false);
                            nextColorBttn.setBackgroundColor(Color.parseColor("#4A655D5C"));
                        } else checkIfColorUsedByUser(email, 1);
                        //Toast.makeText(carryOutTasks.this, "Color exists for the user!",Toast.LENGTH_LONG).show();

                    } else {
                        // Color does not exist in the "colors selected" field
                        //System.out.println("Color does not exist for the user.");
                        // colorHistorySize(email);

                        if (countColorsForUser == 4) {
                            nextColorBttn.setEnabled(false);
                            nextColorBttn.setBackgroundColor(Color.parseColor("#4A655D5C"));
                        } else {
                            nextColorBttn.setEnabled(true);
                            viewColorBox.setBackgroundColor(Color.parseColor(hexValue));
                            onScreenHexValue = hexValue;
                            if(status==1)
                                mp.start();
                            else
                                mpSkip.start();

                            vibe.vibrate(100);


                        }
                        //Toast.makeText(carryOutTasks.this, "Color does not exists for the user!",Toast.LENGTH_LONG).show();


                    }
                } else {
                    // No user found with the specified email
                    System.out.println("No user found with email: " + targetEmail);
                }
            } else {
                // Handle query failure
                System.out.println("Error querying database: " + task.getException().getMessage());
            }
        });

    }

    @SuppressLint("ResourceAsColor")
    public void colorHistorySize(String email) {

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference usersRef = rootRef.collection("users");

        // Specify the email to target
        String targetEmail = email;

        // Create a query to find the user with the specified email
        Query query = usersRef.whereEqualTo("email", targetEmail);

        // Execute the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Check if there is a user with the specified email
                if (!task.getResult().isEmpty()) {
                    // Get the document snapshot for the user
                    DocumentSnapshot userSnapshot = task.getResult().getDocuments().get(0);
                    String colors = userSnapshot.get("colors selected").toString();

                    // Split the string by commas
                    String[] values = colors.split(",");
                    int count = values.length - 1;
                    countColorsForUser = count;
                    //Toast.makeText(carryOutTasks.this, "Result length : "+count,Toast.LENGTH_LONG).show();
                    if (count == 4) {
                        nextColorBttn.setEnabled(false);
                        nextColorBttn.setBackgroundColor(Color.parseColor("#4A655D5C"));

                    }


                } else {
                    // No user found with the specified email
                    System.out.println("No user found with email: " + targetEmail);
                }
            } else {
                // Handle query failure
                System.out.println("Error querying database: " + task.getException().getMessage());
            }
        });
    }


    public void addUserResponse(String hex, String email, String userResponse) {


        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference colorsRef = rootRef.collection("Colors");

        // Create a query to find the user with the specified entry color
        Query query = colorsRef.whereEqualTo("Hex", hex);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Check if there is a document with the specified hex value
                if (!task.getResult().isEmpty()) {
                    // Get the document reference for the first matching document
                    DocumentReference documentRef = task.getResult().getDocuments().get(0).getReference();

                    // Create a map for the new values you want to add to the "Value" field
                    Map<String, Object> newResponse = new HashMap<>();
                    newResponse.put("email", email);
                    newResponse.put("response", userResponse);

                    // Use FieldValue.arrayUnion to add the new response to the existing array
                    documentRef.update("Value.responses", FieldValue.arrayUnion(newResponse)).addOnSuccessListener(aVoid -> {
                        // Handle success
                        System.out.println("Value added successfully.");
                        //colorNameField.setText("");
                        incrementPoints(email);
                        //incrementGlobalPoints();

                    }).addOnFailureListener(e -> {
                        // Handle failure
                        System.out.println("Error adding values: " + e.getMessage());
                    });

                } else {
                    // No document found with the specified hex value
                    System.out.println("No document found with hex: " + hex);
                }
            } else {
                // Handle query failure
                System.out.println("Error querying database: " + task.getException().getMessage());
            }
        });
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


    public void incrementPoints(String userEmail) {
        // Reference to the collection
        CollectionReference usersRef = db.collection("users");

        // Create a query to find the user with the specified email
        Query query = usersRef.whereEqualTo("email", userEmail);

        // Execute the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Check if there is a user with the specified email
                if (!task.getResult().isEmpty()) {
                    // Get the document reference for the first matching document
                    DocumentReference userRef = task.getResult().getDocuments().get(0).getReference();

                    // Use update to increment the points field
                    userRef.update("points", FieldValue.increment(1)).addOnSuccessListener(aVoid -> {
                        // Points incremented successfully

                        // Animate point updating
                        int currentPoints = Integer.parseInt(totalPointsView.getText().toString().trim());
                        //Toast.makeText(carryOutTasks.this, " Number: "+currentPoints, Toast.LENGTH_LONG).show();
                        int newPoints = currentPoints + 1;
                        totalPointsView.setText(String.valueOf(newPoints));
                       // showFloatingAnimation();
                        incrementGlobalPoints();


                        Log.d("Firestore", "Points incremented successfully");
                    }).addOnFailureListener(e -> {
                        // Handle the failure
                        Log.e("Firestore", "Error incrementing points: " + e.getMessage());
                    });
                } else {
                    // No user found with the specified email
                    Log.d("Firestore", "No user found with email: " + userEmail);
                }
            } else {
                // Handle query failure
                Log.e("Firestore", "Error querying database: " + task.getException().getMessage());
            }
        });
    }

    public void incrementGlobalPoints() {
        // Reference to the document
        DocumentReference totalPointsRef = db.collection("total_points").document("1");

        // Use update to increment the points field
        totalPointsRef.update("total_Points", FieldValue.increment(1)).addOnSuccessListener(aVoid -> {
            // Points incremented successfully

            Log.d("Firestore", "Points incremented successfully");
        }).addOnFailureListener(e -> {
            // Handle the failure
            Log.e("Firestore", "Error incrementing points: " + e.getMessage());
        });
    }


    private void showFloatingAnimation() {
        View animationView = LayoutInflater.from(this).inflate(R.layout.animation_layout_plus_one, null);
        animationContainer.addView(animationView);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
        animationSet.addAnimation(new TranslateAnimation(0, 0, 0, -100));
        animationSet.setDuration(1000);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationContainer.removeView(animationView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animationView.startAnimation(animationSet);
    }


    // Initialises our original array list of strings
    public void importJsonColors(){

            valuesList = new ArrayList<>();

            try {
                // Open the JSON file from the res/raw directory
                InputStream inputStream = getResources().openRawResource(R.raw.colors);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                // Read the contents of the file into a StringBuilder
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                // Parse the JSON string
                String jsonString = stringBuilder.toString();
                JSONObject jsonObject = new JSONObject(jsonString);

                // Extract values from the JSON object
                for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                    String key = it.next();
                    String value = jsonObject.getString(key);
                    valuesList.add(value);
                }

                // Close the streams
                inputStream.close();
                inputStreamReader.close();
                bufferedReader.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


    }


    // This method downloads the history of the user (colors they chose from DB), then removes these
    // values from the "ArrayList<String> valuesList" global variable
    public void manageCurrentColorList(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userEmail = preferences.getString("userEmail", "none");

        // Reference to the collection
        db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        // Create a query to find the user with the specified email
        Query query = usersRef.whereEqualTo("email", userEmail);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Update the friendRequests field by adding a new value
                    ArrayList<String> colorHistory = (ArrayList<String>) document.get("colors_selected");
                    // Now that we have the user's history of color selection, we can remove these
                    // values from the current valuesList global variable
                    valuesList.removeAll(colorHistory);
                    // Now our valuesList is ready to go be used by the app

                    loadColor(1);

                }
            }
        });


    }


    // This method chooses a random value from "valuesList" and displays it
    public void loadColor(int y){

        int valuesListSize = valuesList.size();

        if(valuesListSize == 0){
            nextColorBttn.setEnabled(false);
            Toast.makeText(carryOutTasks.this, "You reached the end, there is no more colors to display.", Toast.LENGTH_LONG).show();
        } else {
            int random_int = (int) Math.floor(Math.random() * valuesListSize);
            String hexColor = valuesList.get(random_int);
            viewColorBox.setBackgroundColor(Color.parseColor(hexColor));
            colorNameField.setText("");

            // Now the screen has the new color
        }

        if(y==1) {
            mp.start();
            showFloatingAnimation();
        }
        // colorNameField.setText("");

    }

    public void nextButtonClicked(String userResponse, String userEmail) {

        // Get the background drawable of the view
        Drawable backgroundDrawable = viewColorBox.getBackground();
        int color = ((ColorDrawable) backgroundDrawable).getColor();
        // Convert the color integer to a hex string
        String currentDisplayedColor = String.format("#%06X", (0xFFFFFF & color)).toLowerCase();

        // Now we remove the currentDisplayedColor from our "valueList"
        valuesList.remove(currentDisplayedColor);
        // Now our valueList is updated

        // We now add the current user response as an entry in their color_selected field

        CollectionReference usersRef = db.collection("users");

        // Create a query to find the user with the specified email
        Query query = usersRef.whereEqualTo("email", userEmail);


        // before going for backend, we carry the front end first and process the backend in the background
        loadColor(1);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Update the friendRequests field by adding a new value
                    ArrayList<String> currentColorHistory = (ArrayList<String>) document.get("colors_selected");
                    currentColorHistory.add(currentDisplayedColor);

                    // Update the document with the modified friendRequests field
                    document.getReference().update("colors_selected", currentColorHistory)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Toast.makeText(carryOutTasks.this,"Current color added to history", Toast.LENGTH_SHORT).show();
                                    // Now we load a new color by calling loadColor()
                                   // loadColor();
                                    addUserResponse(currentDisplayedColor,userEmail,userResponse);
                                   // loadColor();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                                    // Handle the error
                                    // You can add your error handling code here
                                }
                            });

                }
            }
        });
    }


    public void setLayoutHeight(){
        ConstraintLayout constraintLayout = findViewById(R.id.consLayout); // Replace with your actual ID

// Get the display metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

// Calculate the percentage of screen width you want (e.g., 80%)
        float percentageOfScreenWidth = 0.95f;
        float percentageOfScreenHeightViewBox = 0.45f;
        float percentageOfScreenLinearLayout = 0.25f;


// Calculate the desired width in pixels
        int desiredWidthInPixels = (int) (displayMetrics.widthPixels * percentageOfScreenWidth);
        int desiredHeightInPixelsViewBox = (int) (displayMetrics.heightPixels * percentageOfScreenHeightViewBox);
        int desiredHeightInPixelsLinearLayout = (int) (displayMetrics.heightPixels * percentageOfScreenLinearLayout);




// Find the views
        View viewColorBox = findViewById(R.id.cardView); // Replace with your actual ID
        LinearLayout linearLayout2 = findViewById(R.id.linearLayout2); // Replace with your actual ID
        View dividerLine = findViewById(R.id.dividerLine);

        // Set layout parameters for dividerLine
        ConstraintLayout.LayoutParams paramsdividerLine = (ConstraintLayout.LayoutParams) dividerLine.getLayoutParams();
        paramsdividerLine.width = desiredWidthInPixels;

// Set layout parameters for viewColorBox
        ConstraintLayout.LayoutParams paramsViewColorBox = (ConstraintLayout.LayoutParams) viewColorBox.getLayoutParams();
        paramsViewColorBox.width = desiredWidthInPixels;
        paramsViewColorBox.height = desiredHeightInPixelsViewBox;

// Set constraints for viewColorBox
        paramsViewColorBox.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        paramsViewColorBox.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        paramsViewColorBox.horizontalBias = 0.5f; // Center horizontally
        viewColorBox.setLayoutParams(paramsViewColorBox);

// Set layout parameters for linearLayout2
        ConstraintLayout.LayoutParams paramsLinearLayout2 = (ConstraintLayout.LayoutParams) linearLayout2.getLayoutParams();
        paramsLinearLayout2.width = desiredWidthInPixels;
        paramsLinearLayout2.height = desiredHeightInPixelsLinearLayout;

// Set constraints for linearLayout2
        paramsLinearLayout2.startToEnd = R.id.viewColorBox; // Set the start constraint to the end of viewColorBox
        paramsLinearLayout2.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        linearLayout2.setLayoutParams(paramsLinearLayout2);


        dividerLine.setLayoutParams(paramsdividerLine);



    }

}