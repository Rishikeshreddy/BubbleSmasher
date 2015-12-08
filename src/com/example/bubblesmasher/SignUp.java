package com.example.bubblesmasher;

import java.io.BufferedReader;
import java.io.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends Activity {
	// Create singleton hashmap object
	public static Map<String, String> UserDataMap = SingletonHashMap.getInstance();
	static boolean getresponce = false;

	static ProgressDialog progressDialog;
	static EditText username;
	static EditText password;
	JSONArray jsonArray;
	static TextView txtverror;
	// Create api string
	String apikey = "apiKey=fTaISYKI8is6wNp-qrB-cS1MmNGfisNm";
	// create username and password string
	static String strusername;
	static String strpassword;
	static String score = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		username = (EditText) findViewById(R.id.txtsignupusername);
		password = (EditText) findViewById(R.id.txtsignuppass);
		txtverror = (TextView) findViewById(R.id.txtvsignerror);
		txtverror.setTextColor(Color.RED);
		txtverror.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

	public void ClickonSignUp(View v) {
		txtverror.setText("");
		getresponce = false;
		UserDataMap.clear();
		// Create condition to check usename and password textboxes are not
		// empty
		if (!username.getText().toString().equals("") && !password.getText().toString().equals("")) {
			if (isInternetOn()) {
				progressDialog = new ProgressDialog(this);
				try {
					// Call class to get all users data from server
					new getallusers().execute().get();
					progressDialog.setMessage("Connecting, please wait...");
					progressDialog.show();
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.setCancelable(true);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!CallServiceforSignupUser()) {
					new Createnewuser().execute();
				}
			} else {
				Toast.makeText(getApplicationContext(), "You are not connected with Internet", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_LONG).show();
		}
	}

	// Create method to check response has got or not
	public static boolean CallServiceforSignupUser() {
		if (!getresponce) {
			CallServiceforSignupUser();
		} else {
			strusername = username.getText().toString();
			strpassword = password.getText().toString();
			if (checkuserisvalid()) {
				progressDialog.cancel();
				txtverror.setText("Enter username is already exists");
				return true;

			} else {
				return false;
			}

		}
		return true;
	}

	// Entered username is in hashmap or not
	public static boolean checkuserisvalid() {
		return UserDataMap.containsKey(strusername);
	}

	// Create asynctask class to get all users details
	private class getallusers extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			// Base url
			String https_url = "https://api.mongolab.com/api/1/databases/bubble/collections/Score?";
			https_url = https_url + apikey;
			URL url;
			try {
				url = new URL(https_url);
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				String urlParameters = "";
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestMethod("GET");
				try {
					Log.i("URL", https_url);
					int status = con.getResponseCode();
					Log.i("status", String.valueOf(status));
					if (status == 200) {
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
							// Store json data into jsonarray
							jsonArray = new JSONArray(sb.toString());
							JSONObject obj = new JSONObject();
							for (int i = 0; i < jsonArray.length(); i++) {
								obj = (JSONObject) jsonArray.get(i);
								// Get data from json and store in to hashmap
								String uname = (String) obj.get("username");
								String score = (String) obj.get("userscore");
								UserDataMap.put(uname, score);
							}
							Log.i("Json Map", UserDataMap.toString());
							getresponce = true;

						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						progressDialog.cancel();
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

	// Create asynctask class to create new user
	private class Createnewuser extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// Base url string
			String https_url = "https://api.mongolab.com/api/1/databases/bubble/collections/Score?";
			https_url = https_url + apikey;
			URL url;
			try {
				url = new URL(https_url);
				// Create httpsurlconnection
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestMethod("POST");
				// Create json object to set parameters
				JSONObject jsonParam = new JSONObject();
				try {
					jsonParam.put("username", strusername);
					jsonParam.put("password", strpassword);
					jsonParam.put("userscore", score);

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				// Add json parameter as https request data
				wr.writeBytes(jsonParam.toString());
				wr.flush();
				wr.close();
				try {
					// Get responce status code
					int status = con.getResponseCode();
					Log.i("status", String.valueOf(status));
					// Create condition to check errors
					if (status == 200) {
						StringBuilder sb = new StringBuilder();
						InputStreamReader in = new InputStreamReader(con.getInputStream(), Charset.defaultCharset());
						BufferedReader bufferedReader = new BufferedReader(in);
						if (bufferedReader != null) {
							int cp;
							while ((cp = bufferedReader.read()) != -1) {
								sb.append((char) cp);
							}
							bufferedReader.close();
						}
						// Create json object to parse json
						JSONObject jsonobj = new JSONObject(sb.toString());
						Log.i("Object Length", "" + jsonobj.length());
						if (jsonobj.length() == 4) {
							progressDialog.cancel();
							// Create new intent to send requested data to the
							// main activity
							Intent intent = new Intent();
							// Parse below details from the response and send to
							// the main activity
							String uname = (String) jsonobj.get("username");
							String score = (String) jsonobj.get("userscore");
							String pass = (String) jsonobj.get("password");
							JSONObject obj1 = new JSONObject();
							obj1 = (JSONObject) jsonobj.get("_id");
							String strid = (String) obj1.get("$oid");
							intent.putExtra("loginname", uname);
							intent.putExtra("loginscore", score);
							intent.putExtra("loginid", strid);
							intent.putExtra("loginpassword", pass);
							setResult(RESULT_OK, intent);
							finish();
						} else {
							progressDialog.cancel();
						}
					} else {
						progressDialog.cancel();
					}

				} catch (IOException e) {
					progressDialog.cancel();
					e.printStackTrace();
				}
			} catch (MalformedURLException e) {
				progressDialog.cancel();
				e.printStackTrace();
			} catch (IOException e) {
				progressDialog.cancel();
				e.printStackTrace();
			} catch (JSONException e1) {
				progressDialog.cancel();
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
	}

	// Method to check internet is on or not on device
	public final boolean isInternetOn() {
		// get Connectivity Manager object to check connection
		ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		// Check for network connections
		if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

			return true;
		} else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

			return false;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
