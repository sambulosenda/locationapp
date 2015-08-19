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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AfterDestinationActivity extends ActionBarActivity {
    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_destination);

        ArrayList<String> seriesIds = new ArrayList<>();
        ArrayList<String> placesIds = new ArrayList<>();
        Bundle extras = getIntent().getExtras();

        seriesIds = extras.getStringArrayList("nextSeries");
        placesIds = extras.getStringArrayList("nextPlaces");

        String name = extras.getString("name");


        getSupportActionBar().setTitle(name);


        new Series().execute(seriesIds, placesIds);
    }

    public class Series extends AsyncTask<ArrayList<String>, Void, JSONObject>{
        protected JSONObject doInBackground(ArrayList<String>...lists){

            String myUrl = "http:/thevisitapp.com/api/series/read?identifiers=";
            ArrayList<String> seriesIds = lists[0];
            ArrayList<String> placesIds = lists[1];

            int count = 0;
            //adds comma after every series but the last one
            for(int i = 0; i < seriesIds.size(); i++){
                myUrl += seriesIds.get(i);
                if(count != seriesIds.size() -1){
                    myUrl += ",";
                }
                count++;
            }

            Log.d("AD URL", myUrl);
            HttpRequest request = new HttpRequest();

            return request.getJSONFromUrl(myUrl);
        }

        protected void onPostExecute(JSONObject result){
            ArrayList<String> modelNames = new ArrayList<>();

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

            mList = (ListView) findViewById(android.R.id.list);
            CustomMainAdapter mainAdapter = new CustomMainAdapter(AfterDestinationActivity.this, modelNames);
            mList.setAdapter(mainAdapter);



            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                String name;
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(AfterDestinationActivity.this, "List View row Clicked at"
                            + position, Toast.LENGTH_SHORT).show();

                    JSONObject passingObject = modelsObjectList.get(position);
                    ArrayList<String> nextSeriesList = new ArrayList<>();
                    ArrayList<String> nextPlacesList = new ArrayList<>();


                    try {
                        JSONArray series = passingObject.getJSONArray("series");
                        JSONArray places = passingObject.getJSONArray("places");

                        //populate list of series we will pass to next activity
                        for(int i = 0; i < series.length(); i++){
                            nextSeriesList.add(series.getString(i));

                        }
                        for(int i = 0; i < places.length(); i++){
                            nextPlacesList.add(places.getString(i));
                        }

                        //get name that we will pass to next activity
                        name = passingObject.getString("name");
                        Log.d("NEXT ACTIVITY NAME", name);
                    } catch(JSONException e){
                        Log.d("JSONEXCEPTION", e.getMessage());
                    }

                    Intent intent = new Intent(AfterDestinationActivity.this, AfterDestinationActivity.class);
//                    intent.putExtra("series", mSeriesList.get(position));
//                    intent.putExtra("nextSeries", nextSeriesList);
//                    intent.putExtra("nextPlaces", nextPlacesList);
//                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            });
        }
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
