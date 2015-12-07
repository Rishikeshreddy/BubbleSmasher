package com.example.bubblesmasher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
	public static Map<String, String> UserDataMap = SingletonHashMap.getInstance();
	static ProgressDialog progressDialog;
	static EditText username;
	static EditText password;
	JSONArray jsonArray;
	static TextView txtverror;
	static boolean getresponce = false;

	String apikey = "apiKey=fTaISYKI8is6wNp-qrB-cS1MmNGfisNm";
	// final EditText edittext = (EditText) findViewById(R.id.fld_username);
	static String strusername;
	static String strpassword;
	String score = "0";
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		context = getApplicationContext();
		txtverror = (TextView) findViewById(R.id.txtvloginerror);
		txtverror.setTextColor(Color.RED);
		txtverror.setText("");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void Clickonlogin(View v) {
		txtverror.setText("");
		UserDataMap.clear();
		getresponce = false;
		// Condition to check textboxes are empty or not
		if (!username.getText().toString().equals("") && !password.getText().toString().equals("")) {
			if (isInternetOn()) {
				progressDialog = new ProgressDialog(this);
				try {
					// Call getalluser to get all users details
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
				if (CallServiceforLogin()) {
					new LoginUser().execute();
				}
			} else {
				Toast.makeText(getApplicationContext(), "You are not connected with Internet", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_LONG).show();

		}
	}

	// Create method to do so
	public static boolean CallServiceforLogin() {
		if (!getresponce) {
			CallServiceforLogin();
		} else {
			strusername = username.getText().toString();
			strpassword = password.getText().toString();
			if (checkuserisvalid()) {
				return true;
			} else {
				progressDialog.cancel();
				txtverror.setText("Please enter correct username and password");
				return false;
			}

		}
		return true;
	}

	// Check Entered username and password are correct or not with hashmap
	public static boolean checkuserisvalid() {
		if (UserDataMap.containsKey(strusername)) {
			if (strpassword.equals(UserDataMap.get(strusername)))
				return true;
			else
				return false;
		} else
			return false;
	}

	// Create asynctask class to get all users data from server
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
					// Get response status
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
							// Store json responce into json array
							jsonArray = new JSONArray(sb.toString());
							JSONObject obj = new JSONObject();
							for (int i = 0; i < jsonArray.length(); i++) {
								obj = (JSONObject) jsonArray.get(i);
								// Parse json and store data into hashmap
								String uname = (String) obj.get("username");
								String password = (String) obj.get("password");
								UserDataMap.put(uname, password);
							}
							Log.i("Json Map", UserDataMap.toString());
							getresponce = true;
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {

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

	public final boolean isInternetOn() {

		// get Connectivity Manager object to check connection
		ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

		// Check for network connections
		if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

			// if connected with internet

			return true;

		} else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

			return false;
		}
		return false;
	}

	private class LoginUser extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			String https_url = "https://api.mongolab.com/api/1/databases/bubble/collections/Score?";
			JSONObject jsonParam = new JSONObject();
			try {
				jsonParam.put("username", strusername);
				jsonParam.put("password", strpassword);
				// jsonParam.put("score", score);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			https_url = https_url + "q=" + jsonParam.toString() + "&" + apikey;
			Log.i("URL ", https_url);
			URL url;
			try {

				url = new URL(https_url);
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				String urlParameters = "";
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestMethod("GET");

				// con.getResponseCode();
				try {
					Log.i("****** Content of the URL ********", "");
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
					try {
						jsonArray = new JSONArray(sb.toString());
						System.out.println("\n\njsonArray: " + jsonArray.length());
						if (jsonArray.length() < 1) {
							progressDialog.cancel();
							txtverror.setVisibility(View.VISIBLE);
							txtverror.setText("Please enter correct username and password");

							// Toast.makeText(getApplicationContext(), "Please
							// enter correct username and password",
							// Toast.LENGTH_LONG).show();

						} else {
							progressDialog.cancel();
							// MainActivity obj = new MainActivity();
							// obj.setStrLoginName(strusername);
							JSONObject obj = new JSONObject();
							obj = (JSONObject) jsonArray.get(0);
							String uname = (String) obj.get("username");
							String strscore = (String) obj.get("userscore");
							String strpassword = (String) obj.get("password");
							JSONObject obj1 = new JSONObject();
							obj1 = (JSONObject) obj.get("_id");
							String strid = (String) obj1.get("$oid");
							Log.i("login username", uname);
							Log.i("login score", strscore);
							Log.i("login id", strid);
							Log.i("login password", strpassword);
							Intent intent = new Intent();
							intent.putExtra("loginname", uname);
							intent.putExtra("loginscore", strscore);
							intent.putExtra("loginid", strid);
							intent.putExtra("loginpassword", strpassword);
							setResult(RESULT_OK, intent);
							finish();

							// MainActivity objmain =
							// (MainActivity)getApplicationContext();
							// objmain.setStrLoginName(strusername);

						}
					} catch (JSONException e) {
						progressDialog.cancel();
						e.printStackTrace();
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
			}
			return null;
		}
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
