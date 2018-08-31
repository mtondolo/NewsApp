package com.example.android.savannapost;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.savannapost.utils.JsonUtils;
import com.example.android.savannapost.utils.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mNewsListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mNewsListTextView = (TextView) findViewById(R.id.tv_news);

        /* Once all of our views are setup, we can load the weather data. */
        loadWeatherData();

    }

    /**
     * This method will tell some background method to get the weather data in the background.
     */
    private void loadWeatherData() {
        new FetchWeatherTask().execute();
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            URL weatherRequestUrl = NetworkUtils.buildUrl();
            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);
                String[] simpleJsonWeatherData = JsonUtils
                        .getSimpleNewsStringsFromJson(MainActivity.this, jsonWeatherResponse);
                return simpleJsonWeatherData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            if (weatherData != null) {
                /*
                 * Iterate through the array and append the Strings to the TextView. We add
                 * the "\n\n\n" after the String to give visual separation between each String in the
                 * TextView.
                 */
                for (String weatherString : weatherData) {
                    mNewsListTextView.append((weatherString) + "\n\n\n");
                }
            }
        }
    }
}

