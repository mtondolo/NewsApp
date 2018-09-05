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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.merchantpost.R;

public class NewsPreferences {

    /**
     * Returns true if the user has selected top stories from display.
     *
     * @param context Context used to get the SharedPreferences
     * @return true news channel from display should be used
     */
    public static String getPreferredNewsChannel(Context context) {
        // Return the user's preferred news channel
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForNewsChannel = context.getString(R.string.pref_new_channel_key);
        String defaultNewsChannel = context.getString(R.string.pref_news_channel_default);
        return prefs.getString(keyForNewsChannel, defaultNewsChannel);
    }

}
