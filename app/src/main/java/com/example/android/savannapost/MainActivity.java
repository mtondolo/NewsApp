package com.example.android.savannapost;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.savannapost.utils.JsonUtils;
import com.example.android.savannapost.utils.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mNewsListTextView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mNewsListTextView = (TextView) findViewById(R.id.tv_news);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the news data. */
        loadNewsData();

    }

    /**
     * This method will tell some background method to get the weather data in the background.
     */
    private void loadNewsData() {

        //Call showNewsDataView before executing the AsyncTask
        showNewsDataView();
        new FetchNewsTask().execute();
    }

    /**
     * This method will make the View for the news data visible and
     * hide the error message.
     */
    private void showNewsDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the news data is visible */
        mNewsListTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the news
     * View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mNewsListTextView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchNewsTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

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
        protected void onPostExecute(String[] newsData) {

            //As soon as the data is finished loading, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (newsData != null) {

                // If the news data was not null, make sure the data view is visible
                showNewsDataView();

                /*
                 * Iterate through the array and append the Strings to the TextView. We add
                 * the "\n\n\n" after the String to give visual separation between each String in the
                 * TextView.
                 */
                for (String newsString : newsData) {
                    mNewsListTextView.append((newsString) + "\n\n\n");
                }
            } else {
                // If the news data was null, show the error message
                showErrorMessage();
            }
        }
    }
}

