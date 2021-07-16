package com.ap.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void findWeather(View v) {

        //hide keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        if(cityName.getText().toString().equals("")){
            resultTextView.setText("");
            Toast.makeText(MainActivity.this, "Please Enter a City!", Toast.LENGTH_SHORT).show();
        }
        else {

            try {
                String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
                DownloadTask task = new DownloadTask();
                task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=d18d5cf22d897c440557d1784034cd22");

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Could not find weather!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url= null;
            try {
                url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char curr = (char) data;
                    result+=curr;
                    data=reader.read();
                }
                return result.toString();

            } catch (Exception e) {
                showToast();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message= "";
                JSONObject jsonObject=new JSONObject(result);
                String weatherInfo=jsonObject.getString("weather");

                JSONArray arr =new JSONArray(weatherInfo);
                for(int i=0;i<arr.length();i++){

                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")) {
                        message=main+": "+description+"\r\n";
                    }
                }

                if (!message.equals("")) {
                    resultTextView.setText(message.toString());
                } else {
                    showToast();
                }

            } catch (JSONException e) {
                showToast();
            }
        }

        private void showToast() {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Could not find weather!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}