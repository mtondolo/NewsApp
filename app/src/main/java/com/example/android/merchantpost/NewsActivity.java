package com.example.android.merchantpost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.merchantpost.data.NewsPreferences;
import com.example.android.merchantpost.utils.JsonUtils;
import com.example.android.merchantpost.utils.NetworkUtils;

import java.net.URL;

public class NewsActivity extends AppCompatActivity implements
        NewsAdapter.NewsAdapterOnClickHandler,
        LoaderCallbacks<String[]>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = NewsActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final int NEWS_LOADER_ID = 0;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

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
        mNewsAdapter = new NewsAdapter(this);

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

        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderId = NEWS_LOADER_ID;

        /*
         * From NewsActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array. (implements LoaderCallbacks<String[]>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderCallbacks<String[]> callback = NewsActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        Bundle bundleForLoader = null;

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
        Log.d(TAG, "onCreate: registering preference changed listener");

        /*
         * Register NewsActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed.
         *
         */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

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

    //If preferences have been changed, refresh the data and set the flag to false
    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "onStart: preferences were updated");
            getSupportLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    // Unregister NewsActivity as a SharedPreferenceChangedListener
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * This method is overridden by the NewsActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param newsItem The news item for the news that was clicked
     */
    @Override
    public void onClick(String newsItem) {
        Context context = this;
        Toast.makeText(context, newsItem, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id         The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle loaderArgs) {

        return new AsyncTaskLoader<String[]>(this) {

            /* This String array will hold and help cache our weather data */
            String[] mNewsData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (mNewsData != null) {
                    deliverResult(mNewsData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from News API in the background.
             *
             * @return news data from News API as an array of Strings.
             *         null if an error occurs
             */
            @Override
            public String[] loadInBackground() {

                String sources = NewsPreferences.getPreferredNewsChannel(NewsActivity.this);

                URL newsRequestUrl = NetworkUtils.buildUrl(sources);
                try {
                    String jsonNewsResponse = NetworkUtils
                            .getResponseFromHttpUrl(newsRequestUrl);
                    String[] simpleJsonNewsData = JsonUtils
                            .getSimpleNewsStringsFromJson(NewsActivity.this, jsonNewsResponse);
                    return simpleJsonNewsData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            @Override
            public void deliverResult(String[] data) {
                mNewsData = data;
                super.deliverResult(data);
            }
        };

    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNewsAdapter.setNewsData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showNewsDataView();
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        /*
         * We aren't using this method at the moment, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.news, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Launch SettingsActivity when the Settings option is clicked
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // et the preferences flag to true
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        /*
         * Set this flag to true so that when control returns to MainActivity, it can refresh the
         * data.
         *
         * This isn't the ideal solution because there really isn't a need to perform another
         * GET request just to change the units, but this is the simplest solution that gets the
         * job done for now.
         *
         */
        PREFERENCES_HAVE_BEEN_UPDATED = true;

    }
}

