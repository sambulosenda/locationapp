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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ListView mList; //this is the list that's going to be passed into the main adapter
    ArrayList<String> mSeriesList;
    JSONObject mJSONObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CallDestination().execute();

    }

    //calls destination
    private class CallDestination extends AsyncTask<String, Void, JSONObject>{
        protected JSONObject doInBackground(String...url){

            //TODO make this dynamic as it's passed in from other activity through intent
            String MYURL = " http://thevisitapp.com/api/destinations/read?identifiers=10011";

            HttpRequest request = new HttpRequest();
            return request.getJSONFromUrl(MYURL);
        }

        protected void onPreExecute(){
            setProgressBarIndeterminateVisibility(true);
        }
        //parses through json and updates the UI with the result
        protected void onPostExecute(JSONObject result) {
            Log.d("JSONOBJECT RESPONSE", result.toString());
            mSeriesList = new ArrayList<>();
            JSONArray modelsArray;
            JSONObject info;

            JSONArray seriesJSONArray;
            JSONObject modelsObjects; //snags "models" array objects in JSON response

            try{
                info = result.getJSONObject("info");
                modelsArray = info.getJSONArray("models");
                Log.d("MODELS ARRAY", modelsArray.toString());

                modelsObjects = modelsArray.getJSONObject(0);
                seriesJSONArray = modelsObjects.getJSONArray("series");
               for(int i = 0; i < seriesJSONArray.length(); i++){
                    mSeriesList.add(seriesJSONArray.get(i).toString());
               }
            } catch(JSONException e ){
                Log.d("JSONEXCEPTION", e.getMessage());
            }

            new CallDestinationSeries().execute(mSeriesList);
        }



    }

    //calls destination series
    public class CallDestinationSeries extends AsyncTask<ArrayList<String>, Void, JSONObject>{
        protected JSONObject doInBackground(ArrayList<String>...series){
            String myUrl = " http://thevisitapp.com/api/series/read?identifiers=";

            ArrayList<String> urlSeries = series[0];

            myUrl = formatUrl(myUrl, urlSeries);

            Log.d("MAIN URL", myUrl);
            HttpRequest request = new HttpRequest();
            return  request.getJSONFromUrl(myUrl);
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
            CustomMainAdapter mainAdapter = new CustomMainAdapter(MainActivity.this, modelNames);
            mList.setAdapter(mainAdapter);
            setProgressBarIndeterminateVisibility(false);

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                String name;
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    JSONObject passingObject = modelsObjectList.get(position);
                    Log.d("PASSING OBJECT", passingObject.toString());
                    ArrayList<String> nextSeriesList = new ArrayList<>();

                    try {
                        JSONArray series = passingObject.getJSONArray("series");

                        //populate list of series we will pass to next activity
                        for(int i = 0; i < series.length(); i++){
                            nextSeriesList.add(series.getString(i));

                        }
                        //get name that we will pass to next activity
                        name = passingObject.getString("name");
                        Log.d("NEXT ACTIVITY NAME", name);
                        Log.d("NEXT SERIES LIST", nextSeriesList.toString());
                    } catch(JSONException e){
                        Log.d("JSONEXCEPTION", e.getMessage());
                    }
                    Log.d("MAINACTIVITY SERIES LIST ITEMS", mSeriesList.get(position));
                    Intent intent = new Intent(MainActivity.this, SeriesActivity.class);
                    intent.putExtra("series", mSeriesList.get(position));
                    intent.putExtra("nextSeries", nextSeriesList);
                    intent.putExtra("name", name);
                    startActivity(intent);

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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
        if(id == R.id.search){
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}