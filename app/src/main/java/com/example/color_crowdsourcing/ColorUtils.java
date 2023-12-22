package com.example.color_crowdsourcing;

import android.content.Context;
import android.content.res.Resources;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ColorUtils {

    public static List<ColorModel> readColorsFromJson(Context context, int resourceId) {
       // Toast.makeText(context,"In json reader", Toast.LENGTH_SHORT).show();
        List<ColorModel> colorList = new ArrayList<>();
        try {
            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(resourceId);

            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                String hexValue = jsonReader.nextString();
                colorList.add(new ColorModel(name, hexValue));
            }
            jsonReader.endObject();
           // Toast.makeText(context,"In json reader, length: "+colorList.size(), Toast.LENGTH_SHORT).show();


            inputStream.close();
        } catch (IOException e) {
            Log.e("ColorUtils", "Error reading JSON file", e);
        }
        return colorList;
    }
}
