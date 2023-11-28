package com.example.color_crowdsourcing;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.color_crowdsourcing.user;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class LeaderboardAdapter extends ArrayAdapter<user> {

   // private ArrayList<user> userList;
   // private Context context;
    Context context;

    public LeaderboardAdapter(Context context, ArrayList<user> users) {
        super(context, 0, users);
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;


        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        user userObj = getItem(position);

        // Auto-incremented number starting from 1
        int number = position + 1;


        // Populate the views with data
        TextView numberTextView = currentItemView.findViewById(R.id.textViewNumber);
        ImageView flagImageView = currentItemView.findViewById(R.id.imageViewCountry);
        TextView nameTextView = currentItemView.findViewById(R.id.textViewName);
        TextView pointsTextView = currentItemView.findViewById(R.id.textViewPoints);
        ImageView trophyImageView = currentItemView.findViewById(R.id.imageTrophyCup);
        LinearLayout llRow = currentItemView.findViewById(R.id.linearLayoutRow);
        LinearLayout mainRow = currentItemView.findViewById(R.id.mainRow);

        // Load the heartbeat animation from the XML file
        Animation heartbeatAnimation = AnimationUtils.loadAnimation(context, R.anim.heartbeat);

        // Start the heartbeat animation on the ImageView
        trophyImageView.startAnimation(heartbeatAnimation);
       // heartbeatAnimation.setRepeatCount(Animation.INFINITE);



        // Since User class has appropriate getter methods like getFlag(), getName(), getPoints()
        // Set data to the views
        nameTextView.setText(userObj.getName()+"\u00A0");
        pointsTextView.setText("Points: "+String.valueOf(userObj.getPoints())+"\u00A0");

        // If row is first place
        if(number == 1){
            numberTextView.setVisibility(View.GONE);
            trophyImageView.setVisibility(View.VISIBLE);
            trophyImageView.setPadding(100,0,100,0);
            flagImageView.getLayoutParams().height = 130;
           flagImageView.getLayoutParams().width = 130;
            flagImageView.requestLayout();
            nameTextView.setTextSize(18);
            pointsTextView.setTextSize(13);
            mainRow.setBackgroundResource(R.drawable.rounded_gradient_background_linear_layout);

        }else {
            numberTextView.setText(String.valueOf(number) + ".");
        }


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
        return currentItemView;
    }
}
