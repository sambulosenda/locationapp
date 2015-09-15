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
 * Created by Rafa on 9/13/15.
 */
public class PlacesListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    private final ArrayList<String> summaries;

    public PlacesListAdapter(Context context, ArrayList<String> values, ArrayList<String> summaries) {
        super(context, R.layout.row_layout_places, values);
        this.context = context;
        this.values = values;
        this.summaries = summaries;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout_places, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView summary = (TextView) rowView.findViewById(R.id.summary);

        textView.setText(values.get(position));

        Typeface roboto = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Bold.ttf");

        String fullSummary = summaries.get(position);
        String summaryPreview = "";


        int count = 0;
        int i = 0;
        String temp = "";

        summaryPreview = formatSummary(fullSummary);
        summary.setText(summaryPreview);
        textView.setTypeface(roboto);



        return rowView;
    }

    private String formatSummary(String fullSummary) {
        String summaryPreview = "";
        int count = 0;
        int stringCount = 0;
        int i = 0;
        String temp = "";
        for(int j = 0; j < fullSummary.length(); i++){
            if(fullSummary.charAt(i) == ' '){
                stringCount++;
            }
        }

        if(stringCount < 10){
            summaryPreview = fullSummary + "...";
            return summaryPreview;
        }else{
            while(count != 10){
                temp += fullSummary.charAt(i);
                if(fullSummary.charAt(i) == ' '){
                    summaryPreview += " " + temp;
                    count++;
                    temp = "";
                }
                if(count == 10){
                    summaryPreview += "...";
                }
                i++;
            }
        }

        return summaryPreview;
    }
}
