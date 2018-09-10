package com.example.android.merchantpost.utils;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.merchantpost.data.NewsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FakeDataUtils {

    /**
     * Creates a single ContentValues object with random news data for the provided date
     *
     * @param date a normalized date
     * @return ContentValues object filled with random news data
     */
    private static ContentValues createTestNewsContentValues(long date) {
        ContentValues testNewsValues = new ContentValues();
        testNewsValues.put(NewsContract.NewsEntry.COLUMN_DATE, date);
        testNewsValues.put(NewsContract.NewsEntry.COLUMN_TITLE, getNewsString());
        testNewsValues.put(NewsContract.NewsEntry.COLUMN_SOURCE, getNewsString());
        return testNewsValues;
    }

    /**
     * Creates random news data for 7 days starting today
     *
     * @param context
     */
    public static void insertFakeData(Context context) {
        //Get today's normalized date
        long today = NewsDateUtils.normalizeDate(System.currentTimeMillis());
        List<ContentValues> fakeValues = new ArrayList<ContentValues>();
        //loop over 7 days starting today onwards
        for (int i = 0; i < 7; i++) {
            fakeValues.add(FakeDataUtils.createTestNewsContentValues(today + TimeUnit.DAYS.toMillis(i)));
        }
        // Bulk Insert our new news data into News's Database
        context.getContentResolver().bulkInsert(
                NewsContract.NewsEntry.CONTENT_URI,
                fakeValues.toArray(new ContentValues[7]));
    }

    public static String getNewsString() {
        String NEWSCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder news = new StringBuilder();
        Random rnd = new Random();
        while (news.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * NEWSCHARS.length());
            news.append(NEWSCHARS.charAt(index));
        }
        String newsStr = news.toString();
        return newsStr;

    }
}
