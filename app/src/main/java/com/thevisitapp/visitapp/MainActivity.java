package com.thevisitapp.visitapp;

import android.app.ActionBar;
import android.app.ListActivity;
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


public class MainActivity extends ActionBarActivity {

    ListView mList;
    JSONObject mJSONObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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




        //parses through json and updates the UI with the result
        protected void onPostExecute(JSONObject result) {
            Log.d("JSONOBJECT RESPONSE", result.toString());
            ArrayList<String> seriesList = new ArrayList<>();
            JSONArray modelsArray;
            JSONObject info;
            JSONObject modelsData = new JSONObject();
            JSONArray seriesJSONArray = new JSONArray();
            JSONObject modelsObjects = new JSONObject();

            try{
                info = result.getJSONObject("info");
                modelsArray = info.getJSONArray("models");
                Log.d("MODELS ARRAY", modelsArray.toString());

                modelsObjects = modelsArray.getJSONObject(0);
                seriesJSONArray = modelsObjects.getJSONArray("series");
               for(int i = 0; i < seriesJSONArray.length(); i++){
                    seriesList.add(seriesJSONArray.get(i).toString());
               }
            } catch(JSONException e ){
                Log.d("JSONEXCEPTION", e.getMessage());
            }

            new CallDestinationSeries().execute(seriesList);
        }



    }

    //calls destination series
    public class CallDestinationSeries extends AsyncTask<ArrayList<String>, Void, JSONObject>{
        protected JSONObject doInBackground(ArrayList<String>...series){
            String myUrl = " http://thevisitapp.com/api/series/read?identifiers=";

            ArrayList<String> urlSeries = series[0];

            int count = 0;
            for(int i = 0; i < urlSeries.size(); i++){
                myUrl += urlSeries.get(i);
                if(count != urlSeries.size() -1){
                    myUrl += ",";
                }
                count++;
            }
            Log.d("MY FINAL URL", myUrl);
            HttpRequest request = new HttpRequest();
            return  request.getJSONFromUrl(myUrl);
        }

        protected void onPostExecute(JSONObject result){
            ArrayList<String> modelNames = new ArrayList<String>();

            try {
                JSONObject info = result.getJSONObject("info");
                JSONArray models = info.getJSONArray("models");


                for(int i = 0; i < models.length(); i++){
                    JSONObject modelsObject = models.getJSONObject(i);
                    modelNames.add(modelsObject.getString("name").toString());

                }
            } catch(JSONException e ){
                Log.d("JSONEXCEPTION", e.getMessage().toString());
            }

            mList = (ListView) findViewById(android.R.id.list);
            CustomMainAdapter mainAdapter = new CustomMainAdapter(MainActivity.this, modelNames);
            mList.setAdapter(mainAdapter);


            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, "List View row Clicked at"
                            + position, Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent();
                //intent.putExtra("")
            });
        }
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
