package com.example.bubblesmasher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class HighScoreActivity extends Activity {
	//Create linkedhashmap to store username and their score
	LinkedHashMap<String,String> UserDataMap = new LinkedHashMap<String,String>();
    JSONArray jsonArray;
    boolean getresponce = false;

    //API Key
	String apikey = "apiKey=fTaISYKI8is6wNp-qrB-cS1MmNGfisNm";

	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_high_score);
 
        // 1. pass context and data to the custom adapter

		try {
			new getallusers().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        DisplayData();


    }
	
	//Create methode to display data by listview
	public void DisplayData(){
		if(!getresponce){
			DisplayData();
			Log.i("DisplayData", "map is emply");
		}
		else{
			Log.i("DisplayData", "map is not emply");

   	 HighScoreAdapter adapter = new HighScoreAdapter(this, generateData());
   	 
        ListView listView = (ListView) findViewById(R.id.listview);
 
        listView.setAdapter(adapter);
		}
   }
 
	//Create methode to get arraylist
    private ArrayList<Score> generateData(){
        ArrayList<Score> scores = new ArrayList<Score>();
        for(String str : UserDataMap.keySet())
        scores.add(new Score(str,UserDataMap.get(str)));
        return scores;
    }
    
    //Create asynctask class to get all users data 
	private class getallusers extends AsyncTask<String, Void, Void> {		
		@Override
		protected Void doInBackground(String...params) {
    	String https_url = "https://api.mongolab.com/api/1/databases/bubble/collections/Score?";
	      try {
	    	  //Adding json data into url to get sorted data
				JSONObject jsonParam = new JSONObject();
				try {
					jsonParam.put("userscore", 1);
					jsonParam.put("userscore", -1);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				//Add jsonn string into url
				https_url = https_url + "s=" + jsonParam.toString() + "&" + apikey;
				Log.i("URL ", https_url);
				URL url;

		     url = new URL(https_url);
		     HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		     con.setRequestProperty("Content-Type","application/json");  
				con.setRequestMethod("GET");
				try {
					StringBuilder sb = new StringBuilder();
					InputStreamReader in = new InputStreamReader(con.getInputStream(), Charset.defaultCharset());
					BufferedReader bufferedReader = new BufferedReader(in);
					if (bufferedReader != null) {
						int cp;
						while ((cp = bufferedReader.read()) != -1) {
							sb.append((char) cp);
						}
					}
					try {
						//Store json responce into json array
						jsonArray = new JSONArray(sb.toString());
						Log.i("Json Array", jsonArray.toString());
						JSONObject obj = new JSONObject();
						for (int i=0;i<jsonArray.length();i++){
							obj = (JSONObject) jsonArray.get(i);
							//Parse json and store data into linkedhashmap
                            String uname = (String)obj.get("username");
                            String score = (String)obj.get("userscore");
    						Log.i("Json obj", obj.toString());
                            UserDataMap.put(uname,score);
						}       
						Log.i("Json Map", UserDataMap.toString());
                        getresponce=true;

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
	      } catch (MalformedURLException e) {
			     e.printStackTrace();
		      } catch (IOException e) {
			     e.printStackTrace();
		      }
return null;
}
}

}
