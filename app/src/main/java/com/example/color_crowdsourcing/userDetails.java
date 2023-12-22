package com.example.color_crowdsourcing;

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
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class userDetails extends AppCompatActivity {

    private boolean userSelectedLanguage = false;
    FloatingActionButton setting;
    private FloatingActionButton backBttn;
    private ImageView userFlagImg;
    private FirebaseFirestore db;
    private TextView usernameHolder;
    private TextView emailHolder;
    private TextView DOBHolder;
    private TextView genderHolder;
    private TextView nameHolder;
    private TextView pointsHolder;
    private ImageView imageViewChevLogout;
    private ImageView imageViewChevDeleteAccount;
    private ImageView contactUs;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setDefaultLanguage();


        setting = (FloatingActionButton) findViewById(R.id.floatingActionButtonSettings);
        backBttn = (FloatingActionButton) findViewById(R.id.floatingActionButtonBack);
        userFlagImg = (ImageView) findViewById(R.id.imageViewUserFlag);
        db = FirebaseFirestore.getInstance();
        usernameHolder = (TextView) findViewById(R.id.textViewUsernameHolder);
        emailHolder = (TextView) findViewById(R.id.textViewEmailHolder);
        DOBHolder = (TextView) findViewById(R.id.textViewDOBholder);
        genderHolder = (TextView) findViewById(R.id.textViewGenderHolder);
        nameHolder = (TextView) findViewById(R.id.textViewNameHolder);
        pointsHolder = (TextView) findViewById(R.id.textViewPointsHolder);
        imageViewChevLogout = (ImageView) findViewById(R.id.imageViewChevLogout);
        imageViewChevDeleteAccount = (ImageView) findViewById(R.id.imageViewDeleteAccount);
        contactUs = (ImageView)findViewById(R.id.imageViewContacuUs);
        mAuth = FirebaseAuth.getInstance();


        // Setting user flag image
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userEmail = preferences.getString("userEmail", "none");
        // Toast.makeText(userDetails.this, "User email: "+userEmail, Toast.LENGTH_SHORT).show();

        setUserFlagImg(userEmail);
        setLayoutHeight();




        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactUseMenu();
            }
        });
        // Goes back to previous screen which is .......
        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        imageViewChevLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_logout_menu();
            }
        });
        imageViewChevLogout.setOnTouchListener(new View.OnTouchListener() {
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

        imageViewChevDeleteAccount.setOnTouchListener(new View.OnTouchListener() {
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

        imageViewChevDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_delete_account_menu();
            }
        });


    }

    public void setUserFlagImg(String email) {

        // Get the reference to the Firestore collection
        CollectionReference usersCollection = db.collection("users");

        // Query for documents where the "email" field is equal to the target email
        Query query = usersCollection.whereEqualTo("email", email);

        // Retrieve the document
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Document exists
                    // Retrieve the value of the "country" field
                    // Retrieve the first document (assuming there's only one matching email)
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    String country = documentSnapshot.getString("country");
                    String countryCode = getCountryCode(country);
                    // Toast.makeText(userDetails.this, "The country code is: "+countryCode, Toast.LENGTH_SHORT).show();
                    String glideUrl = "https://flagsapi.com/" + countryCode + "/shiny/64.png";
                    //String imageUrl = "URL_OF_YOUR_IMAGE"; // Replace with the actual URL or resource ID

                    Glide.with(userDetails.this).load(glideUrl).centerCrop().apply(RequestOptions.bitmapTransform(new CircleCrop())).into(userFlagImg);

                    String name = documentSnapshot.getString("name");
                    String dob = documentSnapshot.getString("DOB");
                    String gender = documentSnapshot.getString("gender");
                    String usernameUser = documentSnapshot.getString("username");
                    Long points = documentSnapshot.getLong("points");


                    nameHolder.setText(name);
                    pointsHolder.setText(getString(R.string.txtPoints)+" "+ points);
                    usernameHolder.setText(usernameUser);
                    emailHolder.setText(email);
                    DOBHolder.setText(dob);
                    genderHolder.setText(gender);


                    // Do something with the country value
                    if (country != null) {
                        // Use the 'country' value as needed
                        System.out.println("Country: " + country);
                    } else {
                        // Handle the case where 'country' field is not present or is null
                        System.out.println("Country field is not present or is null");
                    }
                } else {
                    // Document does not exist
                    System.out.println("Document does not exist for email: " + email);
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

        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("selectedLanguage", currentLanguage); // Store the english language language
            editor.apply();
        }
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
    public String getCurrentLocale() {
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


    public String getCountryCode(String country) {
        String countryCode = "";
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale locale : locales) {
            if (country.equalsIgnoreCase(locale.getDisplayCountry())) {
                countryCode = locale.getCountry();
                break;
            }
        }

        return countryCode;
    }

    public void show_logout_menu() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.overlay_menu_logout_confirm);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent background
        dialog.getWindow().setGravity(Gravity.BOTTOM); // Bottom of the screen
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Set width to match_parent

        TextView exitApp = (TextView) dialog.findViewById(R.id.textViewExitApp);
        TextView cancelMenu = (TextView) dialog.findViewById(R.id.textViewCancelMenu);
        TextView logoutApp = (TextView) dialog.findViewById(R.id.titleTextLogout);


        exitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                dialog.dismiss();
            }
        });

        cancelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        logoutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });


        dialog.show();

    }

    public void show_delete_account_menu() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.overlay_menu_delete_account_confirm);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent background
        dialog.getWindow().setGravity(Gravity.BOTTOM); // Bottom of the screen
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Set width to match_parent


        TextView exitApp = (TextView) dialog.findViewById(R.id.textViewExitApp);
        TextView dltAccount = (TextView) dialog.findViewById(R.id.textViewDeleteAccount);
        TextView cancelMenu = (TextView) dialog.findViewById(R.id.textViewCancelMenu);
        ProgressBar progressBar = (ProgressBar)dialog.findViewById(R.id.progressBar);

        exitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                dialog.dismiss();
            }
        });

        dltAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dltAccount.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                deleteUserAccount();
            }
        });

        cancelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    public void deleteUserAccount() {
       // Toast.makeText(userDetails.this, "Email to dlt: .", Toast.LENGTH_SHORT).show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userEmailToDlt = preferences.getString("userEmail", "none");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) { //User deleted from auth database
                   // Toast.makeText(userDetails.this, "Email to dlt: ."+userEmailToDlt, Toast.LENGTH_SHORT).show();


                    //Toast.makeText(userDetails.this, "User deleted", Toast.LENGTH_SHORT).show();

                    // Now we delete user profile from "users" collection
                    // Query to find the document with the specified email value
                    Query query = db.collection("users").whereEqualTo("email", userEmailToDlt);

                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Document found, now delete it
                                    db.collection("users").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Document successfully deleted
                                            //Toast.makeText(userDetails.this, "Document deleted.", Toast.LENGTH_SHORT).show();

                                            // Now the user profile is deleted we just have to delete their entry from the "colors" collection
                                            // Query to find documents where the "responses" array contains a map with the specified email

                                          //  Toast.makeText(userDetails.this, "Email to dlt: ."+userEmailToDlt, Toast.LENGTH_SHORT).show();


                                            deleteResponses(userEmailToDlt);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to delete document
                                            Toast.makeText(userDetails.this, "Failed to delete document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                // Error getting documents
                                Toast.makeText(userDetails.this, "Error getting documents: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });

    }

    public void deleteResponses(String targetEmail) {

        final CollectionReference colorsCollection = db.collection("Colors");
        colorsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // For each document in the collection
                        DocumentReference docRef = colorsCollection.document(document.getId());
                        deleteResponsesInDocument(docRef, targetEmail);
                    }

                    Toast.makeText(userDetails.this, getString(R.string.toastUserDeleted), Toast.LENGTH_LONG).show();
                    logoutUser();


                } else {
                    // Handle errors
                }
            }
        });
    }


    private void deleteResponsesInDocument(DocumentReference docRef, String targetEmail) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the current responses array
                        Object responsesObj = document.get("Value.responses");
                        if (responsesObj instanceof List) {
                            List<Map<String, Object>> responses = (List<Map<String, Object>>) responsesObj;

                            // Remove responses with the target email
                            responses.removeIf(response -> {
                                Object emailObj = response.get("email");
                                return emailObj instanceof String && targetEmail.equals(emailObj);
                            });

                            // Update the document with the modified responses
                            docRef.update("Value.responses", responses);

                            //Toast.makeText(userDetails.this, "Map removed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else {
                    // Handle errors
                }
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
        Intent myIntent = new Intent(userDetails.this, introductoryPage.class);
        startActivity(myIntent);

    }


    // Shows the overlay menu
    public void showContactUseMenu() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.contact_us);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent background
        dialog.getWindow().setGravity(Gravity.BOTTOM); // bottom of screen
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Set width to match_parent


        // Initialize menu items
        Spinner spinnerCategories = dialog.findViewById(R.id.spinnerCategory);
        EditText complaintInput = dialog.findViewById(R.id.editTextTicketContents);
        View lineUnderComplaint  = dialog.findViewById(R.id.viewSepUnderComplaing);
        TextView submit = dialog.findViewById(R.id.textViewSubmitTicket);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        submit.setVisibility(View.VISIBLE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String complaint = complaintInput.getText().toString();
                String complaintChoice = spinnerCategories.getSelectedItem().toString();
                if (!complaint.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);
                    sendComplaintToDB(complaint, complaintChoice, dialog, progressBar);
                }
                else
                    Toast.makeText(userDetails.this, getString(R.string.toastFillAllFields), Toast.LENGTH_SHORT).show();


            }
        });


        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(position){
                    case 0:
                        submit.setEnabled(false);
                        submit.setTextColor(Color.parseColor("#808080"));
                        lineUnderComplaint.setVisibility(View.GONE);
                        complaintInput.setVisibility(View.GONE);
                        break;
                    default:
                        //sToast.makeText(userDetails.this, "You selected other", Toast.LENGTH_SHORT).show();
                        submit.setEnabled(true);
                        submit.setTextColor(Color.RED);
                        lineUnderComplaint.setVisibility(View.VISIBLE);
                        complaintInput.setVisibility(View.VISIBLE);
                        break;

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dialog.show(); // Show the menu
    }


    public void sendComplaintToDB(String complaint, String spinnerSelection, Dialog dialog, ProgressBar progBar){



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userEmail = preferences.getString("userEmail", "none");

        db = FirebaseFirestore.getInstance();
        DocumentReference  generalSupportDocument = db.collection("tickets").document(spinnerSelection);;


        // Fetch the existing data from the "general support" document
        generalSupportDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, update it with the new ticket


                        // Get the existing data
                        Map<String, Object> existingTickets = document.getData();

                        int size = existingTickets.size();
                        //Toast.makeText(userDetails.this, "Ticektss: "+size ,Toast.LENGTH_SHORT).show();
                        int newTicketID  = size + 1;





                        // Create a new map for the new ticket
                        Map<String, Object> newTicket = new HashMap<>();
                        newTicket.put("email", userEmail);
                        newTicket.put("complaint", complaint);
//
                        // Add the new ticket to the list
                        existingTickets.put("ticket"+newTicketID, newTicket);

                        // Update the "general support" document with the updated list
                        generalSupportDocument.set(existingTickets)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Document updated successfully
                                            // You can add any additional logic or UI updates here
                                            Toast.makeText(userDetails.this, getString(R.string.toastTicketSent), Toast.LENGTH_SHORT).show();
                                            progBar.setVisibility(View.GONE);
                                            dialog.dismiss();

                                        } else {
                                            // Handle the error
                                            Exception e = task.getException();
                                            // Log or show an error message
                                        }
                                    }
                                });
                    } else {
                        // Document does not exist
                        // Handle accordingly, you might want to create it
                    }
                } else {
                    // Handle the error
                    Exception e = task.getException();
                    // Log or show an error message
                }
            }
        });



    }


    public void setLayoutHeight(){



        ConstraintLayout constraintLayout = findViewById(R.id.rootLayout); // Replace with your actual ID

        // Get the display metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Calculate the percentage of screen width and height you want (e.g., 80% and 40%)
        float percentageOfScreenWidth = 0.86f;
        float percentageOfScreenHeight = 0.25f;

        // Calculate the desired width and height in pixels
        int desiredWidthInPixels = (int) (displayMetrics.widthPixels * percentageOfScreenWidth);
        int desiredHeightInPixels = (int) (displayMetrics.heightPixels * percentageOfScreenHeight);

        // Find the views
        LinearLayout personalInfo = findViewById(R.id.linearLayoutPersonalInfo); // Replace with your actual ID
        LinearLayout otherInfo = findViewById(R.id.linearLayoutOtherInformation);




        // Set layout parameters for inputForm
        ConstraintLayout.LayoutParams paramsInputForm = (ConstraintLayout.LayoutParams) personalInfo.getLayoutParams();
        paramsInputForm.width = desiredWidthInPixels;
        paramsInputForm.height = desiredHeightInPixels;

        // Set layout parameters for otherInfo
        ConstraintLayout.LayoutParams paramsotherInfo= (ConstraintLayout.LayoutParams) otherInfo.getLayoutParams();
        paramsotherInfo.width = desiredWidthInPixels;
        paramsotherInfo.height = desiredHeightInPixels;


        // Set layout parameters for lineSepView
        //ConstraintLayout.LayoutParams paramsInputForm3 = (ConstraintLayout.LayoutParams) circles.getLayoutParams();
       // paramsInputForm3.width = desiredWidthInPixels;


        personalInfo.setLayoutParams(paramsInputForm);
        otherInfo.setLayoutParams(paramsotherInfo);
       // lineSepView.setLayoutParams(paramsInputForm2);
       // circles.setLayoutParams(paramsInputForm3);

    }

}