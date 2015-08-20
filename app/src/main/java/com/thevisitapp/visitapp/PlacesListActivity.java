package com.thevisitapp.visitapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PlacesListActivity extends ActionBarActivity {
    ArrayList<String> mSeriesList;
    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        ArrayList<String> seriesIds = new ArrayList<>();
        ArrayList<String> placesIds = new ArrayList<>();
        Bundle extras = getIntent().getExtras();

        Long id = extras.getLong("id");
        String name = extras.getString("name");
        placesIds = extras.getStringArrayList("placesList");

        getSupportActionBar().setTitle(name);

        Place place = new Place();
        place.execute(placesIds);
    }

    private class Place extends AsyncTask<ArrayList<String>, Void, JSONObject>{
        protected JSONObject doInBackground(ArrayList<String>...places){
            String myUrl = "http://thevisitapp.com/api/places/read?identifiers=";
            HttpRequest request = new HttpRequest();

            ArrayList<String> placesList = places[0];


            myUrl = formatUrl(myUrl, placesList);
            Log.d("PLACES ID", myUrl);
            return request.getJSONFromUrl(myUrl);
        }

        protected void onPostExecute(JSONObject result){

            Log.d("JSONOBJECT RESPONSE", result.toString());
            ArrayList<String> placesNames = new ArrayList<>();
            JSONArray modelsArray;
            JSONObject info;

            ArrayList<String> modelsObjects = new ArrayList<>(); //snags "models" array objects in JSON response

            try{
                info = result.getJSONObject("info");
                modelsArray = info.getJSONArray("models");
                Log.d("MODELS ARRAY", modelsArray.toString());

                for(int i = 0; i < modelsArray.length(); i++){
                    placesNames.add(modelsArray.getJSONObject(i).getString("name"));
                }
                Log.d("MODELS OBJECTS", modelsObjects.toString());
            } catch(JSONException e ){
                Log.d("JSONEXCEPTION", e.getMessage());
            }

            mList = (ListView) findViewById(android.R.id.list);
            CustomMainAdapter mainAdapter = new CustomMainAdapter(PlacesListActivity.this, placesNames);
            mList.setAdapter(mainAdapter);
        }
    }

//    private class PopulatePlaces extends AsyncTask<ArrayList<String>, Void, JSONObject>{
//        protected JSONObject doInBackground(ArrayList<String>...seriesList){
//            String myUrl = "http://thevisitapp.com/api/series/read?identifiers=";
//
//            ArrayList<String> urlSeries = seriesList[0];
//            myUrl = formatUrl(myUrl, urlSeries);
//
//            HttpRequest request = new HttpRequest();
//            return request.getJSONFromUrl(myUrl);
//        }
//
//        protected void onPostExecute(JSONObject result){
//            ArrayList<String> modelNames = new ArrayList<>();
//
//            final ArrayList<JSONObject> modelsObjectList = new ArrayList<>();
//
//            try {
//                JSONObject info = result.getJSONObject("info");
//                JSONArray models = info.getJSONArray("models");
//                Log.d("MODELS BEFORE NAMES", models.toString());
//                for(int i = 0; i < models.length(); i++){
//                    modelsObjectList.add(models.getJSONObject(i));
//                }
//                for(int i = 0; i < modelsObjectList.size(); i++){
//                    modelNames.add(modelsObjectList.get(i).getString("name"));
//                }
//
//                Log.d("MODEL NAMES", modelNames.toString());
//            } catch(JSONException e ){
//                Log.d("JSONEXCEPTION", e.getMessage().toString());
//            }
//
//            mList = (ListView) findViewById(android.R.id.list);
//            CustomMainAdapter mainAdapter = new CustomMainAdapter(PlacesListActivity.this, modelNames);
//            mList.setAdapter(mainAdapter);
//
//        }
//    }

    private String formatUrl(String myUrl, ArrayList<String> urlSeries) {
        int count = 0;
        //adds comma after every series but the last one
        for(int i = 0; i < urlSeries.size(); i++){
            myUrl += urlSeries.get(i);
            if(count != urlSeries.size() -1){
                myUrl += ",";
            }
            count++;
        }
        return myUrl;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_places_list, menu);


        Bundle extras = getIntent().getExtras();
        String name = extras.getString("name");
        Long id = extras.getLong("id");

        getSupportActionBar().setTitle(name);


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
