package com.example.color_crowdsourcing;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.PreferenceManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class friendsSearchResultAdapter extends ArrayAdapter<user> {

    Context context;
    private ImageView flagImageView;
    private TextView nameTextView;
    private TextView pointsTextView;
    private FirebaseFirestore db;
    private String currentUsername;



    public friendsSearchResultAdapter(Context context, ArrayList<user> users) {
        super(context, 0, users);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //View currentItemView = convertView;

        final View[] currentItemView = {convertView};

        if (currentItemView[0] == null) {
            currentItemView[0] = LayoutInflater.from(getContext()).inflate(R.layout.search_results_friends, parent, false);
        }


        if (currentItemView[0] == null) {
            currentItemView[0] = LayoutInflater.from(getContext()).inflate(R.layout.search_results_friends, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        user userObj = getItem(position);



        // Populate the views with data
        ImageView flagImageView = currentItemView[0].findViewById(R.id.imageViewCountry);
        // flagImageView = currentItemView.findViewById(R.id.imageViewCountry);
        TextView nameTextView = currentItemView[0].findViewById(R.id.textViewName);
        // nameTextView = currentItemView.findViewById(R.id.textViewName);
        TextView pointsTextView = currentItemView[0].findViewById(R.id.textViewPoints);
        //pointsTextView = currentItemView.findViewById(R.id.textViewPoints);
        //LinearLayout llRow = currentItemView.findViewById(R.id.linearLayoutRow);
        ImageView imageViewAddFriend = currentItemView[0].findViewById(R.id.imageViewAddFriend);
        ProgressBar progressBar = currentItemView[0].findViewById(R.id.progressBar);


        // We need to check in case a user searches for themselves
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String userEmail = preferences.getString("userEmail", "none");
        if(userEmail.equals(userObj.getEmail())) {
            imageViewAddFriend.setVisibility(View.GONE);
        }

        imageViewAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewAddFriend.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                applyShimmerAnimation(currentItemView[0]);
                String usernameToAdd = nameTextView.getText().toString();
                String modifiedString = usernameToAdd.substring(0, usernameToAdd.length() - 1);
                getCurrentUserusername(modifiedString, progressBar, imageViewAddFriend);
                //imageViewAddFriend.setImageResource(R.drawable.how_to_reg);
                //checkIfUserIsFriend(modifiedString);
            }
        });


        // Since User class has appropriate getter methods like getFlag(), getName(), getPoints()
        // Set data to the views
        nameTextView.setText(userObj.getName()+"\u00A0");
        pointsTextView.setText("\u00A0"+context.getString(R.string.txtAddFriendToViewPoints) +"\u00A0");


        flagImageView.getLayoutParams().height = 130;
        flagImageView.getLayoutParams().width = 130;
        flagImageView.requestLayout();
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        pointsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);




        // To set the flag we need to do more stuff a bit
        String countryCode = userObj.getCountryCode();
        String glideUrl = "https://flagsapi.com/"+countryCode+"/shiny/64.png";
        Glide
                .with(context)
                .load(glideUrl)
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .into(flagImageView);

        // then return the recyclable view
        return currentItemView[0];
    }

    public void sendFriendInvite(String username,String currentUsername, ProgressBar progressBar, ImageView addFriend){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String userEmail = preferences.getString("userEmail", "none");
        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        // Find the document with the specified email
        Query query = usersCollection.whereEqualTo("username", username);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Update the friendRequests field by adding a new value
                    ArrayList<String> currentFriendRequests = (ArrayList<String>) document.get("friendRequests");


                    currentFriendRequests.add(currentUsername);

                    // Update the document with the modified friendRequests field
                    document.getReference().update("friendRequests", currentFriendRequests)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Document updated successfully
                                    Toast.makeText(context,context.getString(R.string.toastFriendRequestSent),Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    addFriend.setImageResource(R.drawable.how_to_reg);
                                    addFriend.setVisibility(View.VISIBLE);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                    // You can add your error handling code here
                                }
                            });
                }
            }
        });

    }

    public void getCurrentUserusername(String username, ProgressBar progressBar, ImageView addFriend){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String userEmail = preferences.getString("userEmail", "none");
        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        // Find the document with the specified email
        Query query = usersCollection.whereEqualTo("email", userEmail);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    currentUsername = document.getString("username");
                   // sendFriendInvite(username,currentUsername);

                    checkIfUserIsFriend(username, currentUsername, progressBar, addFriend);
                }
            }
        });
    }


    public void checkIfUserIsFriend(String username, String currentUsername, ProgressBar progressBar, ImageView addFriend){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String userEmail = preferences.getString("userEmail", "none");

        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        // Find the document with the specified email
        Query query = usersCollection.whereEqualTo("email", userEmail);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Update the friendRequests field by adding a new value
                    ArrayList<String> currentFriends = (ArrayList<String>) document.get("friends");


                        if (!currentFriends.contains(username)) {

                        Query query = usersCollection.whereEqualTo("username", username);

                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    // Update the friendRequests field by adding a new value
                                    ArrayList<String> currentFriendRequests = (ArrayList<String>) document.get("friendRequests");

                                        if (!currentFriendRequests.contains(currentUsername)) {
                                            sendFriendInvite(username,currentUsername, progressBar, addFriend);
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.toastFriendRequestAlreadySent), Toast.LENGTH_SHORT).show();
                                        }

                                        progressBar.setVisibility(View.GONE);
                                        addFriend.setImageResource(R.drawable.how_to_reg);
                                        addFriend.setVisibility(View.VISIBLE);


                                }
                            }
                        });


                        // getCurrentUserusername(username);
                    } else {
                        Toast.makeText(context, context.getString(R.string.toastUserIsAFriend), Toast.LENGTH_SHORT).show();

                    }



                }
            }
        });
    }


    private void applyShimmerAnimation(final View view) {
        // Apply a shimmer effect using ViewPropertyAnimator
        view.animate()
                .alpha(0.5f) // Adjust alpha as needed
                .setDuration(700) // Animation duration in milliseconds
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Reset the view properties after the animation ends
                        view.animate().alpha(1f); // Reset alpha
                    }
                })
                .start();
    }


}
