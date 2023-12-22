package com.example.color_crowdsourcing;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class friendList_adapter extends ArrayAdapter<user> {

    Context context;
    private ImageView flagImageView;
    private TextView nameTextView;
    private TextView pointsTextView;
    private FirebaseFirestore db;


    public friendList_adapter(Context context, ArrayList<user> users) {
        super(context, 0, users);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;


        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.friends_list_layout, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        user userObj = getItem(position);


        // Populate the views with data
        ImageView flagImageView = currentItemView.findViewById(R.id.imageViewCountry);
        // flagImageView = currentItemView.findViewById(R.id.imageViewCountry);
        TextView nameTextView = currentItemView.findViewById(R.id.textViewName);
        // nameTextView = currentItemView.findViewById(R.id.textViewName);
        TextView pointsTextView = currentItemView.findViewById(R.id.textViewPoints);
        //pointsTextView = currentItemView.findViewById(R.id.textViewPoints);
        //LinearLayout llRow = currentItemView.findViewById(R.id.linearLayoutRow);
        ImageView imageViewDelete = currentItemView.findViewById(R.id.imageViewDelete);



        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_logout_menu(nameTextView.getText().toString(), position);
            }
        });


        // Since User class has appropriate getter methods like getFlag(), getName(), getPoints()
        // Set data to the views
        nameTextView.setText(userObj.getName() + "\u00A0");
        pointsTextView.setText("\u00A0"+context.getString(R.string.txtPoints)+": " + String.valueOf(userObj.getPoints()) + "\u00A0");



        flagImageView.getLayoutParams().height = 130;
        flagImageView.getLayoutParams().width = 130;
        flagImageView.requestLayout();
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        pointsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);


        // To set the flag we need to do more stuff a bit
        String countryCode = userObj.getCountryCode();
        String glideUrl = "https://flagsapi.com/" + countryCode + "/shiny/64.png";
        Glide
                .with(context)
                .load(glideUrl)
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .into(flagImageView);

        // then return the recyclable view
        return currentItemView;
    }

    public void show_logout_menu(String usernameToDlt, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.unfriend_user_overlay_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent background
        dialog.getWindow().setGravity(Gravity.BOTTOM); // Bottom of the screen
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Set width to match_parent

        TextView noDelete = (TextView) dialog.findViewById(R.id.textViewNo);
        TextView cancelMenu = (TextView) dialog.findViewById(R.id.textViewCancelMenu);
        TextView deleteFriend = (TextView) dialog.findViewById(R.id.titleTextYes);
        ProgressBar progressBar = (ProgressBar)dialog.findViewById(R.id.progressBar);


        noDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String userNameToDelete = nameTextView.getText().toString();
                //Toast.makeText(context, "Username to delete is: " + usernameToDlt, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                deleteFriend.setVisibility(View.GONE);
                unfriendUser(usernameToDlt, position, progressBar, dialog);
                //dialog.dismiss();
            }
        });


        dialog.show();

    }

    public void unfriendUser(String targetUsername, int position, ProgressBar progressBar, Dialog dialog) {

        db = FirebaseFirestore.getInstance();
        // getting user email
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String userEmail = preferences.getString("userEmail", "none");

        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Object friendsObject = document.get("friends");
                                if (friendsObject instanceof ArrayList) {
                                    ArrayList<String> friends = (ArrayList<String>) friendsObject;

                                    String modifiedString = targetUsername.substring(0, targetUsername.length() - 1);

                                    friends.remove(modifiedString);


                                    document.getReference().update("friends", friends)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                       // removeItem(position);
                                                       // Toast.makeText(context, "Friend removed successfully: ", Toast.LENGTH_SHORT).show();
                                                        // now remove the friend from the other side too
                                                        removeFriendFromOtherSide(modifiedString, position, progressBar, dialog);

                                                    } else {
                                                        // Handle the error
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            // Handle the error
                        }
                    }
                });


    }

    // Method to remove an item from the adapter's data source
    public void removeItem(int position) {
        if (getCount() > position) {
            remove(getItem(position));
            notifyDataSetChanged();
        }
    }


    public void removeFriendFromOtherSide(String targetUsername, int position, ProgressBar progressBar, Dialog dialog) {




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
                    String currentUsername  = document.getString("username");

                    // Find the document with the specified email
                    Query query = usersCollection.whereEqualTo("username", targetUsername);

                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                // Update the friendRequests field by adding a new value
                                ArrayList<String> currentFriends  = (ArrayList<String>) document.get("friends");
                                currentFriends.remove(currentUsername);
                                //Toast.makeText(context,"Friends: "+currentFriends.toString(),Toast.LENGTH_SHORT).show();


                                // Update the document with the modified friendRequests field
                                document.getReference().update("friends", currentFriends)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Document updated successfully

                                                Toast.makeText(context,context.getString(R.string.toastUserDeleted),Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                removeItem(position);


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
            }
        });
    }

}











