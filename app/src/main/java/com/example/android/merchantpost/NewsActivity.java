package com.example.android.merchantpost;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.merchantpost.data.NewsContract;
import com.example.android.merchantpost.sync.NewsSyncUtils;

public class NewsActivity extends AppCompatActivity implements
        NewsAdapter.NewsAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    /*
     * The columns of data that we are interested in displaying within our NewsActivity's list of
     * news data.
     */
    public static final String[] MAIN_NEWS_PROJECTION = {
            NewsContract.NewsEntry.COLUMN_TITLE,
            NewsContract.NewsEntry.COLUMN_DATE,
            NewsContract.NewsEntry.COLUMN_AUTHOR,
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_NEWS_TITLE = 0;
    public static final int INDEX_NEWS_DATE = 1;
    public static final int INDEX_NEWS_AUTHOR = 2;

    /*
     * This ID will be used to identify the Loader responsible for loading our news data. In
     * some cases, one Activity can deal with many Loaders.
     */
    private static final int ID_NEWS_LOADER = 44;

    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    private ProgressBar mLoadingIndicator;

    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getSupportActionBar().setElevation(0f);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations.
         */
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list
         * will have the same size.
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * Set mNewsAdapter equal to a new NewsAdapter.
         * The NewsAdapter is responsible for linking our news data with the Views that
         * will end up displaying our news data.
         */
        mNewsAdapter = new NewsAdapter(this, this);

        /*
         * Use mRecyclerView.setAdapter and pass in mNewsAdapter.
         * Setting the adapter attaches it to the RecyclerView in our layout.
         */
        mRecyclerView.setAdapter(mNewsAdapter);

        showLoading();

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader.
         */
        getSupportLoaderManager().initLoader(ID_NEWS_LOADER, null, this);

        // NewsSyncUtils's initialize method instead of startImmediateSync
        NewsSyncUtils.initialize(this);

    }

    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     */
    private void showNewsDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the news View and error
     * message.
     */
    private void showLoading() {
        /* Then, hide the news data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * This method is overridden by the NewsActivity class in order to handle RecyclerView item
     * clicks.
     */
    @Override
    public void onClick(String newsItem) {
        Context context = this;
        Toast.makeText(context, newsItem, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_NEWS_LOADER:

                /* URI for all rows of news data in our news table */
                Uri newsQueryUri = NewsContract.NewsEntry.CONTENT_URI;

                /* Sort order: Descending by date */
                String sortOrder = NewsContract.NewsEntry.COLUMN_DATE + " DESC";

                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our news table.
                 */
                String selection = NewsContract.NewsEntry.COLUMN_DATE;

                return new android.support.v4.content.CursorLoader(this,
                        newsQueryUri,
                        MAIN_NEWS_PROJECTION,
                        selection,
                        null,
                        sortOrder

                );

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    /**
     * Called when a previously created loader has finished its load.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNewsAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showNewsDataView();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mNewsAdapter.swapCursor(null);
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
}

