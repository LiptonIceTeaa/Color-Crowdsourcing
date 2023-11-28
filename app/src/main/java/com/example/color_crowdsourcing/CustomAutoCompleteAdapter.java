package com.example.color_crowdsourcing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

public class CustomAutoCompleteAdapter extends ArrayAdapter<String> {

    public CustomAutoCompleteAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Customize the appearance of the selected item (collapsed AutoCompleteTextView)
        // You can inflate a custom layout here for the selected item if needed
        return super.getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Customize the appearance of each dropdown item
        // You can inflate a custom layout here for the dropdown items

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.spinner_style, parent, false);
        }

        // Customize the background color of the dropdown item here
        convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));

        // Customize the view (e.g., set text, images, etc. based on the item at 'position')
        int desiredWidthInPixels = 8/* Set your desired width in pixels */;
        convertView.getLayoutParams().width = desiredWidthInPixels;

        return convertView;
    }
}
