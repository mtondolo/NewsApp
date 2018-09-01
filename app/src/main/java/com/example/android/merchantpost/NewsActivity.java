package com.example.android.merchantpost;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.merchantpost.utils.JSONUtils;
import com.example.android.merchantpost.utils.NetworkUtils;

import java.net.URL;

public class NewsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        /*
           * Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list
           * will have the same size.
           * Use this setting to improve performance if you know that changes in content do not
           * change the child layout size in the RecyclerView
        */
        mRecyclerView.setHasFixedSize(true);

        /*
         * Set mNewsAdapter equal to a new NewsAdapter.
         * The NewsAdapter is responsible for linking our news data with the Views that
         * will end up displaying our news data.
         */
        mNewsAdapter = new NewsAdapter();

        /*
          * Use mRecyclerView.setAdapter and pass in mNewsAdapter.
          * Setting the adapter attaches it to the RecyclerView in our layout.
        */
        mRecyclerView.setAdapter(mNewsAdapter);

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
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the news
     * View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
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
                String[] simpleJsonWeatherData = JSONUtils
                        .getSimpleNewsStringsFromJson(NewsActivity.this, jsonWeatherResponse);
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

                mNewsAdapter.setNewsData(newsData);
            } else {
                // If the news data was null, show the error message
                showErrorMessage();
            }
        }
    }
}

