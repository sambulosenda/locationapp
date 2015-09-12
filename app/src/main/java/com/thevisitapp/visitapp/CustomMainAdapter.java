package com.thevisitapp.visitapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rafa on 8/10/15.
 */
public class CustomMainAdapter extends ArrayAdapter<String>{

    private final Context context;
    private final ArrayList<String> values;

    public CustomMainAdapter(Context context, ArrayList<String> values) {

        super(context, R.layout.row_layout, values);
        for(int i = 0; i < values.size(); i++){
            System.out.println("PRINTING OUT VALUES " + values.get(i));
        }
        this.context = context;
        this.values = values;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values.get(position));
        Typeface roboto = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Bold.ttf");
        textView.setTypeface(roboto);


        return rowView;
    }
}
