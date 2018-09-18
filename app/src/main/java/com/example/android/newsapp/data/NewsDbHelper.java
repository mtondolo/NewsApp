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
package com.example.android.newsapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.newsapp.data.NewsContract.NewsEntry;

/**
 * Manages a local database for news data.
 */
public class NewsDbHelper extends SQLiteOpenHelper {


    //This is the name of our database.
    public static final String DATABASE_NAME = "news.db";

    /*
     * If we change the database schema, we must increment the database version or the onUpgrade
     * method will not be called.
     *
     */
    private static final int DATABASE_VERSION = 3;

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables happens.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NEWS_TABLE =

                "CREATE TABLE " + NewsEntry.TABLE_NAME + " (" +
                        NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NewsEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        NewsEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                        NewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
