package com.thevisitapp.visitapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Rafa on 8/10/15.
 */
public class CustomMainAdapter extends ArrayAdapter<String>{

    private final Context context;
    private final String[] values;

    public CustomMainAdapter(Context context, String[] values) {
        super(context, R.layout.row_layout, values);
        this.context = context;
        this.values = values;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position]);
        Typeface roboto = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Bold.ttf");
        textView.setTypeface(roboto);


        return rowView;
    }
}
