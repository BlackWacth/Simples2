package com.hua.multiwindow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchBoundsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_bounds);
        setTitle("LaunchBounds");
        setDescription(R.id.launch_bounds_description, R.string.activity_bounds_description);
    }
}
