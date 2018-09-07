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
package com.example.android.merchantpost.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.merchantpost.utils.NewsDateUtils;

/**
 * Defines table and column names for the news database. This class is not necessary, but keeps
 * the code organized.
 */
public class NewsContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.merchantpost";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for News.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that News
     * can handle. For instance,
     *
     *     content://com.example.android.merchantpost/news/
     *     [           BASE_CONTENT_URI         ][ PATH_NEWS ]
     *
     * is a valid path for looking at news data.
     *
     */
    public static final String PATH_NEWS = "news";

    /* Inner class that defines the table contents of the news table */
    public static final class NewsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the News table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NEWS)
                .build();

        /* Used internally as the name of our news table. */
        public static final String TABLE_NAME = "news";

        /* title is stored as a String representing headline */
        public static final String COLUMN_TITLE = "title";

        /*
         * The date column will store the UTC date that correlates to the local date for which
         * each particular news item represents.
         *
         */
        public static final String COLUMN_DATE = "date";

        /* name is stored as a String representing the news channel source name*/
        public static final String COLUMN_SOURCE = "name";

        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = NewsDateUtils.normalizeDate(System.currentTimeMillis());
            return NewsContract.NewsEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
        }

    }
}
