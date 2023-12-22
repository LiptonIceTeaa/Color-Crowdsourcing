package com.example.color_crowdsourcing;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

public class friendRequestsAdapter extends ArrayAdapter<user> {

    Context context;
    private ImageView flagImageView;
    private TextView nameTextView;
    private TextView pointsTextView;
    private FirebaseFirestore db;
    private String currentUsername;



    public friendRequestsAdapter(Context context, ArrayList<user> users) {
        super(context, 0, users);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //View currentItemView = convertView;
        final View[] currentItemView = {convertView};



        if (currentItemView[0] == null) {
            currentItemView[0] = LayoutInflater.from(getContext()).inflate(R.layout.friend_requests_list, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        user userObj = getItem(position);



        // Populate the views with data
        ImageView flagImageView = currentItemView[0].findViewById(R.id.imageViewCountry);
        TextView nameTextView = currentItemView[0].findViewById(R.id.textViewName);
        TextView pointsTextView = currentItemView[0].findViewById(R.id.textViewPoints);
        ImageView imageViewAcceptFriend = currentItemView[0].findViewById(R.id.imageViewAcceptRequest);
        ImageView imageCancelRequest = currentItemView[0].findViewById(R.id.imageCancelRequest);



        // There might be a case where both users send a friend request to each other, if one user accepts the friend request, the request must be removed from both
        // user's DB
        // So now we have to check whether the other other user has a friend request from the current user




        imageViewAcceptFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // There might be a case where both users send a friend request to each other, if one user accepts the friend request, the request must be removed from both
                // user's DB
                // So now we have to check whether the other other user has a friend request from the current user
                applyShimmerAnimation(currentItemView[0]);
                String usernameToAccept = nameTextView.getText().toString();
                String modifiedString = usernameToAccept.substring(0, usernameToAccept.length() - 1);
                checkIfUserAFriend(modifiedString,position);
//                acceptRequest(modifiedString,position);
            }
        });


        // Since User class has appropriate getter methods like getFlag(), getName(), getPoints()
        // Set data to the views
        nameTextView.setText(userObj.getName()+"\u00A0");
        pointsTextView.setText("\u00A0"+context.getString(R.string.txtAcceptFriendToViewPoints)+"\u00A0");



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


    // We accept on the current user's side first then we accept on the other side too
    public void acceptRequest(String usernameToAccept, int postition){

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
                    currentFriends.add(usernameToAccept);

                    // Update the document with the modified friendRequests field
                    document.getReference().update("friends", currentFriends)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Document updated successfully
                                   // Toast.makeText(context,"Friend added ! ",Toast.LENGTH_SHORT).show();
                                    // now we add the firend on the sender's side too
                                    addFriendFromSendersSide(usernameToAccept,postition);

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

public void addFriendFromSendersSide(String originalSender, int postition){


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

                // now add this username as a friend to the sender
                Query query = usersCollection.whereEqualTo("username", originalSender);
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Update the friendRequests field by adding a new value
                            ArrayList<String> currentFriends = (ArrayList<String>) document.get("friends");
                            currentFriends.add(currentUsername);

                            // Update the document with the modified friendRequests field
                            document.getReference().update("friends", currentFriends)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Document updated successfully
                                            // now we remove the current friend from the friend requests
                                            removeFromFriendRequests(originalSender,postition);



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

public void removeFromFriendRequests(String username, int position){
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
                ArrayList<String> currentFriendRequests = (ArrayList<String>) document.get("friendRequests");
                currentFriendRequests.remove(username);

                // Update the document with the modified friendRequests field
                document.getReference().update("friendRequests", currentFriendRequests)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document updated successfully

                                Toast.makeText(context,context.getString(R.string.toastFriendAdded),Toast.LENGTH_SHORT).show();
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

    // Method to remove an item from the adapter's data source
    public void removeItem(int position) {
        if (getCount() > position) {
            remove(getItem(position));
            notifyDataSetChanged();
        }
    }


    public void checkIfUserAFriend(String username,int position){

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
                    if(currentFriends.contains(username)){
                        ArrayList<String> currentFriendRequests = (ArrayList<String>) document.get("friendRequests");
                        currentFriendRequests.remove(username);
                        // Update the document with the modified friendRequests field
                        document.getReference().update("friendRequests", currentFriendRequests)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Document updated successfully
                                        Toast.makeText(context,context.getString(R.string.toastUserIsAFriend),Toast.LENGTH_SHORT).show();
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

                    }else{
                        acceptRequest(username,position);

                    }

                }
            }
        });

    }


    private void applyShimmerAnimation(final View view) {
        // Apply a shimmer effect using ViewPropertyAnimator
        view.animate()
                .alpha(0.5f) // Adjust alpha as needed
                .setDuration(1000) // Animation duration in milliseconds
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

