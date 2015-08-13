package com.thevisitapp.visitapp;

import android.app.ListActivity;
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


public class MainActivity extends ActionBarActivity {

    ListView mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        new NoNameForNow().execute();
    }

    private class NoNameForNow extends AsyncTask<String, Void, JSONObject>{
        protected JSONObject doInBackground(String...url){

            //TODO make this dynamic as it's passed in from other activity through intent
            String MYURL = " http://thevisitapp.com/api/series/read?identifiers=5096845609009152," +
                                                                    "1027," +
                                                                    "5717648100818944," +
                                                                    "14030," +
                                                                    "3028," +
                                                                    "13014";

            HttpRequest request = new HttpRequest();


            return request.getJSONFromUrl(MYURL);
        }

        protected void onPostExecute(JSONObject result) {
            Log.d("JSONOBJECT RESPONSE", result.toString());
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

        }

        private ArrayList<ArrayList<String>> listPopulater(JSONObject json){


            return null;
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if(id == R.id.search){
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
