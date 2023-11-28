package com.example.color_crowdsourcing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;


public class leaderboard extends AppCompatActivity {

     final ArrayList<user> arrayList = new ArrayList<user>();
    LeaderboardAdapter leaderboardAdapter;
    ListView listView;
    ImageView refreshList;
    private TextView textViewLeaderboard;
    private TextView textViewTotalPoints;

    ProgressBar prgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        prgBar = (ProgressBar) findViewById(R.id.progressBarListView);
        refreshList = (ImageView)findViewById(R.id.refreshListButton);
        textViewLeaderboard = (TextView)findViewById(R.id.textViewLeaderboard);
        textViewTotalPoints = (TextView)findViewById(R.id.textViewTotalPoints);





        /** Do confetti animations for leaderboard textview **/
       // ParticleSystem confetti = new ParticleSystem(this, 100, R.drawable.confetti, 3000);


        /** Finish working on confetti animations for leaderboard textview **/



        refreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.INVISIBLE);
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
                            user userObj = new user(name, country, points); // Assuming User class with appropriate fields
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


}