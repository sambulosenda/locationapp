package com.thevisitapp.visitapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PlacesListActivity extends ActionBarActivity {

    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        ArrayList<String> placesIds = new ArrayList<>();
        Bundle extras = getIntent().getExtras();

        Long id = extras.getLong("id");
        String name = extras.getString("name");
        placesIds = extras.getStringArrayList("placesList");


        getSupportActionBar().setTitle(name);
        //TODO certain places don't get passed in i.e. fast food
        Log.d("PLACESLISTNAME", name);

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
            final ArrayList<JSONObject> modelsObjectList = new ArrayList<>(); //snags "models" array objects in JSON response

            JSONArray modelsArray;
            JSONObject info;

            try{
                info = result.getJSONObject("info");
                modelsArray = info.getJSONArray("models");
                Log.d("MODELS ARRAY", modelsArray.toString());

                for(int i = 0; i < modelsArray.length(); i++){
                    modelsObjectList.add(modelsArray.getJSONObject(i));
                }
                for(int i = 0; i < modelsArray.length(); i++){
                    placesNames.add(modelsObjectList.get(i).getString("name"));
                }

                Log.d("MODELS OBJECTS", modelsObjectList.toString());
            } catch(JSONException e ){
                Log.d("JSONEXCEPTION", e.getMessage());
            }


            mList = (ListView) findViewById(android.R.id.list);
            CustomMainAdapter mainAdapter = new CustomMainAdapter(PlacesListActivity.this, placesNames);
            mList.setAdapter(mainAdapter);

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                String name;
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    JSONObject nextObject = modelsObjectList.get(position);
                    try {
                        name = nextObject.getString("name");
                    } catch(JSONException e ){
                        Log.d("JSON EXCEPTION", e.getMessage());
                    }
                    Intent intent = new Intent(PlacesListActivity.this, PlaceActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);

                    startActivity(intent);


                    //setProgressBarIndeterminateVisibility(false);
                }
            });
        }
    }


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
