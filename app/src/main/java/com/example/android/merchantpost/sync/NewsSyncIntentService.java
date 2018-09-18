package com.example.android.merchantpost.sync;

import android.app.IntentService;
import android.content.Intent;

public class NewsSyncIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NewsSyncIntentService() {
        super("NewsSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NewsSyncTask.syncNews(this);

    }
}
