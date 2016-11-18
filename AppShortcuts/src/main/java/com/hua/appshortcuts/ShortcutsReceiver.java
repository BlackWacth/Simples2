package com.hua.appshortcuts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShortcutsReceiver extends BroadcastReceiver {

    public ShortcutsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ShortcutsReceiver", "intent = " + intent);
        if(Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
            new ShortcutHelper(context).refreshShortcuts(true);
        }
    }
}
