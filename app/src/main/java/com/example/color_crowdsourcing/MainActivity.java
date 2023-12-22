package com.example.color_crowdsourcing;

import static android.os.Build.VERSION.SDK_INT;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {

    /**
     * Text Views
     **/
    private TextView logInInstead; // This is the " Have an account? Login instead " line below button
    TextView passConditions;// Text view of the password minimum requirements
    TextView terms;// Text view of the underlined statement " Terms and Conditions "

    /**
     * Edit Texts
     **/
    private EditText nameField;// Input field for user's name
    private EditText emailField;// Input field for user's email
    private EditText passField;// Input field for user's password
    private EditText dateField; // Input field for user's birthdate
    private AutoCompleteTextView autoCompleteTextViewCountry;// Input field for user country

    /**
     * Radio group and radio buttons
     **/
    private RadioGroup genderRadioGroup;// Radio group of {male,female} radio buttons
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    private ProgressBar progressBar;

    /**
     * Checkbox, button
     **/
    CheckBox termsCheckBox;// Checkbox used to confirm user agreement on terms and conditions
    Button createBttn;// Button used to submit " Register user" form
    FloatingActionButton setting;

    private FloatingActionButton backBttn;

    /**
     * Firebase data
     **/
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userEmail;// String extracted to validate user email
    private boolean userSelectedLanguage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);
        setDefaultLanguage();

        setLayoutHeight();

        /** Initialising views **/
        logInInstead = (TextView) findViewById(R.id.txtLoginInInstead);
        passConditions = (TextView) findViewById(R.id.textViewPassConditions);
        terms = (TextView) findViewById(R.id.txtTerms);

        nameField = (EditText) findViewById(R.id.nameTextField);
        emailField = (EditText) findViewById(R.id.emailTextField);
        passField = (EditText) findViewById(R.id.passwordTextField);
        dateField = (EditText) findViewById(R.id.birthDateTextField);
        autoCompleteTextViewCountry = (AutoCompleteTextView) findViewById(R.id.countryTextField);

        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        radioButtonFemale = (RadioButton) findViewById(R.id.radioButtonFemale);

        termsCheckBox = (CheckBox) findViewById(R.id.termsCheckBox);
        createBttn = (Button) findViewById(R.id.createButton);
        setting = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        backBttn = (FloatingActionButton)findViewById(R.id.floatingActionButtonBack);

        mAuth = FirebaseAuth.getInstance();
        /** Finished initialising views **/


        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        /** setting style for txtLoginInInstead text view **/
        //logInInstead = (TextView)findViewById(R.id.txtLoginInInstead);
        logInInstead.setPaintFlags(logInInstead.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        /** Finished setting style for txtLoginInInstead text view **/


        /** setting style for terms and conditions text view **/
        //terms = (TextView)findViewById(R.id.txtTerms);
        terms.setPaintFlags(terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        /** Finished setting style for terms and conditions  text view **/


        /** Working on changing create button color on terms and conditions box checked **/
        AnimatorSet colorTransitionAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bttn_color_switch);
        colorTransitionAnimator.setTarget(createBttn);

        termsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    colorTransitionAnimator.start();
                    createBttn.setEnabled(true);
                } else {
                    if (SDK_INT >= Build.VERSION_CODES.O) {
                        colorTransitionAnimator.reverse();
                    } else {
                        createBttn.setBackgroundColor(Color.parseColor("#BDBDBD")); // Grey color
                    }
                    createBttn.setEnabled(false); // Disable button click
                }
            }
        });

        /** Finished working on changing create button color on terms and conditions box checked **/

        /** Working on password mechanism **/

        passField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String password = passField.getText().toString().trim();
                    Boolean passwordValidity = isValidPassword(password);

                    if (passwordValidity == false && !password.isEmpty()) { // password is invalid and the field is not empty
                        passConditions.setAlpha(0f); // Set initial alpha to 0 (completely transparent)
                        passConditions.setVisibility(View.VISIBLE); // Set visibility to visible before starting the animation
                        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(passConditions, "alpha", 0f, 1f);
                        fadeIn.setDuration(250); // Duration in milliseconds (1 second in this example)
                        fadeIn.start();

                    } else {
                        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(passConditions, "alpha", 1f, 0f);
                        fadeOut.setDuration(250); // Duration in milliseconds (1 second in this example)
                        fadeOut.start();
                    }
                }
            }
        });

        /** Fininshed working on password mechanism **/

        /** Working on AutoCompletetTextView **/
        String currentLocale = getCurrentLocale();
        List<String> countryList = new ArrayList<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (!TextUtils.isEmpty(locale.getDisplayCountry())) {
                String countryName = locale.getDisplayCountry(new Locale(currentLocale));
                if (!countryList.contains(countryName)) {
                    countryList.add(countryName);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_style, countryList);
        autoCompleteTextViewCountry.setThreshold(1);
        autoCompleteTextViewCountry.setAdapter(adapter);

        autoCompleteTextViewCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = autoCompleteTextViewCountry.getText().toString();

                    ListAdapter listAdapter = autoCompleteTextViewCountry.getAdapter();
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    autoCompleteTextViewCountry.setText("");
                }
            }
        });
        /** Finished working on AutoCompletetTextView **/

        /** Working on date picker **/
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        // Handle the selected date here
                        // 'selection' contains the selected date in milliseconds
                        // You can convert it to a desired format using SimpleDateFormat or other methods
                        // As seen below:
                        Date selectedDate = new Date(selection);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = sdf.format(selectedDate);
                        // Now 'formattedDate' contains the selected date in the desired format

                        if (currentLocale.equals("en"))
                            dateField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.date_chosen, 0);
                        else
                            dateField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.date_chosen, 0, 0, 0);

                        dateField.setText(formattedDate);
                    }
                });
                datePicker.show(getSupportFragmentManager(), datePicker.toString());
            }
        });
        /** Finished working on date picker **/

        /** Working on email validation and styling **/
        emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    userEmail = emailField.getText().toString().trim();
                    Boolean emailValid = isValidEmail(userEmail);

                    if (emailValid.toString().equals("false") && !userEmail.isEmpty()) {
                        Toast.makeText(MainActivity.this, getString(R.string.toastInvalidEmail), Toast.LENGTH_SHORT).show();
                        if (currentLocale.equals("en"))
                            emailField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.email_invalid, 0);
                        else
                            emailField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_invalid, 0, 0, 0);

                    } else if (userEmail.isEmpty()) {
                        if (currentLocale.equals("en"))
                            emailField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.email, 0);
                        else
                            emailField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email, 0, 0, 0);

                    } else {
                        if (currentLocale.equals("en"))
                            emailField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.email_valid, 0);
                        else
                            emailField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_valid, 0, 0, 0);

                    }
                }
            }
        });

        /** Finished working on email validation **/

        /** Working on events that take place after "create account button is presses **/
        createBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBttn.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                String name, email, country, dob, password, gender;
                int selectedRadioButtonID;
                name = nameField.getText().toString().trim();
                email = emailField.getText().toString().trim().toLowerCase();
                country = autoCompleteTextViewCountry.getText().toString().trim();
                dob = dateField.getText().toString().trim();
                password = passField.getText().toString().trim();
                selectedRadioButtonID = genderRadioGroup.getCheckedRadioButtonId();

                if (!checkIfFieldsAreEmpty() && selectedRadioButtonID != -1 && isValidEmail(email) && isValidPassword(password)) { // If all fields are completed and valid

                    //Get value of the selected radio button
                    if (selectedRadioButtonID == R.id.radioButtonMale) gender = "Male";
                    else gender = "Female";

                    // Reference to the collection to count documents from
                    CollectionReference collectionRef = db.collection("users");

                    // Query to get the count of documents in the collection to use in setting the ID
                    collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // Get the count of documents in the collection
                            int count = queryDocumentSnapshots.size();

                            // Use the count as the title for the next document
                            String nextDocumentTitle = (String.valueOf(count + 1));

                            // Create a new user data map
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("country", country);
                            user.put("DOB", dob);
                            user.put("gender", gender);
                            user.put("points", 0);


                            // When user is done entering info, redirect to username selection page
                            // Data is passed here (map and user ID)
                            Intent intent = new Intent(MainActivity.this, UserNameActivity.class);
                            intent.putExtra("userMap", (Serializable) user);
                            intent.putExtra("nextDocumentTitle", nextDocumentTitle);
                            intent.putExtra("password", password);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                            createBttn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else if ((!isValidPassword(password) || !isValidEmail(email)) && !name.isEmpty() && !email.isEmpty() && !country.isEmpty() && !dob.isEmpty() && !password.isEmpty()) { // Email or password fields are not valid
                    Toast.makeText(MainActivity.this, getString(R.string.toastEmailPasswordValid), Toast.LENGTH_SHORT).show();
                    createBttn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                } else { // Not all fields are completed
                    Toast.makeText(MainActivity.this, getString(R.string.toastFillAllFields), Toast.LENGTH_SHORT).show();
                    createBttn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        /** Finished working on events that take place after "create account button is pressed **/

        /** Working on styling the create button to add dinking effect when presses **/

        createBttn.setOnTouchListener(new View.OnTouchListener() {
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

        /** Working on what happens if "Login instead is clicked **/
        logInInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, signIn.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        });
        /** Finished working on what happens if "Login instead is clicked **/

        /** Working on styling the "log in instead" to add sinking effect when pressed **/

        logInInstead.setOnTouchListener(new View.OnTouchListener() {
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

        /** Working on displaying terms and conditions dialog box **/
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Once "Terms and Conditions" is clicked, we call method that shows the dialog box
                showTermsAndConditionsDialog();
            }
        });

        /** Finished working on displaying terms and conditions dialog box **/


        /** Working on listener for floating action button **/
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverlayMenu();
            }
        });
    }

    // Method used to verify user email is real
    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // Used to validate a password
    public boolean isValidPassword(String password) {

        // Minimum 8 characters, at least one number, one special character, and one capital letter
        String passwordRegex = "^(?=.*[0-9])(?=.*[!@#$%^&*_?])(?=.*[A-Z]).{8,}$";

        return password.matches(passwordRegex);
    }

    // Used to hide the keyboard and clear focus
    public void hideKeyboardAndClearFocus() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
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

    // Checks if all EditText fields in the register page are filled or not
    public boolean checkIfFieldsAreEmpty() {
        EditText[] editTextArray = new EditText[]{nameField, emailField, autoCompleteTextViewCountry, dateField, passField};
        Boolean isAnyFieldEmpty = false;

        for (EditText editText : editTextArray) {
            if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                // Means that this EditText is empty
                isAnyFieldEmpty = true;
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(8, getResources().getColor(android.R.color.holo_red_dark)); // Set border color
                drawable.setCornerRadius(50); // Set corner radius, adjust as needed
                drawable.setColor(getResources().getColor(android.R.color.white)); // Set background color, if needed
                editText.setBackground(drawable);
                break; // Exit the loop if any field is empty
            }
        }

        return isAnyFieldEmpty;
    }

    // Used to display and customise the terms and conditions dialog box
    private void showTermsAndConditionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.terms_and_conditions, null);

        // Find the TextView inside the dialog layout
        TextView termsTextView = dialogView.findViewById(R.id.termsAndConditionsTextView);

        // Making the TextView scrollable
        termsTextView.setMovementMethod(new ScrollingMovementMethod());

        builder.setView(dialogView)
                .setTitle("Terms and Conditions")
                .setPositiveButton("Close", (dialog, which) -> {
                    // Handle agreement logic if needed
                })
                .show();
    }

    // Shows the overlay menu
    public void showOverlayMenu() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.overlay_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent background
        dialog.getWindow().setGravity(Gravity.CENTER); // Centered on screen

        // Initialize menu items
      //  Button buttonOption1 = dialog.findViewById(R.id.leaderboardButton);
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

    // Returns current locale being used
    public String getCurrentLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLanguage = preferences.getString("selectedLanguage", "en");

        return selectedLanguage;

    }


    // Restarts the app
    public void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
        overridePendingTransition(R.anim.nothing_slide, R.anim.slide_out);
    }


    public void setLayoutHeight(){

        ImageView logoView = findViewById(R.id.logoView);
       // logoView.setVisibility(View.INVISIBLE);

        ConstraintLayout constraintLayout = findViewById(R.id.rootLayout); // Replace with your actual ID

// Get the display metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

// Calculate the percentage of screen width and height you want (e.g., 80% and 40%)
        float percentageOfScreenWidth = 0.91f;
        float percentageOfScreenHeight = 0.46f;

// Calculate the desired width and height in pixels
        int desiredWidthInPixels = (int) (displayMetrics.widthPixels * percentageOfScreenWidth);
        int desiredHeightInPixels = (int) (displayMetrics.heightPixels * percentageOfScreenHeight);

// Find the views
        LinearLayout inputForm = findViewById(R.id.inputForm); // Replace with your actual ID
        TextView formTitle = findViewById(R.id.formTitle); // Replace with your actual ID
        Button createButton = findViewById(R.id.createButton); // Replace with your actual ID

// Set layout parameters for inputForm
        ConstraintLayout.LayoutParams paramsInputForm = (ConstraintLayout.LayoutParams) inputForm.getLayoutParams();
        paramsInputForm.width = desiredWidthInPixels;
        paramsInputForm.height = desiredHeightInPixels;

        inputForm.setLayoutParams(paramsInputForm);

    }


}