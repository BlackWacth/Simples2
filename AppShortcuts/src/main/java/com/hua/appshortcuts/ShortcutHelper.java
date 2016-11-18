package com.hua.appshortcuts;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Created by hzw on 2016/11/8.
 */

public class ShortcutHelper {

    private static final String TAG = MainActivity.TAG;

    private static final String EXTRA_LAST_REFRESH = "com.hua.appshortcuts.EXTRA_LAST_REFRESH";

    private static final long REFRESH_INTERVAL_MS = 60 * 60 * 1000;

    private final Context mContext;

    private final ShortcutManager mShortcutManager;
    private ShortcutInfo.Builder mExtras;

    public ShortcutHelper(Context context) {
        mContext = context;
        mShortcutManager = mContext.getSystemService(ShortcutManager.class);
    }

    public void maybeRestoreAllDynamicShortcuts() {
        if (mShortcutManager.getDynamicShortcuts().size() == 0) {
            // NOTE: If this application is always supposed to have dynamic(动态的) shortcuts, then publish
            // them here.
            // Note when an application is "restored" on a new device, all dynamic shortcuts
            // will *not* be restored but the pinned shortcuts *will*.
        }
    }

    public void reportShortcutUsed(String id) {
        mShortcutManager.reportShortcutUsed(id);
    }

    private void callShortcutManager(BooleanSupplier r){
        try {
            if (!r.getAsBoolean()) {
                Utils.showToast(mContext, "Call to ShortcutManager is rate-limited");
            }
        } catch (Exception e) {
            Log.e(TAG, "Caught Exception", e);
            Utils.showToast(mContext, "Error while calling ShortcutManager: " + e.toString());
        }
    }

    public  List<ShortcutInfo> getShortcuts() {
        final List<ShortcutInfo> ret = new ArrayList<>();
        final HashSet<String> seenKeys = new HashSet<>();

        for(ShortcutInfo shortcut : mShortcutManager.getDynamicShortcuts()) {
            if(!shortcut.isImmutable()) {
                ret.add(shortcut);
                seenKeys.add(shortcut.getId());
            }
        }

        for(ShortcutInfo shortcut : mShortcutManager.getPinnedShortcuts()) {
            if(!shortcut.isImmutable() && !seenKeys.contains(shortcut.getId())) {
                ret.add(shortcut);
                seenKeys.add(shortcut.getId());
            }
        }
        return ret;
    }

    public void refreshShortcuts(boolean force) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Log.i(TAG, "refreshShortcuts ....");
                final long now = System.currentTimeMillis();
                final long staleThreshold = force ? now : now - REFRESH_INTERVAL_MS;

                final List<ShortcutInfo> updateList = new ArrayList<>();

                for (ShortcutInfo shortcut : getShortcuts()) {
                    if (shortcut.isImmutable()) {
                        continue;
                    }
                    final PersistableBundle extras = shortcut.getExtras();
                    if (extras != null && extras.getLong(EXTRA_LAST_REFRESH) >= staleThreshold) {
                        continue;
                    }
                    Log.i(TAG, "Refreshing shortcut : " + shortcut.getId());
                    final ShortcutInfo.Builder b = new ShortcutInfo.Builder(mContext, shortcut.getId());
                    setSiteInformation(b, shortcut.getIntent().getData());
                    setExtras(b);
                    updateList.add(b.build());
                }

                if(updateList.size() >0) {
                    callShortcutManager(() -> mShortcutManager.updateShortcuts(updateList));
                }
                return null;
            }
        }.execute();
    }

    private ShortcutInfo.Builder setSiteInformation(ShortcutInfo.Builder b, Uri uri) {
        if(uri == null) {
            return null;
        }
        b.setShortLabel(uri.getHost() == null ? "default" : uri.getHost());
        b.setLongLabel(uri.toString() == null ? "default" : uri.toString());
        Bitmap bmp = fetchFavicon(uri);
        if(bmp != null) {
            b.setIcon(Icon.createWithBitmap(bmp));
        } else {
            b.setIcon(Icon.createWithResource(mContext, R.drawable.link));
        }
        return b;
    }

    public static Bitmap fetchFavicon(Uri uri) {
        final Uri iconUri = uri.buildUpon().path("favicon.ico").build();
        Log.i(TAG, "Fetching favicon from: " + iconUri);

        InputStream is = null;
        BufferedInputStream bis = null;

        try {
            URLConnection conn = new URL(iconUri.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            Log.w(TAG, "Failed to fetch favicon from " + iconUri, e);
            return null;
        } finally {
            try {
                if(bis != null){
                    bis.close();
                }
                if(is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ShortcutInfo.Builder  setExtras(ShortcutInfo.Builder b) {
        final PersistableBundle extras = new PersistableBundle();
        extras.putLong(EXTRA_LAST_REFRESH, System.currentTimeMillis());
        b.setExtras(extras);
        return b;
    }

    private ShortcutInfo createShortcutForUrl(String urlAsString) {
        Log.i(TAG, "createShortcutForUrl: " + urlAsString);
        final ShortcutInfo.Builder b = new ShortcutInfo.Builder(mContext, urlAsString);
        final Uri uri = Uri.parse(urlAsString);
        b.setIntent(new Intent(Intent.ACTION_VIEW, uri));

        setSiteInformation(b, uri);
        setExtras(b);

        return b.build();
    }

    private String normalizeUrl(String urlString) {
        if(urlString.startsWith("http://") || urlString.startsWith("https://")){
            return urlString;
        } else {
            return "http://" + urlString;
        }
    }

    public void addWebSiteShortcut(String urlAsString) {
        final String urlFinal = urlAsString;
        callShortcutManager(() -> {
            final ShortcutInfo shortcut = createShortcutForUrl(normalizeUrl(urlAsString));
            return mShortcutManager.addDynamicShortcuts(Arrays.asList(shortcut));
        });
    }

    public void removeShortcut(ShortcutInfo shorcut) {
        mShortcutManager.removeDynamicShortcuts(Arrays.asList(shorcut.getId()));
    }

    public void enableShortcut(ShortcutInfo shortcut) {
        mShortcutManager.enableShortcuts(Arrays.asList(shortcut.getId()));
    }

    public void disableShortcut(ShortcutInfo shortcut) {
        mShortcutManager.disableShortcuts(Arrays.asList(shortcut.getId()));
    }
}
