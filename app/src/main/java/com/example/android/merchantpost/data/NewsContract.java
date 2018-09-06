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

import android.provider.BaseColumns;

public class NewsContract {

    /* Inner class that defines the table contents of the news table */
    public static final class NewsEntry implements BaseColumns {

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

    }
}
