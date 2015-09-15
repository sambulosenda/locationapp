package com.thevisitapp.visitapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SeriesActivity extends ActionBarActivity {
    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_destination);


        ArrayList<String> seriesIds;
        ArrayList<String> placesIds;
        Bundle extras = getIntent().getExtras();

        seriesIds = extras.getStringArrayList("nextSeries");
        placesIds = extras.getStringArrayList("nextPlaces");


        String name = extras.getString("name");
        Log.d("ADACTIVITY NAME", name);

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new Series().execute(seriesIds, placesIds);
    }

    public class Series extends AsyncTask<ArrayList<String>, Void, JSONObject>{
        protected JSONObject doInBackground(ArrayList<String>...lists){

            String myUrl = "http://thevisitapp.com/api/series/read?identifiers=";
            ArrayList<String> seriesIds = lists[0];
            Log.d("SERIES IDS IN AD", seriesIds.toString());

            myUrl = formatUrl(myUrl, seriesIds);

            Log.d("AD URL", myUrl);
            HttpRequest request = new HttpRequest();

            Log.d("GETTING JSON FROM URL", request.getJSONFromUrl(myUrl).toString());
            return request.getJSONFromUrl(myUrl);
        }

        protected void onPreExecute(){
            Toast.makeText(SeriesActivity.this, "loading", Toast.LENGTH_LONG);
            setProgressBarIndeterminateVisibility(true);
        }
        protected void onPostExecute(JSONObject result){
            ArrayList<String> modelNames = new ArrayList<>();

            Log.d("JSON RESPONSE", result.toString());

            final ArrayList<JSONObject> modelsObjectList = new ArrayList<>();

            try {
                JSONObject info = result.getJSONObject("info");
                JSONArray models = info.getJSONArray("models");

                for(int i = 0; i < models.length(); i++){
                    modelsObjectList.add(models.getJSONObject(i));
                }

                for(int i = 0; i < modelsObjectList.size(); i++){
                    modelNames.add(modelsObjectList.get(i).getString("name"));
                }

            } catch(JSONException e ){
                Log.d("JSONEXCEPTION", e.getMessage().toString());
            }

            //TODO found problem, model names for whatever reason is passing in the names from previous activity
            Log.d("MODEL NAMES", modelNames.toString());
            Toast.makeText(SeriesActivity.this, modelNames.toString(), Toast.LENGTH_LONG).show();

            mList = (ListView) findViewById(android.R.id.list);
            CustomMainAdapter mainAdapter = new CustomMainAdapter(SeriesActivity.this, modelNames);
            mList.setAdapter(mainAdapter);


            setProgressBarIndeterminateVisibility(false);
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                String name;
                Long id;
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    JSONObject passingObject = modelsObjectList.get(position);
                    ArrayList<String> nextPlacesList = new ArrayList<>();



                    try {


                        if(passingObject.optJSONArray("places") == null){
                            Log.v("WE ARE IN THE LOOP", "in the loop");
                            ArrayList<String> nextSeriesList = new ArrayList<>();
                            name = passingObject.getString("name");
                            id = passingObject.getLong("id");
                            JSONArray series = passingObject.getJSONArray("series");
                            for(int i = 0; i < series.length(); i++){
                                nextSeriesList.add(series.getString(i));
                            }

                            Log.d("NEXT ACTIVITY NAME", name);
                            System.out.println("NEXT ACTIVITY ID " + id);
                            Log.d("NEXT SERIES LIST", nextSeriesList.toString());

                            Intent intent = new Intent(SeriesActivity.this, SecondSeriesActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("name", name);
                            intent.putExtra("nextSeries", nextSeriesList);
                            startActivity(intent);
                            return;
                        }

                        Log.v("SHOULDN'T BE HERE", "this is bad");
                        JSONArray places = passingObject.getJSONArray("places");
                        for(int i = 0; i < places.length(); i++){
                            nextPlacesList.add(places.getString(i));

                        }

                        name = passingObject.getString("name");
                        id = passingObject.getLong("id");
                        Log.d("NEXT ACTIVITY NAME", name);
                        System.out.println("NEXT ACTIVITY ID " + id);
                        Log.d("NEXT SERIES LIST", nextPlacesList.toString());

                    } catch(JSONException e){
                        Log.d("JSONEXCEPTION", e.getMessage());
                    }

                    Intent intent = new Intent(SeriesActivity.this, PlacesListActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("placesList", nextPlacesList);
                    startActivity(intent);
                }
            });
        }
    }

    private String formatUrl(String myUrl, ArrayList<String> seriesIds) {
        int count = 0;
        //adds comma after every series but the last one
        for(int i = 0; i < seriesIds.size(); i++){
            myUrl += seriesIds.get(i);
            if(count != seriesIds.size() -1){
                myUrl += ",";
            }
            count++;
        }
        return myUrl;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_after_destination, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}