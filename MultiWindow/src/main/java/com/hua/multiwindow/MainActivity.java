package com.hua.multiwindow;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStartBasicActivity(View view) {
        Log.i(tag, "starting BasicMultiWindowActivity class");
        mStartActivity(BasicMultiWindowActivity.class, false, 0);
    }

    public void onStartUnresizableClick(View view) {
        Log.i(tag, "starting UnresizableActivity class");
        mStartActivity(UnresizableActivity.class, true, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public void onStartAdjacentActivity(View view) {
        Log.i(tag, "starting AdjacentActivity class");
        mStartActivity(AdjacentActivity.class, true, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
    }

    public void onStartCustomConfigurationActivity(View view) {
        Log.i(tag, "starting CustomConfigurationChangeActivity class");
        mStartActivity(CustomConfigurationChangeActivity.class, false, 0);
    }

    public void onStartMinimumSizeActivity(View view) {
        Log.i(tag, "starting MinimumSizeActivity class");
        mStartActivity(MinimumSizeActivity.class, false, 0);
    }

    public void onStartLaunchBoundsActivity(View view) {
        Log.i(tag, "starting LaunchBoundsActivity class");
        Rect bounds = new Rect(500,300, 100, 0);
        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchBounds(bounds);

        Intent intent = new Intent(this, LaunchBoundsActivity.class);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(tag, "onConfigurationChanged: " + newConfig.toString());
    }
}
