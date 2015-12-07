package com.example.bubblesmasher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// To store login user name
	String strLoginName = "";
	// To store login user score
	static String strLoginScore = "";
	// To store login id
	String strLoginId = "";
	// To store users new score
	static String strnewScore = "";
	// To store login user's password
	String strLoginPassword = "";
	// To show process
	static ProgressDialog progressDialog;
	// Api key to get and send data to the server
	String apikey = "apiKey=fTaISYKI8is6wNp-qrB-cS1MmNGfisNm";
	static boolean getresponce = false;
	static Button btnsignup;
	TextView txtvLoginName;
	static TextView txtvScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtvLoginName = (TextView) findViewById(R.id.textViewLoginName);
		txtvLoginName.setText(strLoginName);
		txtvScore = (TextView) findViewById(R.id.textViewScore);
		txtvScore.setText(strLoginScore);
		btnsignup = (Button) findViewById(R.id.btnsignup);
		btnsignup.setTag(1);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Button Click method
	public void playGame(View v) {
		// Redirect to the GameMainActivity
		Intent intent = new Intent(this, GameMainActivity.class);
		// Condition to check user did login or not
		if (!strLoginName.equals(""))
			// Requesting data
			startActivityForResult(intent, 2);
		else
			startActivity(intent);
	}

	public void highScore(View v) {
		if (isInternetOn()) {
			Intent intent = new Intent(this, HighScoreActivity.class);
			startActivity(intent);
		} else
			Toast.makeText(this, " You are not Connected with internet ", Toast.LENGTH_LONG).show();

	}

	public void LoginClick(View v) {
		if (isInternetOn()) {
			// Check user did login or not
			if (strLoginName.isEmpty()) {
				Intent intent = new Intent(this, Login.class);
				// Requesting data
				startActivityForResult(intent, 1);
			} else {
				strLoginName = "";
				txtvLoginName.setText("");
				strLoginScore = "";
				strnewScore = "";
				txtvScore.setText("");
				Button button = (Button) findViewById(R.id.btnlogin);
				button.setText("Login");
				btnsignup.setText("Sign Up");
				btnsignup.setVisibility(View.VISIBLE);
				btnsignup.setTag(1);
			}
		} else
			Toast.makeText(this, " You are not Connected with internet ", Toast.LENGTH_LONG).show();
	}

	public void SignUpClick(View v) {
		getresponce = false;
		// Check button tag to call method, tag=1 then call sign activity and if
		// tag=2 then update score
		if (btnsignup.getTag().equals(2)) {
			Log.i("Update score", "ready for update score");
			if (isInternetOn()) {
				progressDialog = new ProgressDialog(this);
				try {
					// Call update score
					new UpdateUserScore().execute().get();
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
				CheckResponce();

			} else
				Toast.makeText(this, " You are not Connected with internet ", Toast.LENGTH_LONG).show();
		} else {
			if (isInternetOn()) {
				Intent intent = new Intent(this, SignUp.class);
				// Requesting data
				startActivityForResult(intent, 3);
			} else
				Toast.makeText(this, " You are not Connected with internet ", Toast.LENGTH_LONG).show();
		}
	}

	// Create method to check response has been got or not
	public static boolean CheckResponce() {
		if (!getresponce)
			CheckResponce();
		else {
			strnewScore = "";
			txtvScore.setText(strLoginScore);
			getresponce = false;
			btnsignup.setVisibility(View.GONE);
		}
		return true;
	}

	public final boolean isInternetOn() {
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

	// Method to get requested data from other activities
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// RequestCode 1 is for login activity
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				strLoginName = data.getStringExtra("loginname");
				Log.i("login name", strLoginName);
				strLoginScore = data.getStringExtra("loginscore");
				Log.i("login score", strLoginScore);
				strLoginId = data.getStringExtra("loginid");
				Log.i("MainActivity login Value", strLoginName);
				txtvLoginName.setText(strLoginName);
				txtvScore.setText(strLoginScore);
				strLoginPassword = data.getStringExtra("loginpassword");
				Button button = (Button) findViewById(R.id.btnlogin);
				button.setText("Logout");
				btnsignup.setVisibility(View.GONE);
			}
		}
		// RequestCode 2 is for game activity

		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				strnewScore = "";
				strnewScore = data.getStringExtra("newscore");
				Log.i("New Game Score", strnewScore);
				int newgamescore = Integer.parseInt(strnewScore);
				int gamescore = Integer.parseInt(strLoginScore);
				Log.i("User Game Score", strLoginScore);
				if (newgamescore > gamescore && !strLoginName.equals("")) {
					btnsignup.setVisibility(View.VISIBLE);
					btnsignup.setText("Update Score");
					btnsignup.setTag(2);
				} else {
					btnsignup.setVisibility(View.GONE);
				}
			}
		}

		// RequestCode 3 is for signup activity
		if (requestCode == 3) {
			if (resultCode == RESULT_OK) {
				strLoginName = data.getStringExtra("loginname");
				Log.i("login name", strLoginName);
				strLoginScore = data.getStringExtra("loginscore");
				Log.i("login score", strLoginScore);
				strLoginId = data.getStringExtra("loginid");
				Log.i("MainActivity Value", strLoginName);
				txtvLoginName.setText(strLoginName);
				txtvScore.setText(strLoginScore);
				strLoginPassword = data.getStringExtra("loginpassword");
				Button button = (Button) findViewById(R.id.btnlogin);
				button.setText("Logout");
				btnsignup.setVisibility(View.GONE);
			}

		}
	}

	// Create class to perform https request operation get update server data by
	// using api
	private class UpdateUserScore extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// Base api url
			String https_url = "https://api.mongolab.com/api/1/databases/bubble/collections/Score/" + strLoginId + "?";
			https_url = https_url + apikey;
			Log.i("Update score url", https_url);
			URL url;
			try {

				url = new URL(https_url);
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestMethod("PUT");
				// Adding data parameters in json formate
				JSONObject jsonParam = new JSONObject();
				try {
					Log.i("username", strLoginName);
					Log.i("password", strLoginPassword);
					Log.i("userscore", strnewScore);
					jsonParam.put("username", strLoginName);
					jsonParam.put("password", strLoginPassword);
					jsonParam.put("userscore", strnewScore);

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(jsonParam.toString());
				wr.flush();
				wr.close();

				try {
					// Https response status code
					int status = con.getResponseCode();
					Log.i("status", String.valueOf(status));
					// Create condition to defining errors
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
						JSONObject jsonobj = new JSONObject(sb.toString());
						Log.i("Object Length", "" + jsonobj.length());
						progressDialog.cancel();
						if (jsonobj.length() == 4) {
							// Store new updated score
							strLoginScore = (String) jsonobj.get("userscore");
							getresponce = true;

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
				e1.printStackTrace();
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
