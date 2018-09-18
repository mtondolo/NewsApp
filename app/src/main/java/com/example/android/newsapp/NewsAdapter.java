/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.newsapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link NewsAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.support.v7.widget.RecyclerView}.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsAdapterViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final NewsAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface NewsAdapterOnClickHandler {
        void onClick(String newsItem);
    }

    private Cursor mCursor;

    /**
     * Creates a NewsAdapter.
     */
    public NewsAdapter(Context context, NewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a news list item.
     */
    public class NewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mNewsTextView;

        // Constructor for the NewsAdapter class that accepts a View as a parameter
        public NewsAdapterViewHolder(View view) {
            super(view);

            // Get a reference to this layout's TextView and save it to mNewsTextView
            mNewsTextView = (TextView) view.findViewById(R.id.tv_news_data);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         */
        @Override
        public void onClick(View v) {
            String newsItem = mNewsTextView.getText().toString();
            mClickHandler.onClick(newsItem);
        }
    }

    /**
     * This gets called when each new ViewHolder is created.
     */
    @Override
    public NewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Inflate the list item xml into a view
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.news_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        // Return a new NewsAdapterViewHolder with the above view passed in as a parameter
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new NewsAdapterViewHolder(view);

    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     */
    @Override
    public void onBindViewHolder(NewsAdapterViewHolder newsAdapterViewHolder, int position) {

        // Move the cursor to the appropriate position
        mCursor.moveToPosition(position);

        /* Read title, name and date from the cursor */
        String title = mCursor.getString(NewsActivity.INDEX_NEWS_TITLE);
        String author = mCursor.getString(NewsActivity.INDEX_NEWS_AUTHOR);
        String date = mCursor.getString(NewsActivity.INDEX_NEWS_DATE);

        String newsItem = title + " - " + author + " - " + date;

        // Display the summary that we created above
        newsAdapterViewHolder.mNewsTextView.setText(newsItem);
    }

    /**
     * This method simply returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Swaps the cursor used by the NewsAdapter for its news data.
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
