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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * {@link NewsAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.support.v7.widget.RecyclerView}.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsAdapterViewHolder> {

    // Constant IDs for the ViewType for latest and for past news
    private static final int VIEW_TYPE_LATEST = 0;
    private static final int VIEW_TYPE_PAST = 1;

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
        void onClick(String date);
    }

    private boolean mUseLatestLayout;

    private Cursor mCursor;

    /**
     * Creates a NewsAdapter.
     */
    public NewsAdapter(Context context, NewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUseLatestLayout = mContext.getResources().getBoolean(R.bool.use_latest_layout);
    }

    /**
     * Cache of the children views for a news list item.
     */
    public class NewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageView;
        final TextView titleView;
        final TextView timeView;

        // Constructor for the NewsAdapter class that accepts a View as a parameter
        public NewsAdapterViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.image);
            titleView = (TextView) view.findViewById(R.id.title);
            timeView = (TextView) view.findViewById(R.id.time);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String url = mCursor.getString(NewsActivity.INDEX_NEWS_URL);
            mClickHandler.onClick(url);
        }
    }

    /**
     * This gets called when each new ViewHolder is created.
     */
    @Override
    public NewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_LATEST: {
                layoutId = R.layout.list_item_news_latest;
                break;
            }
            case VIEW_TYPE_PAST: {
                layoutId = R.layout.news_list_item;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
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

        /* Get image, title, name, date and web page from the cursor and display the values*/
        String urlToImage = mCursor.getString(NewsActivity.INDEX_NEWS_IMAGE);

        if (urlToImage.isEmpty()) {//url.isEmpty()
            Picasso.get()
                    .load(R.color.colorGrey)
                    .placeholder(R.color.colorGrey)
                    .error(R.color.colorGrey)
                    .resize(126, 78)
                    .centerCrop()
                    .into(newsAdapterViewHolder.imageView);
        } else {
            Picasso.get()
                    .load(urlToImage)
                    .placeholder(R.color.colorGrey)
                    .resize(126, 78)
                    .centerCrop()
                    .into(newsAdapterViewHolder.imageView);//this is our ImageView
        }

        String title = mCursor.getString(NewsActivity.INDEX_NEWS_TITLE);
        newsAdapterViewHolder.titleView.setText(title);

        String date = mCursor.getString(NewsActivity.INDEX_NEWS_DATE);
        newsAdapterViewHolder.timeView.setText(date);
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
     * Returns an integer code related to the type of View we want the ViewHolder to be at a given
     * position.
     */
    @Override
    public int getItemViewType(int position) {
        if (mUseLatestLayout && position == 0) {
            return VIEW_TYPE_LATEST;
        } else {
            return VIEW_TYPE_PAST;
        }
    }

    /**
     * Swaps the cursor used by the NewsAdapter for its news data.
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
